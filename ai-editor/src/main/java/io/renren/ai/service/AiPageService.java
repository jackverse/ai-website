package io.renren.ai.service;

import io.renren.ai.entity.AiPage;
import io.renren.ai.dao.AiPageDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiPageService {

    private static final Long DEFAULT_TENANT = 1L;
    private static final Long DEFAULT_USER = 1L;

    private final AiPageDao pageDao;

    public List<AiPage> listByTenant(Long tenantId) {
        return pageDao.listByTenantId(tenantId);
    }

    public List<AiPage> listByProjectId(Long projectId) {
        return pageDao.listByProjectId(projectId);
    }

    public AiPage getById(Long id) {
        return pageDao.selectById(id);
    }

    public AiPage create(AiPage page) {
        if (page.getTenantId() == null) page.setTenantId(DEFAULT_TENANT);
        if (page.getCreatedBy() == null) page.setCreatedBy(DEFAULT_USER);
        if (page.getVersion() == null) page.setVersion(1);
        if (page.getIsPublished() == null) page.setIsPublished(0);
        pageDao.insert(page);
        return page;
    }

    public AiPage update(AiPage page) {
        pageDao.updateById(page);
        return page;
    }

    public void delete(Long id) {
        pageDao.deleteById(id);
    }

    public AiPage createFromGenerate(String name, String code, Integer pageType, Long projectId, Long templateId) {
        AiPage page = AiPage.builder()
                .tenantId(DEFAULT_TENANT)
                .projectId(projectId)
                .templateId(templateId)
                .name(name)
                .code(code)
                .pageType(pageType)
                .version(1)
                .isPublished(0)
                .createdBy(DEFAULT_USER)
                .build();
        return create(page);
    }
}
