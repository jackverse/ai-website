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
@TableName("ai_project_version")
public class AiProjectVersion implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long projectId;

    private String version;

    private String description;

    /** 是否当前版本: 0否 1是 */
    private Integer isCurrent;

    private Integer pageCount;

    private Long createdBy;

    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;
}
