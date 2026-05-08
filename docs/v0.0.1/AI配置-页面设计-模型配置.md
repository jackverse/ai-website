# AI 配置 - 页面设计 - 模型配置

> 用于讨论 AI 模型配置的详细需求

---

## 一、设计目标

### 核心原则：极简配置

1. **用户只需输入 API Key**，其他信息由系统预设
2. **系统维护模型配置库**：
   - endpoint（API 地址）
   - 支持的模型列表
   - 默认参数（temperature、max_tokens 等）
   - 计费信息（单价、所属厂商）
3. **配置后可即时测试**：通过 Chat 对话验证连接是否正常
4. **后期可扩展**：兼容不全面没关系，逐步积累模型配置

---

## 二、功能模块

### 2.1 模型配置列表

| 字段 | 说明 | 维护方 |
|------|------|--------|
| 厂商名称 | 如：MiniMax、OpenAI、Anthropic | 系统预设 |
| Logo | 厂商标识 | 系统预设 |
| API Endpoint | 如：`https://api.minimax.com` | 系统预设 |
| API Key | 用户输入 | 用户 |
| 支持的模型 | 如：`MiniMax-M2.7-highspeed` | 系统预设 |
| 默认参数 | temperature、max_tokens 等 | 系统预设 |
| 状态 | 启用 / 禁用 | 用户 |
| 测试状态 | 已测试 / 未测试 / 测试失败 | 系统记录 |

### 2.2 添加/编辑模型

**用户操作流程**：
1. 选择厂商（从系统预设列表选择，如 MiniMax）
2. 填写 API Key
3. 保存

**系统自动填充**：
- Endpoint
- 支持的模型列表
- 默认参数

### 2.3 测试功能

**测试方式**：Chat 对话
- 用户在配置页面直接输入测试内容
- 与 AI 模型对话，验证 API Key 和连接是否正常
- 简单验证即可，不需要复杂功能

---

## 三、首批集成

### 优先级：MiniMax

其他模型（OpenAI、Claude、通义等）后续按需扩展。

---

## 四、MiniMax 集成详情

### 4.1 已确认信息

| 字段 | 值 | 来源 |
|------|-----|------|
| 官网 | https://www.minimaxi.com | 确认 |
| API Base URL | `https://api.minimaxi.com` | 确认 |
| Chat 端点 | `https://api.minimaxi.com/v1/chat/completions` | 测试确认 |
| 认证方式 | `Authorization: Bearer <API_KEY>` | 测试确认 |
| API 格式 | 兼容 OpenAI ChatGPT 格式 | 测试确认 |

### 4.2 支持的模型

| 模型 ID | 说明 |
|---------|------|
| `MiniMax-M2.7` | 最新旗舰版 |
| `MiniMax-M2.7-highspeed` | 最新旗舰版（高速） |
| `MiniMax-M2.5` | 上代旗舰 |
| `MiniMax-M2.5-highspeed` | 上代旗舰（高速） |
| `MiniMax-M2.1` | 更早版本 |
| `MiniMax-M2.1-highspeed` | 更早版本（高速） |
| `MiniMax-M2` | 基础版 |

### 4.3 请求示例

```json
POST https://api.minimaxi.com/v1/chat/completions
Authorization: Bearer <API_KEY>
Content-Type: application/json

{
  "model": "MiniMax-M2.7-highspeed",
  "messages": [{"role": "user", "content": "你好"}],
  "stream": true
}
```

### 4.4 响应示例

```json
{
  "id": "gen-xxx",
  "object": "chat.completion",
  "created": 1234567890,
  "model": "MiniMax-M2.7-highspeed",
  "choices": [{
    "index": 0,
    "message": {
      "role": "assistant",
      "content": "你好！有什么可以帮助你的吗？"
    }
  }],
  "usage": {
    "prompt_tokens": 10,
    "completion_tokens": 20,
    "total_tokens": 30
  }
}
```

---

## 五、后续计划

### 5.1 本次上线（MiniMax）

- 单账号支持（一个 API Key）
- 系统预设模型列表（已确认）
- Chat 流式对话测试

### 5.2 后续扩展（OpenAI / GPT）

- 兼容 OpenAI API 格式
- 预留多厂商扩展性
- 厂商选择器架构设计

---

## 六、讨论点

- [x] MiniMax API Endpoint 已确认
- [x] 测试对话需要流式输出
- [x] 模型选择：用户选厂商 → 系统列出模型 → 用户选默认模型
- [x] 先支持一个 API Key
- [x] 后续扩展 OpenAI（GPT）
- [x] MiniMax 支持的具体模型 ID 列表（已查询）
- [ ] 系统预设的默认参数（temperature、max_tokens 等）需要确认
- [ ] 计费信息是否需要记录
