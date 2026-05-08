<template>
  <div class="h-full flex flex-col bg-gray-50">
    <!-- 顶部导航 -->
    <div class="h-14 bg-white border-b border-gray-200 px-4 flex items-center gap-4 flex-shrink-0">
      <button @click="$router.back()" class="text-blue-500 hover:text-blue-600">← 返回</button>
      <h1 class="text-lg font-semibold">{{ project?.name || '加载中...' }} - 版本管理</h1>
    </div>

    <div class="flex-1 flex overflow-hidden">
      <!-- 左侧：版本列表 -->
      <div class="w-72 bg-white border-r border-gray-200 flex flex-col">
        <div class="p-4 border-b border-gray-200 flex items-center justify-between">
          <h2 class="font-semibold">版本列表</h2>
          <button
            @click="showCreateVersionModal = true"
            class="text-sm text-blue-500 hover:text-blue-600"
          >
            + 新建版本
          </button>
        </div>
        <div class="flex-1 overflow-y-auto p-2">
          <div v-if="loadingVersions" class="flex items-center justify-center py-8">
            <svg class="animate-spin h-6 w-6 text-blue-600" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"/>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"/>
            </svg>
          </div>
          <div v-else class="space-y-2">
            <div
              v-for="version in versions"
              :key="version.id"
              @click="selectVersion(version)"
              :class="selectedVersion?.id === version.id ? 'bg-blue-50 border-blue-500' : 'hover:bg-gray-50 border-transparent'"
              class="p-3 rounded-lg border cursor-pointer transition"
            >
              <div class="flex justify-between items-center">
                <span class="font-medium">{{ version.version }}</span>
                <span v-if="version.isCurrent === 1" class="text-xs bg-green-100 text-green-700 px-2 py-0.5 rounded">当前</span>
              </div>
              <div class="text-sm text-gray-500 mt-1">{{ formatDate(version.createdAt) }}</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧：版本详情 -->
      <div class="flex-1 overflow-auto">
        <div v-if="selectedVersion" class="p-6">
          <div class="bg-white rounded-lg shadow-sm border border-gray-200">
            <div class="p-6 border-b">
              <div class="flex justify-between items-start">
                <div>
                  <h2 class="text-xl font-bold">{{ selectedVersion.version }}</h2>
                  <p class="text-gray-500 mt-1">创建于 {{ formatDate(selectedVersion.createdAt) }} · {{ selectedVersion.pageCount || 0 }} 个页面</p>
                </div>
                <div class="flex gap-2">
                  <button
                    v-if="selectedVersion.isCurrent !== 1"
                    @click="handleSwitchVersion"
                    class="px-4 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 text-sm"
                  >
                    设为当前版本
                  </button>
                  <button
                    @click="confirmDeleteVersion"
                    class="px-4 py-2 border border-red-300 text-red-500 rounded-lg hover:bg-red-50 text-sm"
                  >
                    删除
                  </button>
                </div>
              </div>
            </div>

            <!-- 版本信息 -->
            <div class="p-6 border-b">
              <h3 class="font-semibold mb-4">版本信息</h3>
              <div class="grid grid-cols-2 gap-4">
                <div>
                  <label class="text-gray-500 text-sm">版本号</label>
                  <div class="mt-1">{{ selectedVersion.version }}</div>
                </div>
                <div>
                  <label class="text-gray-500 text-sm">状态</label>
                  <div class="mt-1">
                    <span :class="selectedVersion.isCurrent === 1 ? 'text-green-500' : 'text-gray-400'" class="inline-flex items-center">
                      {{ selectedVersion.isCurrent === 1 ? '● 当前版本' : '○ 历史版本' }}
                    </span>
                  </div>
                </div>
                <div>
                  <label class="text-gray-500 text-sm">页面数</label>
                  <div class="mt-1">{{ selectedVersion.pageCount || 0 }} 个</div>
                </div>
                <div>
                  <label class="text-gray-500 text-sm">创建时间</label>
                  <div class="mt-1">{{ formatDate(selectedVersion.createdAt) }}</div>
                </div>
              </div>
              <div v-if="selectedVersion.description" class="mt-4">
                <label class="text-gray-500 text-sm">描述</label>
                <div class="mt-1">{{ selectedVersion.description }}</div>
              </div>
            </div>

            <!-- 页面列表 -->
            <div class="p-6">
              <div class="flex justify-between items-center mb-4">
                <h3 class="font-semibold">页面列表</h3>
                <button class="px-3 py-1.5 text-sm bg-blue-50 text-blue-600 rounded-lg hover:bg-blue-100">+ 添加页面</button>
              </div>
              <div v-if="pages.length === 0" class="text-center py-8 text-gray-400">
                暂无页面
              </div>
              <table v-else class="w-full">
                <thead class="bg-gray-50">
                  <tr>
                    <th class="px-4 py-2 text-left text-sm">页面名称</th>
                    <th class="px-4 py-2 text-left text-sm">类型</th>
                    <th class="px-4 py-2 text-left text-sm">状态</th>
                    <th class="px-4 py-2 text-left text-sm">操作</th>
                  </tr>
                </thead>
                <tbody class="divide-y">
                  <tr v-for="page in pages" :key="page.id" class="hover:bg-gray-50">
                    <td class="px-4 py-3">{{ page.name }}</td>
                    <td class="px-4 py-3 text-gray-500">{{ getPageTypeName(page.pageType) }}</td>
                    <td class="px-4 py-3">
                      <span :class="page.isPublished === 1 ? 'text-green-500' : 'text-gray-400'" class="text-sm">
                        {{ page.isPublished === 1 ? '● 已发布' : '○ 未发布' }}
                      </span>
                    </td>
                    <td class="px-4 py-3">
                      <button class="text-blue-500 hover:underline mr-2 text-sm">编辑</button>
                      <button class="text-red-500 hover:underline text-sm">移除</button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>

        <!-- 无选中版本 -->
        <div v-else class="flex items-center justify-center h-full text-gray-500">
          请选择一个版本查看详情
        </div>
      </div>
    </div>

    <!-- 新建版本弹窗 -->
    <div v-if="showCreateVersionModal" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50" @click.self="showCreateVersionModal = false">
      <div class="bg-white rounded-lg w-[450px]">
        <div class="px-4 py-3 border-b flex items-center justify-between">
          <h3 class="font-medium">新建版本</h3>
          <button @click="showCreateVersionModal = false" class="text-gray-400 hover:text-gray-600">×</button>
        </div>
        <div class="p-4 space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">版本号</label>
            <input
              v-model="newVersion.version"
              type="text"
              class="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="如: v2.0"
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">版本描述</label>
            <textarea
              v-model="newVersion.description"
              rows="3"
              class="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 resize-none"
              placeholder="输入版本描述"
            ></textarea>
          </div>
        </div>
        <div class="px-4 py-3 border-t flex justify-end gap-3">
          <button
            @click="showCreateVersionModal = false"
            class="px-4 py-2 border border-gray-200 rounded-lg text-sm hover:bg-gray-50"
          >
            取消
          </button>
          <button
            @click="createVersion"
            class="px-4 py-2 bg-blue-600 text-white rounded-lg text-sm hover:bg-blue-700"
          >
            创建
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
          <p class="text-gray-500 mb-6">确定要删除版本 "{{ selectedVersion?.version }}" 吗？此操作不可恢复。</p>
          <div class="flex gap-3 justify-center">
            <button
              @click="showDeleteModal = false"
              class="px-4 py-2 border border-gray-200 rounded-lg text-sm hover:bg-gray-50"
            >
              取消
            </button>
            <button
              @click="deleteVersion"
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
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getProject } from '@/api/project'
import {
  getProjectVersionList,
  createProjectVersion,
  switchProjectVersion,
  deleteProjectVersion,
  type ProjectVersion
} from '@/api/project'
import { getPageList, type Page } from '@/api/page'

