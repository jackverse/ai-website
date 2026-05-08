package io.renren.ai.service;

import io.renren.ai.entity.AiTemplate;
import io.renren.ai.dao.AiTemplateDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiTemplateService {

    private static final Long DEFAULT_TENANT = 1L;
    private static final Long DEFAULT_USER = 1L;

    private final AiTemplateDao templateDao;

    public List<AiTemplate> listByTenant(Long tenantId) {
        return templateDao.listByTenantId(tenantId);
    }

    public List<AiTemplate> listByType(Long tenantId, Integer type) {
        return templateDao.listByType(tenantId, type);
    }

    public List<AiTemplate> listOfficial(Long tenantId) {
        return templateDao.listOfficial(tenantId);
    }

    public AiTemplate getById(Long id) {
        return templateDao.selectById(id);
    }

    public AiTemplate create(AiTemplate template) {
        if (template.getTenantId() == null) template.setTenantId(DEFAULT_TENANT);
        if (template.getCreatedBy() == null) template.setCreatedBy(DEFAULT_USER);
        if (template.getUsageCount() == null) template.setUsageCount(0);
        if (template.getQualityScore() == null) template.setQualityScore(0);
        if (template.getIsOfficial() == null) template.setIsOfficial(0);
        if (template.getIsPublished() == null) template.setIsPublished(0);
        templateDao.insert(template);
        return template;
    }

    public AiTemplate update(AiTemplate template) {
        templateDao.updateById(template);
        return template;
    }

    public void delete(Long id) {
        templateDao.deleteById(id);
    }

    public AiTemplate saveFromGenerate(String name, String code, Long tenantId) {
        AiTemplate template = AiTemplate.builder()
                .tenantId(tenantId != null ? tenantId : DEFAULT_TENANT)
                .name(name)
                .code(code)
                .type(1) // 单页
                .isOfficial(0)
                .isPublished(0)
                .usageCount(0)
                .qualityScore(0)
                .createdBy(DEFAULT_USER)
                .build();
        return create(template);
    }

    public void incrementUsage(Long id) {
        AiTemplate template = getById(id);
        if (template != null) {
            template.setUsageCount(template.getUsageCount() + 1);
            templateDao.updateById(template);
        }
    }
}
