<template>
  <div class="h-full flex flex-col bg-gray-50">
    <!-- 顶部导航 -->
    <div class="h-14 bg-white border-b border-gray-200 px-4 flex items-center justify-between flex-shrink-0">
      <div>
        <h1 class="text-lg font-semibold text-gray-900">项目管理</h1>
      </div>
      <button
        @click="showCreateModal = true"
        class="px-4 py-2 bg-blue-600 text-white text-sm rounded-lg hover:bg-blue-700 transition-colors flex items-center gap-2"
      >
        <span class="text-lg">+</span>
        新建项目
      </button>
    </div>

    <!-- 工具栏 -->
    <div class="bg-white border-b border-gray-200 px-4 py-3 flex items-center gap-4">
      <!-- 搜索 -->
      <div class="relative flex-1 max-w-md">
        <input
          v-model="searchKeyword"
          type="text"
          placeholder="搜索项目..."
          class="w-full pl-10 pr-4 py-2 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
        />
        <svg class="absolute left-3 top-2.5 w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"/>
        </svg>
      </div>

      <!-- 状态筛选 -->
      <select
        v-model="statusFilter"
        class="border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white"
      >
        <option :value="undefined">全部状态</option>
        <option :value="1">启用</option>
        <option :value="0">禁用</option>
      </select>

      <button
        @click="loadProjects"
        class="px-3 py-2 border border-gray-200 rounded-lg text-sm text-gray-600 hover:bg-gray-50"
      >
        ↻ 刷新
      </button>
    </div>

    <!-- 项目列表 -->
    <div class="flex-1 overflow-auto p-4">
      <div v-if="loading" class="flex items-center justify-center h-64">
        <svg class="animate-spin h-8 w-8 text-blue-600" viewBox="0 0 24 24">
          <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"/>
          <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"/>
        </svg>
      </div>

      <div v-else-if="filteredProjects.length === 0" class="flex flex-col items-center justify-center h-64 text-gray-400">
        <div class="text-5xl mb-3">📂</div>
        <div>暂无项目</div>
        <button @click="showCreateModal = true" class="mt-4 text-blue-500 hover:text-blue-600">创建第一个项目</button>
      </div>

      <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        <div
          v-for="project in filteredProjects"
          :key="project.id"
          class="bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden hover:shadow-md transition-all hover:border-blue-200"
        >
          <!-- 卡片头部 -->
          <div class="h-24 bg-gradient-to-br from-blue-500 to-purple-600 flex items-center justify-center relative">
            <span class="text-white text-4xl opacity-80">📁</span>
            <span
              :class="project.status === 1 ? 'bg-emerald-500' : 'bg-gray-400'"
              class="absolute top-3 right-3 px-2.5 py-1 rounded-full text-white text-xs font-medium"
            >
              {{ project.status === 1 ? '● 启用' : '○ 禁用' }}
            </span>
          </div>

          <!-- 卡片内容 -->
          <div class="p-4">
            <h3 class="font-bold text-gray-900 mb-1">{{ project.name }}</h3>
            <p class="text-gray-500 text-sm mb-3 line-clamp-2">{{ project.description || '暂无描述' }}</p>

            <div class="flex items-center gap-4 text-sm text-gray-500 mb-3">
              <span class="flex items-center gap-1">
                <span>📌</span> {{ project.currentVersion || 'v1.0' }}
              </span>
            </div>

            <div class="text-xs text-gray-400 mb-4">
              创建于 {{ formatDate(project.createdAt) }}
            </div>

            <div class="flex gap-2 pt-3 border-t border-gray-100">
              <router-link
                :to="`/project/${project.id}/version`"
                class="flex-1 px-3 py-2 bg-blue-50 text-blue-600 rounded-lg hover:bg-blue-100 transition-colors text-center text-sm font-medium"
              >
                版本管理
              </router-link>
              <button
                @click="editProject(project)"
                class="px-3 py-2 bg-gray-100 text-gray-600 rounded-lg hover:bg-gray-200 transition-colors text-sm"
              >
                编辑
              </button>
              <button
                @click="confirmDelete(project)"
                class="px-3 py-2 bg-gray-100 text-red-500 rounded-lg hover:bg-red-50 transition-colors text-sm"
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
        共 {{ total }} 个项目
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

    <!-- 创建/编辑项目弹窗 -->
    <div v-if="showCreateModal" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50" @click.self="closeModal">
      <div class="bg-white rounded-lg w-[500px] max-h-[90vh] flex flex-col">
        <div class="px-4 py-3 border-b border-gray-200 flex items-center justify-between">
          <h3 class="font-medium">{{ editingProject ? '编辑项目' : '创建项目' }}</h3>
          <button @click="closeModal" class="text-gray-400 hover:text-gray-600">×</button>
        </div>
        <div class="flex-1 overflow-y-auto p-4 space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">项目名称</label>
            <input
              v-model="formData.name"
              type="text"
              class="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="输入项目名称"
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">项目描述</label>
            <textarea
              v-model="formData.description"
              rows="3"
              class="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 resize-none"
              placeholder="输入项目描述"
            ></textarea>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">项目图标</label>
            <input
              v-model="formData.icon"
              type="text"
              class="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="输入图标 URL 或 emoji"
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">状态</label>
            <select
              v-model="formData.status"
              class="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white"
            >
              <option :value="1">启用</option>
              <option :value="0">禁用</option>
            </select>
          </div>
        </div>
        <div class="px-4 py-3 border-t border-gray-200 flex justify-end gap-3">
          <button
            @click="closeModal"
            class="px-4 py-2 border border-gray-200 rounded-lg text-sm hover:bg-gray-50"
          >
            取消
          </button>
          <button
            @click="saveProject"
            class="px-4 py-2 bg-blue-600 text-white rounded-lg text-sm hover:bg-blue-700"
          >
            保存
          </button>
        </div>
      </div>
    </div>

    <!-- 删除确认弹窗 -->
    <div v-if="showDeleteModal" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50" @click.self="showDeleteModal = false">
      <div class="bg-white rounded-lg w-[400px] p-6">
        <div class="text-center">
          <div class="text-5xl mb-4">⚠️</div>
          <h3 class="text-lg font-medium mb-2">确认删除</h3>
          <p class="text-gray-500 mb-6">确定要删除项目 "{{ projectToDelete?.name }}" 吗？此操作不可恢复。</p>
          <div class="flex gap-3 justify-center">
            <button
              @click="showDeleteModal = false"
              class="px-4 py-2 border border-gray-200 rounded-lg text-sm hover:bg-gray-50"
            >
              取消
            </button>
            <button
              @click="deleteProject"
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
import { ref, computed, onMounted } from 'vue'
import { getProjectList, createProject, updateProject, deleteProject as deleteProjectApi, type Project } from '@/api/project'

