-- 测试用例表
CREATE TABLE IF NOT EXISTS test_cases (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL COMMENT '用例名称',
    description TEXT COMMENT '用例描述',
    browser VARCHAR(50) DEFAULT 'chrome' COMMENT '浏览器类型',
    url VARCHAR(500) COMMENT '起始URL',
    steps JSON NOT NULL COMMENT '测试步骤(JSON数组)',
    enabled BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_name (name),
    INDEX idx_enabled (enabled)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='测试用例表';

-- 测试执行记录表
CREATE TABLE IF NOT EXISTS test_executions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    test_case_id BIGINT NOT NULL COMMENT '测试用例ID',
    task_id VARCHAR(50) COMMENT '任务ID',
    status VARCHAR(20) NOT NULL COMMENT '执行状态',
    total_steps INT DEFAULT 0 COMMENT '总步骤数',
    passed_steps INT DEFAULT 0 COMMENT '通过步骤数',
    failed_step VARCHAR(255) COMMENT '失败步骤',
    error_message TEXT COMMENT '错误信息',
    duration BIGINT DEFAULT 0 COMMENT '执行时长(毫秒)',
    screenshot_path VARCHAR(500) COMMENT '截图路径',
    executed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '执行时间',
    INDEX idx_test_case_id (test_case_id),
    INDEX idx_status (status),
    INDEX idx_executed_at (executed_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='测试执行记录表';
