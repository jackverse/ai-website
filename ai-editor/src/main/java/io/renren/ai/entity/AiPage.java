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
@TableName("ai_page")
public class AiPage implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long tenantId;

    private Long projectId;

    private Long versionId;

    private Long templateId;

    private String name;

    private String description;

    /** Vue代码 */
    private String code;

    /** 页面结构JSON */
    private String structure;

    /** 页面类型: 1首页 2列表页 3详情页 4表单页 5关于页 */
    private Integer pageType;

    /** 路由路径 */
    private String routePath;

    /** 跳转关系JSON */
    private String links;

    /** 是否发布: 0否 1是 */
    private Integer isPublished;

    /** 页面版本号 */
    private Integer version;

    private Long createdBy;

    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updatedAt;

    /** 删除标记: 0正常 1删除 */
    @TableLogic
    private Integer deleted;
}
