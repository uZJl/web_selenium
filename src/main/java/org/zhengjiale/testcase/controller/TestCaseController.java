package org.zhengjiale.testcase.controller;

import org.zhengjiale.api.ApiResponse;
import org.zhengjiale.testcase.dto.TestCaseRequest;
import org.zhengjiale.testcase.entity.TestCase;
import org.zhengjiale.testcase.entity.TestExecution;
import org.zhengjiale.testcase.executor.TestCaseExecutor;
import org.zhengjiale.testcase.service.TestCaseService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 测试用例管理 API
 */
@RestController
@RequestMapping("/api/testcase")
public class TestCaseController {
    private static final Logger logger = LogManager.getLogger(TestCaseController.class);

    @Autowired
    private TestCaseService testCaseService;

    @Autowired
    private TestCaseExecutor testCaseExecutor;

    /**
     * 创建测试用例
     * POST /api/testcase
     */
    @PostMapping
    public ResponseEntity<ApiResponse<TestCase>> createTestCase(@Valid @RequestBody TestCaseRequest request) {
        logger.info("Creating test case: {}", request.getName());
        TestCase testCase = testCaseService.createTestCase(request);
        return ResponseEntity.ok(ApiResponse.success("测试用例创建成功", testCase));
    }

    /**
     * 获取所有测试用例
     * GET /api/testcase
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<TestCase>>> getAllTestCases(
            @RequestParam(required = false) Boolean enabled) {
        List<TestCase> testCases;
        if (enabled != null && enabled) {
            testCases = testCaseService.getEnabledTestCases();
        } else {
            testCases = testCaseService.getAllTestCases();
        }
        return ResponseEntity.ok(ApiResponse.success(testCases));
    }

    /**
     * 获取单个测试用例
     * GET /api/testcase/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TestCase>> getTestCase(@PathVariable Long id) {
        return testCaseService.getTestCase(id)
                .map(testCase -> ResponseEntity.ok(ApiResponse.success(testCase)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 更新测试用例
     * PUT /api/testcase/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TestCase>> updateTestCase(
            @PathVariable Long id,
            @Valid @RequestBody TestCaseRequest request) {
        logger.info("Updating test case: {}", id);
        TestCase testCase = testCaseService.updateTestCase(id, request);
        return ResponseEntity.ok(ApiResponse.success("测试用例更新成功", testCase));
    }

    /**
     * 删除测试用例
     * DELETE /api/testcase/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTestCase(@PathVariable Long id) {
        logger.info("Deleting test case: {}", id);
        testCaseService.deleteTestCase(id);
        return ResponseEntity.ok(ApiResponse.success("测试用例删除成功", null));
    }

    /**
     * 启用/禁用测试用例
     * PATCH /api/testcase/{id}/toggle
     */
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<ApiResponse<TestCase>> toggleTestCase(
            @PathVariable Long id,
            @RequestParam boolean enabled) {
        TestCase testCase = testCaseService.toggleTestCase(id, enabled);
        return ResponseEntity.ok(ApiResponse.success(enabled ? "测试用例已启用" : "测试用例已禁用", testCase));
    }

    /**
     * 执行测试用例
     * POST /api/testcase/{id}/run
     */
    @PostMapping("/{id}/run")
    public ResponseEntity<ApiResponse<String>> runTestCase(@PathVariable Long id) {
        logger.info("Running test case: {}", id);
        String taskId = testCaseExecutor.executeTestCase(id);
        return ResponseEntity.ok(ApiResponse.success("测试执行已启动", taskId));
    }

    /**
     * 获取执行状态
     * GET /api/testcase/status/{taskId}
     */
    @GetMapping("/status/{taskId}")
    public ResponseEntity<ApiResponse<TestExecution>> getExecutionStatus(@PathVariable String taskId) {
        TestExecution execution = testCaseExecutor.getExecutionStatus(taskId);
        if (execution == null) {
            execution = testCaseService.getExecutionByTaskId(taskId).orElse(null);
        }
        if (execution == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ApiResponse.success(execution));
    }

    /**
     * 获取测试用例执行历史
     * GET /api/testcase/{id}/history
     */
    @GetMapping("/{id}/history")
    public ResponseEntity<ApiResponse<List<TestExecution>>> getExecutionHistory(@PathVariable Long id) {
        List<TestExecution> history = testCaseService.getExecutionHistory(id);
        return ResponseEntity.ok(ApiResponse.success(history));
    }

    /**
     * 获取最近执行记录
     * GET /api/testcase/executions/recent
     */
    @GetMapping("/executions/recent")
    public ResponseEntity<ApiResponse<List<TestExecution>>> getRecentExecutions() {
        List<TestExecution> executions = testCaseService.getRecentExecutions();
        return ResponseEntity.ok(ApiResponse.success(executions));
    }
}
