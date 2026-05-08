package io.renren.ai.service;

import io.renren.ai.entity.AiModelConfig;
import io.renren.ai.dao.AiModelConfigDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.ProxySelector;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiModelConfigService {

    /**
     * Provider type -> base endpoint 映射。
     * 用户选择 type 后自动使用对应 endpoint，前端无需填写。
     * key: type, value: API base URL
     */
    private static final Map<String, String> PROVIDER_ENDPOINTS = Map.of(
            "openai",    "https://api.openai.com/v1",
            "anthropic", "https://api.anthropic.com",
            "minimax",   "https://api.minimaxi.com"
    );

    /** 各 provider 的完整 endpoint 路径（跟在 base URL 后面） */
    private static final Map<String, String> PROVIDER_PATHS = Map.of(
            "openai",    "/chat/completions",
            "anthropic", "/messages",
            "minimax",   "/anthropic/v1/messages",
            "aliyun",    "/services/aigc/text-generation/generation"
    );

    private final AiModelConfigDao modelConfigDao;

    /** 默认超时10秒 */
    private static final int DEFAULT_TIMEOUT_SECONDS = 10;

    private HttpClient getHttpClient(AiModelConfig config) {
        int timeoutSec = config.getTimeout() != null && config.getTimeout() > 0
                ? config.getTimeout() : DEFAULT_TIMEOUT_SECONDS;
        return HttpClient.newBuilder()
                .proxy(ProxySelector.of(null))
                .connectTimeout(Duration.ofSeconds(timeoutSec))
                .build();
    }

    public List<AiModelConfig> listByTenantId(Long tenantId) {
        return modelConfigDao.listByTenantId(tenantId);
    }

    public AiModelConfig getById(Long id) {
        return modelConfigDao.getById(id);
    }

    public AiModelConfig getDefault(Long tenantId) {
        List<AiModelConfig> list = listByTenantId(tenantId);
        return list.stream()
                .filter(m -> "1".equals(String.valueOf(m.getIsDefault())))
                .findFirst()
                .orElse(list.stream().findFirst().orElse(null));
    }

    public AiModelConfig create(AiModelConfig config) {
        if ("1".equals(String.valueOf(config.getIsDefault()))) {
            modelConfigDao.clearDefault(config.getTenantId());
        }
        modelConfigDao.insert(config);
        return config;
    }

    public AiModelConfig update(AiModelConfig config) {
        if ("1".equals(String.valueOf(config.getIsDefault()))) {
            modelConfigDao.clearDefault(config.getTenantId());
        }
        modelConfigDao.updateById(config);
        return modelConfigDao.getById(config.getId());
    }

    public boolean delete(Long id) {
        return modelConfigDao.deleteById(id) > 0;
    }

    public void setDefault(Long tenantId, Long modelId) {
        modelConfigDao.clearDefault(tenantId);
        modelConfigDao.setDefault(modelId);
    }

    public void setEnabled(Long modelId, boolean enabled) {
        modelConfigDao.setEnabled(modelId, enabled ? 1 : 0);
    }

    /**
     * 根据 type 返回标准的 base URL，供前端使用。
     * 前端可根据此值决定是否隐藏 endpoint 输入框。
     */
    public String getEndpointByType(String type) {
        return PROVIDER_ENDPOINTS.getOrDefault(type, "");
    }

    /**
     * 聊天
     */
    public String chat(AiModelConfig config, String question) {
        try {
            log.info("聊天 - 模型: {}, 类型: {}, 问题: {}", config.getName(), config.getType(), question);

            if (question == null || question.trim().isEmpty()) {
                return "请输入问题";
            }

            // 根据不同类型调用
            return switch (config.getType()) {
                case "openai" -> chatOpenAI(config, question);
                case "minimax" -> chatMiniMax(config, question);
                case "anthropic" -> chatAnthropic(config, question);
                case "aliyun" -> chatQwen(config, question);
                default -> "暂不支持此模型类型: " + config.getType();
            };
        } catch (Exception e) {
            log.error("聊天失败: {}", e.getMessage());
            return "Error: " + e.getMessage();
        }
    }

    /**
     * 根据 type 获取完整的 API URL（自动拼接 base + path）
     */
    private String buildUrl(AiModelConfig config) {
        String base = PROVIDER_ENDPOINTS.get(config.getType());
        String path = PROVIDER_PATHS.get(config.getType());
        // 如果数据库里存了自定义 endpoint，优先用数据库的（aliyun 等可能不同）
        if (base != null && path != null) {
            return base + path;
        }
        //兜底：用户填的
        return config.getEndpoint();
    }

    private String chatOpenAI(AiModelConfig config, String question) throws Exception {
        String url = buildUrl(config);

        Map<String, Object> body = new HashMap<>();
        body.put("model", config.getModel());
        body.put("messages", new Object[]{
                Map.of("role", "user", "content", question)
        });

        String jsonBody = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(body);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(java.net.URI.create(url))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + config.getApiKey())
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .timeout(Duration.ofSeconds(config.getTimeout() != null && config.getTimeout() > 0 ? config.getTimeout() : DEFAULT_TIMEOUT_SECONDS))
                .build();

        HttpResponse<String> response = getHttpClient(config).send(request, HttpResponse.BodyHandlers.ofString());

        log.info("OpenAI响应: {}", response.body());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode node = mapper.readTree(response.body());
            return node.path("choices").get(0).path("message").path("content").asText();
        } else {
            return "API Error: " + response.body();
        }
    }

    private String chatAnthropic(AiModelConfig config, String question) throws Exception {
        String url = buildUrl(config);

        Map<String, Object> body = new HashMap<>();
        body.put("model", config.getModel());
        body.put("messages", new Object[]{
                Map.of("role", "user", "content", question)
        });
        body.put("max_tokens", 1024);

        String jsonBody = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(body);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(java.net.URI.create(url))
                .header("Content-Type", "application/json")
                .header("x-api-key", config.getApiKey())
                .header("anthropic-version", "2023-06-01")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .timeout(Duration.ofSeconds(config.getTimeout() != null && config.getTimeout() > 0 ? config.getTimeout() : DEFAULT_TIMEOUT_SECONDS))
                .build();

        HttpResponse<String> response = getHttpClient(config).send(request, HttpResponse.BodyHandlers.ofString());

        log.info("Anthropic响应: {}", response.body());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return extractTextFromAnthropicResponse(response.body());
        } else {
            return "API Error: " + response.body();
        }
    }

    private String chatMiniMax(AiModelConfig config, String question) throws Exception {
        String url = buildUrl(config);

        Map<String, Object> body = new HashMap<>();
        body.put("model", config.getModel());
        body.put("messages", new Object[]{
                Map.of("role", "user", "content", question)
        });
        body.put("max_tokens", 1024);

        String jsonBody = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(body);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(java.net.URI.create(url))
                .header("Content-Type", "application/json")
                .header("x-api-key", config.getApiKey())
                .header("anthropic-version", "2023-06-01")
                 .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .timeout(Duration.ofSeconds(config.getTimeout() != null && config.getTimeout() > 0 ? config.getTimeout() : DEFAULT_TIMEOUT_SECONDS))
                .build();

        HttpResponse<String> response = getHttpClient(config).send(request, HttpResponse.BodyHandlers.ofString());

        log.info("MiniMax响应: {}", response.body());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return extractTextFromAnthropicResponse(response.body());
        } else {
            return "API Error: " + response.body();
        }
    }

    private String chatQwen(AiModelConfig config, String question) throws Exception {
        String url = buildUrl(config);

        Map<String, Object> body = new HashMap<>();
        body.put("model", config.getModel());
        body.put("input", question);

        String jsonBody = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(body);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(java.net.URI.create(url))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + config.getApiKey())
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .timeout(Duration.ofSeconds(config.getTimeout() != null && config.getTimeout() > 0 ? config.getTimeout() : DEFAULT_TIMEOUT_SECONDS))
                .build();

        HttpResponse<String> response = getHttpClient(config).send(request, HttpResponse.BodyHandlers.ofString());

        log.info("Qwen响应: {}", response.body());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode node = mapper.readTree(response.body());
            return node.path("output").path("text").asText();
        } else {
            return "API Error: " + response.body();
        }
    }

    /**
     * 从 Anthropic/MiniMax 响应体中提取文本内容。
     * content 数组可能包含 thinking 块(type=thinking)和文本块(type=text)，
     * 需找到第一个 type=text 的项。
     */
    private String extractTextFromAnthropicResponse(String responseBody) throws Exception {
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        com.fasterxml.jackson.databind.JsonNode node = mapper.readTree(responseBody);
        for (com.fasterxml.jackson.databind.JsonNode item : node.path("content")) {
            if ("text".equals(item.path("type").asText())) {
                return item.path("text").asText();
            }
        }
        return "No text in response";
    }
}
