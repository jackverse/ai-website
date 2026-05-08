<template>
  <div class="h-full flex flex-col bg-gray-50">
    <!-- 顶部导航 -->
    <div class="h-14 bg-white border-b border-gray-200 px-4 flex items-center justify-between flex-shrink-0">
      <div class="flex items-center gap-4">
        <h1 class="text-lg font-semibold text-gray-900">模板库</h1>
        <div class="flex bg-gray-100 rounded-lg p-0.5">
          <button
            v-for="tab in tabs"
            :key="tab.value"
            @click="currentTab = tab.value"
            :class="[
              'px-4 py-1.5 text-sm rounded-md transition-colors',
              currentTab === tab.value ? 'bg-white shadow text-gray-900' : 'text-gray-500 hover:text-gray-700'
            ]"
          >
            {{ tab.label }}
          </button>
        </div>
      </div>
      <button
        @click="showCreateModal = true"
        class="px-4 py-2 bg-blue-600 text-white text-sm rounded-lg hover:bg-blue-700 transition-colors"
      >
        创建模板
      </button>
    </div>

    <!-- 工具栏 -->
    <div class="bg-white border-b border-gray-200 px-4 py-3 flex items-center gap-4">
      <!-- 搜索 -->
      <div class="relative flex-1 max-w-md">
        <input
          v-model="searchKeyword"
          type="text"
          placeholder="搜索模板..."
          class="w-full pl-10 pr-4 py-2 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
        />
        <svg class="absolute left-3 top-2.5 w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"/>
        </svg>
      </div>

      <!-- 类型筛选 -->
      <select
        v-model="filterType"
        class="border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white"
      >
        <option :value="undefined">全部类型</option>
        <option :value="1">单页</option>
        <option :value="2">联动页面</option>
        <option :value="3">组件</option>
      </select>

      <!-- 标签筛选 -->
      <select
        v-model="filterTag"
        class="border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white"
      >
        <option :value="undefined">全部标签</option>
        <option v-for="tag in availableTags" :key="tag" :value="tag">{{ tag }}</option>
      </select>
    </div>

    <!-- 模板列表 -->
    <div class="flex-1 overflow-auto p-4">
      <div v-if="loading" class="flex items-center justify-center h-64">
        <svg class="animate-spin h-8 w-8 text-blue-600" viewBox="0 0 24 24">
          <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"/>
          <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"/>
        </svg>
      </div>

      <div v-else-if="filteredTemplates.length === 0" class="flex flex-col items-center justify-center h-64 text-gray-400">
        <div class="text-5xl mb-3">📭</div>
        <div>暂无模板</div>
        <button @click="showCreateModal = true" class="mt-4 text-blue-500 hover:text-blue-600">创建第一个模板</button>
      </div>

      <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
        <div
          v-for="template in filteredTemplates"
          :key="template.id"
          class="bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden hover:shadow-md transition-shadow cursor-pointer"
          @click="previewTemplate(template)"
        >
          <!-- 预览图 -->
          <div class="h-40 bg-gradient-to-br from-gray-100 to-gray-200 flex items-center justify-center relative">
            <div v-if="template.isOfficial === 1" class="absolute top-2 left-2 px-2 py-0.5 bg-yellow-400 text-yellow-800 text-xs rounded font-medium">
              官方
            </div>
            <div class="text-4xl">{{ getTemplateIcon(template.type) }}</div>
          </div>

          <!-- 信息 -->
          <div class="p-4">
            <div class="flex items-start justify-between">
              <div>
                <h3 class="font-medium text-gray-900">{{ template.name }}</h3>
                <p v-if="template.description" class="text-xs text-gray-500 mt-1 line-clamp-2">{{ template.description }}</p>
              </div>
              <div class="flex items-center gap-1 text-yellow-400 text-sm">
                <span>⭐</span>
                <span>{{ template.qualityScore || 0 }}</span>
              </div>
            </div>

            <!-- 标签 -->
            <div v-if="template.tags" class="flex flex-wrap gap-1 mt-3">
              <span
                v-for="tag in template.tags.split(',').slice(0, 3)"
                :key="tag"
                class="px-2 py-0.5 bg-gray-100 text-gray-600 text-xs rounded"
              >
                {{ tag.trim() }}
              </span>
            </div>

            <!-- 操作 -->
            <div class="flex items-center justify-between mt-4 pt-3 border-t border-gray-100">
              <div class="text-xs text-gray-400">
                使用 {{ template.usageCount || 0 }} 次
              </div>
              <div class="flex gap-2">
                <button
                  @click.stop="useTemplate(template)"
                  class="px-3 py-1 bg-blue-50 text-blue-600 text-xs rounded hover:bg-blue-100 transition-colors"
                >
                  使用
                </button>
                <button
                  v-if="currentTab === 'mine'"
                  @click.stop="editTemplate(template)"
                  class="px-3 py-1 bg-gray-100 text-gray-600 text-xs rounded hover:bg-gray-200 transition-colors"
                >
                  编辑
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div v-if="totalPages > 1" class="bg-white border-t border-gray-200 px-4 py-3 flex items-center justify-between">
      <div class="text-sm text-gray-500">
        共 {{ total }} 个模板
      </div>
      <div class="flex gap-2">
        <button
          @click="currentPage--"
          :disabled="currentPage <= 1"
          class="px-3 py-1 border border-gray-200 rounded text-sm hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
        >
          上一页
        </button>
        <button
          @click="currentPage++"
          :disabled="currentPage >= totalPages"
          class="px-3 py-1 border border-gray-200 rounded text-sm hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
        >
          下一页
        </button>
      </div>
    </div>

    <!-- 预览弹窗 -->
    <div v-if="showPreviewModal" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50" @click.self="showPreviewModal = false">
      <div class="bg-white rounded-lg w-[900px] max-h-[90vh] flex flex-col">
        <div class="px-4 py-3 border-b border-gray-200 flex items-center justify-between">
          <h3 class="font-medium">{{ selectedTemplate?.name }}</h3>
          <button @click="showPreviewModal = false" class="text-gray-400 hover:text-gray-600">×</button>
        </div>
        <div class="flex-1 overflow-hidden">
          <iframe
            v-if="previewDoc"
            :srcdoc="previewDoc"
            class="w-full h-[500px] border-0"
          ></iframe>
        </div>
        <div class="px-4 py-3 border-t border-gray-200 flex justify-end gap-3">
          <button
            @click="showPreviewModal = false"
            class="px-4 py-2 border border-gray-200 rounded-lg text-sm hover:bg-gray-50"
          >
            关闭
          </button>
          <button
            @click="useTemplate(selectedTemplate)"
            class="px-4 py-2 bg-blue-600 text-white rounded-lg text-sm hover:bg-blue-700"
          >
            使用此模板
          </button>
        </div>
      </div>
    </div>

    <!-- 创建/编辑模板弹窗 -->
    <div v-if="showCreateModal" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50" @click.self="closeCreateModal">
      <div class="bg-white rounded-lg w-[600px] max-h-[90vh] flex flex-col">
        <div class="px-4 py-3 border-b border-gray-200 flex items-center justify-between">
          <h3 class="font-medium">{{ editingTemplate ? '编辑模板' : '创建模板' }}</h3>
          <button @click="closeCreateModal" class="text-gray-400 hover:text-gray-600">×</button>
        </div>
        <div class="flex-1 overflow-y-auto p-4 space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">模板名称</label>
            <input
              v-model="formData.name"
              type="text"
              class="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="输入模板名称"
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">描述</label>
            <textarea
              v-model="formData.description"
              rows="3"
              class="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 resize-none"
              placeholder="输入模板描述"
            ></textarea>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">类型</label>
            <select
              v-model="formData.type"
              class="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white"
            >
              <option :value="1">单页</option>
              <option :value="2">联动页面</option>
              <option :value="3">组件</option>
            </select>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">标签（逗号分隔）</label>
            <input
              v-model="formData.tags"
              type="text"
              class="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="如: 产品, 列表, 电商"
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">代码</label>
            <textarea
              v-model="formData.code"
              rows="10"
              class="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm font-mono focus:outline-none focus:ring-2 focus:ring-blue-500 resize-none"
              placeholder="输入 Vue 代码"
            ></textarea>
          </div>
        </div>
        <div class="px-4 py-3 border-t border-gray-200 flex justify-end gap-3">
          <button
            @click="closeCreateModal"
            class="px-4 py-2 border border-gray-200 rounded-lg text-sm hover:bg-gray-50"
          >
            取消
          </button>
          <button
            @click="saveTemplate"
            class="px-4 py-2 bg-blue-600 text-white rounded-lg text-sm hover:bg-blue-700"
          >
            保存
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { getTemplateList, getOfficialTemplates, createTemplate, updateTemplate, useTemplate as useTemplateApi, type Template } from '@/api/template'

