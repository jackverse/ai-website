package io.renren.ai.dao;

import io.renren.ai.entity.AiTemplate;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface AiTemplateDao extends BaseMapper<AiTemplate> {
    List<AiTemplate> listByTenantId(@Param("tenantId") Long tenantId);

    List<AiTemplate> listByType(@Param("tenantId") Long tenantId, @Param("type") Integer type);

    List<AiTemplate> listOfficial(@Param("tenantId") Long tenantId);
}
