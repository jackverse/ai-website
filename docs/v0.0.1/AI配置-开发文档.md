# AI 配置 - 开发文档（ai-editor）

## 一、技术架构

### 1.1 整体架构

```
┌─────────────────────────────────────────────────────────────────────┐
│                         ai-editor-web 前端                          │
│                    (Vue3 + Vite + Headless UI + Tailwind)          │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌───────────┐ │
│  │  项目管理   │  │   模板库    │  │  AI 生成    │  │ 组件管理   │ │
│  └─────────────┘  └─────────────┘  └─────────────┘  └───────────┘ │
│                                                                     │
└──────────────────────────────┬──────────────────────────────────────┘
                               │ HTTP API
                               ▼
┌──────────────────────────────────────────────────────────────────────┐
│                         ai-editor 后端                               │
│                   (Spring Boot + renren-security)                   │
├──────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌───────────┐ │
│  │ 项目服务    │  │ 模板服务    │  │ AI 生成服务 │  │ 组件服务   │ │
│  └─────────────┘  └─────────────┘  └─────────────┘  └───────────┘ │
│                                                                     │
└──────────────────────────────┬──────────────────────────────────────┘
                               │
                               ▼
┌──────────────────────────────────────────────────────────────────────┐
│                    renren-security 中台                              │
│              (用户、角色、权限、租户 - 复用)                          │
└──────────────────────────────────────────────────────────────────────┘
```

### 1.2 技术栈

| 层级 | 技术 | 说明 |
|------|------|------|
| 前端框架 | Vue 3 | Composition API |
| 构建工具 | Vite | 快速开发启动 |
| UI 组件 | Headless UI | 无样式组件库 |
| 样式 | Tailwind CSS | 原子化 CSS |
| 状态管理 | Pinia | Vue 3 推荐 |
| 后端框架 | Spring Boot 3.x | 复用 renren-admin |
| 安全 | Spring Security + JWT | 复用 renren-security |
| ORM | MyBatis Plus | 快速开发 |
| 数据库 | MySQL 8.x | 主数据存储 |

### 1.3 模块结构

#### 前端（ai-editor-web）

```
ai-editor-web/
├── src/
│   ├── api/                      # API 接口
│   │   ├── project.ts           # 项目接口
│   │   ├── template.ts          # 模板接口
│   │   ├── page.ts              # 页面接口
│   │   ├── generate.ts          # AI 生成接口
│   │   ├── component.ts         # 组件接口
│   │   └── quota.ts             # 配额接口
│   │
│   ├── assets/                   # 静态资源
│   │
│   ├── components/               # 通用组件
│   │   ├── common/              # 通用组件（按钮、弹窗等）
│   │   ├── editor/              # 编辑器核心组件
│   │   │   ├── PageCanvas.vue   # 页面画布
│   │   │   ├── ComponentPanel.vue # 组件面板
│   │   │   ├── PropertyPanel.vue  # 属性面板
│   │   │   └── LayerPanel.vue    # 图层面板
│   │   └── preview/             # 预览组件
│   │       ├── PreviewModal.vue # 预览弹窗
│   │       ├── PcPreview.vue    # PC 端预览
│   │       └── MobilePreview.vue # 移动端预览
│   │
│   ├── composables/              # 组合式函数
│   │   ├── useAI.ts             # AI 生成逻辑
│   │   ├── usePreview.ts        # 预览逻辑
│   │   └── useDrag.ts           # 拖拽逻辑
│   │
│   ├── layouts/                  # 布局
│   │   └── AdminLayout.vue      # 管理后台布局
│   │
│   ├── router/                   # 路由
│   │   └── index.ts
│   │
│   ├── stores/                   # Pinia 状态管理
│   │   ├── project.ts           # 项目状态
│   │   ├── template.ts          # 模板状态
│   │   ├── editor.ts            # 编辑器状态
│   │   └── user.ts              # 用户状态
│   │
│   ├── styles/                   # 全局样式
│   │   └── main.css
│   │
│   ├── utils/                    # 工具函数
│   │   ├── api.ts               # 请求封装
│   │   └── tools.ts             # 工具函数
│   │
│   └── views/                    # 页面视图
│       ├── dashboard/           # 首页
│       ├── project/             # 项目管理
│       │   ├── List.vue         # 项目列表
│       │   ├── Detail.vue       # 项目详情
│       │   └── Version.vue      # 版本管理
│       ├── template/            # 模板库
│       │   ├── List.vue         # 模板列表
│       │   ├── Detail.vue       # 模板详情
│       │   └── Create.vue       # 创建模板
│       ├── page/                # 页面管理
│       │   ├── List.vue         # 页面列表
│       │   └── Editor.vue       # 页面编辑器
│       ├── generate/            # AI 生成
│       │   └── Index.vue        # AI 生成页面
│       ├── component/           # 组件管理
│       │   └── List.vue         # 组件列表
│       └── quota/               # 成本控制
│           └── Index.vue        # 配额管理
│
├── index.html
├── vite.config.ts
├── tailwind.config.js
├── postcss.config.js
└── package.json
```

