package org.zhengjiale.listener;

import io.qameta.allure.Allure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.zhengjiale.driver.DriverFactory;
import org.zhengjiale.utils.ScreenshotUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * TestNG 测试监听器 - 处理测试生命周期事件
 */
public class TestListener implements ITestListener {
    private static final Logger logger = LogManager.getLogger(TestListener.class);

    @Override
    public void onStart(ITestContext context) {
        logger.info("========== Test Suite Started: {} ==========", context.getName());
    }

    @Override
    public void onFinish(ITestContext context) {
        logger.info("========== Test Suite Finished: {} ==========", context.getName());
        logger.info("Passed: {}, Failed: {}, Skipped: {}",
                context.getPassedTests().size(),
                context.getFailedTests().size(),
                context.getSkippedTests().size());
    }

    @Override
    public void onTestStart(ITestResult result) {
        logger.info("Test Started: {}", getTestName(result));
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        logger.info("Test Passed: {}", getTestName(result));
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String testName = getTestName(result);
        logger.error("Test Failed: {}", testName);

        // 获取异常信息
        Throwable throwable = result.getThrowable();
        if (throwable != null) {
            logger.error("Exception: {}", throwable.getMessage());
        }

        // 截图
        try {
            WebDriver driver = DriverFactory.getDriver();
            String screenshotPath = ScreenshotUtils.takeScreenshotForTest(driver, testName);
            if (screenshotPath != null) {
                attachScreenshotToAllure(screenshotPath);
            }
        } catch (Exception e) {
            logger.warn("Failed to take screenshot: {}", e.getMessage());
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        logger.warn("Test Skipped: {}", getTestName(result));
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        logger.warn("Test Failed but within success percentage: {}", getTestName(result));
    }

    private String getTestName(ITestResult result) {
        return result.getTestClass().getName() + "." + result.getMethod().getMethodName();
    }

    private void attachScreenshotToAllure(String screenshotPath) {
        try {
            File screenshot = new File(screenshotPath);
            if (screenshot.exists()) {
                Allure.addAttachment("Screenshot", "image/png",
                        new FileInputStream(screenshot), ".png");
            }
        } catch (IOException e) {
            logger.warn("Failed to attach screenshot to Allure: {}", e.getMessage());
        }
    }
}