// 状态
const loading = ref(false)
const projects = ref<Project[]>([])
const searchKeyword = ref('')
const statusFilter = ref<number | undefined>(undefined)
const currentPage = ref(1)
const pageSize = ref(12)

// 弹窗状态
const showCreateModal = ref(false)
const showDeleteModal = ref(false)
const editingProject = ref<Project | null>(null)
const projectToDelete = ref<Project | null>(null)

// 表单数据
const formData = ref({
  name: '',
  description: '',
  icon: '',
  status: 1 as 0 | 1
})

// 计算属性
const filteredProjects = computed(() => {
  let result = projects.value

  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(p =>
      p.name.toLowerCase().includes(keyword) ||
      p.description?.toLowerCase().includes(keyword)
    )
  }

  if (statusFilter.value !== undefined) {
    result = result.filter(p => p.status === statusFilter.value)
  }

  return result
})

const total = computed(() => filteredProjects.value.length)
const totalPages = computed(() => Math.ceil(total.value / pageSize.value))

// 方法
const loadProjects = async () => {
  loading.value = true
  try {
    const res: any = await getProjectList()
    if (res.data) {
      projects.value = res.data
    }
  } catch (e) {
    console.error('加载项目失败', e)
  } finally {
    loading.value = false
  }
}

const formatDate = (date: string | Date | undefined) => {
  if (!date) return '-'
  const d = new Date(date)
  return d.toLocaleDateString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit' })
}

const editProject = (project: Project) => {
  editingProject.value = project
  formData.value = {
    name: project.name,
    description: project.description || '',
    icon: project.icon || '',
    status: project.status as 0 | 1
  }
  showCreateModal.value = true
}

const closeModal = () => {
  showCreateModal.value = false
  editingProject.value = null
  formData.value = {
    name: '',
    description: '',
    icon: '',
    status: 1
  }
}

const saveProject = async () => {
  if (!formData.value.name) {
    alert('请填写项目名称')
    return
  }

  try {
    if (editingProject.value) {
      await updateProject({ ...formData.value, id: editingProject.value.id })
    } else {
      await createProject(formData.value)
    }
    closeModal()
    await loadProjects()
  } catch (e) {
    console.error('保存项目失败', e)
    alert('保存失败')
  }
}

const confirmDelete = (project: Project) => {
  projectToDelete.value = project
  showDeleteModal.value = true
}

const deleteProject = async () => {
  if (!projectToDelete.value) return
  try {
    await deleteProjectApi(projectToDelete.value.id)
    showDeleteModal.value = false
    projectToDelete.value = null
    await loadProjects()
  } catch (e) {
    console.error('删除项目失败', e)
    alert('删除失败')
  }
}

// 生命周期
onMounted(() => {
  loadProjects()
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