#### 后端（ai-editor）

```
ai-editor/
├── src/main/java/io/renren/ai/
│   ├── config/                   # 配置类
│   │   ├── AIConfig.java        # AI 配置
│   │   └── CorsConfig.java      # 跨域配置
│   │
│   ├── constant/                 # 常量
│   │   └── AiConstant.java
│   │
│   ├── controller/               # 控制器
│   │   ├── ProjectController.java
│   │   ├── TemplateController.java
│   │   ├── PageController.java
│   │   ├── GenerateController.java
│   │   ├── ComponentController.java
│   │   └── QuotaController.java
│   │
│   ├── service/                  # 业务服务
│   │   ├── ProjectService.java
│   │   ├── TemplateService.java
│   │   ├── PageService.java
│   │   ├── GenerateService.java
│   │   ├── ComponentService.java
│   │   └── QuotaService.java
│   │
│   ├── entity/                   # 实体类
│   │   ├── AiProject.java
│   │   ├── AiProjectVersion.java
│   │   ├── AiTemplate.java
│   │   ├── AiPage.java
│   │   ├── AiComponent.java
│   │   └── AiPromptLog.java
│   │
│   ├── dao/                      # 数据访问层
│   │   ├── ProjectDao.java
│   │   ├── TemplateDao.java
│   │   └── ...
│   │
│   ├── model/                    # 模型定义
│   │   ├── AIModel.java         # AI 模型接口
│   │   ├── OpenAIModel.java     # OpenAI 实现
│   │   ├── ClaudeModel.java     # Claude 实现
│   │   └── AIFactory.java       # 模型工厂
│   │
│   └── common/                   # 公共类
│       ├── Result.java          # 统一返回
│       └── exception/           # 异常处理
│
├── src/main/resources/
│   ├── mapper/                   # MyBatis XML
│   │   ├── ProjectDao.xml
│   │   ├── TemplateDao.xml
│   │   └── ...
│   │
│   ├── application.yml           # 配置
│   └── logback-spring.xml        # 日志配置
│
└── pom.xml
```

### 1.4 依赖关系

```xml
<!-- ai-editor/pom.xml -->
<dependencies>
    <!-- 继承 renren-common -->
    <dependency>
        <groupId>io.renren</groupId>
        <artifactId>renren-common</artifactId>
        <version>${renren.version}</version>
    </dependency>
    
    <!-- 引用 renren-system (用户、部门、角色、权限) -->
    <dependency>
        <groupId>io.renren</groupId>
        <artifactId>renren-system</artifactId>
        <version>${renren.version}</version>
    </dependency>
    
    <!-- Spring Boot Web -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <!-- MyBatis Plus -->
    <dependency>
        <groupId>com.baomidou</groupId>
        <artifactId>mybatis-plus-boot-starter</artifactId>
    </dependency>
    
    <!-- AI SDK -->
    <dependency>
        <groupId>com.theokanning.openai-gpt</groupId>
        <artifactId>openai-java</artifactId>
        <version>0.18.3</version>
    </dependency>
</dependencies>
```

