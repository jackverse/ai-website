package io.renren.ai.dao;

import io.renren.ai.entity.AiProjectVersion;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface AiProjectVersionDao extends BaseMapper<AiProjectVersion> {
    List<AiProjectVersion> listByProjectId(@Param("projectId") Long projectId);
}
