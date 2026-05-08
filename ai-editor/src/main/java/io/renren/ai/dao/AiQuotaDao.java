package io.renren.ai.dao;

import io.renren.ai.entity.AiQuota;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AiQuotaDao extends BaseMapper<AiQuota> {
    AiQuota findByTenantId(@Param("tenantId") Long tenantId);
}
