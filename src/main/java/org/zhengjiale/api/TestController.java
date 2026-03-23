package org.zhengjiale.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 测试执行 REST API 控制器
 *
 * API 端点:
 * POST /api/test/execute     - 执行测试
 * GET  /api/test/status/{id} - 查询测试状态
 * GET  /api/test/tasks       - 获取所有任务
 * DELETE /api/test/{id}      - 取消/删除任务
 */
@RestController
@RequestMapping("/api/test")
public class TestController {

    private final TestExecutor testExecutor = new TestExecutor();

    /**
     * 执行测试
     *
     * 请求示例:
     * {
     *   "testClass": "BingSearchTest",
     *   "testMethod": "testSearchFunction",
     *   "browser": "chrome",
     *   "headless": true,
     *   "parallel": false
     * }
     */
    @PostMapping("/execute")
    public ResponseEntity<ApiResponse<String>> executeTest(@RequestBody TestRequest request) {
        try {
            String taskId = testExecutor.executeTest(request);
            return ResponseEntity.ok(ApiResponse.success("Test execution started", taskId));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error(500, "Failed to start test: " + e.getMessage()));
        }
    }

    /**
     * 查询测试状态
     */
    @GetMapping("/status/{taskId}")
    public ResponseEntity<ApiResponse<TestResult>> getTestStatus(@PathVariable String taskId) {
        TestResult result = testExecutor.getTestResult(taskId);
        if (result == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * 获取所有任务
     */
    @GetMapping("/tasks")
    public ResponseEntity<ApiResponse<List<TestResult>>> getAllTasks() {
        List<TestResult> tasks = testExecutor.getAllTasks();
        return ResponseEntity.ok(ApiResponse.success(tasks));
    }

    /**
     * 取消测试
     */
    @DeleteMapping("/{taskId}")
    public ResponseEntity<ApiResponse<String>> cancelTest(@PathVariable String taskId) {
        boolean cancelled = testExecutor.cancelTest(taskId);
        if (cancelled) {
            return ResponseEntity.ok(ApiResponse.success("Test cancelled", taskId));
        }

        boolean deleted = testExecutor.deleteTask(taskId);
        if (deleted) {
            return ResponseEntity.ok(ApiResponse.success("Task deleted", taskId));
        }

        return ResponseEntity.notFound().build();
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.success("Service is running", "OK"));
    }
}
