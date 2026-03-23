package org.zhengjiale.testcase.executor;

import org.zhengjiale.testcase.entity.TestCase;
import org.zhengjiale.testcase.entity.TestExecution;
import org.zhengjiale.testcase.entity.TestStep;
import org.zhengjiale.testcase.entity.TestStepResult;
import org.zhengjiale.testcase.repository.TestStepResultRepository;
import org.zhengjiale.testcase.service.TestCaseService;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 动态测试用例执行器
 */
@Component
public class TestCaseExecutor {
    private static final Logger logger = LogManager.getLogger(TestCaseExecutor.class);

    @Value("${grid.url:http://localhost:4444/wd/hub}")
    private String gridUrl;

    @Value("${screenshot.dir:screenshots}")
    private String screenshotDir;

    @Autowired
    private TestCaseService testCaseService;

    @Autowired
    private TestStepResultRepository stepResultRepository;

    private final Map<String, TestExecution> executions = new ConcurrentHashMap<>();
    private final Map<Long, List<TestStepResult>> stepResults = new ConcurrentHashMap<>();

    /**
     * 执行测试用例
     */
    public String executeTestCase(Long testCaseId) {
        TestCase testCase = testCaseService.getTestCase(testCaseId)
                .orElseThrow(() -> new RuntimeException("测试用例不存在: " + testCaseId));

        String taskId = UUID.randomUUID().toString().substring(0, 8);

        TestExecution execution = new TestExecution();
        execution.setTestCaseId(testCaseId);
        execution.setTaskId(taskId);
        execution.setStatus("PENDING");
        execution.setTotalSteps(testCase.getSteps().size());
        executions.put(taskId, execution);

        // 异步执行
        new Thread(() -> runTest(taskId, testCase)).start();

        return taskId;
    }

    /**
     * 运行测试
     */
    private void runTest(String taskId, TestCase testCase) {
        TestExecution execution = executions.get(taskId);
        execution.setStatus("RUNNING");
        execution = testCaseService.saveExecution(execution);

        WebDriver driver = null;
        long startTime = System.currentTimeMillis();
        int passedSteps = 0;
        List<TestStepResult> results = new ArrayList<>();

        try {
            // 创建 WebDriver
            driver = createDriver(testCase.getBrowser());

            // 执行步骤
            for (int i = 0; i < testCase.getSteps().size(); i++) {
                TestStep step = testCase.getSteps().get(i);
                String stepName = step.getDescription() != null ? step.getDescription()
                        : "Step " + (i + 1) + ": " + step.getAction();

                TestStepResult stepResult = new TestStepResult();
                stepResult.setExecutionId(execution.getId());
                stepResult.setStepIndex(i + 1);
                stepResult.setStepName(stepName);
                stepResult.setAction(step.getAction());

                long stepStart = System.currentTimeMillis();

                try {
                    executeStep(driver, step, testCase.getUrl());
                    stepResult.setStatus("PASSED");
                    passedSteps++;
                    logger.info("[{}] {} - PASSED", taskId, stepName);
                } catch (Exception e) {
                    stepResult.setStatus("FAILED");
                    stepResult.setError(e.getMessage());
                    logger.error("[{}] {} - FAILED: {}", taskId, stepName, e.getMessage());

                    execution.setFailedStep(stepName);
                    execution.setErrorMessage(e.getMessage());
                }

                // 每个步骤都截图
                stepResult.setDuration(System.currentTimeMillis() - stepStart);
                String screenshotPath = takeScreenshot(driver, taskId, i + 1, stepResult.getStatus());
                stepResult.setScreenshotPath(screenshotPath);

                // 保存步骤结果
                stepResult = stepResultRepository.save(stepResult);
                results.add(stepResult);

                // 如果步骤失败，停止执行
                if ("FAILED".equals(stepResult.getStatus())) {
                    break;
                }
            }

            execution.setStatus(passedSteps == testCase.getSteps().size() ? "COMPLETED" : "FAILED");
            execution.setPassedSteps(passedSteps);

        } catch (Exception e) {
            logger.error("[{}] Test failed: {}", taskId, e.getMessage());
            execution.setStatus("FAILED");
            execution.setErrorMessage(e.getMessage());
        } finally {
            if (driver != null) {
                driver.quit();
            }

            execution.setDuration(System.currentTimeMillis() - startTime);
            testCaseService.saveExecution(execution);
            executions.put(taskId, execution);
            stepResults.put(execution.getId(), results);
        }
    }

