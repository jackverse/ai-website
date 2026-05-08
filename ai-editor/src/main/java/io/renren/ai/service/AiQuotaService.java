package io.renren.ai.service;

import io.renren.ai.entity.AiQuota;
import io.renren.ai.dao.AiQuotaDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiQuotaService {

    private static final Long DEFAULT_TENANT = 1L;

    private final AiQuotaDao quotaDao;

    public AiQuota getByTenant(Long tenantId) {
        AiQuota quota = quotaDao.findByTenantId(tenantId);
        if (quota == null) {
            // 首次访问，创建默认配额记录
            quota = AiQuota.builder()
                    .tenantId(tenantId)
                    .dailyLimit(100)
                    .dailyUsed(0)
                    .monthlyLimit(3000)
                    .monthlyUsed(0)
                    .monthlyTokens(0)
                    .tokenLimit(1000000)
                    .lastResetDate(LocalDate.now())
                    .build();
            quotaDao.insert(quota);
        }
        // 检查是否需要重置每日配额
        resetIfNeeded(quota);
        return quota;
    }

    public Map<String, Object> getStats(Long tenantId) {
        AiQuota quota = getByTenant(tenantId);
        Map<String, Object> stats = new HashMap<>();
        stats.put("usedTimes", quota.getMonthlyUsed());
        stats.put("totalTimes", quota.getMonthlyLimit());
        stats.put("usedTokens", quota.getMonthlyTokens());
        stats.put("totalTokens", quota.getTokenLimit());
        stats.put("todayUsage", quota.getDailyUsed());
        stats.put("dailyLimit", quota.getDailyLimit());
        return stats;
    }

    /** 每次生成后调用，更新配额 */
    public void consume(Long tenantId, int tokens) {
        AiQuota quota = getByTenant(tenantId);
        resetIfNeeded(quota);
        quota.setDailyUsed(quota.getDailyUsed() + 1);
        quota.setMonthlyUsed(quota.getMonthlyUsed() + 1);
        quota.setMonthlyTokens(quota.getMonthlyTokens() + tokens);
        quotaDao.updateById(quota);
    }

    /** 更新每日限额设置 */
    public void updateDailyLimit(Long tenantId, int dailyLimit) {
        AiQuota quota = getByTenant(tenantId);
        quota.setDailyLimit(dailyLimit);
        quotaDao.updateById(quota);
    }

    private void resetIfNeeded(AiQuota quota) {
        LocalDate today = LocalDate.now();
        if (quota.getLastResetDate() == null || !quota.getLastResetDate().equals(today)) {
            quota.setDailyUsed(0);
            quota.setLastResetDate(today);
            // 跨月则重置月配额
            if (quota.getLastResetDate() != null &&
                    quota.getLastResetDate().getMonth() != today.getMonth()) {
                quota.setMonthlyUsed(0);
                quota.setMonthlyTokens(0);
            }
            quotaDao.updateById(quota);
        }
    }
}
