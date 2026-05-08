<template>
  <div class="h-full flex flex-col bg-gray-50">
    <!-- 顶部工具栏 -->
    <div class="h-14 bg-white border-b border-gray-200 px-4 flex items-center justify-between flex-shrink-0">
      <div class="flex items-center gap-4">
        <h1 class="text-lg font-semibold text-gray-900">AI 生成</h1>
        <select
          v-model="selectedModelId"
          class="border border-gray-200 rounded-lg px-3 py-1.5 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white"
        >
          <option :value="undefined">默认模型</option>
          <option v-for="model in modelList" :key="model.id" :value="model.id">
            {{ model.displayName }}
          </option>
        </select>
      </div>
      <button
        @click="showEditor = !showEditor"
        class="text-sm text-blue-600 hover:text-blue-700 font-medium flex items-center gap-1"
      >
        {{ showEditor ? '隐藏代码编辑器' : '显示代码编辑器' }}
        <span class="text-xs">{{ showEditor ? '▲' : '▼' }}</span>
      </button>
    </div>

    <!-- 主内容区 -->
    <div class="flex-1 flex overflow-hidden">
      <!-- 左侧：需求输入 -->
      <div class="w-80 bg-white border-r border-gray-200 flex flex-col flex-shrink-0">
        <div class="flex-1 overflow-y-auto p-4">
          <!-- 需求描述 -->
          <div class="mb-4">
            <label class="block text-sm font-medium text-gray-700 mb-2">描述需求</label>
            <textarea
              v-model="prompt"
              rows="6"
              class="w-full border border-gray-200 rounded-lg px-3 py-2.5 focus:outline-none focus:ring-2 focus:ring-blue-500 resize-none text-sm"
              placeholder="例如: 生成一个产品展示页面，包含产品图片、产品名称、价格、购买按钮"
              @keydown.ctrl.enter="handleGenerate"
            ></textarea>
          </div>

          <!-- 页面类型 -->
          <div class="mb-4">
            <label class="block text-sm font-medium text-gray-700 mb-2">页面类型</label>
            <select
              v-model="pageType"
              class="w-full border border-gray-200 rounded-lg px-3 py-2.5 focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white text-sm"
            >
              <option :value="1">首页</option>
              <option :value="2">列表页</option>
              <option :value="3">详情页</option>
              <option :value="4">表单页</option>
              <option :value="5">单页</option>
            </select>
          </div>

          <!-- 风格选择 -->
          <div class="mb-4">
            <label class="block text-sm font-medium text-gray-700 mb-2">风格</label>
            <select
              v-model="style"
              class="w-full border border-gray-200 rounded-lg px-3 py-2.5 focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white text-sm"
            >
              <option value="tech">科技风</option>
              <option value="simple">简约风</option>
              <option value="business">商务风</option>
              <option value="modern">现代简洁</option>
            </select>
          </div>

          <!-- 端选择 -->
          <div class="mb-4">
            <label class="block text-sm font-medium text-gray-700 mb-2">目标端</label>
            <select
              v-model="platform"
              class="w-full border border-gray-200 rounded-lg px-3 py-2.5 focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white text-sm"
            >
              <option value="pc">PC端</option>
              <option value="mobile">移动端</option>
              <option value="both">PC+移动端</option>
            </select>
          </div>

          <!-- 参考图上传 -->
          <div class="mb-4">
            <label class="block text-sm font-medium text-gray-700 mb-2">参考图（可选）</label>
            <div
              @click="triggerUpload"
              class="border-2 border-dashed border-gray-200 rounded-lg p-4 text-center hover:border-blue-300 transition-colors cursor-pointer"
            >
              <div v-if="referenceImage" class="relative">
                <img :src="referenceImageUrl" class="w-full h-24 object-cover rounded" alt="参考图" />
                <button
                  @click.stop="removeReferenceImage"
                  class="absolute -top-2 -right-2 w-5 h-5 bg-red-500 text-white rounded-full text-xs flex items-center justify-center"
                >×</button>
              </div>
              <div v-else>
                <div class="text-2xl mb-1">📷</div>
                <div class="text-xs text-gray-500">点击上传参考图</div>
              </div>
            </div>
            <input ref="fileInput" type="file" accept="image/*" class="hidden" @change="handleImageUpload" />
          </div>
        </div>

        <!-- 发送按钮 + 历史记录 -->
        <div class="border-t border-gray-200 p-4 flex-shrink-0">
          <button
            @click="handleGenerate"
            :disabled="isGenerating || !prompt.trim()"
            class="w-full px-4 py-2.5 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors font-medium text-sm flex items-center justify-center gap-2 mb-3"
          >
            <span v-if="isGenerating" class="flex items-center gap-2">
              <svg class="animate-spin h-4 w-4" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"/>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"/>
              </svg>
              生成中...
            </span>
            <span v-else>🤖 发送生成 (Ctrl+Enter)</span>
          </button>

          <!-- 历史记录 -->
          <div class="text-sm">
            <div class="text-gray-500 mb-2">最近使用</div>
            <div v-if="historyList.length === 0" class="text-gray-400 text-xs">暂无历史记录</div>
            <div v-else class="space-y-1 max-h-32 overflow-y-auto">
              <div
                v-for="item in historyList"
                :key="item.id"
                @click="useHistory(item)"
                class="text-xs text-gray-600 hover:text-blue-600 cursor-pointer truncate py-1 px-2 rounded hover:bg-gray-50"
                :title="item.prompt"
              >
                {{ item.prompt.substring(0, 30) }}{{ item.prompt.length > 30 ? '...' : '' }}
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 中间：代码编辑器（可折叠） -->
      <div v-if="showEditor" class="w-[600px] border-r border-gray-200 flex flex-col bg-[#1e1e1e] flex-shrink-0">
        <div class="h-10 bg-[#323232] flex items-center px-3 border-b border-[#3c3c3c]">
          <div class="flex text-xs">
            <button
              v-for="tab in codeTabs"
              :key="tab"
              :class="[
                'px-3 py-1 rounded',
                activeTab === tab ? 'bg-[#094771] text-white' : 'text-gray-400 hover:text-white'
              ]"
              @click="activeTab = tab"
            >
              {{ tab }}
            </button>
          </div>
          <div class="flex-1"></div>
          <div class="flex gap-2">
            <button @click="copyCode" class="text-xs text-gray-400 hover:text-white px-2 py-1">复制</button>
            <button @click="exportCode" class="text-xs text-gray-400 hover:text-white px-2 py-1">导出</button>
          </div>
        </div>
        <div class="flex-1 overflow-hidden p-2">
          <textarea
            ref="codeEditorRef"
            v-model="generatedCode"
            @keydown.ctrl.s.prevent="handleSaveTemplate"
            @keydown.ctrl.z.prevent="undo"
            @keydown.ctrl.y.prevent="redo"
            class="w-full h-full bg-[#1e1e1e] text-gray-100 font-mono text-sm resize-none focus:outline-none p-2 leading-relaxed"
            spellcheck="false"
            placeholder="生成的代码将显示在这里..."
          ></textarea>
        </div>
        <div class="h-8 bg-[#323232] flex items-center px-3 border-t border-[#3c3c3c]">
          <button
            @click="handleSaveTemplate"
            :disabled="!generatedCode.trim()"
            class="text-xs text-gray-400 hover:text-white disabled:opacity-50"
          >
            保存为模板 (Ctrl+S)
          </button>
          <div class="flex-1"></div>
          <span class="text-xs text-gray-500">行: {{ lineCount }}</span>
        </div>
      </div>

      <!-- 右侧：预览区域 -->
      <div class="flex-1 flex flex-col bg-gray-100 overflow-hidden">
        <!-- 设备切换 -->
        <div class="h-10 bg-white border-b border-gray-200 px-4 flex items-center justify-between flex-shrink-0">
          <div class="flex items-center gap-2">
            <span class="text-sm text-gray-600">设备:</span>
            <div class="flex bg-gray-100 rounded-lg p-0.5">
              <button
                v-for="device in devices"
                :key="device.value"
                @click="currentDevice = device.value"
                :class="[
                  'px-3 py-1 text-xs rounded-md transition-colors',
                  currentDevice === device.value ? 'bg-white shadow text-gray-900' : 'text-gray-500 hover:text-gray-700'
                ]"
              >
                {{ device.label }}
              </button>
            </div>
          </div>
          <div class="flex gap-2">
            <button @click="refreshPreview" class="text-xs text-gray-500 hover:text-gray-700 px-2 py-1 hover:bg-gray-100 rounded">刷新</button>
            <button @click="toggleFullscreen" class="text-xs text-gray-500 hover:text-gray-700 px-2 py-1 hover:bg-gray-100 rounded">全屏</button>
          </div>
        </div>

        <!-- 错误提示 -->
        <div v-if="errorMessage" class="bg-red-50 border-b border-red-200 px-4 py-2 flex items-center gap-2">
          <span class="text-red-500 text-sm">⚠️ {{ errorMessage }}</span>
          <button @click="errorMessage = ''" class="ml-auto text-red-400 hover:text-red-600">×</button>
        </div>

        <!-- 预览内容 -->
        <div class="flex-1 overflow-auto p-4 flex justify-center">
          <div
            :class="[
              'bg-white rounded-lg shadow-lg transition-all duration-300',
              deviceWidthClass
            ]"
            :style="{ minHeight: '400px' }"
          >
            <!-- 预览框架 -->
            <iframe
              v-if="previewReady"
              ref="previewFrameRef"
              :srcdoc="safePreviewDoc"
              class="w-full border-0"
              :style="{ height: iframeHeight }"
              @load="onPreviewLoad"
              @error="onPreviewError"
            ></iframe>
            <div v-else class="w-full h-full flex items-center justify-center min-h-[400px]">
              <div class="text-center">
                <div class="text-5xl mb-3">🎨</div>
                <div class="text-gray-400 text-sm">输入描述后点击"发送生成"</div>
                <div class="text-gray-300 text-xs mt-1">支持 Ctrl+Enter 快捷发送</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onUnmounted, nextTick } from 'vue'
