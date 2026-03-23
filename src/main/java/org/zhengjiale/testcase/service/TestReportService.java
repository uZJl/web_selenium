package org.zhengjiale.testcase.service;

import org.zhengjiale.testcase.dto.TestReport;
import org.zhengjiale.testcase.entity.TestCase;
import org.zhengjiale.testcase.entity.TestExecution;
import org.zhengjiale.testcase.entity.TestStepResult;
import org.zhengjiale.testcase.executor.TestCaseExecutor;
import org.zhengjiale.testcase.repository.TestStepResultRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

/**
 * 测试报告服务
 */
@Service
public class TestReportService {
    private static final Logger logger = LogManager.getLogger(TestReportService.class);

    @Autowired
    private TestCaseService testCaseService;

    @Autowired
    private TestStepResultRepository stepResultRepository;

    @Autowired
    private TestCaseExecutor testCaseExecutor;

    @Value("${screenshot.dir:screenshots}")
    private String screenshotDir;

    /**
     * 获取测试报告
     */
    public TestReport getReport(String taskId) {
        TestExecution execution = testCaseExecutor.getExecutionStatus(taskId);
        if (execution == null) {
            execution = testCaseService.getExecutionByTaskId(taskId).orElse(null);
        }
        if (execution == null) {
            return null;
        }

        TestReport report = new TestReport();
        report.setExecution(execution);

        // 获取测试用例名称
        Optional<TestCase> testCase = testCaseService.getTestCase(execution.getTestCaseId());
        testCase.ifPresent(tc -> report.setTestCaseName(tc.getName()));

        // 获取步骤结果
        List<TestStepResult> steps = testCaseExecutor.getStepResults(execution.getId());
        if (steps == null || steps.isEmpty()) {
            steps = stepResultRepository.findByExecutionIdOrderByStepIndexAsc(execution.getId());
        }
        report.setSteps(steps);

        // 生成 HTML 报告
        report.setReportHtml(generateHtmlReport(report));

        return report;
    }

