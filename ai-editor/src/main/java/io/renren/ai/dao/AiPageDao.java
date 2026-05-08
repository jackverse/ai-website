package io.renren.ai.dao;

import io.renren.ai.entity.AiPage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface AiPageDao extends BaseMapper<AiPage> {
    List<AiPage> listByTenantId(@Param("tenantId") Long tenantId);

    List<AiPage> listByProjectId(@Param("projectId") Long projectId);

    List<AiPage> listByVersionId(@Param("versionId") Long versionId);
}
