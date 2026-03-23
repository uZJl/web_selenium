package org.zhengjiale.testcase.dto;

import org.zhengjiale.testcase.entity.TestExecution;
import org.zhengjiale.testcase.entity.TestStepResult;

import java.util.List;

/**
 * 测试报告
 */
public class TestReport {

    private TestExecution execution;
    private String testCaseName;
    private List<TestStepResult> steps;
    private String reportHtml;

    // Getters and Setters
    public TestExecution getExecution() {
        return execution;
    }

    public void setExecution(TestExecution execution) {
        this.execution = execution;
    }

    public String getTestCaseName() {
        return testCaseName;
    }

    public void setTestCaseName(String testCaseName) {
        this.testCaseName = testCaseName;
    }

    public List<TestStepResult> getSteps() {
        return steps;
    }

    public void setSteps(List<TestStepResult> steps) {
        this.steps = steps;
    }

    public String getReportHtml() {
        return reportHtml;
    }

    public void setReportHtml(String reportHtml) {
        this.reportHtml = reportHtml;
    }
}
