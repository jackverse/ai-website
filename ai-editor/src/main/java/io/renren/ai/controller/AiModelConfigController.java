package io.renren.ai.controller;

import io.renren.ai.entity.AiModelConfig;
import io.renren.ai.service.AiModelConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ai/model")
@RequiredArgsConstructor
public class AiModelConfigController {

    private final AiModelConfigService modelConfigService;

    /**
     * 获取支持的 provider 列表及默认 endpoint
     */
    @GetMapping("/providers")
    public Map<String, Object> providers() {
        List<Map<String, String>> providers = List.of(
                Map.of("type", "openai",    "name", "OpenAI",     "endpoint", "https://api.openai.com/v1"),
                Map.of("type", "anthropic", "name", "Claude",     "endpoint", "https://api.anthropic.com"),
                Map.of("type", "minimax",   "name", "MiniMax",    "endpoint", "https://api.minimaxi.com"),
                Map.of("type", "aliyun",    "name", "阿里云Qwen", "endpoint", "")
        );

        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("data", providers);
        return result;
    }

    /**
     * 获取模型列表
     */
    @GetMapping("/list")
    public Map<String, Object> list() {
        // 模拟租户ID，实际从登录信息获取
        Long tenantId = 1L;
        List<AiModelConfig> list = modelConfigService.listByTenantId(tenantId);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("data", list);
        return result;
    }

    /**
     * 获取默认模型
     */
    @GetMapping("/default")
    public Map<String, Object> getDefault() {
        Long tenantId = 1L;
        AiModelConfig model = modelConfigService.getDefault(tenantId);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("data", model);
        return result;
    }

    /**
     * 获取模型详情
     */
    @GetMapping("/{id}")
    public Map<String, Object> getById(@PathVariable Long id) {
        AiModelConfig model = modelConfigService.getById(id);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("data", model);
        return result;
    }

    /**
     * 创建模型
     */
    @PostMapping
    public Map<String, Object> create(@RequestBody AiModelConfig config) {
        config.setTenantId(1L);
        config.setCreatedBy(1L);
        AiModelConfig created = modelConfigService.create(config);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("data", created);
        return result;
    }

    /**
     * 更新模型
     */
    @PutMapping("/{id}")
    public Map<String, Object> update(@PathVariable Long id, @RequestBody AiModelConfig config) {
        config.setId(id);
        config.setTenantId(1L);
        AiModelConfig updated = modelConfigService.update(config);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("data", updated);
        return result;
    }

    /**
     * 删除模型
     */
    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        boolean success = modelConfigService.delete(id);

        Map<String, Object> result = new HashMap<>();
        result.put("code", success ? 0 : 1);
        result.put("msg", success ? "删除成功" : "删除失败");
        return result;
    }

    /**
     * 设置默认模型
     */
    @PostMapping("/{id}/default")
    public Map<String, Object> setDefault(@PathVariable Long id) {
        Long tenantId = 1L;
        modelConfigService.setDefault(tenantId, id);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("msg", "设置成功");
        return result;
    }

    /**
     * 启用/禁用模型
     */
    @PostMapping("/{id}/enabled")
    public Map<String, Object> setEnabled(@PathVariable Long id, @RequestParam boolean enabled) {
        modelConfigService.setEnabled(id, enabled);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("msg", enabled ? "启用成功" : "禁用成功");
        return result;
    }

    /**
     * 测试模型连接
     */
    @PostMapping("/{id}/test")
    public Map<String, Object> test(@PathVariable Long id) {
        AiModelConfig model = modelConfigService.getById(id);
        String result = modelConfigService.chat(model, "Hello, please reply with 'OK' if you receive this message.");

        Map<String, Object> res = new HashMap<>();
        res.put("code", 0);
        res.put("data", result);
        res.put("msg", "连接成功");
        return res;
    }

    /**
     * 测试模型聊天
     */
    @PostMapping("/{id}/chat")
    public Map<String, Object> chat(@PathVariable Long id, @RequestBody Map<String, String> request) {
        AiModelConfig model = modelConfigService.getById(id);
        String question = request.get("message");
        String answer = modelConfigService.chat(model, question);

        Map<String, Object> res = new HashMap<>();
        res.put("code", 0);
        res.put("data", answer);
        return res;
    }
}
