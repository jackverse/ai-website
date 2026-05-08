package io.renren.ai.dao;

import io.renren.ai.entity.AiGenerateLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface AiGenerateLogDao extends BaseMapper<AiGenerateLog> {
    List<AiGenerateLog> listByTenantId(@Param("tenantId") Long tenantId);

    List<AiGenerateLog> listByUserId(@Param("userId") Long userId);

    List<AiGenerateLog> recentList(@Param("tenantId") Long tenantId, @Param("limit") Integer limit);
}
