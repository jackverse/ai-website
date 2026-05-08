package io.renren.project.service;

import io.renren.project.entity.Project;
import io.renren.project.dao.ProjectDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectDao projectDao;

    public List<Project> listByTenantId(Long tenantId) {
        return projectDao.listByTenantId(tenantId);
    }

    public Project getById(Long id) {
        return projectDao.selectById(id);
    }

    @Transactional
    public Project create(Project project) {
        project.setCreatedBy(1L);
        project.setCreatedAt(LocalDateTime.now());
        project.setUpdatedAt(LocalDateTime.now());
        project.setStatus(1);
        project.setPageCount(0);
        if (project.getVersion() == null) {
            project.setVersion("1.0");
        }
        projectDao.insert(project);
        return project;
    }

    @Transactional
    public Project update(Project project) {
        Project existing = projectDao.selectById(project.getId());
        if (existing == null) {
            throw new RuntimeException("项目不存在");
        }
        if (project.getName() != null) {
            existing.setName(project.getName());
        }
        if (project.getDescription() != null) {
            existing.setDescription(project.getDescription());
        }
        if (project.getVersion() != null) {
            existing.setVersion(project.getVersion());
        }
        if (project.getPageCount() != null) {
            existing.setPageCount(project.getPageCount());
        }
        if (project.getStatus() != null) {
            existing.setStatus(project.getStatus());
        }
        existing.setUpdatedAt(LocalDateTime.now());
        projectDao.update(existing);
        return projectDao.selectById(existing.getId());
    }

    @Transactional
    public boolean delete(Long id) {
        projectDao.deleteById(id);
        return true;
    }

    public Long countByTenantId(Long tenantId) {
        return projectDao.countByTenantId(tenantId);
    }

    public List<Map<String, Object>> recentOperations(Long tenantId) {
        return projectDao.recentOperations(tenantId, 10);
    }
}