import { generatePage, getGenerateHistory, type GenerateHistory } from '@/api/generate'
import { getModelList } from '@/api/model'
import { saveTemplateFromGenerate } from '@/api/template'

// 状态
const selectedModelId = ref<number | undefined>(undefined)
const modelList = ref<Array<{id: number; displayName: string}>>([])
const prompt = ref('')
const pageType = ref(1)
const style = ref('tech')
const platform = ref('pc')
const referenceImage = ref<File | null>(null)
const referenceImageUrl = ref('')
const isGenerating = ref(false)
const showEditor = ref(false)
const generatedCode = ref('')
const currentDevice = ref('pc')
const activeTab = ref('template')
const errorMessage = ref('')
const previewReady = ref(false)

// 代码编辑器历史（用于撤销/重做）
const codeHistory = ref<string[]>([])
const historyIndex = ref(-1)

// refs
const fileInput = ref<HTMLInputElement | null>(null)
const previewFrameRef = ref<HTMLIFrameElement | null>(null)
const codeEditorRef = ref<HTMLTextAreaElement | null>(null)

// 设备选项
const devices = [
  { label: 'PC', value: 'pc' },
  { label: '平板', value: 'tablet' },
  { label: '手机', value: 'mobile' }
]

// 代码标签页
const codeTabs = ['template', 'script', 'style']

