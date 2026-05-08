package io.renren.ai.service;

import io.renren.ai.entity.AiProject;
import io.renren.ai.entity.AiProjectVersion;
import io.renren.ai.dao.AiProjectDao;
import io.renren.ai.dao.AiProjectVersionDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiProjectService {

    private static final Long DEFAULT_TENANT = 1L;
    private static final Long DEFAULT_USER = 1L;

    private final AiProjectDao projectDao;
    private final AiProjectVersionDao versionDao;

    public List<AiProject> listByTenant(Long tenantId) {
        return projectDao.listByTenantId(tenantId);
    }

    public AiProject getById(Long id) {
        return projectDao.selectById(id);
    }

    public AiProject create(AiProject project) {
        if (project.getTenantId() == null) project.setTenantId(DEFAULT_TENANT);
        if (project.getCreatedBy() == null) project.setCreatedBy(DEFAULT_USER);
        if (project.getStatus() == null) project.setStatus(1);
        projectDao.insert(project);

        // 自动创建第一个版本
        AiProjectVersion firstVersion = AiProjectVersion.builder()
                .projectId(project.getId())
                .version("v1.0")
                .description("初始版本")
                .isCurrent(1)
                .pageCount(0)
                .createdBy(DEFAULT_USER)
                .build();
        versionDao.insert(firstVersion);

        project.setCurrentVersion("v1.0");
        projectDao.updateById(project);

        return project;
    }

    public AiProject update(AiProject project) {
        projectDao.updateById(project);
        return project;
    }

    public void delete(Long id) {
        projectDao.deleteById(id);
    }

    // ============ 版本相关 ============

    public List<AiProjectVersion> getVersionList(Long projectId) {
        return versionDao.listByProjectId(projectId);
    }

    @Transactional
    public AiProjectVersion createVersion(Long projectId, AiProjectVersion version) {
        version.setProjectId(projectId);
        version.setCreatedBy(DEFAULT_USER);
        version.setPageCount(0);

        // 如果是第一个版本，设为当前版本
        List<AiProjectVersion> existing = versionDao.listByProjectId(projectId);
        if (existing.isEmpty()) {
            version.setIsCurrent(1);
        } else {
            version.setIsCurrent(0);
        }

        versionDao.insert(version);
        return version;
    }

    @Transactional
    public void switchVersion(Long projectId, Long versionId) {
        // 取消所有当前版本
        List<AiProjectVersion> versions = versionDao.listByProjectId(projectId);
        for (AiProjectVersion v : versions) {
            v.setIsCurrent(0);
            versionDao.updateById(v);
        }

        // 设为新的当前版本
        AiProjectVersion newCurrent = versionDao.selectById(versionId);
        if (newCurrent != null) {
            newCurrent.setIsCurrent(1);
            versionDao.updateById(newCurrent);

            // 更新项目当前版本号
            AiProject project = projectDao.selectById(projectId);
            if (project != null) {
                project.setCurrentVersion(newCurrent.getVersion());
                projectDao.updateById(project);
            }
        }
    }

    public void deleteVersion(Long versionId) {
        versionDao.deleteById(versionId);
    }
}
