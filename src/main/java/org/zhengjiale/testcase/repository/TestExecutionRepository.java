package org.zhengjiale.testcase.repository;

import org.zhengjiale.testcase.entity.TestExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 测试执行记录 Repository
 */
@Repository
public interface TestExecutionRepository extends JpaRepository<TestExecution, Long> {

    /**
     * 按测试用例ID查询执行记录
     */
    List<TestExecution> findByTestCaseIdOrderByExecutedAtDesc(Long testCaseId);

    /**
     * 按任务ID查询
     */
    Optional<TestExecution> findByTaskId(String taskId);

    /**
     * 按状态查询
     */
    List<TestExecution> findByStatus(String status);

    /**
     * 查询最近N条记录
     */
    List<TestExecution> findTop10ByOrderByExecutedAtDesc();
}
