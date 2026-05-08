<template>
  <div class="h-full flex flex-col bg-gray-50">
    <!-- 顶部导航 -->
    <div class="h-14 bg-white border-b border-gray-200 px-4 flex items-center justify-between flex-shrink-0">
      <div>
        <h1 class="text-lg font-semibold text-gray-900">页面管理</h1>
      </div>
      <div class="flex gap-3">
        <router-link
          to="/generate"
          class="px-4 py-2 border border-gray-200 rounded-lg hover:bg-gray-50 text-sm flex items-center gap-2"
        >
          🤖 AI生成
        </router-link>
        <button class="px-4 py-2 bg-blue-600 text-white text-sm rounded-lg hover:bg-blue-700">
          + 新建页面
        </button>
      </div>
    </div>

    <!-- 工具栏 -->
    <div class="bg-white border-b border-gray-200 px-4 py-3 flex items-center gap-4">
      <!-- 搜索 -->
      <div class="relative flex-1 max-w-md">
        <input
          v-model="searchKeyword"
          type="text"
          placeholder="搜索页面..."
          class="w-full pl-10 pr-4 py-2 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
        />
        <svg class="absolute left-3 top-2.5 w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"/>
        </svg>
      </div>

      <!-- 项目筛选 -->
      <select
        v-model="filterProject"
        class="border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white"
      >
        <option :value="undefined">全部项目</option>
        <option v-for="p in projects" :key="p.id" :value="p.id">{{ p.name }}</option>
      </select>

      <!-- 状态筛选 -->
      <select
        v-model="filterStatus"
        class="border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white"
      >
        <option :value="undefined">全部状态</option>
        <option :value="1">已发布</option>
        <option :value="0">未发布</option>
      </select>

      <button
        @click="loadPages"
        class="px-3 py-2 border border-gray-200 rounded-lg text-sm text-gray-600 hover:bg-gray-50"
      >
        ↻ 刷新
      </button>
    </div>

    <!-- 页面列表 -->
    <div class="flex-1 overflow-auto p-4">
      <div v-if="loading" class="flex items-center justify-center h-64">
        <svg class="animate-spin h-8 w-8 text-blue-600" viewBox="0 0 24 24">
          <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"/>
          <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"/>
        </svg>
      </div>

      <div v-else-if="filteredPages.length === 0" class="flex flex-col items-center justify-center h-64 text-gray-400">
        <div class="text-5xl mb-3">📄</div>
        <div>暂无页面</div>
      </div>

      <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
        <div
          v-for="page in filteredPages"
          :key="page.id"
          class="bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden hover:shadow-md hover:border-blue-200 transition-all"
        >
          <!-- 预览区 -->
          <div class="h-32 bg-gradient-to-br from-gray-100 to-gray-50 flex items-center justify-center relative">
            <div class="text-gray-300 text-3xl">📄</div>
            <span
              :class="page.isPublished === 1 ? 'bg-emerald-500' : 'bg-amber-500'"
              class="absolute top-3 right-3 px-2 py-0.5 rounded-full text-white text-xs font-medium"
            >
              {{ page.isPublished === 1 ? '● 已发布' : '○ 草稿' }}
            </span>
          </div>

          <!-- 信息区 -->
          <div class="p-4">
            <h3 class="font-semibold text-gray-900 mb-2">{{ page.name }}</h3>
            <div class="flex items-center gap-3 text-sm text-gray-500 mb-3">
              <span class="px-2 py-0.5 bg-gray-100 rounded text-xs">{{ getPageTypeName(page.pageType) }}</span>
              <span class="text-xs">v{{ page.version || 1 }}</span>
            </div>

            <!-- 操作按钮 -->
            <div class="flex gap-2 pt-3 border-t border-gray-100">
              <button class="flex-1 px-3 py-2 text-sm bg-blue-50 text-blue-600 rounded-lg hover:bg-blue-100 transition-colors font-medium">
                编辑
              </button>
              <button
                @click="togglePublish(page)"
                class="flex-1 px-3 py-2 text-sm bg-emerald-50 text-emerald-600 rounded-lg hover:bg-emerald-100 transition-colors font-medium"
              >
                {{ page.isPublished === 1 ? '取消发布' : '发布' }}
              </button>
              <button
                @click="confirmDelete(page)"
                class="px-3 py-2 text-sm text-red-500 hover:bg-red-50 rounded-lg transition-colors"
              >
                🗑
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div v-if="totalPages > 1" class="bg-white border-t border-gray-200 px-4 py-3 flex items-center justify-between">
      <div class="text-sm text-gray-500">
        共 {{ total }} 个页面
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

    <!-- 删除确认弹窗 -->
    <div v-if="showDeleteModal" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50" @click.self="showDeleteModal = false">
      <div class="bg-white rounded-lg w-[400px] p-6">
        <div class="text-center">
          <div class="text-5xl mb-4">⚠️</div>
          <h3 class="text-lg font-medium mb-2">确认删除</h3>
          <p class="text-gray-500 mb-6">确定要删除页面 "{{ pageToDelete?.name }}" 吗？此操作不可恢复。</p>
          <div class="flex gap-3 justify-center">
            <button
              @click="showDeleteModal = false"
              class="px-4 py-2 border border-gray-200 rounded-lg text-sm hover:bg-gray-50"
            >
              取消
            </button>
            <button
              @click="deletePage"
              class="px-4 py-2 bg-red-600 text-white rounded-lg text-sm hover:bg-red-700"
            >
              删除
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { getPageList, deletePage as deletePageApi, publishPage, type Page } from '@/api/page'
import { getProjectList } from '@/api/project'

