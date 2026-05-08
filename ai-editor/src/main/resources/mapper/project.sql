-- 项目表
CREATE TABLE IF NOT EXISTS `project` (
    `id`              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `tenant_id`       BIGINT NOT NULL COMMENT '租户ID',
    `name`            VARCHAR(100) NOT NULL COMMENT '项目名称',
    `description`     VARCHAR(500) DEFAULT NULL COMMENT '项目描述',
    `version`         VARCHAR(20) DEFAULT '1.0' COMMENT '版本号',
    `page_count`      INT DEFAULT 0 COMMENT '页面数量',
    `status`          TINYINT DEFAULT 1 COMMENT '状态: 0禁用 1启用',
    `created_by`      BIGINT NOT NULL COMMENT '创建人ID',
    `created_at`      DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`         TINYINT DEFAULT 0 COMMENT '删除标记: 0正常 1删除',
    INDEX `idx_tenant` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目表';

INSERT INTO `project` (`tenant_id`, `name`, `description`, `version`, `page_count`, `status`, `created_by`) VALUES
(1, '企业官网', '公司官网项目，包含产品展示、新闻动态等模块', '2.0', 12, 1, 1),
(1, '电商平台', '在线商城项目，支持商品展示、购物车、订单管理', '1.5', 25, 1, 1),
(1, '个人博客', '技术博客网站，分享技术与生活', '1.0', 8, 0, 1);
