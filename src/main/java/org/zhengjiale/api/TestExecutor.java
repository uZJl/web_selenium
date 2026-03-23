package org.zhengjiale.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 测试执行服务
 * 通过调用 Maven 命令执行测试
 */
public class TestExecutor {
    private static final Logger logger = LogManager.getLogger(TestExecutor.class);
    private static final Map<String, TestResult> taskResults = new ConcurrentHashMap<>();
    private static final Map<String, Process> runningProcesses = new ConcurrentHashMap<>();

    /**
     * 执行测试
     */
    public String executeTest(TestRequest request) {
        String taskId = UUID.randomUUID().toString().substring(0, 8);

        TestResult result = new TestResult();
        result.setTaskId(taskId);
        result.setStatus("PENDING");
        result.setStartTime(LocalDateTime.now());
        taskResults.put(taskId, result);

        // 异步执行测试
        new Thread(() -> runTest(taskId, request)).start();

        return taskId;
    }

    /**
     * 运行测试
     */
    private void runTest(String taskId, TestRequest request) {
        TestResult result = taskResults.get(taskId);
        result.setStatus("RUNNING");
        taskResults.put(taskId, result);

        try {
            // 构建 Maven 命令
            List<String> command = buildMavenCommand(request);
            logger.info("Executing command: {}", String.join(" ", command));

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(new File(System.getProperty("user.dir")));
            pb.redirectErrorStream(true);

            Process process = pb.start();
            runningProcesses.put(taskId, process);

            // 读取输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            StringBuilder output = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                logger.info("[{}] {}", taskId, line);
                output.append(line).append("\n");
            }

            int exitCode = process.waitFor();
            runningProcesses.remove(taskId);

            // 解析测试结果
            parseTestResults(result, output.toString(), exitCode);
            result.setStatus("COMPLETED");
            result.setEndTime(LocalDateTime.now());
            result.setReportUrl("/api/test/report/" + taskId);

        } catch (Exception e) {
            logger.error("Test execution failed for task: {}", taskId, e);
            result.setStatus("FAILED");
            result.setErrorMessage(e.getMessage());
            result.setEndTime(LocalDateTime.now());
        }

        taskResults.put(taskId, result);
    }

    /**
     * 构建 Maven 命令
     */
    private List<String> buildMavenCommand(TestRequest request) {
        List<String> command = new ArrayList<>();
        command.add("mvn");
        command.add("test");

        // 指定测试类
        if (request.getTestClass() != null && !request.getTestClass().isEmpty()) {
            if (request.getTestMethod() != null && !request.getTestMethod().isEmpty()) {
                command.add("-Dtest=" + request.getTestClass() + "#" + request.getTestMethod());
            } else {
                command.add("-Dtest=" + request.getTestClass());
            }
        }

        // Grid 配置 - 支持环境变量覆盖
        String gridUrl = System.getenv().getOrDefault("GRID_URL", "http://localhost:4444/wd/hub");
        command.add("-Dgrid.enabled=true");
        command.add("-Dgrid.url=" + gridUrl);

        // 浏览器
        command.add("-Dbrowser=" + request.getBrowser());

        // 无头模式
        command.add("-Dheadless=" + request.isHeadless());

        // 重试次数
        command.add("-Dretry.count=" + request.getRetryCount());

        // 并行执行
        if (request.isParallel()) {
            command.add("-DsuiteXmlFile=src/test/resources/testng-parallel.xml");
        }

        return command;
    }

    /**
     * 解析测试结果
     */
    private void parseTestResults(TestResult result, String output, int exitCode) {
        // 解析 Maven Surefire 输出
        // 示例: Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
        try {
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
                "Tests run: (\\d+), Failures: (\\d+), Errors: (\\d+), Skipped: (\\d+)"
            );
            java.util.regex.Matcher matcher = pattern.matcher(output);

            if (matcher.find()) {
                result.setTotalTests(Integer.parseInt(matcher.group(1)));
                result.setFailedTests(Integer.parseInt(matcher.group(2)) + Integer.parseInt(matcher.group(3)));
                result.setSkippedTests(Integer.parseInt(matcher.group(4)));
                result.setPassedTests(result.getTotalTests() - result.getFailedTests() - result.getSkippedTests());
            }

            // 解析测试时间
            java.util.regex.Pattern timePattern = java.util.regex.Pattern.compile(
                "Total time:\\s+(\\d+\\.?\\d*)\\s*s"
            );
            java.util.regex.Matcher timeMatcher = timePattern.matcher(output);
            if (timeMatcher.find()) {
                double seconds = Double.parseDouble(timeMatcher.group(1));
                result.setDuration((long) (seconds * 1000));
            }
        } catch (Exception e) {
            logger.warn("Failed to parse test results", e);
        }
    }

    /**
     * 获取测试结果
     */
    public TestResult getTestResult(String taskId) {
        return taskResults.get(taskId);
    }

    /**
     * 获取所有任务
     */
    public List<TestResult> getAllTasks() {
        return new ArrayList<>(taskResults.values());
    }

    /**
     * 取消测试
     */
    public boolean cancelTest(String taskId) {
        Process process = runningProcesses.get(taskId);
        if (process != null) {
            process.destroy();
            runningProcesses.remove(taskId);

            TestResult result = taskResults.get(taskId);
            if (result != null) {
                result.setStatus("CANCELLED");
                result.setEndTime(LocalDateTime.now());
                taskResults.put(taskId, result);
            }
            return true;
        }
        return false;
    }

    /**
     * 删除任务记录
     */
    public boolean deleteTask(String taskId) {
        if (taskResults.containsKey(taskId)) {
            taskResults.remove(taskId);
            return true;
        }
        return false;
    }
}
