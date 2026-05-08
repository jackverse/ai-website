<template>
  <div class="h-full flex flex-col bg-gray-50">
    <!-- 标题区 -->
    <div class="bg-white border-b border-gray-200 px-6 py-4 flex-shrink-0">
      <div class="flex justify-between items-center">
        <div>
          <h1 class="text-xl font-bold text-gray-900">模型配置</h1>
          <p class="text-gray-500 text-sm mt-0.5">配置和管理AI模型</p>
        </div>
        <button @click="openAddModal()" class="px-4 py-2.5 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors font-medium flex items-center gap-2 shadow-sm">
          <span class="text-lg">+</span> 添加模型
        </button>
      </div>
    </div>

    <!-- 模型列表 -->
    <div class="flex-1 overflow-auto p-6">
      <div v-if="loading" class="text-center py-10 text-gray-500">加载中...</div>
      <div v-else-if="error" class="text-center py-10 text-red-500">{{ error }}</div>
      <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <div
          v-for="model in models"
          :key="model.id"
          class="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden hover:shadow-lg hover:border-blue-200 transition-all"
        >
          <div class="p-5">
            <div class="flex justify-between items-start mb-4">
              <div>
                <h3 class="text-lg font-bold text-gray-900">{{ model.displayName }}</h3>
                <p class="text-gray-500 text-sm">{{ getProviderLabel(model.type) }}</p>
              </div>
              <div class="flex items-center gap-2">
                <span v-if="model.isDefault === 1" class="px-2 py-1 bg-yellow-100 text-yellow-700 rounded text-xs font-medium">⭐ 默认</span>
                <span :class="model.isEnabled === 1 ? 'bg-emerald-100 text-emerald-700' : 'bg-gray-100 text-gray-500'" class="px-2 py-1 rounded-full text-xs font-medium">
                  {{ model.isEnabled === 1 ? '● 已启用' : '○ 已禁用' }}
                </span>
              </div>
            </div>

            <div class="space-y-2 mb-4 text-sm">
              <div class="flex justify-between">
                <span class="text-gray-500">Endpoint</span>
                <span class="text-gray-900 truncate max-w-[180px]" :title="getEndpointByType(model.type)">{{ getEndpointByType(model.type) }}</span>
              </div>
              <div class="flex justify-between">
                <span class="text-gray-500">模型ID</span>
                <span class="text-gray-900">{{ model.model }}</span>
              </div>
              <div class="flex justify-between">
                <span class="text-gray-500">超时</span>
                <span class="text-gray-900">{{ model.timeout || 10 }}s</span>
              </div>
            </div>

            <div class="flex gap-2 pt-3 border-t border-gray-100">
              <button @click="openChat(model)" class="flex-1 px-2 py-2 bg-blue-50 hover:bg-blue-100 text-blue-600 rounded-lg font-medium text-xs transition-colors">
                💬 聊天
              </button>
              <button @click="setDefault(model)" :disabled="model.isDefault === 1" class="flex-1 px-2 py-2 bg-yellow-50 hover:bg-yellow-100 text-yellow-600 rounded-lg font-medium text-xs transition-colors disabled:opacity-50">
                {{ model.isDefault === 1 ? '✓ 默认' : '⭐ 设默认' }}
              </button>
              <button @click="openEditModal(model)" class="flex-1 px-2 py-2 bg-gray-100 hover:bg-gray-200 text-gray-600 rounded-lg font-medium text-xs transition-colors">
                ✏️ 编辑
              </button>
              <button @click="toggleModel(model)" class="flex-1 px-2 py-2 rounded-lg font-medium text-xs transition-colors" :class="model.isEnabled === 1 ? 'bg-gray-100 hover:bg-gray-200 text-gray-600' : 'bg-blue-50 hover:bg-blue-100 text-blue-600'">
                {{ model.isEnabled === 1 ? '禁用' : '启用' }}
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 添加/编辑模型弹窗 -->
    <div v-if="showModal" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50" @click.self="closeModal">
      <div class="bg-white rounded-2xl shadow-xl w-[520px] max-h-[90vh] overflow-auto">
        <div class="p-6 border-b border-gray-100 flex justify-between items-center">
          <h2 class="text-xl font-bold text-gray-900">{{ isEditing ? '编辑模型' : '添加模型' }}</h2>
          <button @click="closeModal" class="text-gray-400 hover:text-gray-600 text-2xl">&times;</button>
        </div>
        <div class="p-6 space-y-5">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1.5">显示名称 *</label>
            <input v-model="formData.displayName" type="text" class="w-full border border-gray-200 rounded-lg px-4 py-2.5 focus:outline-none focus:ring-2 focus:ring-blue-500" placeholder="GPT-4" />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1.5">模型标识 *</label>
            <input v-model="formData.name" type="text" class="w-full border border-gray-200 rounded-lg px-4 py-2.5 focus:outline-none focus:ring-2 focus:ring-blue-500" placeholder="gpt-4" />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1.5">类型 *</label>
            <select v-model="formData.type" class="w-full border border-gray-200 rounded-lg px-4 py-2.5 focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white">
              <option value="minimax">MiniMax</option>
              <option value="openai">OpenAI</option>
              <option value="anthropic">Anthropic</option>
              <option value="aliyun">阿里云</option>
            </select>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1.5">API Endpoint</label>
            <div class="w-full border border-gray-200 rounded-lg px-4 py-2.5 bg-gray-50 text-gray-600 text-sm truncate" :title="getDisplayEndpoint(formData.type, formData.endpoint)">
              {{ getDisplayEndpoint(formData.type, formData.endpoint) || '自动设置' }}
            </div>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1.5">模型 ID *</label>
            <input v-model="formData.model" type="text" class="w-full border border-gray-200 rounded-lg px-4 py-2.5 focus:outline-none focus:ring-2 focus:ring-blue-500" placeholder="gpt-4" />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1.5">API Key *</label>
            <input v-model="formData.apiKey" type="password" class="w-full border border-gray-200 rounded-lg px-4 py-2.5 focus:outline-none focus:ring-2 focus:ring-blue-500" placeholder="sk-..." />
          </div>
          <div>
              <label class="block text-sm font-medium text-gray-700 mb-1.5">每日Token限额</label>
              <input v-model.number="formData.tokenLimit" type="number" class="w-full border border-gray-200 rounded-lg px-4 py-2.5 focus:outline-none focus:ring-2 focus:ring-blue-500" placeholder="1000000" />
          </div>
          <div>
              <label class="block text-sm font-medium text-gray-700 mb-1.5">超时时间（秒）</label>
              <input v-model.number="formData.timeout" type="number" class="w-full border border-gray-200 rounded-lg px-4 py-2.5 focus:outline-none focus:ring-2 focus:ring-blue-500" placeholder="10" />
          </div>
        </div>
        <div class="p-6 border-t border-gray-100 flex justify-end gap-3">
          <button @click="closeModal" class="px-5 py-2.5 border border-gray-200 rounded-lg hover:bg-gray-50 transition-colors font-medium">取消</button>
          <button @click="submitForm" class="px-5 py-2.5 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors font-medium">{{ isEditing ? '保存' : '添加' }}</button>
        </div>
      </div>
    </div>

    <!-- 聊天弹窗 -->
    <div v-if="showChat" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50" @click.self="closeChat">
      <div class="bg-white rounded-2xl shadow-xl w-[700px] h-[600px] flex flex-col">
        <div class="p-4 border-b border-gray-200 flex items-center justify-between flex-shrink-0">
          <div>
            <h2 class="text-lg font-bold text-gray-900">{{ chatModel?.displayName }}</h2>
            <p class="text-gray-500 text-xs">{{ getProviderLabel(chatModel?.type) }} · {{ chatModel?.model }}</p>
          </div>
          <button @click="closeChat" class="text-gray-400 hover:text-gray-600 text-2xl">&times;</button>
        </div>

        <!-- 聊天消息 -->
        <div ref="chatMessages" class="flex-1 overflow-y-auto p-4 space-y-4">
          <div v-if="getCurrentChatMessages().length === 0" class="text-center text-gray-400 py-10">
            <div class="text-4xl mb-2">💬</div>
            <div>开始和 {{ chatModel?.displayName }} 对话吧</div>
          </div>

          <div v-for="(msg, index) in getCurrentChatMessages()" :key="index" :class="msg.role === 'user' ? 'flex justify-end' : 'flex justify-start'">
            <div :class="msg.role === 'user' ? 'bg-blue-600 text-white rounded-2xl rounded-br-sm px-4 py-2 max-w-[70%]' : 'bg-gray-100 text-gray-800 rounded-2xl rounded-bl-sm px-4 py-2 max-w-[70%]'">
              <div class="text-sm whitespace-pre-wrap">{{ msg.content }}</div>
              <div :class="msg.role === 'user' ? 'text-blue-100 text-xs mt-1 text-right' : 'text-gray-400 text-xs mt-1'">{{ msg.time }}</div>
            </div>
          </div>

          <div v-if="chatLoading" class="flex justify-start">
            <div class="bg-gray-100 rounded-2xl rounded-bl-sm px-4 py-3">
              <span class="animate-pulse">AI 思考中...</span>
            </div>
          </div>
        </div>

        <!-- 输入框 -->
        <div class="p-4 border-t border-gray-200 flex-shrink-0">
          <div class="flex gap-2">
            <input
              v-model="chatInput"
              @keyup.enter="sendChat"
              :disabled="chatLoading"
              type="text"
              class="flex-1 border border-gray-200 rounded-xl px-4 py-3 focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="输入消息..."
            />
            <button
              @click="sendChat"
              :disabled="chatLoading || !chatInput.trim()"
              class="px-6 py-3 bg-blue-600 text-white rounded-xl hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
            >
              发送
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import * as api from '@/api/model'

