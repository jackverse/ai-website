package io.renren.ai.dao;

import io.renren.ai.entity.AiPromptLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface AiPromptLogDao extends BaseMapper<AiPromptLog> {
    List<AiPromptLog> listByTenantId(@Param("tenantId") Long tenantId);
}