---

## 二、接口设计

### 2.1 接口规范

- 基础路径：`/api/ai`
- 请求方式：RESTful
- 统一返回格式：
```json
{
  "code": 200,
  "msg": "success",
  "data": {}
}
```

### 2.2 项目管理

| 接口 | 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|------|
| 项目列表 | GET | `/project/list` | 分页查询项目 | ai:project:list |
| 创建项目 | POST | `/project` | 创建新项目 | ai:project:save |
| 项目详情 | GET | `/project/{id}` | 获取项目详情 | ai:project:info |
| 更新项目 | PUT | `/project` | 更新项目 | ai:project:update |
| 删除项目 | DELETE | `/project/{id}` | 删除项目 | ai:project:delete |
| 版本列表 | GET | `/project/{id}/version/list` | 获取版本列表 | ai:project:info |
| 创建版本 | POST | `/project/{id}/version` | 创建新版本 | ai:project:save |
| 切换版本 | PUT | `/project/{id}/version/{vid}/switch` | 切换当前版本 | ai:project:update |

### 2.3 模板管理

| 接口 | 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|------|
| 模板列表 | GET | `/template/list` | 分页查询模板 | ai:template:list |
| 创建模板 | POST | `/template` | 创建新模板 | ai:template:save |
| 模板详情 | GET | `/template/{id}` | 获取模板详情 | ai:template:info |
| 更新模板 | PUT | `/template` | 更新模板 | ai:template:update |
| 删除模板 | DELETE | `/template/{id}` | 删除模板 | ai:template:delete |
| 使用模板 | POST | `/template/{id}/use` | 使用模板创建页面 | ai:template:use |
| 复制模板 | POST | `/template/{id}/copy` | 复制模板 | ai:template:save |

### 2.4 页面管理

| 接口 | 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|------|
| 页面列表 | GET | `/page/list` | 分页查询页面 | ai:page:list |
| 创建页面 | POST | `/page` | 创建新页面 | ai:page:save |
| 页面详情 | GET | `/page/{id}` | 获取页面详情 | ai:page:info |
| 更新页面 | PUT | `/page` | 更新页面 | ai:page:update |
| 删除页面 | DELETE | `/page/{id}` | 删除页面 | ai:page:delete |
| 发布页面 | PUT | `/page/{id}/publish` | 发布页面 | ai:page:publish |
| 页面版本 | GET | `/page/{id}/version/list` | 获取版本历史 | ai:page:info |
| 回滚版本 | POST | `/page/{id}/version/{vid}/rollback` | 回滚版本 | ai:page:update |

### 2.5 AI 生成

| 接口 | 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|------|
| 生成页面 | POST | `/generate/page` | AI 生成页面 | ai:generate:save |
| 批量生成 | POST | `/generate/batch` | 批量生成页面 | ai:generate:save |
| 重新生成 | POST | `/generate/regenerate` | 重新生成 | ai:generate:save |
| 修改页面 | POST | `/generate/modify` | 描述修改 | ai:generate:save |
| 区域修改 | POST | `/generate/modify-by-area` | 区域修改 | ai:generate:save |

### 2.6 组件管理

| 接口 | 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|------|
| 组件列表 | GET | `/component/list` | 分页查询组件 | ai:component:list |
| 创建组件 | POST | `/component` | 创建新组件 | ai:component:save |
| 组件详情 | GET | `/component/{id}` | 获取组件详情 | ai:component:info |
| 更新组件 | PUT | `/component` | 更新组件 | ai:component:update |
| 删除组件 | DELETE | `/component/{id}` | 删除组件 | ai:component:delete |
| 分类列表 | GET | `/component/category/list` | 获取分类列表 | ai:component:list |

### 2.7 成本控制

| 接口 | 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|------|
| 配额信息 | GET | `/quota/info` | 获取配额信息 | ai:quota:info |
| 使用日志 | GET | `/quota/log` | 获取使用日志 | ai:quota:list |

### 2.8 模型配置