interface Model {
  id?: number
  name: string
  displayName: string
  type: string
  endpoint: string
  apiKey?: string
  model: string
  timeout?: number
  isDefault: number
  isEnabled: number
  tokenLimit?: number
}

interface Provider {
  type: string
  name: string
  endpoint: string
}

const providers = ref<Provider[]>([])
const models = ref<Model[]>([])
const loading = ref(false)
const error = ref('')
const showModal = ref(false)
const isEditing = ref(false)
const editingId = ref<number | null>(null)

const formData = ref({
  name: '',
  displayName: '',
  type: 'minimax',
  endpoint: '',
  apiKey: '',
  model: '',
  tokenLimit: 1000000,
  timeout: 10,
  isDefault: 0,
  isEnabled: 1
})

// 聊天相关
const showChat = ref(false)
const chatModel = ref<Model | null>(null)
const chatInput = ref('')
// 按模型 ID 隔离聊天记录
const chatMessagesMap = ref<Record<number, Array<{role: string; content: string; time: string}>>>({})
const chatLoading = ref(false)
const chatMessages = ref<HTMLElement | null>(null)

// 当前模型的聊天记录
function getCurrentChatMessages() {
  if (!chatModel.value?.id) return []
  return chatMessagesMap.value[chatModel.value.id] || []
}

