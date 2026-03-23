package org.zhengjiale.driver;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.util.HashMap;
import java.util.Map;

/**
 * 本地 WebDriver 创建工厂
 */
class LocalDriverFactory {

    private LocalDriverFactory() {
        // 私有构造函数
    }

    /**
     * 创建本地 WebDriver
     *
     * @param browser  浏览器类型
     * @param headless 是否无头模式
     * @return WebDriver 实例
     */
    static WebDriver createDriver(String browser, boolean headless) {
        switch (browser.toLowerCase()) {
            case "chrome":
                return createChromeDriver(headless);
            case "firefox":
                return createFirefoxDriver(headless);
            case "edge":
                return createEdgeDriver(headless);
            default:
                throw new IllegalArgumentException("Unsupported browser: " + browser);
        }
    }

    private static WebDriver createChromeDriver(boolean headless) {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = createChromeOptions(headless);
        return new ChromeDriver(options);
    }

    /**
     * 创建 Chrome 选项（包含反检测配置）
     */
    static ChromeOptions createChromeOptions(boolean headless) {
        ChromeOptions options = new ChromeOptions();

        if (headless) {
            options.addArguments("--headless=new");
        }

        // 基础选项
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-popup-blocking");

        // 反检测配置 - 绕过自动化检测
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);

        // 设置正常的 User-Agent
        options.addArguments("--user-agent=Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");

        // 禁用 webdriver 标志
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        options.setExperimentalOption("prefs", prefs);

        return options;
    }

    private static WebDriver createFirefoxDriver(boolean headless) {
        WebDriverManager.firefoxdriver().setup();
        FirefoxOptions options = createFirefoxOptions(headless);
        return new FirefoxDriver(options);
    }

    /**
     * 创建 Firefox 选项
     */
    static FirefoxOptions createFirefoxOptions(boolean headless) {
        FirefoxOptions options = new FirefoxOptions();
        if (headless) {
            options.addArguments("-headless");
        }
        // 反检测配置
        options.addPreference("dom.webdriver.enabled", false);
        options.addPreference("useAutomationExtension", false);
        return options;
    }

    private static WebDriver createEdgeDriver(boolean headless) {
        WebDriverManager.edgedriver().setup();
        EdgeOptions options = createEdgeOptions(headless);
        return new EdgeDriver(options);
    }

    /**
     * 创建 Edge 选项
     */
    static EdgeOptions createEdgeOptions(boolean headless) {
        EdgeOptions options = new EdgeOptions();
        if (headless) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        // 反检测配置
        options.addArguments("--disable-blink-features=AutomationControlled");
        return options;
    }
}