const router = useRouter()

// 状态
const tabs = [
  { label: '我的模板', value: 'mine' },
  { label: '官方模板', value: 'official' }
]
const currentTab = ref('mine')
const loading = ref(false)
const templates = ref<Template[]>([])
const searchKeyword = ref('')
const filterType = ref<number | undefined>(undefined)
const filterTag = ref<string | undefined>(undefined)
const currentPage = ref(1)
const pageSize = ref(20)

// 弹窗状态
const showPreviewModal = ref(false)
const showCreateModal = ref(false)
const selectedTemplate = ref<Template | null>(null)
const editingTemplate = ref<Template | null>(null)

// 表单数据
const formData = ref({
  name: '',
  description: '',
  type: 1,
  tags: '',
  code: ''
})

// 计算属性
const filteredTemplates = computed(() => {
  let result = templates.value

  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(t =>
      t.name.toLowerCase().includes(keyword) ||
      t.description?.toLowerCase().includes(keyword) ||
      t.tags?.toLowerCase().includes(keyword)
    )
  }

  if (filterType.value !== undefined) {
    result = result.filter(t => t.type === filterType.value)
  }

  if (filterTag.value) {
    result = result.filter(t => t.tags?.split(',').map(tag => tag.trim()).includes(filterTag.value!))
  }

  return result
})