function getProviderLabel(type: string): string {
  const map: Record<string, string> = {
    minimax: 'MiniMax',
    openai: 'OpenAI',
    anthropic: 'Anthropic',
    aliyun: '阿里云'
  }
  return map[type] || type
}

function getEndpointByType(type: string): string {
  const p = providers.value.find(p => p.type === type)
  return p?.endpoint || ''
}

function getDisplayEndpoint(type: string, storedEndpoint?: string): string {
  const computed = getEndpointByType(type)
  if (computed) return computed
  return storedEndpoint || '未设置'
}

function loadProviders() {
  api.getProviders().then((res: any) => {
    if (res.code === 0) {
      providers.value = res.data || []
    }
  })
}

function loadModels() {
  loading.value = true
  error.value = ''
  api.getModelList().then((res: any) => {
    if (res.code === 0) {
      models.value = res.data || []
    } else {
      error.value = res.msg || '加载失败'
    }
  }).catch((err: any) => {
    error.value = err.message || '网络错误'
  }).finally(() => {
    loading.value = false
  })
}

function toggleModel(model: Model) {
  const enabled = model.isEnabled === 1 ? 0 : 1
  api.setModelEnabled(model.id!, enabled === 1).then((res: any) => {
    if (res.code === 0) {
      model.isEnabled = enabled
    }
  })
}

