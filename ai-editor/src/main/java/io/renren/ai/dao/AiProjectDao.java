package io.renren.ai.dao;

import io.renren.ai.entity.AiProject;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface AiProjectDao extends BaseMapper<AiProject> {
    List<AiProject> listByTenantId(@Param("tenantId") Long tenantId);
}