| 接口 | 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|------|
| 模型列表 | GET | `/model/list` | 获取模型列表 | ai:model:list |
| 配置模型 | POST | `/model` | 配置模型 | ai:model:save |
| 更新模型 | PUT | `/model` | 更新模型 | ai:model:update |
| 删除模型 | DELETE | `/model/{id}` | 删除模型 | ai:model:delete |

---

## 三、核心流程

### 3.1 页面生成流程

```
用户输入需求
    │
    ▼
┌─────────────────────────────────────────────────────────────┐
│ 1. 参数校验                                                 │
│    - 验证配额                                                │
│    - 验证项目权限                                            │
└─────────────────────────────────────────────────────────────┘
    │
    ▼
┌─────────────────────────────────────────────────────────────┐
│ 2. 选择 AI 模型                                             │
│    - 根据配置选择默认/备用模型                                 │
└─────────────────────────────────────────────────────────────┘
    │
    ▼
┌─────────────────────────────────────────────────────────────┐
│ 3. 构造 Prompt                                              │
│    ┌─────────────────────────────────────────────────────┐ │
│    │ 你是一个前端开发专家，请使用 Vue3 + Tailwind CSS    │ │
│    │ + Headless UI 组件库，生成一个页面。                 │ │
│    │                                                     │ │
│    │ 需求：{用户输入的需求描述}                            │ │
│    │ 页面类型：{首页/列表页/详情页/表单页}                 │ │
│    │ 技术栈：Vue3 + TypeScript + Tailwind + Headless UI  │ │
│    └─────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
    │
    ▼
┌─────────────────────────────────────────────────────────────┐
│ 4. 调用 AI 接口                                             │
│    - 发送 Prompt                                            │
│    - 记录调用开始时间                                         │
└─────────────────────────────────────────────────────────────┘
    │
    ▼
┌─────────────────────────────────────────────────────────────┐
│ 5. AI 返回结果                                              │
│    - 解析 Vue 代码                                           │
│    - 检查语法正确性                                          │
└─────────────────────────────────────────────────────────────┘
    │
    ▼
┌─────────────────────────────────────────────────────────────┐
│ 6. AI 自检                                                  │
│    - 代码完整性检查                                          │
│    - 依赖检查                                                │
│    - 质量评分                                                │
└─────────────────────────────────────────────────────────────┘
    │
    ▼
┌─────────────────────────────────────────────────────────────┐
│ 7. 保存数据                                                 │
│    - 保存页面到 ai_page                                      │
│    - 保存模板到 ai_template（如选择保存）                     │
│    - 记录跳转关系                                            │
└─────────────────────────────────────────────────────────────┘
    │
    ▼
┌─────────────────────────────────────────────────────────────┐
│ 8. 记录日志                                                 │
│    - 保存提示词到 ai_prompt_log                              │
│    - 更新配额使用量                                          │
│    - 记录 Token 消耗                                         │
└─────────────────────────────────────────────────────────────┘
    │
    ▼
返回预览地址 + 页面ID
```

### 3.2 区域修改流程

```
1. 用户框选页面区域
2. 前端获取坐标 {x, y, width, height}
3. 用户输入修改需求
4. 调用 /api/ai/generate/modify-by-area
5. 后端构造 Prompt：
   - 包含原始代码
   - 包含坐标信息
   - 包含修改需求
6. 调用 AI 修改
7. 保存新代码，版本 +1
8. 返回更新后的预览
```

### 3.3 模板复用流程

```
1. 选择已有模板
2. 选择目标项目/版本
3. 选择是否基于模板修改
4. 如需修改，输入修改需求
5. AI 在模板基础上生成
6. 保存为新页面
7. 模板使用次数 +1
```

---

## 四、关键技术点

### 4.1 多模型接入

```java
// AI 模型接口
public interface AIModel {
    /**
     * 生成内容
     * @param prompt 提示词
     * @param options 生成选项
     * @return 生成的代码
     */
    String generate(String prompt, GenerateOptions options);
    
    /**
     * 获取模型信息
     */
    ModelInfo getModelInfo();
}
```

