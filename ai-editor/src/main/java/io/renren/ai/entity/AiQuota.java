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
@TableName("ai_quota")
public class AiQuota implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long tenantId;

    /** 每日限制 */
    private Integer dailyLimit;

    /** 每日已用 */
    private Integer dailyUsed;

    /** 每月限制 */
    private Integer monthlyLimit;

    /** 每月已用 */
    private Integer monthlyUsed;

    /** token限制 */
    private Integer tokenLimit;

    /** token已用 */
    private Integer monthlyTokens;

    /** 上次重置日期 */
    private java.time.LocalDate lastResetDate;

    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updatedAt;
}
