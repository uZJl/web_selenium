package org.zhengjiale.driver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.zhengjiale.config.ConfigManager;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

/**
 * WebDriver 工厂类 - 支持本地和 Grid 远程执行
 */
public class DriverFactory {
    private static final Logger logger = LogManager.getLogger(DriverFactory.class);
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    private DriverFactory() {
        // 私有构造函数
    }

    /**
     * 创建 WebDriver 实例
     *
     * @return WebDriver 实例
     */
    public static WebDriver createDriver() {
        ConfigManager config = ConfigManager.getInstance();
        WebDriver driver;

        if (config.isGridEnabled()) {
            driver = createRemoteDriver(config);
        } else {
            driver = createLocalDriver(config);
        }

        configureDriver(driver, config);
        driverThreadLocal.set(driver);
        logger.info("WebDriver created successfully - Grid: {}, Browser: {}",
                config.isGridEnabled(), config.getBrowser());
        return driver;
    }

    /**
     * 创建本地 WebDriver
     */
    private static WebDriver createLocalDriver(ConfigManager config) {
        String browser = config.getBrowser().toLowerCase();
        return LocalDriverFactory.createDriver(browser, config.isHeadless());
    }

    /**
     * 创建远程 WebDriver (连接到 Selenium Grid)
     */
    private static WebDriver createRemoteDriver(ConfigManager config) {
        String browser = config.getBrowser().toLowerCase();
        URL gridUrl;
        try {
            gridUrl = new URL(config.getGridUrl());
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid Grid URL: " + config.getGridUrl(), e);
        }

        switch (browser) {
            case "chrome":
                ChromeOptions chromeOptions = LocalDriverFactory.createChromeOptions(config.isHeadless());
                return new RemoteWebDriver(gridUrl, chromeOptions);

            case "firefox":
                FirefoxOptions firefoxOptions = LocalDriverFactory.createFirefoxOptions(config.isHeadless());
                return new RemoteWebDriver(gridUrl, firefoxOptions);

            case "edge":
                EdgeOptions edgeOptions = LocalDriverFactory.createEdgeOptions(config.isHeadless());
                return new RemoteWebDriver(gridUrl, edgeOptions);

            default:
                throw new IllegalArgumentException("Unsupported browser: " + browser);
        }
    }

    /**
     * 配置 WebDriver 超时时间
     */
    private static void configureDriver(WebDriver driver, ConfigManager config) {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(config.getImplicitWait()));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(config.getPageLoadTimeout()));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(config.getScriptTimeout()));
        driver.manage().window().maximize();
    }

    /**
     * 获取当前线程的 WebDriver
     *
     * @return WebDriver 实例
     */
    public static WebDriver getDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver == null) {
            throw new IllegalStateException("WebDriver not initialized for current thread. Call createDriver() first.");
        }
        return driver;
    }

    /**
     * 关闭当前线程的 WebDriver
     */
    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            driver.quit();
            driverThreadLocal.remove();
            logger.info("WebDriver quit successfully");
        }
    }
}
