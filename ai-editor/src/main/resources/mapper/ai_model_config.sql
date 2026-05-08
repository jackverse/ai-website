-- AI模型配置表
CREATE TABLE IF NOT EXISTS `ai_model_config` (
    `id`                  BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `tenant_id`           BIGINT NOT NULL COMMENT '租户ID',
    `name`                VARCHAR(50) NOT NULL COMMENT '模型名称: gpt-4, claude-3, tongyi',
    `display_name`        VARCHAR(100) NOT NULL COMMENT '显示名称',
    `type`                VARCHAR(20) NOT NULL COMMENT '类型: openai, anthropic, aliyun, ollama',
    `endpoint`            VARCHAR(500) DEFAULT NULL COMMENT 'API地址',
    `api_key`             VARCHAR(500) NOT NULL COMMENT 'API密钥(加密存储)',
    `model`               VARCHAR(100) NOT NULL COMMENT '模型标识',
    `is_default`          TINYINT DEFAULT 0 COMMENT '是否默认: 0否 1是',
    `is_enabled`          TINYINT DEFAULT 1 COMMENT '是否启用: 0否 1是',
    `sort`                INT DEFAULT 0 COMMENT '排序',
    `config_json`         JSON DEFAULT NULL COMMENT '额外配置JSON',
    `created_by`          BIGINT NOT NULL COMMENT '创建人ID',
    `created_at`          DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`          DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`             TINYINT DEFAULT 0 COMMENT '删除标记: 0正常 1删除',
    UNIQUE KEY `uk_tenant_name` (`tenant_id`, `name`),
    INDEX `idx_tenant` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI模型配置表';

-- 插入默认数据
INSERT INTO `ai_model_config` (`tenant_id`, `name`, `display_name`, `type`, `endpoint`, `api_key`, `model`, `is_default`, `is_enabled`, `sort`, `created_by`) VALUES
(1, 'gpt-4', 'GPT-4', 'openai', 'https://api.openai.com/v1', 'sk-xxx', 'gpt-4', 1, 1, 1, 1),
(1, 'gpt-3.5-turbo', 'GPT-3.5', 'openai', 'https://api.openai.com/v1', 'sk-xxx', 'gpt-3.5-turbo', 0, 1, 2, 1),
(1, 'claude-3', 'Claude-3', 'anthropic', 'https://api.anthropic.com', 'sk-ant-xxx', 'claude-3-sonnet', 0, 1, 3, 1);
