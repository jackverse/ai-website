package io.renren.ai.controller;

import io.renren.ai.service.AiQuotaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/ai/quota")
@RequiredArgsConstructor
public class AiQuotaController {

    private final AiQuotaService quotaService;

    @GetMapping("/stats")
    public Map<String, Object> stats() {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("data", quotaService.getStats(1L));
        return result;
    }

    @PostMapping("/daily-limit")
    public Map<String, Object> updateDailyLimit(@RequestBody Map<String, Object> body) {
        int dailyLimit = ((Number) body.get("dailyLimit")).intValue();
        quotaService.updateDailyLimit(1L, dailyLimit);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("msg", "更新成功");
        return result;
    }
}
