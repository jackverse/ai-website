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
@TableName("ai_prompt_log")
public class AiPromptLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long tenantId;

    private Long userId;

    private Long projectId;

    private Long pageId;

    /** 提示词内容 */
    private String prompt;

    /** 使用的模型 */
    private String model;

    /** 模型返回内容 */
    private String modelResponse;

    /** 请求消耗token数 */
    private Integer requestTokens;

    /** 响应消耗token数 */
    private Integer responseTokens;

    /** 总token数 */
    private Integer totalTokens;

    /** 状态: 1成功 0失败 */
    private Integer status;

    /** 错误信息 */
    private String errorMsg;

    /** 质量评分 1-5 */
    private Integer qualityScore;

    /** 耗时(毫秒) */
    private Integer duration;

    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;
}