    /**
     * 创建 WebDriver
     */
    private WebDriver createDriver(String browser) throws Exception {
        URL hubUrl = new URL(gridUrl);

        if ("firefox".equalsIgnoreCase(browser)) {
            FirefoxOptions options = new FirefoxOptions();
            options.addArguments("--headless");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            return new RemoteWebDriver(hubUrl, options);
        } else {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless=new");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-gpu");
            options.addArguments("--window-size=1920,1080");
            return new RemoteWebDriver(hubUrl, options);
        }
    }

    /**
     * 执行单个步骤
     */
    private void executeStep(WebDriver driver, TestStep step, String baseUrl) {
        String action = step.getAction().toLowerCase();
        String locator = step.getLocator();
        String value = step.getValue();
        Integer timeout = step.getTimeout() != null ? step.getTimeout() : 10;

        switch (action) {
            case "open":
                String url = value != null ? value : baseUrl;
                if (url == null) {
                    throw new RuntimeException("open 操作需要指定 url 或在用例中设置起始URL");
                }
                driver.get(url);
                break;

            case "input":
            case "type":
                WebElement inputElement = findElement(driver, locator, timeout);
                inputElement.clear();
                inputElement.sendKeys(value);
                break;

            case "click":
                WebElement clickElement = findElement(driver, locator, timeout);
                clickElement.click();
                break;

            case "select":
                org.openqa.selenium.support.ui.Select select = new org.openqa.selenium.support.ui.Select(
                        findElement(driver, locator, timeout));
                select.selectByVisibleText(value);
                break;

            case "wait":
                try {
                    Thread.sleep(timeout * 1000L);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                break;

            case "wait_for":
                findElement(driver, locator, timeout);
                break;

            case "assert":
                assertElement(driver, locator, step.getCondition(), timeout);
                break;

            case "assert_text":
                WebElement textElement = findElement(driver, locator, timeout);
                String actualText = textElement.getText();
                if (!actualText.contains(value)) {
                    throw new RuntimeException("文本断言失败: 期望包含 '" + value + "', 实际为 '" + actualText + "'");
                }
                break;

            case "script":
                ((JavascriptExecutor) driver).executeScript(value);
                break;

            case "screenshot":
                // 手动截图步骤，截图在步骤完成后自动执行
                break;

            case "refresh":
                driver.navigate().refresh();
                break;

            case "back":
                driver.navigate().back();
                break;

            case "forward":
                driver.navigate().forward();
                break;

            default:
                throw new RuntimeException("不支持的操作类型: " + action);
        }
    }

    /**
     * 查找元素
     */
    private WebElement findElement(WebDriver driver, String locator, int timeout) {
        By by = parseLocator(locator);
        org.openqa.selenium.support.ui.WebDriverWait wait = new org.openqa.selenium.support.ui.WebDriverWait(
                driver, java.time.Duration.ofSeconds(timeout));
        return wait.until(org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated(by));
    }

    /**
     * 解析定位器
     */
    private By parseLocator(String locator) {
        if (locator == null || locator.isEmpty()) {
            throw new RuntimeException("定位器不能为空");
        }

        if (locator.startsWith("id=")) {
            return By.id(locator.substring(3));
        } else if (locator.startsWith("name=")) {
            return By.name(locator.substring(5));
        } else if (locator.startsWith("css=") || locator.startsWith("#") || locator.startsWith(".") || locator.startsWith("[")) {
            String css = locator.startsWith("css=") ? locator.substring(4) : locator;
            return By.cssSelector(css);
        } else if (locator.startsWith("xpath=") || locator.startsWith("//") || locator.startsWith("./")) {
            String xpath = locator.startsWith("xpath=") ? locator.substring(6) : locator;
            return By.xpath(xpath);
        } else if (locator.startsWith("class=")) {
            return By.className(locator.substring(6));
        } else if (locator.startsWith("tag=")) {
            return By.tagName(locator.substring(4));
        } else if (locator.startsWith("link=")) {
            return By.linkText(locator.substring(5));
        } else if (locator.startsWith("partial=")) {
            return By.partialLinkText(locator.substring(8));
        } else {
            return By.cssSelector(locator);
        }
    }

    /**
     * 断言元素
     */
    private void assertElement(WebDriver driver, String locator, String condition, int timeout) {
        By by = parseLocator(locator);
        org.openqa.selenium.support.ui.WebDriverWait wait = new org.openqa.selenium.support.ui.WebDriverWait(
                driver, java.time.Duration.ofSeconds(timeout));

        switch (condition.toLowerCase()) {
            case "visible":
            case "displayed":
                wait.until(org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated(by));
                break;

            case "hidden":
            case "not_displayed":
                wait.until(org.openqa.selenium.support.ui.ExpectedConditions.invisibilityOfElementLocated(by));
                break;

            case "present":
            case "exist":
                wait.until(org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated(by));
                break;

            case "clickable":
                wait.until(org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable(by));
                break;

            case "selected":
                wait.until(org.openqa.selenium.support.ui.ExpectedConditions.elementToBeSelected(by));
                break;

            default:
                throw new RuntimeException("不支持的断言条件: " + condition);
        }
    }

    /**
     * 截图
     */
    private String takeScreenshot(WebDriver driver, String taskId, int stepNum, String status) {
        try {
            File dir = new File(screenshotDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String filename = taskId + "_step" + stepNum + "_" + status.toLowerCase() + ".png";
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File destFile = new File(dir, filename);
            FileUtils.copyFile(screenshot, destFile);

            logger.info("Screenshot saved: {}", destFile.getAbsolutePath());
            return destFile.getAbsolutePath();
        } catch (Exception e) {
            logger.error("Failed to take screenshot", e);
            return null;
        }
    }

    /**
     * 获取执行状态
     */
    public TestExecution getExecutionStatus(String taskId) {
        return executions.get(taskId);
    }

    /**
     * 获取步骤结果
     */
    public List<TestStepResult> getStepResults(Long executionId) {
        List<TestStepResult> results = stepResults.get(executionId);
        if (results == null) {
            results = stepResultRepository.findByExecutionIdOrderByStepIndexAsc(executionId);
        }
        return results;
    }
}
