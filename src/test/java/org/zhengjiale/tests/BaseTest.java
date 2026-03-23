package org.zhengjiale.tests;

import io.qameta.allure.Allure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.zhengjiale.config.ConfigManager;
import org.zhengjiale.driver.DriverFactory;
import org.zhengjiale.listener.TestListener;

import java.lang.reflect.Method;

/**
 * 测试基类 - 所有测试类都应继承此类
 */
@Listeners({TestListener.class})
public abstract class BaseTest {
    protected WebDriver driver;
    protected Logger logger = LogManager.getLogger(getClass());

    @BeforeMethod
    @Parameters({"browser"})
    public void setUp(Method method, @Optional String browser) {
        // 如果 TestNG XML 中指定了浏览器，则覆盖配置文件中的设置
        if (browser != null && !browser.isEmpty()) {
            System.setProperty("browser", browser);
        }

        logger.info("Setting up test: {}", method.getName());
        driver = DriverFactory.createDriver();

        // 添加 Allure 报告信息
        Allure.step("Initialize WebDriver");
        Allure.parameter("Browser", ConfigManager.getInstance().getBrowser());
        Allure.parameter("Grid Enabled", ConfigManager.getInstance().isGridEnabled());
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        logger.info("Tearing down test");
        DriverFactory.quitDriver();
        Allure.step("Quit WebDriver");
    }

    /**
     * 获取 WebDriver 实例
     *
     * @return WebDriver 实例
     */
    protected WebDriver getDriver() {
        return driver;
    }

    /**
     * 打开页面
     *
     * @param url 页面 URL
     */
    protected void openPage(String url) {
        logger.info("Opening page: {}", url);
        driver.get(url);
        Allure.step("Open page: " + url);
    }

    /**
     * 获取当前页面标题
     *
     * @return 页面标题
     */
    protected String getPageTitle() {
        return driver.getTitle();
    }

    /**
     * 获取当前页面 URL
     *
     * @return 页面 URL
     */
    protected String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}
