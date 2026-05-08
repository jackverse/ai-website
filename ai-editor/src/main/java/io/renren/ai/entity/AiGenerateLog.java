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
@TableName("ai_generate_log")
public class AiGenerateLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long tenantId;

    private Long userId;

    private Long projectId;

    private Long pageId;

    /** 页面类型: 1首页 2列表页 3详情页 4表单页 5单页 */
    private Integer pageType;

    /** 风格: tech/simple/business/modern */
    private String style;

    /** 目标端: pc/mobile/both */
    private String platform;

    /** 用户需求描述 */
    private String prompt;

    /** 参考图片URL */
    private String referenceImage;

    /** 使用的模型 */
    private String model;

    /** 生成的代码 */
    private String generatedCode;

    /** 状态: 0失败 1成功 */
    private Integer status;

    /** 错误信息 */
    private String errorMsg;

    /** 耗时(毫秒) */
    private Integer duration;

    /** 质量评分: 1-5 */
    private Integer qualityScore;

    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;
}