    /**
     * 生成 HTML 报告
     */
    private String generateHtmlReport(TestReport report) {
        StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html>\n");
        html.append("<html lang=\"zh-CN\">\n");
        html.append("<head>\n");
        html.append("    <meta charset=\"UTF-8\">\n");
        html.append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        html.append("    <title>测试报告 - ").append(report.getTestCaseName()).append("</title>\n");
        html.append("    <style>\n");
        html.append("        * { margin: 0; padding: 0; box-sizing: border-box; }\n");
        html.append("        body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif; background: #f5f5f5; padding: 20px; }\n");
        html.append("        .container { max-width: 1000px; margin: 0 auto; background: #fff; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); overflow: hidden; }\n");
        html.append("        .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: #fff; padding: 30px; }\n");
        html.append("        .header h1 { font-size: 24px; margin-bottom: 10px; }\n");
        html.append("        .header .meta { opacity: 0.9; font-size: 14px; }\n");
        html.append("        .summary { display: flex; padding: 20px; background: #fafafa; border-bottom: 1px solid #eee; }\n");
        html.append("        .summary-item { flex: 1; text-align: center; padding: 15px; }\n");
        html.append("        .summary-item .value { font-size: 32px; font-weight: bold; }\n");
        html.append("        .summary-item .label { font-size: 14px; color: #666; margin-top: 5px; }\n");
        html.append("        .passed { color: #52c41a; }\n");
        html.append("        .failed { color: #f5222d; }\n");
        html.append("        .steps { padding: 20px; }\n");
        html.append("        .step { border: 1px solid #e8e8e8; border-radius: 8px; margin-bottom: 15px; overflow: hidden; }\n");
        html.append("        .step-header { display: flex; align-items: center; padding: 15px; background: #fafafa; cursor: pointer; }\n");
        html.append("        .step-header:hover { background: #f0f0f0; }\n");
        html.append("        .step-status { width: 24px; height: 24px; border-radius: 50%; display: flex; align-items: center; justify-content: center; margin-right: 15px; font-size: 14px; }\n");
        html.append("        .step-status.passed { background: #52c41a; color: #fff; }\n");
        html.append("        .step-status.failed { background: #f5222d; color: #fff; }\n");
        html.append("        .step-info { flex: 1; }\n");
        html.append("        .step-name { font-weight: 500; margin-bottom: 3px; }\n");
        html.append("        .step-meta { font-size: 12px; color: #999; }\n");
        html.append("        .step-duration { font-size: 14px; color: #666; }\n");
        html.append("        .step-content { padding: 15px; background: #fff; border-top: 1px solid #e8e8e8; }\n");
        html.append("        .step-error { background: #fff2f0; border: 1px solid #ffccc7; border-radius: 4px; padding: 12px; margin-bottom: 15px; color: #f5222d; font-family: monospace; font-size: 13px; white-space: pre-wrap; word-break: break-all; }\n");
        html.append("        .step-screenshot { margin-top: 10px; }\n");
        html.append("        .step-screenshot img { max-width: 100%; border: 1px solid #e8e8e8; border-radius: 4px; }\n");
        html.append("        .footer { padding: 20px; text-align: center; color: #999; font-size: 12px; border-top: 1px solid #eee; }\n");
        html.append("    </style>\n");
        html.append("</head>\n");
        html.append("<body>\n");
        html.append("    <div class=\"container\">\n");
        html.append("        <div class=\"header\">\n");
        html.append("            <h1>").append(escapeHtml(report.getTestCaseName())).append("</h1>\n");
        html.append("            <div class=\"meta\">\n");
        html.append("                任务ID: ").append(report.getExecution().getTaskId()).append(" | ");
        html.append("                执行时间: ").append(report.getExecution().getExecutedAt()).append(" | ");
        html.append("                耗时: ").append(formatDuration(report.getExecution().getDuration())).append("\n");
        html.append("            </div>\n");
        html.append("        </div>\n");

        // 摘要
        TestExecution exec = report.getExecution();
        String statusClass = "COMPLETED".equals(exec.getStatus()) ? "passed" : "failed";
        html.append("        <div class=\"summary\">\n");
        html.append("            <div class=\"summary-item\">\n");
        html.append("                <div class=\"value ").append(statusClass).append("\">").append(exec.getStatus()).append("</div>\n");
        html.append("                <div class=\"label\">状态</div>\n");
        html.append("            </div>\n");
        html.append("            <div class=\"summary-item\">\n");
        html.append("                <div class=\"value\">").append(exec.getTotalSteps()).append("</div>\n");
        html.append("                <div class=\"label\">总步骤</div>\n");
        html.append("            </div>\n");
        html.append("            <div class=\"summary-item\">\n");
        html.append("                <div class=\"value passed\">").append(exec.getPassedSteps()).append("</div>\n");
        html.append("                <div class=\"label\">通过</div>\n");
        html.append("            </div>\n");
        html.append("            <div class=\"summary-item\">\n");
        html.append("                <div class=\"value failed\">").append(exec.getTotalSteps() - exec.getPassedSteps()).append("</div>\n");
        html.append("                <div class=\"label\">失败</div>\n");
        html.append("            </div>\n");
        html.append("        </div>\n");

        // 步骤详情
        html.append("        <div class=\"steps\">\n");
        if (report.getSteps() != null) {
            for (TestStepResult step : report.getSteps()) {
                String stepStatusClass = "PASSED".equals(step.getStatus()) ? "passed" : "failed";
                String stepIcon = "PASSED".equals(step.getStatus()) ? "✓" : "✗";

                html.append("            <div class=\"step\">\n");
                html.append("                <div class=\"step-header\">\n");
                html.append("                    <div class=\"step-status ").append(stepStatusClass).append("\">").append(stepIcon).append("</div>\n");
                html.append("                    <div class=\"step-info\">\n");
                html.append("                        <div class=\"step-name\">Step ").append(step.getStepIndex()).append(": ").append(escapeHtml(step.getStepName())).append("</div>\n");
                html.append("                        <div class=\"step-meta\">Action: ").append(step.getAction()).append("</div>\n");
                html.append("                    </div>\n");
                html.append("                    <div class=\"step-duration\">").append(formatDuration(step.getDuration())).append("</div>\n");
                html.append("                </div>\n");
                html.append("                <div class=\"step-content\">\n");

                // 错误信息
                if (step.getError() != null && !step.getError().isEmpty()) {
                    html.append("                    <div class=\"step-error\">").append(escapeHtml(step.getError())).append("</div>\n");
                }

                // 截图
                if (step.getScreenshotPath() != null && !step.getScreenshotPath().isEmpty()) {
                    String screenshotBase64 = getImageBase64(step.getScreenshotPath());
                    if (screenshotBase64 != null) {
                        html.append("                    <div class=\"step-screenshot\">\n");
                        html.append("                        <img src=\"data:image/png;base64,").append(screenshotBase64).append("\" alt=\"截图\" />\n");
                        html.append("                    </div>\n");
                    }
                }

                html.append("                </div>\n");
                html.append("            </div>\n");
            }
        }
        html.append("        </div>\n");

        html.append("        <div class=\"footer\">\n");
        html.append("            Generated by Selenium Grid Automation Framework\n");
        html.append("        </div>\n");
        html.append("    </div>\n");
        html.append("</body>\n");
        html.append("</html>");

        return html.toString();
    }

    /**
     * 获取图片的 Base64 编码
     */
    private String getImageBase64(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                return null;
            }
            byte[] bytes = Files.readAllBytes(file.toPath());
            return Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            logger.error("Failed to read screenshot: {}", path, e);
            return null;
        }
    }

    /**
     * 格式化时长
     */
    private String formatDuration(Long ms) {
        if (ms == null || ms == 0) {
            return "0ms";
        }
        if (ms < 1000) {
            return ms + "ms";
        }
        double seconds = ms / 1000.0;
        return String.format("%.2fs", seconds);
    }

    /**
     * HTML 转义
     */
    private String escapeHtml(String str) {
        if (str == null) {
            return "";
        }
        return str.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