// 历史记录
const historyList = ref<GenerateHistory[]>([])

// 计算属性
const deviceWidthClass = computed(() => {
  switch (currentDevice.value) {
    case 'pc': return 'w-full'
    case 'tablet': return 'w-[768px]'
    case 'mobile': return 'w-[375px]'
    default: return 'w-full'
  }
})

const iframeHeight = computed(() => {
  switch (currentDevice.value) {
    case 'pc': return '600px'
    case 'tablet': return '1024px'
    case 'mobile': return '667px'
    default: return '600px'
  }
})

const lineCount = computed(() => {
  if (!generatedCode.value) return 0
  return generatedCode.value.split('\n').length
})

/**
 * 生成安全的预览 HTML
 * 使用更可靠的转义方法
 */
const safePreviewDoc = computed(() => {
  const code = generatedCode.value.trim()

  if (!code) return ''

  // 如果代码是完整的 Vue SFC（包含 template/script/style 标签），直接使用
  const hasSfcStructure = code.includes('<template>') ||
                          code.includes('<script>') ||
                          code.includes('<style>')

  let htmlCode = code
  let cssCode = ''
  let jsCode = ''

  if (hasSfcStructure) {
    // 解析 SFC 结构
    const templateMatch = code.match(/<template>([\s\S]*?)<\/template>/i)
    const scriptMatch = code.match(/<script[^>]*>([\s\S]*?)<\/script>/i)
    const styleMatch = code.match(/<style[^>]*>([\s\S]*?)<\/style>/i)

    htmlCode = templateMatch ? templateMatch[1].trim() : '<div>No template</div>'
    jsCode = scriptMatch ? scriptMatch[1].trim() : ''
    cssCode = styleMatch ? styleMatch[1].trim() : ''
  } else {
    // 没有 SFC 结构，直接作为 HTML 模板使用
    htmlCode = code
  }

  // 对 HTML 代码进行转义，防止模板字面量问题
  // 1. 先转义反斜杠
  let escaped = htmlCode.replace(/\\/g, '\\\\')
  // 2. 转义反引号
  escaped = escaped.replace(/`/g, '\\`')
  // 3. 转义 ${ 防止被当作模板字面量
  escaped = escaped.replace(/\$\{/g, '\\${')
  // 4. 转义 </ 防止提前结束脚本标签
  escaped = escaped.replace(/<\//g, '<\\/')

  // 构建预览 HTML
  return `<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <script src="https://unpkg.com/vue@3/dist/vue.global.js"><\/script>
  <script src="https://cdn.tailwindcss.com"><\/script>
  ${cssCode ? `<style>${cssCode}</style>` : ''}
  <style>
    body { font-family: system-ui, -apple-system, sans-serif; margin: 0; padding: 16px; background: #fff; }
    * { box-sizing: border-box; }
  </style>
</head>
<body>
  <div id="app"></div>
  <script>
    const { createApp, ref, computed, watch } = Vue
    ${jsCode}
    const App = {
      template: \`${escaped}\`,
      ${jsCode.includes('setup()') ? '' : 'setup() { return {} }'}
    }
    createApp(App).mount('#app')
  <\/script>
</body>
</html>`
})

// 方法
const loadModels = async () => {
  try {
    const res = await getModelList()
    if (res.data) {
      modelList.value = res.data.filter((m: any) => m.isEnabled === 1)
    }
  } catch (e) {
    console.error('加载模型列表失败', e)
  }
}

const loadHistory = async () => {
  try {
    const res = await getGenerateHistory(10)
    if (res.data) {
      historyList.value = res.data
    }
  } catch (e) {
    console.error('加载历史记录失败', e)
  }
}

const handleGenerate = async () => {
  if (!prompt.value.trim() || isGenerating.value) return

  isGenerating.value = true
  errorMessage.value = ''
  previewReady.value = false

  try {
    const res: any = await generatePage({
      prompt: prompt.value,
      pageType: pageType.value,
      style: style.value,
      platform: platform.value,
      referenceImage: referenceImageUrl.value || undefined,
      modelId: selectedModelId.value
    })

    if (res.code === 0 && res.data?.success) {
      // 保存到历史
      saveToHistory(generatedCode.value)
      generatedCode.value = res.data.code
      previewReady.value = true
      if (!showEditor.value) {
        showEditor.value = true
      }
      await loadHistory()
    } else {
      errorMessage.value = res.msg || res.data?.error || '生成失败'
    }
  } catch (e: any) {
    errorMessage.value = e.message || '网络错误，请重试'
    console.error('生成失败', e)
  } finally {
    isGenerating.value = false
  }
}

const useHistory = (item: GenerateHistory) => {
  prompt.value = item.prompt
  pageType.value = item.pageType || 1
  style.value = item.style || 'tech'
  platform.value = item.platform || 'pc'
  if (item.generatedCode) {
    saveToHistory(generatedCode.value)
    generatedCode.value = item.generatedCode
    previewReady.value = true
    if (!showEditor.value) {
      showEditor.value = true
    }
  }
}

// 上传参考图
const triggerUpload = () => {
  fileInput.value?.click()
}

const handleImageUpload = (event: Event) => {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]
  if (file) {
    referenceImage.value = file
    referenceImageUrl.value = URL.createObjectURL(file)
  }
}

const removeReferenceImage = () => {
  referenceImage.value = null
  referenceImageUrl.value = ''
}

// 刷新预览
const refreshPreview = () => {
  previewReady.value = false
  nextTick(() => {
    previewReady.value = true
  })
}

// 全屏
const toggleFullscreen = () => {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen()
  } else {
    document.exitFullscreen()
  }
}

// 复制代码
const copyCode = async () => {
  try {
    await navigator.clipboard.writeText(generatedCode.value)
    alert('代码已复制')
  } catch (err) {
    console.error('复制失败', err)
  }
}

// 导出代码
const exportCode = () => {
  const code = generatedCode.value
  if (!code) return
  const blob = new Blob([code], { type: 'text/plain' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = `generated-${Date.now()}.vue`
  a.click()
  URL.revokeObjectURL(url)
}

// 保存为模板
const handleSaveTemplate = async () => {
  if (!generatedCode.value) return

  const name = prompt.value.substring(0, 50) || '未命名模板'

  try {
    await saveTemplateFromGenerate(name, generatedCode.value)
    alert('模板保存成功')
  } catch (e) {
    console.error('保存模板失败', e)
    alert('保存模板失败')
  }
}

// 代码历史管理
const saveToHistory = (code: string) => {
  if (!code) return
  // 如果当前不在最新位置，删除后面的历史
  if (historyIndex.value < codeHistory.value.length - 1) {
    codeHistory.value = codeHistory.value.slice(0, historyIndex.value + 1)
  }
  // 添加新历史（如果与最后一个不同）
  if (codeHistory.value.length === 0 || codeHistory.value[codeHistory.value.length - 1] !== code) {
    codeHistory.value.push(code)
    historyIndex.value = codeHistory.value.length - 1
    // 限制历史长度
    if (codeHistory.value.length > 50) {
      codeHistory.value.shift()
      historyIndex.value--
    }
  }
}

const undo = () => {
  if (historyIndex.value > 0) {
    historyIndex.value--
    generatedCode.value = codeHistory.value[historyIndex.value]
    previewReady.value = true
  }
}

const redo = () => {
  if (historyIndex.value < codeHistory.value.length - 1) {
    historyIndex.value++
    generatedCode.value = codeHistory.value[historyIndex.value]
    previewReady.value = true
  }
}

// 预览加载/错误处理
const onPreviewLoad = () => {
  errorMessage.value = ''
}

const onPreviewError = () => {
  errorMessage.value = '预览加载失败，请检查代码是否正确'
}

// 键盘快捷键
const handleKeydown = (e: KeyboardEvent) => {
  if (e.ctrlKey && e.key === 'Enter') {
    e.preventDefault()
    handleGenerate()
  }
  if (e.key === 'F5') {
    e.preventDefault()
    refreshPreview()
  }
  if (e.key === 'F11') {
    e.preventDefault()
    toggleFullscreen()
  }
}

// 防抖预览更新
let previewTimeout: number | null = null
watch(generatedCode, (newCode, oldCode) => {
  // 保存到历史（避免频繁保存，只在变化时）
  if (newCode !== oldCode) {
    saveToHistory(newCode)
  }

  // 防抖刷新预览
  if (previewTimeout) {
    clearTimeout(previewTimeout)
  }
  previewTimeout = window.setTimeout(() => {
    previewReady.value = false
    nextTick(() => {
      previewReady.value = true
    })
  }, 500)
}, { immediate: false })

// 生命周期
onMounted(() => {
  loadModels()
  loadHistory()
  document.addEventListener('keydown', handleKeydown)
  // 初始化预览状态
  previewReady.value = !!generatedCode.value
})

onUnmounted(() => {
  document.removeEventListener('keydown', handleKeydown)
  if (previewTimeout) {
    clearTimeout(previewTimeout)
  }
})
</script>

<style scoped>
textarea {
  font-family: 'Fira Code', Consolas, 'Courier New', monospace;
}
</style>
