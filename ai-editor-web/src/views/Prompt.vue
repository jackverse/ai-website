<template>
  <div class="p-6">
    <!-- 标题区 -->
    <div class="flex justify-between items-center mb-6">
      <div>
        <h1 class="text-2xl font-bold text-gray-900">提示词优化</h1>
        <p class="text-gray-500 text-sm mt-1">分析AI生成效果，优化提示词策略</p>
      </div>
      <button class="px-4 py-2.5 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors font-medium flex items-center gap-2 shadow-sm">
        📥 导出数据
      </button>
    </div>

    <!-- 标签页切换 -->
    <div class="flex gap-6 mb-6 border-b border-gray-200">
      <button
        @click="activeTab = 'log'"
        :class="activeTab === 'log' ? 'text-blue-600 border-blue-600' : 'text-gray-500 border-transparent hover:text-gray-700'"
        class="pb-3 px-1 font-medium border-b-2 transition-colors"
      >
        日志
      </button>
      <button
        @click="activeTab = 'stats'"
        :class="activeTab === 'stats' ? 'text-blue-600 border-blue-600' : 'text-gray-500 border-transparent hover:text-gray-700'"
        class="pb-3 px-1 font-medium border-b-2 transition-colors"
      >
        统计分析
      </button>
    </div>

    <!-- 日志页 -->
    <div v-if="activeTab === 'log'">
      <!-- 筛选 -->
      <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-4 mb-6">
        <div class="flex gap-4 flex-wrap items-center">
          <input
            v-model="filters.search"
            type="text"
            placeholder="搜索提示词..."
            class="flex-1 min-w-[150px] border border-gray-200 rounded-lg px-4 py-2.5 focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
          <select v-model="filters.model" class="border border-gray-200 rounded-lg px-4 py-2.5 focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white">
            <option value="">模型筛选</option>
            <option v-for="m in modelOptions" :key="m" :value="m">{{ m }}</option>
          </select>
          <select v-model="filters.status" class="border border-gray-200 rounded-lg px-4 py-2.5 focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white">
            <option value="">状态筛选</option>
            <option value="1">成功</option>
            <option value="0">失败</option>
          </select>
        </div>
      </div>

      <!-- 日志表格 -->
      <div class="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
        <table class="w-full">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-5 py-3.5 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">时间</th>
              <th class="px-5 py-3.5 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">模型</th>
              <th class="px-5 py-3.5 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">提示词摘要</th>
              <th class="px-5 py-3.5 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">质量</th>
              <th class="px-5 py-3.5 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">状态</th>
              <th class="px-5 py-3.5 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">耗时</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-100">
            <tr v-if="filteredLogs.length === 0">
              <td colspan="6" class="px-5 py-8 text-center text-gray-400">暂无数据</td>
            </tr>
            <tr v-for="log in filteredLogs" :key="log.id" class="hover:bg-gray-50 transition-colors">
              <td class="px-5 py-4 text-sm text-gray-500">{{ formatTime(log.createdAt) }}</td>
              <td class="px-5 py-4">
                <span class="px-2.5 py-1 bg-blue-50 text-blue-600 rounded-full text-xs font-medium">{{ log.model }}</span>
              </td>
              <td class="px-5 py-4 text-sm text-gray-700 max-w-[300px] truncate" :title="log.prompt">{{ truncate(log.prompt, 40) }}</td>
              <td class="px-5 py-4">
                <span class="text-amber-400">{{ renderStars(log.qualityScore) }}</span>
              </td>
              <td class="px-5 py-4">
                <span
                  :class="log.status === 1 ? 'bg-emerald-50 text-emerald-600' : 'bg-red-50 text-red-600'"
                  class="px-2.5 py-1 rounded-full text-xs font-medium"
                >
                  {{ log.status === 1 ? '✓ 成功' : '✕ 失败' }}
                </span>
              </td>
              <td class="px-5 py-4 text-sm text-gray-500">{{ log.duration ? (log.duration / 1000).toFixed(1) + 's' : '-' }}</td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- 分页 -->
      <div class="mt-4 flex justify-between items-center bg-white rounded-xl shadow-sm border border-gray-100 p-4">
        <span class="text-gray-500 text-sm">共 {{ filteredLogs.length }} 条</span>
        <div class="flex gap-1">
          <button :disabled="page <= 1" @click="page--" class="w-10 h-10 border border-gray-200 rounded-lg hover:bg-gray-50 transition-colors flex items-center justify-center text-gray-600 disabled:opacity-40">‹</button>
          <button class="w-10 h-10 border border-gray-200 rounded-lg hover:bg-gray-50 transition-colors flex items-center justify-center text-gray-600">›</button>
        </div>
      </div>
    </div>

    <!-- 统计分析页 -->
    <div v-if="activeTab === 'stats'">
      <div class="grid grid-cols-1 md:grid-cols-2 gap-6 mb-6">
        <!-- 模型使用分布 -->
        <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
          <h2 class="text-lg font-semibold text-gray-900 mb-6">模型使用分布</h2>
          <div class="space-y-5">
            <div v-for="(item, key) in stats.modelUsage" :key="key" class="flex items-center gap-4">
              <span class="w-24 text-sm font-medium text-gray-600">{{ key }}</span>
              <div class="flex-1 bg-gray-100 rounded-full h-3 overflow-hidden">
                <div class="bg-gradient-to-r from-blue-500 to-blue-600 h-3 rounded-full" :style="{ width: getUsagePercent(item) + '%' }"></div>
              </div>
              <span class="w-12 text-sm font-semibold text-gray-900">{{ item }}次</span>
            </div>
            <div v-if="!stats.modelUsage || Object.keys(stats.modelUsage).length === 0" class="text-gray-400 text-sm text-center py-4">暂无数据</div>
          </div>
        </div>

        <!-- 质量评分分布 -->
        <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
          <h2 class="text-lg font-semibold text-gray-900 mb-6">质量评分分布</h2>
          <div class="space-y-5">
            <div v-for="i in [5,4,3,2,1]" :key="i" class="flex items-center gap-4">
              <span class="w-20 text-sm font-medium text-gray-600">{{ '★'.repeat(i) }}{{ '☆'.repeat(5-i) }}</span>
              <div class="flex-1 bg-gray-100 rounded-full h-3 overflow-hidden">
                <div class="bg-gradient-to-r from-amber-400 to-amber-500 h-3 rounded-full" :style="{ width: getRatingPercent(i) + '%' }"></div>
              </div>
              <span class="w-12 text-sm font-semibold text-gray-900">{{ getRatingCount(i) }}次</span>
            </div>
            <div v-if="ratingCount === 0" class="text-gray-400 text-sm text-center py-4">暂无数据</div>
          </div>
        </div>
      </div>

      <!-- 统计指标 -->
      <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-6">
        <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-6 text-center">
          <div class="text-4xl font-bold text-blue-600 mb-2">{{ stats.avgDuration || '0s' }}</div>
          <div class="text-gray-500">平均响应时间</div>
        </div>
        <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-6 text-center">
          <div class="text-4xl font-bold text-emerald-600 mb-2">{{ stats.avgTokens || '0' }}</div>
          <div class="text-gray-500">平均 Token 消耗</div>
        </div>
        <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-6 text-center">
          <div class="text-4xl font-bold text-purple-600 mb-2">{{ stats.successRate || '0%' }}</div>
          <div class="text-gray-500">成功率</div>
        </div>
      </div>

      <!-- 优化建议 -->
      <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
        <h2 class="text-lg font-semibold text-gray-900 mb-4">💡 优化建议</h2>
        <ul class="space-y-4">
          <li class="flex gap-4 p-4 bg-blue-50 rounded-xl">
            <span class="w-8 h-8 bg-blue-100 text-blue-600 rounded-full flex items-center justify-center font-bold flex-shrink-0">1</span>
            <p class="text-gray-700">提示词越具体详细，生成质量越高。建议包含页面类型、风格、必含元素等。</p>
          </li>
          <li class="flex gap-4 p-4 bg-emerald-50 rounded-xl">
            <span class="w-8 h-8 bg-emerald-100 text-emerald-600 rounded-full flex items-center justify-center font-bold flex-shrink-0">2</span>
            <p class="text-gray-700">生成失败时可查看详细日志，了解具体错误原因并调整提示词。</p>
          </li>
          <li v-if="stats.totalLogs > 0" class="flex gap-4 p-4 bg-purple-50 rounded-xl">
            <span class="w-8 h-8 bg-purple-100 text-purple-600 rounded-full flex items-center justify-center font-bold flex-shrink-0">3</span>
            <p class="text-gray-700">您已有 {{ stats.totalLogs }} 条使用记录，建议定期回顾评分较低的记录进行优化。</p>
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import * as promptApi from '@/api/prompt'