```java
// 模型工厂
@Service
public class AIFactory {
    @Autowired
    private Map<String, AIModel> modelMap;
    
    public AIModel getModel(String modelName) {
        AIModel model = modelMap.get(modelName);
        if (model == null) {
            // 使用默认模型
            model = modelMap.get("gpt-4");
        }
        return model;
    }
}
```

### 4.2 代码预览渲染

```typescript
// 前端渲染 Vue 代码
import { createApp, defineComponent, h } from 'vue'

function previewPage(code: string, container: HTMLElement) {
  // 1. 解析 Vue 代码
  const parsed = parseVueCode(code)
  
  // 2. 创建组件
  const Component = defineComponent({
    name: 'PreviewPage',
    setup() {
      return () => h('div', { 
        class: 'preview-container p-4',
        innerHTML: parsed.template 
      })
    },
    ...parsed
  })
  
  // 3. 创建应用并挂载
  const app = createApp(Component)
  app.mount(container)
  
  return () => app.unmount()
}
```

### 4.3 跳转关系解析

```typescript
interface PageLink {
  sourcePage: string    // 源页面ID
  targetPage: string    // 目标页面ID
  trigger: 'button' | 'card' | 'list-item' | 'form-submit'
  event: 'click' | 'submit'
  params?: Record<string, string>
}

// 从 AI 返回结果中解析跳转关系
function parseLinks(aiResponse: string, pages: Page[]): PageLink[] {
  const links: PageLink[] = []
  
  // 1. 查找页面间跳转的模式
  // 2. 提取触发元素和目标页面
  // 3. 构建跳转关系
  
  return links
}
```

### 4.4 区域定位修改

```typescript
interface AreaSelection {
  x: number
  y: number
  width: number
  height: number
}

// 前端框选实现
function selectArea(element: HTMLElement): AreaSelection {
  const rect = element.getBoundingClientRect()
  return {
    x: rect.left,
    y: rect.top,
    width: rect.width,
    height: rect.height
  }
}
```

### 4.5 代码质量保障

```java
@Service
public class CodeQualityService {
    
    /**
     * 语法检查
     */
    public List<String> checkSyntax(String vueCode) {
        List<String> errors = new ArrayList<>();
        
        // 1. 检查 Vue 语法
        // 2. 检查 Template 语法
        // 3. 检查 Script 语法
        // 4. 检查 Style 语法
        
        return errors;
    }
    
    /**
     * AI 自检
     */
    public QualityResult selfCheck(String vueCode, String prompt) {
        // 调用 AI 审查代码
        // 返回质量评分和改进建议
    }
}
```

### 4.6 成本控制

```java
@Service
public class QuotaService {
    
    /**
     * 检查配额
     */
    public void checkQuota(Long tenantId) {
        // 1. 获取租户今日使用量
        // 2. 比较配额限制
        // 3. 超额则抛出异常
    }
    
    /**
     * 记录使用
     */
    public void recordUsage(Long tenantId, int tokens) {
        // 1. 更新今日使用量
        // 2. 记录详细日志
        // 3. 检查是否需要告警
    }
}
```

---

## 五、配置清单

### 5.1 AI 模型配置

```yaml
ai:
  models:
    - name: gpt-4
      type: openai
      endpoint: https://api.openai.com/v1
      api-key: ${OPENAI_API_KEY}
      default: true
    - name: gpt-3.5-turbo
      type: openai
      api-key: ${OPENAI_API_KEY}
    - name: claude-3
      type: anthropic
      endpoint: https://api.anthropic.com
      api-key: ${CLAUDE_API_KEY}
    - name: tongyi
      type: aliyun
      endpoint: https://dashscope.aliyuncs.com
      api-key: ${TONGYI_API_KEY}
```

### 5.2 成本控制配置

```yaml
ai:
  quota:
    daily-limit: 100          # 每天生成次数限制
    monthly-limit: 3000       # 每月生成次数限制
    token-limit: 1000000      # 每月 Token 限制
    token-warning: 80%        # 配额警告阈值
    reset-time: "00:00"       # 重置时间
```

---

*持续更新中...*