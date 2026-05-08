package io.renren.project.dao;

import io.renren.project.entity.Project;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

@Mapper
public interface ProjectDao {

    List<Project> listByTenantId(@Param("tenantId") Long tenantId);

    Project selectById(@Param("id") Long id);

    void insert(Project project);

    void update(Project project);

    void deleteById(@Param("id") Long id);

    Long countByTenantId(@Param("tenantId") Long tenantId);

    List<Map<String, Object>> recentOperations(@Param("tenantId") Long tenantId, @Param("limit") Integer limit);
}
