package org.zhengjiale.api;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 测试执行结果
 */
public class TestResult {
    private String taskId;
    private String status;           // PENDING, RUNNING, COMPLETED, FAILED
    private int totalTests;
    private int passedTests;
    private int failedTests;
    private int skippedTests;
    private long duration;           // 毫秒
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String reportUrl;        // Allure 报告 URL
    private List<TestCaseResult> testCases;
    private String errorMessage;

    // 内部类：单个测试用例结果
    public static class TestCaseResult {
        private String className;
        private String methodName;
        private String status;       // PASS, FAIL, SKIP
        private long duration;
        private String errorMessage;

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public String getMethodName() {
            return methodName;
        }

        public void setMethodName(String methodName) {
            this.methodName = methodName;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public long getDuration() {
            return duration;
        }

        public void setDuration(long duration) {
            this.duration = duration;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }

    // Getters and Setters
    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTotalTests() {
        return totalTests;
    }

    public void setTotalTests(int totalTests) {
        this.totalTests = totalTests;
    }

    public int getPassedTests() {
        return passedTests;
    }

    public void setPassedTests(int passedTests) {
        this.passedTests = passedTests;
    }

    public int getFailedTests() {
        return failedTests;
    }

    public void setFailedTests(int failedTests) {
        this.failedTests = failedTests;
    }

    public int getSkippedTests() {
        return skippedTests;
    }

    public void setSkippedTests(int skippedTests) {
        this.skippedTests = skippedTests;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getReportUrl() {
        return reportUrl;
    }

    public void setReportUrl(String reportUrl) {
        this.reportUrl = reportUrl;
    }

    public List<TestCaseResult> getTestCases() {
        return testCases;
    }

    public void setTestCases(List<TestCaseResult> testCases) {
        this.testCases = testCases;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
