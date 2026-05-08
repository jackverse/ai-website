<template>
  <div class="p-6">
    <!-- 标题区 -->
    <div class="mb-6">
      <h1 class="text-2xl font-bold text-gray-900">成本控制</h1>
      <p class="text-gray-500 text-sm mt-1">监控和管理您的AI资源使用</p>
    </div>

    <!-- 本月配额 -->
    <div class="grid grid-cols-1 md:grid-cols-2 gap-6 mb-6">
      <!-- 生成次数 -->
      <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
        <div class="flex items-center justify-between mb-4">
          <h2 class="text-lg font-semibold text-gray-900">生成次数</h2>
          <span class="text-2xl">📊</span>
        </div>
        <div class="flex items-end gap-2 mb-4">
          <span class="text-4xl font-bold text-gray-900">{{ quota.usedTimes.toLocaleString() }}</span>
          <span class="text-gray-400 mb-1">/ {{ quota.totalTimes.toLocaleString() }}</span>
        </div>
        <div class="w-full bg-gray-100 rounded-full h-3 mb-3 overflow-hidden">
          <div
            class="bg-gradient-to-r from-blue-500 to-blue-600 h-3 rounded-full transition-all duration-500"
            :style="{ width: Math.min(100, (quota.usedTimes / quota.totalTimes * 100)) + '%' }"
          ></div>
        </div>
        <div class="flex justify-between items-center text-sm">
          <span class="text-gray-500">剩余可用</span>
          <span class="font-semibold text-blue-600">{{ (quota.totalTimes - quota.usedTimes).toLocaleString() }} 次</span>
        </div>
      </div>

      <!-- Token 使用 -->
      <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
        <div class="flex items-center justify-between mb-4">
          <h2 class="text-lg font-semibold text-gray-900">Token 使用</h2>
          <span class="text-2xl">💎</span>
        </div>
        <div class="flex items-end gap-2 mb-4">
          <span class="text-4xl font-bold text-gray-900">{{ formatNumber(quota.usedTokens) }}</span>
          <span class="text-gray-400 mb-1">/ {{ formatNumber(quota.totalTokens) }}</span>
        </div>
        <div class="w-full bg-gray-100 rounded-full h-3 mb-3 overflow-hidden">
          <div
            class="bg-gradient-to-r from-emerald-500 to-emerald-600 h-3 rounded-full transition-all duration-500"
            :style="{ width: Math.min(100, (quota.usedTokens / quota.totalTokens * 100)) + '%' }"
          ></div>
        </div>
        <div class="flex justify-between items-center text-sm">
          <span class="text-gray-500">剩余可用</span>
          <span class="font-semibold text-emerald-600">{{ formatNumber(quota.totalTokens - quota.usedTokens) }} Token</span>
        </div>
      </div>
    </div>

    <!-- 今日使用 -->
    <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-6 mb-6">
      <div class="flex flex-wrap items-center justify-between gap-4">
        <div class="flex gap-8">
          <div>
            <span class="text-gray-500 text-sm">今日使用</span>
            <div class="text-2xl font-bold text-gray-900 mt-1">{{ quota.todayUsage }} <span class="text-sm font-normal text-gray-400">次</span></div>
          </div>
          <div>
            <span class="text-gray-500 text-sm">每日限额</span>
            <div class="text-2xl font-bold text-gray-900 mt-1">{{ quota.dailyLimit }} <span class="text-sm font-normal text-gray-400">次</span></div>
          </div>
        </div>
        <div class="flex gap-3">
          <button @click="showLimitModal = true" class="px-4 py-2.5 border border-gray-200 rounded-lg hover:bg-gray-50 transition-colors font-medium text-sm">
            设置每日限额
          </button>
        </div>
      </div>
    </div>

    <!-- 快速操作 -->
    <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
      <h2 class="text-lg font-semibold text-gray-900 mb-4">快速操作</h2>
      <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
        <button @click="showLimitModal = true" class="px-4 py-4 bg-gray-50 rounded-xl hover:bg-gray-100 transition-colors text-left">
          <div class="text-lg mb-1">⚙️</div>
          <div class="font-medium text-gray-900 text-sm">设置每日限额</div>
          <div class="text-xs text-gray-500 mt-1">控制每日使用量</div>
        </button>
        <button class="px-4 py-4 bg-gray-50 rounded-xl hover:bg-gray-100 transition-colors text-left cursor-not-allowed opacity-50">
          <div class="text-lg mb-1">📋</div>
          <div class="font-medium text-gray-900 text-sm">查看详细日志</div>
          <div class="text-xs text-gray-500 mt-1">提示词优化页面</div>
        </button>
        <button class="px-4 py-4 bg-gray-50 rounded-xl hover:bg-gray-100 transition-colors text-left cursor-not-allowed opacity-50">
          <div class="text-lg mb-1">🔔</div>
          <div class="font-medium text-gray-900 text-sm">配置提醒</div>
          <div class="text-xs text-gray-500 mt-1">待开发</div>
        </button>
        <button class="px-4 py-4 bg-gray-50 rounded-xl hover:bg-gray-100 transition-colors text-left cursor-not-allowed opacity-50">
          <div class="text-lg mb-1">💰</div>
          <div class="font-medium text-gray-900 text-sm">购买更多配额</div>
          <div class="text-xs text-gray-500 mt-1">待开发</div>
        </button>
      </div>
    </div>

    <!-- 设置每日限额弹窗 -->
    <div v-if="showLimitModal" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
      <div class="bg-white rounded-2xl shadow-xl w-[400px] p-6">
        <h3 class="text-lg font-bold text-gray-900 mb-4">设置每日限额</h3>
        <input
          v-model.number="newDailyLimit"
          type="number"
          min="1"
          class="w-full border border-gray-200 rounded-lg px-4 py-3 focus:outline-none focus:ring-2 focus:ring-blue-500 mb-4"
          placeholder="每日次数上限"
        />
        <div class="flex justify-end gap-3">
          <button @click="showLimitModal = false" class="px-4 py-2 border border-gray-200 rounded-lg hover:bg-gray-50">取消</button>
          <button @click="saveDailyLimit" :disabled="saving" class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-50">
            {{ saving ? '保存中...' : '保存' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import * as quotaApi from '@/api/quota'

const quota = ref({
  usedTimes: 0,
  totalTimes: 3000,
  usedTokens: 0,
  totalTokens: 1000000,
  todayUsage: 0,
  dailyLimit: 100
})

const showLimitModal = ref(false)
const newDailyLimit = ref(100)
const saving = ref(false)

function formatNumber(num: number): string {
  return num.toLocaleString()
}

function loadQuota() {
  quotaApi.getQuotaStats().then((res: any) => {
    if (res.code === 0) {
      quota.value = { ...quota.value, ...res.data }
      newDailyLimit.value = res.data.dailyLimit || 100
    }
  }).catch(() => {
    // ignore
  })
}

function saveDailyLimit() {
  saving.value = true
  quotaApi.updateDailyLimit(newDailyLimit.value).then((res: any) => {
    if (res.code === 0) {
      quota.value.dailyLimit = newDailyLimit.value
      showLimitModal.value = false
    }
  }).finally(() => {
    saving.value = false
  })
}

onMounted(() => {
  loadQuota()
})
</script>
