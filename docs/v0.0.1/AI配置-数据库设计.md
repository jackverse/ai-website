# AI 配置 - 数据库设计（ai-editor）

## 一、设计原则

1. **多租户隔离**：所有表包含 `tenant_id` 字段
2. **软删除**：使用 `deleted` 标记删除状态
3. **多对多关系**：通过关联表处理
4. **JSON 存储**：复杂结构使用 JSON 字段

## 二、数据库配置

### 2.1 Schema 规划

| Schema | 说明 |
|--------|------|
| `renren_security` | 用户、角色、权限、租户等基础表（复用） |
| `ai_editor` | AI 配置相关业务表（本模块） |

### 2.2 表结构清单（17 张）

#### 基础表（8）

| 表名 | 说明 |
|------|------|
| `ai_project` | 项目表 |
| `ai_project_version` | 项目版本表 |
| `ai_template` | 模板表 |
| `ai_page` | 页面表 |
| `ai_template_project` | 模板项目关联表（多对多） |
| `ai_prompt_log` | 提示词日志表 |
| `ai_model_config` | AI 模型配置表 |
| `ai_quota` | 配额表 |

#### 扩展表（9）

| 表名 | 说明 |
|------|------|
| `ai_component` | 组件表（基础/业务/官方） |
| `ai_component_category` | 组件分类表 |
| `ai_page_component` | 页面使用的组件实例表 |
| `ai_page_version` | 页面版本历史表 |
| `ai_template_version` | 模板版本历史表 |
| `ai_category` | 分类表（项目分类、模板分类） |
| `ai_tag` | 标签表 |
| `ai_template_tag` | 模板标签关联表 |
| `ai_batch_task` | 批量生成任务表 |

---

## 三、详细表设计

### 3.1 项目表 (ai_project)

```sql
CREATE TABLE `ai_project` (
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
```

### 3.2 项目版本表 (ai_project_version)

```sql
CREATE TABLE `ai_project_version` (
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
```

### 3.3 模板表 (ai_template)

```sql
CREATE TABLE `ai_template` (
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
```

### 3.4 页面表 (ai_page)

```sql
CREATE TABLE `ai_page` (
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
```

### 3.5 模板项目关联表 (ai_template_project)

```sql
CREATE TABLE `ai_template_project` (
    `id`                  BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `template_id`         BIGINT NOT NULL COMMENT '模板ID',
    `project_id`          BIGINT NOT NULL COMMENT '项目ID',
    `created_at`          DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY `uk_template_project` (`template_id`, `project_id`),
    INDEX `idx_template` (`template_id`),
    INDEX `idx_project` (`project_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='模板项目关联表(多对多)';
