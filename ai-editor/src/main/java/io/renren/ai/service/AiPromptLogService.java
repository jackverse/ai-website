package io.renren.ai.service;

import io.renren.ai.entity.AiPromptLog;
import io.renren.ai.dao.AiPromptLogDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiPromptLogService {

    private static final Long DEFAULT_TENANT = 1L;
    private static final Long DEFAULT_USER = 1L;

    private final AiPromptLogDao promptLogDao;

    public List<AiPromptLog> listByTenant(Long tenantId) {
        return promptLogDao.listByTenantId(tenantId);
    }

    public AiPromptLog createLog(AiPromptLog log) {
        if (log.getTenantId() == null) log.setTenantId(DEFAULT_TENANT);
        if (log.getUserId() == null) log.setUserId(DEFAULT_USER);
        promptLogDao.insert(log);
        return log;
    }
}
