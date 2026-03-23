package org.zhengjiale.config;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 测试配置 POJO
 */
public class TestConfig {
    @JsonProperty("grid_enabled")
    private boolean gridEnabled;

    @JsonProperty("grid_url")
    private String gridUrl;

    private String browser;

    @JsonProperty("implicit_wait")
    private int implicitWait;

    @JsonProperty("page_load_timeout")
    private int pageLoadTimeout;

    @JsonProperty("script_timeout")
    private int scriptTimeout;

    private boolean headless;

    @JsonProperty("retry_count")
    private int retryCount;

    @JsonProperty("screenshot_on_failure")
    private boolean screenshotOnFailure;

    // Getters and Setters
    public boolean isGridEnabled() {
        return gridEnabled;
    }

    public void setGridEnabled(boolean gridEnabled) {
        this.gridEnabled = gridEnabled;
    }

    public String getGridUrl() {
        return gridUrl;
    }

    public void setGridUrl(String gridUrl) {
        this.gridUrl = gridUrl;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public int getImplicitWait() {
        return implicitWait;
    }

    public void setImplicitWait(int implicitWait) {
        this.implicitWait = implicitWait;
    }

    public int getPageLoadTimeout() {
        return pageLoadTimeout;
    }

    public void setPageLoadTimeout(int pageLoadTimeout) {
        this.pageLoadTimeout = pageLoadTimeout;
    }

    public int getScriptTimeout() {
        return scriptTimeout;
    }

    public void setScriptTimeout(int scriptTimeout) {
        this.scriptTimeout = scriptTimeout;
    }

    public boolean isHeadless() {
        return headless;
    }

    public void setHeadless(boolean headless) {
        this.headless = headless;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public boolean isScreenshotOnFailure() {
        return screenshotOnFailure;
    }

    public void setScreenshotOnFailure(boolean screenshotOnFailure) {
        this.screenshotOnFailure = screenshotOnFailure;
    }
}
