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
@TableName("ai_template")
public class AiTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long tenantId;

    private String name;

    private String description;

    /** 类型: 1单页 2联动页面 3组件 */
    private Integer type;

    /** Vue代码 */
    private String code;

    /** 页面结构JSON */
    private String structure;

    /** 标签,逗号分隔 */
    private String tags;

    /** 是否官方模板: 0否 1是 */
    private Integer isOfficial;

    /** 是否发布: 0否 1是 */
    private Integer isPublished;

    private Integer usageCount;

    /** 质量评分: 1-5 */
    private Integer qualityScore;

    private Long createdBy;

    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updatedAt;

    /** 删除标记: 0正常 1删除 */
    @TableLogic
    private Integer deleted;
}
