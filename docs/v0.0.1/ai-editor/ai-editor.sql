-- AI Editor 数据库初始化脚本
-- 注意：与 renren_security 共用数据库，表前缀 ai_

-- 项目表
CREATE TABLE IF NOT EXISTS `ai_project` (
    `id`                  BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `tenant_id`           BIGINT NOT NULL COMMENT '租户ID',
    `name`                VARCHAR(100) NOT NULL COMMENT '项目名称',
    `description`         VARCHAR(500) DEFAULT NULL COMMENT '项目描述',
    `icon`                VARCHAR(500) DEFAULT NULL COMMENT '项目图标',
    `status`              TINYINT DEFAULT 1 COMMENT '状态: 0禁用 1启用',
    `current_version`     VARCHAR(20) DEFAULT NULL COMMENT '当前版本号',
    `created_by`          BIGINT NOT NULL COMMENT '创建人ID',
    `created_at`          DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`          DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`             TINYINT DEFAULT 0 COMMENT '删除标记: 0正常 1删除',
    INDEX `idx_tenant` (`tenant_id`),
    INDEX `idx_created_by` (`created_by`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI项目表';

-- 项目版本表
CREATE TABLE IF NOT EXISTS `ai_project_version` (
    `id`                  BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `project_id`          BIGINT NOT NULL COMMENT '项目ID',
    `version`             VARCHAR(20) NOT NULL COMMENT '版本号: v1.0',
    `description`         VARCHAR(500) DEFAULT NULL COMMENT '版本描述',
    `is_current`          TINYINT DEFAULT 0 COMMENT '是否当前版本: 0否 1是',
    `page_count`          INT DEFAULT 0 COMMENT '页面数量',
    `created_by`          BIGINT NOT NULL COMMENT '创建人ID',
    `created_at`          DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_project` (`project_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目版本表';

-- 模板表
CREATE TABLE IF NOT EXISTS `ai_template` (
    `id`                  BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `tenant_id`           BIGINT NOT NULL COMMENT '租户ID',
    `name`                VARCHAR(100) NOT NULL COMMENT '模板名称',
    `description`         VARCHAR(500) DEFAULT NULL COMMENT '模板描述',
    `type`                TINYINT NOT NULL COMMENT '类型: 1单页 2联动页面 3组件',
    `code`                LONGTEXT NOT NULL COMMENT 'Vue代码',
    `structure`           JSON DEFAULT NULL COMMENT '页面结构JSON',
    `tags`                VARCHAR(500) DEFAULT NULL COMMENT '标签,逗号分隔',
    `is_official`         TINYINT DEFAULT 0 COMMENT '是否官方模板: 0否 1是',
    `is_published`        TINYINT DEFAULT 0 COMMENT '是否发布: 0否 1是',
    `usage_count`         INT DEFAULT 0 COMMENT '使用次数',
    `quality_score`       TINYINT DEFAULT 0 COMMENT '质量评分: 1-5',
    `created_by`          BIGINT NOT NULL COMMENT '创建人ID',
    `created_at`          DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`          DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`             TINYINT DEFAULT 0 COMMENT '删除标记: 0正常 1删除',
    INDEX `idx_tenant` (`tenant_id`),
    INDEX `idx_type` (`type`),
    INDEX `idx_tags` (`tags`(100)),
    INDEX `idx_official` (`is_official`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI模板表';

-- 页面表
CREATE TABLE IF NOT EXISTS `ai_page` (
    `id`                  BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `tenant_id`           BIGINT NOT NULL COMMENT '租户ID',
    `project_id`          BIGINT DEFAULT NULL COMMENT '项目ID',
    `version_id`          BIGINT DEFAULT NULL COMMENT '版本ID',
    `template_id`         BIGINT DEFAULT NULL COMMENT '来源模板ID',
    `name`                VARCHAR(100) NOT NULL COMMENT '页面名称',
    `description`         VARCHAR(500) DEFAULT NULL COMMENT '页面描述',
    `code`                LONGTEXT NOT NULL COMMENT 'Vue代码',
    `structure`           JSON DEFAULT NULL COMMENT '页面结构JSON',
    `page_type`           TINYINT DEFAULT NULL COMMENT '页面类型: 1首页 2列表页 3详情页 4表单页 5关于页',
    `route_path`          VARCHAR(100) DEFAULT NULL COMMENT '路由路径',
    `links`               JSON DEFAULT NULL COMMENT '跳转关系JSON',
    `is_published`        TINYINT DEFAULT 0 COMMENT '是否发布: 0否 1是',
    `version`             INT DEFAULT 1 COMMENT '页面版本号',
    `created_by`          BIGINT NOT NULL COMMENT '创建人ID',
    `created_at`          DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`          DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`             TINYINT DEFAULT 0 COMMENT '删除标记: 0正常 1删除',
    INDEX `idx_tenant` (`tenant_id`),
    INDEX `idx_project` (`project_id`),
    INDEX `idx_version` (`version_id`),
    INDEX `idx_template` (`template_id`),
    INDEX `idx_published` (`is_published`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI页面表';

-- 提示词日志表
CREATE TABLE IF NOT EXISTS `ai_prompt_log` (
    `id`                  BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `tenant_id`           BIGINT NOT NULL COMMENT '租户ID',
    `user_id`             BIGINT NOT NULL COMMENT '用户ID',
    `project_id`          BIGINT DEFAULT NULL COMMENT '项目ID',
    `page_id`             BIGINT DEFAULT NULL COMMENT '页面ID',
    `prompt`              TEXT NOT NULL COMMENT '用户提示词',
    `model`               VARCHAR(50) NOT NULL COMMENT '使用的模型',
    `model_response`      LONGTEXT DEFAULT NULL COMMENT '模型响应',
    `request_tokens`      INT DEFAULT NULL COMMENT '请求Token数',
    `response_tokens`     INT DEFAULT NULL COMMENT '响应Token数',
    `total_tokens`        INT DEFAULT NULL COMMENT '总Token数',
    `result_code`         LONGTEXT DEFAULT NULL COMMENT '生成的代码',
    `quality_score`       TINYINT DEFAULT NULL COMMENT '质量评分: 1-5',
    `status`              TINYINT DEFAULT 1 COMMENT '状态: 0失败 1成功',
    `error_msg`           VARCHAR(500) DEFAULT NULL COMMENT '错误信息',
    `duration`            INT DEFAULT NULL COMMENT '耗时(毫秒)',
    `created_at`          DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_tenant` (`tenant_id`),
    INDEX `idx_user` (`user_id`),
    INDEX `idx_project` (`project_id`),
    INDEX `idx_page` (`page_id`),
    INDEX `idx_model` (`model`),
    INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI提示词日志表';

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

-- 配额表
CREATE TABLE IF NOT EXISTS `ai_quota` (
    `id`                  BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `tenant_id`           BIGINT NOT NULL COMMENT '租户ID',
    `daily_limit`         INT DEFAULT 100 COMMENT '每日生成次数限制',
    `daily_used`          INT DEFAULT 0 COMMENT '今日已使用次数',
    `monthly_limit`       INT DEFAULT 3000 COMMENT '每月生成次数限制',
    `monthly_used`        INT DEFAULT 0 COMMENT '本月已使用次数',
    `monthly_tokens`      INT DEFAULT 0 COMMENT '本月已使用Token',
    `token_limit`         INT DEFAULT 1000000 COMMENT '每月Token限制',
    `last_reset_date`     DATE DEFAULT NULL COMMENT '最后重置日期',
    `created_at`          DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`          DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY `uk_tenant` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI配额表';
