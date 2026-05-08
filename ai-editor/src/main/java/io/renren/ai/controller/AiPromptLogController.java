package io.renren.ai.controller;

import io.renren.ai.entity.AiPromptLog;
import io.renren.ai.service.AiPromptLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/ai/prompt")
@RequiredArgsConstructor
public class AiPromptLogController {

    private final AiPromptLogService promptLogService;

    @GetMapping("/logs")
    public Map<String, Object> logs() {
        List<AiPromptLog> logs = promptLogService.listByTenant(1L);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("data", logs);
        return result;
    }

    @GetMapping("/stats")
    public Map<String, Object> stats() {
        List<AiPromptLog> logs = promptLogService.listByTenant(1L);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);

        // 按模型统计使用次数
        Map<String, Long> modelUsage = logs.stream()
                .collect(Collectors.groupingBy(AiPromptLog::getModel, Collectors.counting()));

        // 成功率
        long successCount = logs.stream().filter(l -> l.getStatus() == 1).count();
        double successRate = logs.isEmpty() ? 0 : (successCount * 100.0 / logs.size());

        // 平均耗时
        double avgDuration = logs.stream()
                .filter(l -> l.getDuration() != null)
                .mapToInt(AiPromptLog::getDuration)
                .average().orElse(0);

        // 平均token
        double avgTokens = logs.stream()
                .filter(l -> l.getTotalTokens() != null)
                .mapToInt(AiPromptLog::getTotalTokens)
                .average().orElse(0);

        Map<String, Object> data = new HashMap<>();
        data.put("modelUsage", modelUsage);
        data.put("successRate", String.format("%.0f%%", successRate));
        data.put("avgDuration", String.format("%.1fs", avgDuration / 1000));
        data.put("avgTokens", String.format("%.0f", avgTokens));
        data.put("totalLogs", logs.size());
        result.put("data", data);
        return result;
    }
}