const route = useRoute()
const router = useRouter()

const projectId = Number(route.params.id)

// 状态
const loadingVersions = ref(false)
const project = ref<any>(null)
const versions = ref<ProjectVersion[]>([])
const selectedVersion = ref<ProjectVersion | null>(null)
const pages = ref<Page[]>([])

// 弹窗状态
const showCreateVersionModal = ref(false)
const showDeleteModal = ref(false)
const newVersion = ref({
  version: '',
  description: ''
})

// 方法
const loadProject = async () => {
  try {
    const res: any = await getProject(projectId)
    if (res.data) {
      project.value = res.data
    }
  } catch (e) {
    console.error('加载项目失败', e)
  }
}

const loadVersions = async () => {
  loadingVersions.value = true
  try {
    const res: any = await getProjectVersionList(projectId)
    if (res.data) {
      versions.value = res.data
      // 默认选中当前版本
      if (versions.value.length > 0 && !selectedVersion.value) {
        const current = versions.value.find(v => v.isCurrent === 1)
        selectedVersion.value = current || versions.value[0]
      }
    }
  } catch (e) {
    console.error('加载版本失败', e)
  } finally {
    loadingVersions.value = false
  }
}

const loadPages = async (versionId: number) => {
  try {
    const res: any = await getPageList()
    if (res.data) {
      // 筛选当前版本的页面
      pages.value = res.data.filter((p: Page) => p.versionId === versionId)
    }
  } catch (e) {
    console.error('加载页面失败', e)
  }
}

const selectVersion = (version: ProjectVersion) => {
  selectedVersion.value = version
  loadPages(version.id)
}

const formatDate = (date: string | Date | undefined) => {
  if (!date) return '-'
  const d = new Date(date)
  return d.toLocaleDateString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit' })
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

const createVersion = async () => {
  if (!newVersion.value.version) {
    alert('请填写版本号')
    return
  }

  try {
    await createProjectVersion(projectId, {
      version: newVersion.value.version,
      description: newVersion.value.description
    })
    showCreateVersionModal.value = false
    newVersion.value = { version: '', description: '' }
    await loadVersions()
  } catch (e) {
    console.error('创建版本失败', e)
    alert('创建失败')
  }
}

const handleSwitchVersion = async () => {
  if (!selectedVersion.value) return
  try {
    await switchProjectVersion(projectId, selectedVersion.value.id)
    await loadVersions()
  } catch (e) {
    console.error('切换版本失败', e)
    alert('切换失败')
  }
}

const confirmDeleteVersion = () => {
  if (versions.value.length <= 1) {
    alert('至少需要保留一个版本')
    return
  }
  showDeleteModal.value = true
}

const deleteVersion = async () => {
  if (!selectedVersion.value) return
  try {
    await deleteProjectVersion(projectId, selectedVersion.value.id)
    showDeleteModal.value = false
    selectedVersion.value = null
    await loadVersions()
  } catch (e) {
    console.error('删除版本失败', e)
    alert('删除失败')
  }
}

// 监听版本选择
watch(selectedVersion, (newVal) => {
  if (newVal) {
    loadPages(newVal.id)
  }
})

// 生命周期
onMounted(() => {
  loadProject()
  loadVersions()
})
</script>