const total = computed(() => filteredTemplates.value.length)
const totalPages = computed(() => Math.ceil(total.value / pageSize.value))

const availableTags = computed(() => {
  const tagSet = new Set<string>()
  templates.value.forEach(t => {
    t.tags?.split(',').forEach(tag => tagSet.add(tag.trim()))
  })
  return Array.from(tagSet).sort()
})

// 预览文档
const previewDoc = computed(() => {
  if (!selectedTemplate.value?.code) return ''

  const code = selectedTemplate.value.code
  const escapedCode = code.replace(/\\/g, '\\\\').replace(/`/g, '\\`').replace(/\$/g, '\\$')

  return `<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <script src="https://unpkg.com/vue@3/dist/vue.global.js"><\/script>
  <script src="https://cdn.tailwindcss.com"><\/script>
  <style>
    body { font-family: system-ui, -apple-system, sans-serif; margin: 0; padding: 16px; background: #fff; }
    * { box-sizing: border-box; }
  </style>
</head>
<body>
  <div id="app"></div>
  <script>
    const { createApp, ref } = Vue
    const App = {
      template: \`${escapedCode}\`,
      setup() { return {} }
    }
    createApp(App).mount('#app')
  <\/script>
</body>
</html>`
})

// 方法
const loadTemplates = async () => {
  loading.value = true
  try {
    let res: any
    if (currentTab.value === 'official') {
      res = await getOfficialTemplates()
    } else {
      res = await getTemplateList()
    }
    if (res.data) {
      templates.value = res.data
    }
  } catch (e) {
    console.error('加载模板失败', e)
  } finally {
    loading.value = false
  }
}

const getTemplateIcon = (type: number) => {
  switch (type) {
    case 1: return '📄'
    case 2: return '🔗'
    case 3: return '🧩'
    default: return '📄'
  }
}

const previewTemplate = (template: Template) => {
  selectedTemplate.value = template
  showPreviewModal.value = true
}

const useTemplate = async (template: Template | null) => {
  if (!template) return
  try {
    await useTemplateApi(template.id)
    showPreviewModal.value = false
    // 跳转到 AI 生成页面并使用此模板
    router.push({
      path: '/generate',
      query: { templateId: template.id.toString(), templateCode: template.code }
    })
  } catch (e) {
    console.error('使用模板失败', e)
  }
}

const editTemplate = (template: Template) => {
  editingTemplate.value = template
  formData.value = {
    name: template.name,
    description: template.description || '',
    type: template.type,
    tags: template.tags || '',
    code: template.code
  }
  showCreateModal.value = true
}

const closeCreateModal = () => {
  showCreateModal.value = false
  editingTemplate.value = null
  formData.value = {
    name: '',
    description: '',
    type: 1,
    tags: '',
    code: ''
  }
}

const saveTemplate = async () => {
  if (!formData.value.name || !formData.value.code) {
    alert('请填写名称和代码')
    return
  }

  try {
    const data = {
      name: formData.value.name,
      description: formData.value.description,
      type: formData.value.type,
      tags: formData.value.tags,
      code: formData.value.code
    }

    if (editingTemplate.value) {
      await updateTemplate({ ...data, id: editingTemplate.value.id })
    } else {
      await createTemplate(data)
    }

    closeCreateModal()
    await loadTemplates()
  } catch (e) {
    console.error('保存模板失败', e)
    alert('保存失败')
  }
}

// 监听标签页切换
watch(currentTab, () => {
  currentPage.value = 1
  loadTemplates()
})

// 生命周期
onMounted(() => {
  loadTemplates()
})
</script>

<style scoped>
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
