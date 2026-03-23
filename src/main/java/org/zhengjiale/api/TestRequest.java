package org.zhengjiale.api;

/**
 * 测试执行请求
 */
public class TestRequest {
    private String testClass;        // 测试类名，如 "BingSearchTest"
    private String testMethod;       // 测试方法名（可选）
    private String browser = "chrome";
    private boolean headless = true;
    private boolean parallel = false;
    private int retryCount = 2;

    // Getters and Setters
    public String getTestClass() {
        return testClass;
    }

    public void setTestClass(String testClass) {
        this.testClass = testClass;
    }

    public String getTestMethod() {
        return testMethod;
    }

    public void setTestMethod(String testMethod) {
        this.testMethod = testMethod;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public boolean isHeadless() {
        return headless;
    }

    public void setHeadless(boolean headless) {
        this.headless = headless;
    }

    public boolean isParallel() {
        return parallel;
    }

    public void setParallel(boolean parallel) {
        this.parallel = parallel;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }
}
