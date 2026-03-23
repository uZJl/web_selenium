package org.zhengjiale.listener;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;
import org.zhengjiale.config.ConfigManager;

/**
 * 测试失败重试监听器
 */
public class RetryListener implements IRetryAnalyzer {
    private int retryCount = 0;
    private final int maxRetryCount = ConfigManager.getInstance().getRetryCount();

    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < maxRetryCount) {
            retryCount++;
            return true;
        }
        return false;
    }
}