const activeTab = ref('log')
const page = ref(1)

const filters = ref({
  search: '',
  model: '',
  status: ''
})

const logs = ref<any[]>([])
const stats = ref<any>({
  modelUsage: {},
  successRate: '0%',
  avgDuration: '0s',
  avgTokens: '0',
  totalLogs: 0
})

const modelOptions = computed(() => {
  const models = new Set(logs.value.map(l => l.model).filter(Boolean))
  return [...models]
})

const ratingCount = computed(() => {
  return logs.value.filter(l => l.qualityScore != null).length
})

const filteredLogs = computed(() => {
  let result = logs.value
  if (filters.value.model) {
    result = result.filter(l => l.model === filters.value.model)
  }
  if (filters.value.status) {
    result = result.filter(l => String(l.status) === filters.value.status)
  }
  if (filters.value.search) {
    result = result.filter(l => l.prompt?.includes(filters.value.search))
  }
  return result
})

function formatTime(ts: string): string {
  if (!ts) return '-'
  const d = new Date(ts)
  return d.toLocaleString('zh-CN', { month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })
}

function truncate(str: string, len: number): string {
  if (!str) return ''
  return str.length > len ? str.substring(0, len) + '...' : str
}

function renderStars(score: number): string {
  if (!score) return '-'
  return '★'.repeat(score) + '☆'.repeat(5 - score)
}

function getUsagePercent(count: number): number {
  const total = Object.values(stats.value.modelUsage || {}).reduce((a: number, b: any) => a + (b as number), 0)
  if (!total) return 0
  return Math.round((count / total) * 100)
}

function getRatingPercent(rating: number): number {
  const total = ratingCount.value
  if (!total) return 0
  const cnt = logs.value.filter(l => l.qualityScore === rating).length
  return Math.round((cnt / total) * 100)
}

function getRatingCount(rating: number): number {
  return logs.value.filter(l => l.qualityScore === rating).length
}

function loadLogs() {
  promptApi.getPromptLogs().then((res: any) => {
    if (res.code === 0) {
      logs.value = res.data || []
    }
  })
}

function loadStats() {
  promptApi.getPromptStats().then((res: any) => {
    if (res.code === 0) {
      stats.value = { ...stats.value, ...res.data }
    }
  })
}

onMounted(() => {
  loadLogs()
  loadStats()
})
</script>