function setDefault(model: Model) {
  api.setDefaultModel(model.id!).then((res: any) => {
    if (res.code === 0) {
      models.value.forEach(m => m.isDefault = 0)
      model.isDefault = 1
    }
  })
}

function openAddModal() {
  isEditing.value = false
  formData.value = {
    name: '', displayName: '', type: 'minimax', endpoint: '',
    apiKey: '', model: '', tokenLimit: 1000000, timeout: 10, isDefault: 0, isEnabled: 1
  }
  showModal.value = true
}

function openEditModal(model: Model) {
  isEditing.value = true
  editingId.value = model.id!
  formData.value = {
    name: model.name,
    displayName: model.displayName,
    type: model.type,
    endpoint: model.endpoint || '',
    apiKey: model.apiKey || '',
    model: model.model,
    tokenLimit: model.tokenLimit || 1000000,
    timeout: model.timeout || 10,
    isDefault: model.isDefault,
    isEnabled: model.isEnabled
  }
  showModal.value = true
}

function closeModal() {
  showModal.value = false
  editingId.value = null
}

function submitForm() {
  if (!formData.value.name || !formData.value.displayName || !formData.value.model) {
    alert('请填写必填项')
    return
  }

  // 提交时自动补上 endpoint（computed 优先，computed 为空则保留原值）
  const computed = getEndpointByType(formData.value.type)
  const endpoint = computed || formData.value.endpoint
  const data = { ...formData.value, endpoint }

  if (isEditing.value && editingId.value) {
    api.updateModel(editingId.value, data as any).then((res: any) => {
      if (res.code === 0) { loadModels(); closeModal() }
      else { alert(res.msg || '更新失败') }
    })
  } else {
    api.createModel(data as any).then((res: any) => {
      if (res.code === 0) { loadModels(); closeModal() }
      else { alert(res.msg || '创建失败') }
    })
  }
}

// 聊天功能
let chatAbortController: AbortController | null = null

function openChat(model: Model) {
  chatModel.value = model
  chatInput.value = ''
  showChat.value = true
}

function closeChat() {
  if (chatAbortController) {
    chatAbortController.abort()
    chatAbortController = null
  }
  chatLoading.value = false
  showChat.value = false
  // 不清空 chatModel.value 和聊天记录，保留
}

function getOrCreateMessages(modelId: number) {
  if (!chatMessagesMap.value[modelId]) {
    chatMessagesMap.value[modelId] = []
  }
  return chatMessagesMap.value[modelId]
}

async function sendChat() {
  if (!chatInput.value.trim() || chatLoading.value || !chatModel.value?.id) return

  const modelId = chatModel.value.id!
  const message = chatInput.value.trim()
  chatInput.value = ''

  const msgs = getOrCreateMessages(modelId)
  msgs.push({ role: 'user', content: message, time: new Date().toLocaleTimeString() })

  scrollToBottom()
  chatLoading.value = true

  chatAbortController = new AbortController()

  try {
    const res: any = await api.chatWithModel(modelId, message, chatAbortController.signal)
    if (res.code === 0) {
      msgs.push({ role: 'assistant', content: res.data, time: new Date().toLocaleTimeString() })
    } else {
      msgs.push({ role: 'assistant', content: 'Error: ' + (res.msg || res.data || '请求失败'), time: new Date().toLocaleTimeString() })
    }
  } catch (err: any) {
    if (err.name !== 'CanceledError' && err.name !== 'Cancel') {
      msgs.push({ role: 'assistant', content: 'Error: ' + (err.message || '网络错误'), time: new Date().toLocaleTimeString() })
    }
  } finally {
    chatLoading.value = false
    chatAbortController = null
    scrollToBottom()
  }
}

function scrollToBottom() {
  nextTick(() => {
    if (chatMessages.value) {
      chatMessages.value.scrollTop = chatMessages.value.scrollHeight
    }
  })
}

onMounted(() => {
  loadProviders()
  loadModels()
})
</script>
