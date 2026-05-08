package io.renren.ai.controller;

import io.renren.ai.entity.AiGenerateLog;
import io.renren.ai.service.AiGenerateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ai/generate")
@RequiredArgsConstructor
public class AiGenerateController {

    private final AiGenerateService generateService;

    private static final Long DEFAULT_TENANT = 1L;

    /**
     * 生成页面
     */
    @PostMapping("/page")
    public Map<String, Object> generatePage(@RequestBody Map<String, Object> params) {
        String prompt = (String) params.get("prompt");
        Integer pageType = params.get("pageType") != null ? Integer.valueOf(params.get("pageType").toString()) : 1;
        String style = (String) params.get("style");
        String platform = (String) params.get("platform");
        String referenceImage = (String) params.get("referenceImage");
        Long modelId = params.get("modelId") != null ? Long.valueOf(params.get("modelId").toString()) : null;

        Map<String, Object> result = generateService.generatePage(
                prompt, pageType, style, platform, referenceImage, modelId);

        Map<String, Object> response = new HashMap<>();
        if (Boolean.TRUE.equals(result.get("success"))) {
            response.put("code", 0);
            response.put("msg", "生成成功");
            response.put("data", result);
        } else {
            response.put("code", 500);
            response.put("msg", result.get("error"));
            response.put("data", null);
        }
        return response;
    }

    /**
     * 重新生成
     */
    @PostMapping("/regenerate")
    public Map<String, Object> regenerate(@RequestBody Map<String, Object> params) {
        Long logId = Long.valueOf(params.get("logId").toString());
        String additionalPrompt = (String) params.get("additionalPrompt");

        Map<String, Object> result = generateService.regenerate(logId, additionalPrompt);

        Map<String, Object> response = new HashMap<>();
        if (Boolean.TRUE.equals(result.get("success"))) {
            response.put("code", 0);
            response.put("msg", "重新生成成功");
            response.put("data", result);
        } else {
            response.put("code", 500);
            response.put("msg", result.get("error"));
        }
        return response;
    }

    /**
     * 获取历史记录
     */
    @GetMapping("/history")
    public Map<String, Object> history(@RequestParam(required = false) Integer limit) {
        List<AiGenerateLog> list = generateService.getRecentHistory(DEFAULT_TENANT, limit);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("data", list);
        return result;
    }
}