```

### 3.6 提示词日志表 (ai_prompt_log)

```sql
CREATE TABLE `ai_prompt_log` (
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
```

### 3.7 AI 模型配置表 (ai_model_config)

```sql
CREATE TABLE `ai_model_config` (
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
```

### 3.8 配额表 (ai_quota)

```sql
CREATE TABLE `ai_quota` (
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
```

### 3.9 组件表 (ai_component)

```sql
CREATE TABLE `ai_component` (
    `id`                  BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `tenant_id`           BIGINT NOT NULL COMMENT '租户ID',
    `category_id`         BIGINT DEFAULT NULL COMMENT '分类ID',
    `name`                VARCHAR(100) NOT NULL COMMENT '组件名称',
    `code`                LONGTEXT NOT NULL COMMENT '组件代码',
    `description`         VARCHAR(500) DEFAULT NULL COMMENT '组件描述',
    `props_schema`        JSON DEFAULT NULL COMMENT '属性Schema',
    `type`                TINYINT DEFAULT 1 COMMENT '类型: 1基础 2业务 3官方',
    `is_official`         TINYINT DEFAULT 0 COMMENT '是否官方: 0否 1是',
    `is_published`        TINYINT DEFAULT 0 COMMENT '是否发布: 0否 1是',
    `usage_count`         INT DEFAULT 0 COMMENT '使用次数',
    `created_by`          BIGINT NOT NULL COMMENT '创建人ID',
    `created_at`          DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`          DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`             TINYINT DEFAULT 0 COMMENT '删除标记: 0正常 1删除',
    INDEX `idx_tenant` (`tenant_id`),
    INDEX `idx_category` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI组件表';
```

### 3.10 组件分类表 (ai_component_category)

```sql
CREATE TABLE `ai_component_category` (
    `id`                  BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `tenant_id`           BIGINT NOT NULL COMMENT '租户ID',
    `parent_id`           BIGINT DEFAULT 0 COMMENT '父分类ID',
    `name`                VARCHAR(100) NOT NULL COMMENT '分类名称',
    `sort`                INT DEFAULT 0 COMMENT '排序',
    `created_at`          DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`          DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`             TINYINT DEFAULT 0 COMMENT '删除标记: 0正常 1删除',
    INDEX `idx_tenant` (`tenant_id`),
    INDEX `idx_parent` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI组件分类表';
```

### 3.11 页面组件实例表 (ai_page_component)

```sql
CREATE TABLE `ai_page_component` (
    `id`                  BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `page_id`             BIGINT NOT NULL COMMENT '页面ID',
    `component_id`        BIGINT NOT NULL COMMENT '组件ID',
    `props`               JSON DEFAULT NULL COMMENT '组件属性',
    `style`               JSON DEFAULT NULL COMMENT '组件样式',
    `slot_name`           VARCHAR(50) DEFAULT NULL COMMENT '插槽名称',
    `sort`                INT DEFAULT 0 COMMENT '排序',
    `parent_id`           BIGINT DEFAULT NULL COMMENT '父组件ID',
    `created_at`          DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_page` (`page_id`),
    INDEX `idx_component` (`component_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='页面组件实例表';
```

### 3.12 页面版本历史表 (ai_page_version)

```sql
CREATE TABLE `ai_page_version` (
    `id`                  BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `page_id`             BIGINT NOT NULL COMMENT '页面ID',
    `version`             INT NOT NULL COMMENT '版本号',
    `code`                LONGTEXT NOT NULL COMMENT 'Vue代码',
    `structure`           JSON DEFAULT NULL COMMENT '页面结构JSON',
    `change_desc`         VARCHAR(500) DEFAULT NULL COMMENT '变更说明',
    `created_by`          BIGINT NOT NULL COMMENT '创建人ID',
    `created_at`          DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_page` (`page_id`),
    INDEX `idx_version` (`page_id`, `version`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='页面版本历史表';
```

### 3.13 模板版本历史表 (ai_template_version)

```sql
CREATE TABLE `ai_template_version` (
    `id`                  BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `template_id`         BIGINT NOT NULL COMMENT '模板ID',
    `version`             INT NOT NULL COMMENT '版本号',
    `code`                LONGTEXT NOT NULL COMMENT 'Vue代码',
    `structure`           JSON DEFAULT NULL COMMENT '模板结构JSON',
    `change_desc`         VARCHAR(500) DEFAULT NULL COMMENT '变更说明',
    `created_by`          BIGINT NOT NULL COMMENT '创建人ID',
    `created_at`          DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_template` (`template_id`),
    INDEX `idx_version` (`template_id`, `version`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='模板版本历史表';
```

### 3.14 分类表 (ai_category)

```sql
CREATE TABLE `ai_category` (
    `id`                  BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `tenant_id`           BIGINT NOT NULL COMMENT '租户ID',
    `parent_id`           BIGINT DEFAULT 0 COMMENT '父分类ID',
    `name`                VARCHAR(100) NOT NULL COMMENT '分类名称',
    `type`                TINYINT NOT NULL COMMENT '类型: 1项目分类 2模板分类 3参考图分类',
    `icon`                VARCHAR(500) DEFAULT NULL COMMENT '图标',
    `sort`                INT DEFAULT 0 COMMENT '排序',
    `created_at`          DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`          DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`             TINYINT DEFAULT 0 COMMENT '删除标记: 0正常 1删除',
    INDEX `idx_tenant` (`tenant_id`),
    INDEX `idx_type` (`type`),
    INDEX `idx_parent` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI分类表';
```

### 3.15 标签表 (ai_tag)

```sql
CREATE TABLE `ai_tag` (
    `id`                  BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `tenant_id`           BIGINT NOT NULL COMMENT '租户ID',
    `name`                VARCHAR(50) NOT NULL COMMENT '标签名称',
    `color`               VARCHAR(20) DEFAULT NULL COMMENT '标签颜色',
    `created_at`          DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted`             TINYINT DEFAULT 0 COMMENT '删除标记: 0正常 1删除',
    UNIQUE KEY `uk_tenant_name` (`tenant_id`, `name`),
    INDEX `idx_tenant` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI标签表';
```

### 3.16 模板标签关联表 (ai_template_tag)

```sql
CREATE TABLE `ai_template_tag` (
    `id`                  BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `template_id`         BIGINT NOT NULL COMMENT '模板ID',
    `tag_id`              BIGINT NOT NULL COMMENT '标签ID',
    UNIQUE KEY `uk_template_tag` (`template_id`, `tag_id`),
    INDEX `idx_template` (`template_id`),
    INDEX `idx_tag` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='模板标签关联表';
```

### 3.17 批量生成任务表 (ai_batch_task)

```sql
CREATE TABLE `ai_batch_task` (
    `id`                  BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `tenant_id`           BIGINT NOT NULL COMMENT '租户ID',
    `user_id`             BIGINT NOT NULL COMMENT '用户ID',
    `project_id`          BIGINT DEFAULT NULL COMMENT '项目ID',
    `name`                VARCHAR(100) NOT NULL COMMENT '任务名称',
    `prompts`             JSON NOT NULL COMMENT '提示词列表JSON',
    `status`              TINYINT DEFAULT 0 COMMENT '状态: 0进行中 1已完成 2失败',
    `total_count`         INT DEFAULT 0 COMMENT '总数量',
    `success_count`       INT DEFAULT 0 COMMENT '成功数量',
    `fail_count`          INT DEFAULT 0 COMMENT '失败数量',
    `created_at`          DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `finished_at`         DATETIME DEFAULT NULL COMMENT '完成时间',
    INDEX `idx_tenant` (`tenant_id`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='批量生成任务表';
```

---

## 四、实体关系

```
┌─────────────────────────────────────────────────────────────────────┐
│                           基础业务                                    │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│   ai_project  ──────────── ai_project_version                       │
│        │                         │                                   │
│        │ N                       │ N                                 │
│        ▼                         ▼                                   │
│   ai_page ◄─────────── ai_template ◄──── ai_template_project       │
│        │                         │              │                    │
│        │ N                       │ N            │ N                  │
│        ▼                         ▼              ▼                    │
│   ai_page_version           ai_template_version                     │
│                                                                     │
│   ─────────────────────────────────────────────────────              │
│                           扩展能力                                    │
│   ─────────────────────────────────────────────────────              │
│                                                                     │
│   ai_component ─── ai_component_category                            │
│        │                                                             │
│        │ N                                                           │
│        ▼                                                             │
│   ai_page_component                                                 │
│                                                                     │
│   ai_template ◄──── ai_template_tag ──► ai_tag                      │
│                                                                     │
│   ai_category                                                       │
│                                                                     │
│   ai_batch_task                                                     │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 五、索引优化

| 表名 | 索引名 | 字段 | 说明 |
|------|--------|------|------|
| ai_project | idx_tenant | tenant_id | 租户查询 |
| ai_project | idx_created_by | created_by | 创建人查询 |
| ai_template | idx_tenant_type | tenant_id, type | 租户+类型筛选 |
| ai_template | idx_tags | tags | 标签搜索 |
| ai_page | idx_project_version | project_id, version_id | 项目版本查询 |
| ai_page | idx_published | is_published | 发布状态筛选 |
| ai_prompt_log | idx_created | created_at | 时间范围查询 |
| ai_prompt_log | idx_model | model | 模型统计 |

---

## 六、注意事项

1. **JSON 字段**：如 `structure`、`links`、`config_json` 使用 JSON 类型，需 MySQL 5.7+
2. **大文本**：代码字段使用 `LONGTEXT`，支持最大 4GB
3. **加密存储**：敏感信息（如 API Key）需加密存储
4. **分页查询**：日志表数据量大，考虑分区或定时归档