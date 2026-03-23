package org.zhengjiale.testcase.service;

import org.zhengjiale.testcase.dto.TestCaseRequest;
import org.zhengjiale.testcase.entity.TestCase;
import org.zhengjiale.testcase.entity.TestExecution;
import org.zhengjiale.testcase.repository.TestCaseRepository;
import org.zhengjiale.testcase.repository.TestExecutionRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 测试用例服务
 */
@Service
public class TestCaseService {
    private static final Logger logger = LogManager.getLogger(TestCaseService.class);

    @Autowired
    private TestCaseRepository testCaseRepository;

    @Autowired
    private TestExecutionRepository testExecutionRepository;

    /**
     * 创建测试用例
     */
    @Transactional
    public TestCase createTestCase(TestCaseRequest request) {
        TestCase testCase = new TestCase();
        testCase.setName(request.getName());
        testCase.setDescription(request.getDescription());
        testCase.setBrowser(request.getBrowser());
        testCase.setUrl(request.getUrl());
        testCase.setSteps(request.getSteps());
        testCase.setEnabled(request.getEnabled());
        return testCaseRepository.save(testCase);
    }

    /**
     * 更新测试用例
     */
    @Transactional
    public TestCase updateTestCase(Long id, TestCaseRequest request) {
        TestCase testCase = testCaseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("测试用例不存在: " + id));

        testCase.setName(request.getName());
        testCase.setDescription(request.getDescription());
        testCase.setBrowser(request.getBrowser());
        testCase.setUrl(request.getUrl());
        testCase.setSteps(request.getSteps());
        testCase.setEnabled(request.getEnabled());

        return testCaseRepository.save(testCase);
    }

    /**
     * 获取所有测试用例
     */
    public List<TestCase> getAllTestCases() {
        return testCaseRepository.findAll();
    }

    /**
     * 获取启用的测试用例
     */
    public List<TestCase> getEnabledTestCases() {
        return testCaseRepository.findByEnabledTrue();
    }

    /**
     * 获取单个测试用例
     */
    public Optional<TestCase> getTestCase(Long id) {
        return testCaseRepository.findById(id);
    }

    /**
     * 删除测试用例
     */
    @Transactional
    public void deleteTestCase(Long id) {
        testCaseRepository.deleteById(id);
    }

    /**
     * 启用/禁用测试用例
     */
    @Transactional
    public TestCase toggleTestCase(Long id, boolean enabled) {
        TestCase testCase = testCaseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("测试用例不存在: " + id));
        testCase.setEnabled(enabled);
        return testCaseRepository.save(testCase);
    }

    /**
     * 保存执行记录
     */
    @Transactional
    public TestExecution saveExecution(TestExecution execution) {
        return testExecutionRepository.save(execution);
    }

    /**
     * 获取执行记录
     */
    public Optional<TestExecution> getExecutionByTaskId(String taskId) {
        return testExecutionRepository.findByTaskId(taskId);
    }

    /**
     * 获取测试用例的执行历史
     */
    public List<TestExecution> getExecutionHistory(Long testCaseId) {
        return testExecutionRepository.findByTestCaseIdOrderByExecutedAtDesc(testCaseId);
    }

    /**
     * 获取最近执行记录
     */
    public List<TestExecution> getRecentExecutions() {
        return testExecutionRepository.findTop10ByOrderByExecutedAtDesc();
    }
}
