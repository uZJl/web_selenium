package org.zhengjiale.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * 配置管理类 - 从 YAML 文件加载测试配置，支持命令行参数覆盖
 *
 * 支持的命令行参数：
 * -Dgrid.enabled=true/false     - 是否启用 Grid
 * -Dgrid.url=http://ip:4444/wd/hub - Grid 服务地址
 * -Dbrowser=chrome/firefox/edge - 浏览器类型
 * -Dheadless=true/false         - 无头模式
 */
public class ConfigManager {
    private static final Logger logger = LogManager.getLogger(ConfigManager.class);
    private static final String CONFIG_FILE = "config/config.yaml";
    private static ConfigManager instance;
    private TestConfig config;

    private ConfigManager() {
        loadConfig();
        applySystemProperties();
    }

    public static synchronized ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    private void loadConfig() {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            // 首先尝试从外部配置文件加载
            File externalConfig = new File(CONFIG_FILE);
            if (externalConfig.exists()) {
                config = mapper.readValue(externalConfig, TestConfig.class);
                logger.info("Loaded config from external file: {}", externalConfig.getAbsolutePath());
            } else {
                // 从 classpath 加载
                InputStream inputStream = getClass().getClassLoader().getResourceAsStream("config.yaml");
                if (inputStream != null) {
                    config = mapper.readValue(inputStream, TestConfig.class);
                    logger.info("Loaded config from classpath");
                } else {
                    // 使用默认配置
                    config = getDefaultConfig();
                    logger.warn("Config file not found, using default configuration");
                }
            }
        } catch (IOException e) {
            logger.error("Failed to load config file", e);
            config = getDefaultConfig();
        }
    }

    /**
     * 应用系统属性覆盖配置文件
     * 允许通过命令行参数 -Dxxx=xxx 覆盖配置
     */
    private void applySystemProperties() {
        // Grid 启用状态
        String gridEnabled = System.getProperty("grid.enabled");
        if (gridEnabled != null) {
            config.setGridEnabled(Boolean.parseBoolean(gridEnabled));
            logger.info("Overridden grid.enabled from system property: {}", gridEnabled);
        }

        // Grid URL
        String gridUrl = System.getProperty("grid.url");
        if (gridUrl != null) {
            config.setGridUrl(gridUrl);
            logger.info("Overridden grid.url from system property: {}", gridUrl);
        }

        // 浏览器
        String browser = System.getProperty("browser");
        if (browser != null) {
            config.setBrowser(browser);
            logger.info("Overridden browser from system property: {}", browser);
        }

        // 无头模式
        String headless = System.getProperty("headless");
        if (headless != null) {
            config.setHeadless(Boolean.parseBoolean(headless));
            logger.info("Overridden headless from system property: {}", headless);
        }

        // 隐式等待
        String implicitWait = System.getProperty("implicit.wait");
        if (implicitWait != null) {
            config.setImplicitWait(Integer.parseInt(implicitWait));
        }

        // 页面加载超时
        String pageLoadTimeout = System.getProperty("page.load.timeout");
        if (pageLoadTimeout != null) {
            config.setPageLoadTimeout(Integer.parseInt(pageLoadTimeout));
        }

        // 重试次数
        String retryCount = System.getProperty("retry.count");
        if (retryCount != null) {
            config.setRetryCount(Integer.parseInt(retryCount));
        }
    }

    private TestConfig getDefaultConfig() {
        TestConfig defaultConfig = new TestConfig();
        defaultConfig.setGridEnabled(false);
        defaultConfig.setGridUrl("http://localhost:4444/wd/hub");
        defaultConfig.setBrowser("chrome");
        defaultConfig.setImplicitWait(10);
        defaultConfig.setPageLoadTimeout(30);
        defaultConfig.setScriptTimeout(30);
        defaultConfig.setHeadless(false);
        defaultConfig.setRetryCount(2);
        defaultConfig.setScreenshotOnFailure(true);
        return defaultConfig;
    }

    public TestConfig getConfig() {
        return config;
    }

    public String getGridUrl() {
        return config.getGridUrl();
    }

    public boolean isGridEnabled() {
        return config.isGridEnabled();
    }

    public String getBrowser() {
        return config.getBrowser();
    }

    public int getImplicitWait() {
        return config.getImplicitWait();
    }

    public int getPageLoadTimeout() {
        return config.getPageLoadTimeout();
    }

    public int getScriptTimeout() {
        return config.getScriptTimeout();
    }

    public boolean isHeadless() {
        return config.isHeadless();
    }

    public int getRetryCount() {
        return config.getRetryCount();
    }

    public boolean isScreenshotOnFailure() {
        return config.isScreenshotOnFailure();
    }

    /**
     * 打印当前配置（用于调试）
     */
    public void printConfig() {
        logger.info("========== Current Configuration ==========");
        logger.info("Grid Enabled: {}", config.isGridEnabled());
        logger.info("Grid URL: {}", config.getGridUrl());
        logger.info("Browser: {}", config.getBrowser());
        logger.info("Headless: {}", config.isHeadless());
        logger.info("Implicit Wait: {}s", config.getImplicitWait());
        logger.info("Page Load Timeout: {}s", config.getPageLoadTimeout());
        logger.info("Retry Count: {}", config.getRetryCount());
        logger.info("===========================================");
    }
}
