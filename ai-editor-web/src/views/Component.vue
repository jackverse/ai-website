<template>
  <div class="p-6">
    <!-- 标题区 -->
    <div class="flex justify-between items-center mb-6">
      <div>
        <h1 class="text-2xl font-bold text-gray-900">组件管理</h1>
        <p class="text-gray-500 text-sm mt-1">管理和使用页面组件库</p>
      </div>
      <button class="px-4 py-2.5 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors font-medium flex items-center gap-2 shadow-sm">
        <span class="text-lg">+</span> 新建组件
      </button>
    </div>

    <!-- 标签筛选 -->
    <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-1.5 mb-6 inline-flex gap-1">
      <button
        v-for="tab in tabs"
        :key="tab"
        @click="activeTab = tab"
        :class="activeTab === tab ? 'bg-blue-600 text-white shadow-sm' : 'text-gray-600 hover:bg-gray-100'"
        class="px-5 py-2.5 rounded-lg font-medium transition-all text-sm"
      >
        {{ tab }}
      </button>
    </div>

    <!-- 搜索 -->
    <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-4 mb-6">
      <div class="flex gap-4">
        <input
          v-model="searchQuery"
          type="text"
          placeholder="搜索组件..."
          class="flex-1 border border-gray-200 rounded-lg px-4 py-2.5 focus:outline-none focus:ring-2 focus:ring-blue-500"
        />
        <select v-model="categoryFilter" class="border border-gray-200 rounded-lg px-4 py-2.5 focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white">
          <option value="">分类筛选</option>
          <option value="form">表单</option>
          <option value="display">展示</option>
          <option value="layout">布局</option>
        </select>
        <button class="px-4 py-2.5 border border-gray-200 rounded-lg hover:bg-gray-50 transition-colors text-gray-600">
          ↻ 刷新
        </button>
      </div>
    </div>

    <!-- 组件展示区域 -->
    <div v-if="activeTab === '基础' || activeTab === '全部'" class="mb-8">
      <h2 class="text-lg font-semibold text-gray-900 mb-4 flex items-center gap-2">
        <span>🧩</span> 基础组件
      </h2>
      <div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-6 gap-4">
        <div
          v-for="comp in filteredBasicComponents"
          :key="comp.id"
          class="bg-white rounded-xl shadow-sm border border-gray-100 p-5 text-center cursor-pointer hover:shadow-lg hover:border-blue-200 transition-all group"
        >
          <div class="w-14 h-14 bg-gradient-to-br from-gray-50 to-gray-100 rounded-xl mx-auto mb-3 flex items-center justify-center text-2xl group-hover:from-blue-50 group-hover:to-blue-100 transition-colors">
            {{ comp.icon }}
          </div>
          <h3 class="font-medium text-gray-900 mb-1">{{ comp.name }}</h3>
          <span class="text-xs px-2 py-0.5 bg-gray-100 text-gray-500 rounded">{{ comp.category }}</span>
        </div>
      </div>
    </div>

    <div v-if="activeTab === '业务' || activeTab === '全部'" class="mb-8">
      <h2 class="text-lg font-semibold text-gray-900 mb-4 flex items-center gap-2">
        <span>📦</span> 业务组件
      </h2>
      <div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-6 gap-4">
        <div
          v-for="comp in filteredBusinessComponents"
          :key="comp.id"
          class="bg-white rounded-xl shadow-sm border border-gray-100 p-5 text-center cursor-pointer hover:shadow-lg hover:border-blue-200 transition-all group"
        >
          <div class="w-14 h-14 bg-gradient-to-br from-gray-50 to-gray-100 rounded-xl mx-auto mb-3 flex items-center justify-center text-2xl group-hover:from-purple-50 group-hover:to-purple-100 transition-colors">
            {{ comp.icon }}
          </div>
          <h3 class="font-medium text-gray-900 mb-1">{{ comp.name }}</h3>
          <span class="text-xs px-2 py-0.5 bg-purple-100 text-purple-600 rounded">{{ comp.category }}</span>
        </div>
      </div>
    </div>

    <div v-if="activeTab === '官方' || activeTab === '全部'" class="mb-8">
      <h2 class="text-lg font-semibold text-gray-900 mb-4 flex items-center gap-2">
        <span>🏛</span> 官方组件
        <span class="text-xs text-gray-500 font-normal">（位于"更多组件"中）</span>
      </h2>
      <div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-6 gap-4">
        <div
          v-for="comp in filteredOfficialComponents"
          :key="comp.id"
          class="bg-white rounded-xl shadow-sm border border-gray-100 p-5 text-center cursor-pointer hover:shadow-lg hover:border-blue-200 transition-all group"
        >
          <div class="w-14 h-14 bg-gradient-to-br from-gray-50 to-gray-100 rounded-xl mx-auto mb-3 flex items-center justify-center text-2xl group-hover:from-amber-50 group-hover:to-amber-100 transition-colors">
            {{ comp.icon }}
          </div>
          <h3 class="font-medium text-gray-900 mb-1">{{ comp.name }}</h3>
          <span class="text-xs px-2 py-0.5 bg-amber-100 text-amber-600 rounded">官方</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'

const tabs = ['全部', '基础', '业务', '官方']
const activeTab = ref('全部')
const searchQuery = ref('')
const categoryFilter = ref('')

const basicComponents = [
  { id: 1, name: '按钮', icon: '🔘', category: '表单' },
  { id: 2, name: '输入框', icon: '📝', category: '表单' },
  { id: 3, name: '选择器', icon: '📋', category: '表单' },
  { id: 4, name: '表格', icon: '📊', category: '展示' },
  { id: 5, name: '开关', icon: '🔲', category: '表单' },
  { id: 6, name: '滑块', icon: '🎚️', category: '表单' }
]

const businessComponents = [
  { id: 7, name: '卡片', icon: '🃏', category: '展示' },
  { id: 8, name: '轮播', icon: '🎠', category: '展示' },
  { id: 9, name: '表单', icon: '📄', category: '表单' },
  { id: 10, name: '列表', icon: '📑', category: '展示' }
]

const officialComponents = [
  { id: 11, name: '图表', icon: '📈', category: '展示' },
  { id: 12, name: '富文本', icon: '📜', category: '编辑' }
]

function filterComponents(components: typeof basicComponents) {
  return components.filter(c => {
    if (searchQuery.value && !c.name.includes(searchQuery.value)) return false
    if (categoryFilter.value && c.category !== categoryFilter.value) return false
    return true
  })
}

const filteredBasicComponents = computed(() => filterComponents(basicComponents))
const filteredBusinessComponents = computed(() => filterComponents(businessComponents))
const filteredOfficialComponents = computed(() => filterComponents(officialComponents))
</script>
