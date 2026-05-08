package io.renren.ai.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("ai_model_config")
public class AiModelConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 租户ID */
    private Long tenantId;

    /** 模型名称 */
    private String name;

    /** 显示名称 */
    private String displayName;

    /** 类型: openai, anthropic, aliyun, ollama */
    private String type;

    /** API地址 */
    private String endpoint;

    /** API密钥(加密存储) */
    private String apiKey;

    /** 模型标识 */
    private String model;

    /** 请求超时时间(秒)，默认10 */
    private Integer timeout;

    /** 是否默认: 0否 1是 */
    private Integer isDefault;

    /** 是否启用: 0否 1是 */
    private Integer isEnabled;

    /** 排序 */
    private Integer sort;

    /** 额外配置JSON */
    private String configJson;

    /** 创建人ID */
    private Long createdBy;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updatedAt;

    /** 删除标记: 0正常 1删除 */
    @TableLogic
    private Integer deleted;
}
