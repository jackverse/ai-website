package io.renren.ai.service;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import io.renren.ai.entity.AiGenerateLog;
import io.renren.ai.entity.AiModelConfig;
import io.renren.ai.dao.AiGenerateLogDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiGenerateService {

    private static final Long DEFAULT_TENANT = 1L;
    private static final Long DEFAULT_USER = 1L;

    private final AiGenerateLogDao generateLogDao;
    private final AiModelConfigService modelConfigService;

    /**
     * 生成页面
     */
    public Map<String, Object> generatePage(String prompt, Integer pageType, String style,
                                            String platform, String referenceImage, Long modelId) {
        Map<String, Object> result = new HashMap<>();
        long startTime = System.currentTimeMillis();

        try {
            // 1. 获取模型配置
            AiModelConfig modelConfig;
            if (modelId != null) {
                modelConfig = modelConfigService.getById(modelId);
            } else {
                modelConfig = modelConfigService.getDefault(DEFAULT_TENANT);
            }

            if (modelConfig == null) {
                throw new RuntimeException("未配置 AI 模型，请先在模型配置中添加模型");
            }

            // 2. 构造 Prompt
            String fullPrompt = buildPrompt(prompt, pageType, style, platform);

            // 3. 调用 AI
            String generatedCode = callAi(modelConfig, fullPrompt);

            // 4. 记录日志
            long duration = System.currentTimeMillis() - startTime;
            saveGenerateLog(prompt, pageType, style, platform, referenceImage,
                    modelConfig.getName(), generatedCode, 1, null, (int) duration, null);

            result.put("success", true);
            result.put("code", generatedCode);
            result.put("model", modelConfig.getName());

        } catch (Exception e) {
            log.error("生成失败", e);
            long duration = System.currentTimeMillis() - startTime;
            saveGenerateLog(prompt, pageType, style, platform, referenceImage,
                    null, null, 0, e.getMessage(), (int) duration, null);

            result.put("success", false);
            result.put("error", e.getMessage());
        }

        return result;
    }

    /**
     * 重新生成
     */
    public Map<String, Object> regenerate(Long logId, String additionalPrompt) {
        AiGenerateLog originalLog = generateLogDao.selectById(logId);
        if (originalLog == null) {
            throw new RuntimeException("原始生成记录不存在");
        }

        String newPrompt = originalLog.getPrompt();
        if (StringUtils.hasText(additionalPrompt)) {
            newPrompt = originalLog.getPrompt() + "\n" + additionalPrompt;
        }

        return generatePage(newPrompt, originalLog.getPageType(), originalLog.getStyle(),
                originalLog.getPlatform(), originalLog.getReferenceImage(), null);
    }

    /**
     * 构造 Prompt
     */
    private String buildPrompt(String prompt, Integer pageType, String style, String platform) {
        StringBuilder sb = new StringBuilder();
        sb.append("你是一个前端开发专家，请使用 Vue3 + Tailwind CSS 生成页面。\n\n");

        // 页面类型
        String pageTypeName = switch (pageType != null ? pageType : 1) {
            case 1 -> "首页";
            case 2 -> "列表页";
            case 3 -> "详情页";
            case 4 -> "表单页";
            case 5 -> "单页";
            default -> "页面";
        };
        sb.append("页面类型：").append(pageTypeName).append("\n");

        // 风格
        if (StringUtils.hasText(style)) {
            String styleName = switch (style) {
                case "tech" -> "科技风";
                case "simple" -> "简约风";
                case "business" -> "商务风";
                case "modern" -> "现代简洁";
                default -> style;
            };
            sb.append("设计风格：").append(styleName).append("\n");
        }

        // 目标端
        if (StringUtils.hasText(platform)) {
            String platformName = switch (platform) {
                case "pc" -> "PC端";
                case "mobile" -> "移动端";
                case "both" -> "PC和移动端适配";
                default -> platform;
            };
            sb.append("目标端：").append(platformName).append("\n");
        }

        sb.append("\n需求描述：\n").append(prompt);

        sb.append("\n\n要求：\n");
        sb.append("1. 只返回 Vue 模板代码，不需要 script 和 style\n");
        sb.append("2. 使用 Tailwind CSS 类名\n");
        sb.append("3. 代码要完整、可直接使用\n");
        sb.append("4. 响应式设计，适配不同屏幕尺寸\n");
        sb.append("5. 只输出代码，不要有其他解释文字\n");

        return sb.toString();
    }

    /**
     * 调用 AI
     */
    private String callAi(AiModelConfig config, String prompt) {
        // 根据模型类型调用不同的 API
        String type = config.getType();
        if ("openai".equals(type)) {
            return callOpenAI(config, prompt);
        } else if ("anthropic".equals(type)) {
            return callAnthropic(config, prompt);
        } else if ("aliyun".equals(type)) {
            return callAliyun(config, prompt);
        } else if ("minimax".equals(type)) {
            return callMiniMax(config, prompt);
        }

        throw new RuntimeException("不支持的模型类型: " + type);
    }

    /**
     * 调用 OpenAI API
     */
    private String callOpenAI(AiModelConfig config, String prompt) {
        String url = config.getEndpoint();
        if (StringUtils.isEmpty(url)) {
            url = "https://api.openai.com/v1";
        }
        if (!url.endsWith("/v1/chat/completions")) {
            url = url + "/v1/chat/completions";
        }

        Map<String, Object> body = new HashMap<>();
        body.put("model", config.getModel());
        body.put("messages", new Object[]{
                Map.of("role", "user", "content", prompt)
        });
        body.put("temperature", 0.7);

        log.info("调用 OpenAI API: {}", url);

        String response = HttpRequest.post(url)
                .header("Authorization", "Bearer " + config.getApiKey())
                .header("Content-Type", "application/json")
                .body(JSONUtil.toJsonStr(body))
                .timeout(120000)
                .execute()
                .body();

        log.info("OpenAI 响应: {}", response);

        // 检查错误响应
        JSONObject json = JSONUtil.parseObj(response);
        if (json.containsKey("error")) {
            JSONObject error = json.getJSONObject("error");
            throw new RuntimeException("OpenAI API 错误: " + error.getStr("message", "未知错误"));
        }

        JSONArray choices = json.getJSONArray("choices");
        if (choices == null || choices.isEmpty()) {
            throw new RuntimeException("OpenAI API 返回为空");
        }

        JSONObject choice = choices.getJSONObject(0);
        JSONObject message = choice.getJSONObject("message");
        if (message == null) {
            throw new RuntimeException("OpenAI API 响应格式错误");
        }

        return message.getStr("content");
    }

    /**
     * 调用 Anthropic API
     */
    private String callAnthropic(AiModelConfig config, String prompt) {
        String url = config.getEndpoint();
        if (StringUtils.isEmpty(url)) {
            url = "https://api.anthropic.com";
        }
        if (!url.endsWith("/v1/messages")) {
            url = url + "/v1/messages";
        }

        Map<String, Object> body = new HashMap<>();
        body.put("model", config.getModel());
        body.put("messages", new Object[]{
                Map.of("role", "user", "content", prompt)
        });
        body.put("max_tokens", 4096);

        log.info("调用 Anthropic API: {}", url);

        String response = HttpRequest.post(url)
                .header("x-api-key", config.getApiKey())
                .header("Content-Type", "application/json")
                .header("anthropic-version", "2023-06-01")
                .body(JSONUtil.toJsonStr(body))
                .timeout(120000)
                .execute()
                .body();

        log.info("Anthropic 响应: {}", response);

        // 检查错误响应
        JSONObject json = JSONUtil.parseObj(response);
        if (json.containsKey("error")) {
            JSONObject error = json.getJSONObject("error");
            throw new RuntimeException("Anthropic API 错误: " + error.getStr("type", "未知错误"));
        }

        // Anthropic 返回格式: {"content": [{"type": "text", "text": "..."}]}
        JSONArray content = json.getJSONArray("content");
        if (content == null || content.isEmpty()) {
            throw new RuntimeException("Anthropic API 返回为空");
        }

        JSONObject contentBlock = content.getJSONObject(0);
        return contentBlock.getStr("text");
    }

    /**
     * 调用阿里云通义千问 API
     */
    private String callAliyun(AiModelConfig config, String prompt) {
        String url = config.getEndpoint();
        if (StringUtils.isEmpty(url)) {
            url = "https://dashscope.aliyuncs.com";
        }
        if (!url.contains("/compatible-mode/v1/chat/completions")) {
            url = url + "/compatible-mode/v1/chat/completions";
        }

        Map<String, Object> body = new HashMap<>();
        body.put("model", config.getModel());
        body.put("input", prompt);

        log.info("调用通义千问 API: {}", url);

        String response = HttpRequest.post(url)
                .header("Authorization", "Bearer " + config.getApiKey())
                .header("Content-Type", "application/json")
                .body(JSONUtil.toJsonStr(body))
                .timeout(120000)
                .execute()
                .body();

        log.info("通义千问 响应: {}", response);

        // 检查错误响应
        JSONObject json = JSONUtil.parseObj(response);
        if (json.containsKey("error")) {
            JSONObject error = json.getJSONObject("error");
            throw new RuntimeException("通义千问 API 错误: " + error.getStr("message", "未知错误"));
        }

        JSONObject output = json.getJSONObject("output");
        if (output == null) {
            throw new RuntimeException("通义千问 API 返回格式错误");
        }

        return output.getStr("text");
    }

    /**
     * 调用 MiniMax API
     */
    private String callMiniMax(AiModelConfig config, String prompt) {
        String url = config.getEndpoint();
        if (StringUtils.isEmpty(url)) {
            url = "https://api.minimaxi.com";
        }
        if (!url.endsWith("/v1/chat_completion_v2")) {
            url = url + "/v1/chat_completion_v2";
        }

        Map<String, Object> body = new HashMap<>();
        body.put("model", config.getModel());
        body.put("messages", new Object[]{
                Map.of("role", "user", "content", prompt)
        });

        log.info("调用 MiniMax API: {}", url);

        String response = HttpRequest.post(url)
                .header("Authorization", "Bearer " + config.getApiKey())
                .header("Content-Type", "application/json")
                .body(JSONUtil.toJsonStr(body))
                .timeout(120000)
                .execute()
                .body();

        log.info("MiniMax 响应: {}", response);

        // 检查错误响应
        JSONObject json = JSONUtil.parseObj(response);
        if (json.containsKey("error")) {
            JSONObject error = json.getJSONObject("error");
            throw new RuntimeException("MiniMax API 错误: " + error.getStr("message", "未知错误"));
        }

        JSONArray choices = json.getJSONArray("choices");
        if (choices == null || choices.isEmpty()) {
            throw new RuntimeException("MiniMax API 返回为空");
        }

        JSONObject choice = choices.getJSONObject(0);
        JSONObject message = choice.getJSONObject("message");
        if (message == null) {
            throw new RuntimeException("MiniMax API 响应格式错误");
        }

        return message.getStr("content");
    }

    /**
     * 保存生成日志
     */
    private void saveGenerateLog(String prompt, Integer pageType, String style, String platform,
                                 String referenceImage, String model, String generatedCode,
                                 Integer status, String errorMsg, Integer duration, Integer qualityScore) {
        AiGenerateLog log = AiGenerateLog.builder()
                .tenantId(DEFAULT_TENANT)
                .userId(DEFAULT_USER)
                .prompt(prompt)
                .pageType(pageType)
                .style(style)
                .platform(platform)
                .referenceImage(referenceImage)
                .model(model)
                .generatedCode(generatedCode)
                .status(status)
                .errorMsg(errorMsg)
                .duration(duration)
                .qualityScore(qualityScore)
                .build();
        generateLogDao.insert(log);
    }

    /**
     * 获取历史生成记录
     */
    public java.util.List<AiGenerateLog> getRecentHistory(Long tenantId, Integer limit) {
        return generateLogDao.recentList(tenantId != null ? tenantId : DEFAULT_TENANT,
                limit != null ? limit : 10);
    }
}
