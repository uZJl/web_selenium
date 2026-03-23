package org.zhengjiale.testcase.repository;

import org.zhengjiale.testcase.entity.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 测试用例 Repository
 */
@Repository
public interface TestCaseRepository extends JpaRepository<TestCase, Long> {

    /**
     * 查询所有启用的测试用例
     */
    List<TestCase> findByEnabledTrue();

    /**
     * 按名称模糊查询
     */
    List<TestCase> findByNameContaining(String name);

    /**
     * 按浏览器类型查询
     */
    List<TestCase> findByBrowser(String browser);
}
