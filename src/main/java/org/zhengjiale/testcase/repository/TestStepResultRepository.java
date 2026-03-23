package org.zhengjiale.testcase.repository;

import org.zhengjiale.testcase.entity.TestStepResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 测试步骤结果 Repository
 */
@Repository
public interface TestStepResultRepository extends JpaRepository<TestStepResult, Long> {

    /**
     * 按执行ID查询步骤结果
     */
    List<TestStepResult> findByExecutionIdOrderByStepIndexAsc(Long executionId);

    /**
     * 删除执行记录的所有步骤
     */
    void deleteByExecutionId(Long executionId);
}