// 状态
const loading = ref(false)
const pages = ref<Page[]>([])
const projects = ref<any[]>([])
const searchKeyword = ref('')
const filterProject = ref<number | undefined>(undefined)
const filterStatus = ref<number | undefined>(undefined)
const currentPage = ref(1)
const pageSize = ref(20)

// 删除弹窗
const showDeleteModal = ref(false)
const pageToDelete = ref<Page | null>(null)

// 计算属性
const filteredPages = computed(() => {
  let result = pages.value

  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(p =>
      p.name.toLowerCase().includes(keyword) ||
      p.description?.toLowerCase().includes(keyword)
    )
  }

  if (filterProject.value !== undefined) {
    result = result.filter(p => p.projectId === filterProject.value)
  }

  if (filterStatus.value !== undefined) {
    result = result.filter(p => p.isPublished === filterStatus.value)
  }

  return result
})

const total = computed(() => filteredPages.value.length)
const totalPages = computed(() => Math.ceil(total.value / pageSize.value))

// 方法
const loadPages = async () => {
  loading.value = true
  try {
    const res: any = await getPageList()
    if (res.data) {
      pages.value = res.data
    }
  } catch (e) {
    console.error('加载页面失败', e)
  } finally {
    loading.value = false
  }
}

const loadProjects = async () => {
  try {
    const res: any = await getProjectList()
    if (res.data) {
      projects.value = res.data
    }
  } catch (e) {
    console.error('加载项目失败', e)
  }
}

const getPageTypeName = (type: number | undefined) => {
  switch (type) {
    case 1: return '首页'
    case 2: return '列表页'
    case 3: return '详情页'
    case 4: return '表单页'
    case 5: return '单页'
    default: return '-'
  }
}

const togglePublish = async (page: Page) => {
  try {
    if (page.isPublished === 1) {
      // 取消发布 - 目前后端只提供发布接口，暂时忽略
      await loadPages()
    } else {
      await publishPage(page.id)
      await loadPages()
    }
  } catch (e) {
    console.error('发布失败', e)
    alert('操作失败')
  }
}

const confirmDelete = (page: Page) => {
  pageToDelete.value = page
  showDeleteModal.value = true
}

const deletePage = async () => {
  if (!pageToDelete.value) return
  try {
    await deletePageApi(pageToDelete.value.id)
    showDeleteModal.value = false
    pageToDelete.value = null
    await loadPages()
  } catch (e) {
    console.error('删除页面失败', e)
    alert('删除失败')
  }
}

// 监听筛选变化
watch([searchKeyword, filterProject, filterStatus], () => {
  currentPage.value = 1
})

// 生命周期
onMounted(() => {
  loadPages()
  loadProjects()
})
</script>
