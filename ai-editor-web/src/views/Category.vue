<template>
  <div class="p-6">
    <div class="flex gap-6">
      <!-- 分类管理 -->
      <div class="flex-1">
        <div class="flex justify-between items-center mb-6">
          <div>
            <h1 class="text-xl font-bold text-gray-900">分类管理</h1>
            <p class="text-gray-500 text-sm mt-1">管理页面分类</p>
          </div>
          <button @click="showCategoryModal = true" class="px-4 py-2.5 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors font-medium flex items-center gap-2 shadow-sm">
            <span class="text-lg">+</span> 新建分类
          </button>
        </div>

        <div class="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
          <table class="w-full">
            <thead class="bg-gray-50">
              <tr>
                <th class="px-5 py-3.5 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">分类名称</th>
                <th class="px-5 py-3.5 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">编码</th>
                <th class="px-5 py-3.5 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">排序</th>
                <th class="px-5 py-3.5 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">状态</th>
                <th class="px-5 py-3.5 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">操作</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-100">
              <tr v-for="cat in categories" :key="cat.id" class="hover:bg-gray-50 transition-colors">
                <td class="px-5 py-4 font-medium text-gray-900">{{ cat.name }}</td>
                <td class="px-5 py-4 text-gray-500">{{ cat.code }}</td>
                <td class="px-5 py-4 text-gray-500">{{ cat.sort }}</td>
                <td class="px-5 py-4">
                  <span :class="cat.status === 'enabled' ? 'bg-emerald-100 text-emerald-700' : 'bg-gray-100 text-gray-500'" class="px-2.5 py-1 rounded-full text-xs font-medium">
                    {{ cat.status === 'enabled' ? '● 启用' : '○ 禁用' }}
                  </span>
                </td>
                <td class="px-5 py-4">
                  <button class="text-blue-600 hover:text-blue-700 font-medium text-sm mr-3">编辑</button>
                  <button class="text-red-500 hover:text-red-600 font-medium text-sm">删除</button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- 标签管理 -->
      <div class="flex-1">
        <div class="flex justify-between items-center mb-6">
          <div>
            <h1 class="text-xl font-bold text-gray-900">标签管理</h1>
            <p class="text-gray-500 text-sm mt-1">管理页面标签</p>
          </div>
          <button @click="showTagModal = true" class="px-4 py-2.5 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors font-medium flex items-center gap-2 shadow-sm">
            <span class="text-lg">+</span> 新建标签
          </button>
        </div>

        <!-- 标签搜索和展示 -->
        <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-5 mb-6">
          <input v-model="tagSearch" type="text" placeholder="搜索标签..." class="w-full border border-gray-200 rounded-lg px-4 py-2.5 focus:outline-none focus:ring-2 focus:ring-blue-500 mb-4" />
          <div class="flex flex-wrap gap-2">
            <span
              v-for="tag in filteredTags"
              :key="tag.id"
              class="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-full text-sm font-medium"
              :style="{ backgroundColor: tag.color + '20', color: tag.color }"
            >
              {{ tag.name }}
              <span class="text-xs opacity-60">({{ tag.usageCount }})</span>
              <button class="ml-1 hover:text-red-500 transition-colors">×</button>
            </span>
          </div>
        </div>

        <!-- 标签列表 -->
        <div class="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
          <table class="w-full">
            <thead class="bg-gray-50">
              <tr>
                <th class="px-5 py-3.5 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">标签名称</th>
                <th class="px-5 py-3.5 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">颜色</th>
                <th class="px-5 py-3.5 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">使用次数</th>
                <th class="px-5 py-3.5 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">操作</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-100">
              <tr v-for="tag in filteredTags" :key="tag.id" class="hover:bg-gray-50 transition-colors">
                <td class="px-5 py-4">
                  <span class="inline-flex items-center gap-2">
                    <span class="w-4 h-4 rounded" :style="{ backgroundColor: tag.color }"></span>
                    <span class="font-medium text-gray-900">{{ tag.name }}</span>
                  </span>
                </td>
                <td class="px-5 py-4 text-gray-500 font-mono text-sm">{{ tag.color }}</td>
                <td class="px-5 py-4 text-gray-500">{{ tag.usageCount }}</td>
                <td class="px-5 py-4">
                  <button class="text-blue-600 hover:text-blue-700 font-medium text-sm mr-3">编辑</button>
                  <button class="text-red-500 hover:text-red-600 font-medium text-sm">删除</button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- 新建分类弹窗 -->
    <div v-if="showCategoryModal" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
      <div class="bg-white rounded-2xl shadow-xl w-96">
        <div class="p-6 border-b border-gray-100">
          <h2 class="text-lg font-bold text-gray-900">新建分类</h2>
        </div>
        <div class="p-6 space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1.5">分类名称</label>
            <input v-model="newCategory.name" type="text" class="w-full border border-gray-200 rounded-lg px-4 py-2.5 focus:outline-none focus:ring-2 focus:ring-blue-500" />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1.5">编码</label>
            <input v-model="newCategory.code" type="text" class="w-full border border-gray-200 rounded-lg px-4 py-2.5 focus:outline-none focus:ring-2 focus:ring-blue-500" />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1.5">排序</label>
            <input v-model="newCategory.sort" type="number" class="w-full border border-gray-200 rounded-lg px-4 py-2.5 focus:outline-none focus:ring-2 focus:ring-blue-500" />
          </div>
        </div>
        <div class="p-6 border-t border-gray-100 flex justify-end gap-3">
          <button @click="showCategoryModal = false" class="px-4 py-2.5 border border-gray-200 rounded-lg hover:bg-gray-50 transition-colors font-medium">取消</button>
          <button @click="addCategory" class="px-4 py-2.5 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors font-medium">确定</button>
        </div>
      </div>
    </div>

    <!-- 新建标签弹窗 -->
    <div v-if="showTagModal" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
      <div class="bg-white rounded-2xl shadow-xl w-96">
        <div class="p-6 border-b border-gray-100">
          <h2 class="text-lg font-bold text-gray-900">新建标签</h2>
        </div>
        <div class="p-6 space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1.5">标签名称</label>
            <input v-model="newTag.name" type="text" class="w-full border border-gray-200 rounded-lg px-4 py-2.5 focus:outline-none focus:ring-2 focus:ring-blue-500" />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1.5">颜色</label>
            <div class="flex gap-3 items-center">
              <input v-model="newTag.color" type="color" class="w-12 h-12 border border-gray-200 rounded-lg cursor-pointer" />
              <input v-model="newTag.color" type="text" class="flex-1 border border-gray-200 rounded-lg px-4 py-2.5 focus:outline-none focus:ring-2 focus:ring-blue-500 font-mono" />
            </div>
          </div>
        </div>
        <div class="p-6 border-t border-gray-100 flex justify-end gap-3">
          <button @click="showTagModal = false" class="px-4 py-2.5 border border-gray-200 rounded-lg hover:bg-gray-50 transition-colors font-medium">取消</button>
          <button @click="addTag" class="px-4 py-2.5 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors font-medium">确定</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'

const showCategoryModal = ref(false)
const showTagModal = ref(false)
const tagSearch = ref('')

const newCategory = ref({ name: '', code: '', sort: 0 })
const newTag = ref({ name: '', color: '#3B82F6' })

const categories = ref([
  { id: 1, name: '首页', code: 'home', sort: 1, status: 'enabled' },
  { id: 2, name: '列表页', code: 'list', sort: 2, status: 'enabled' },
  { id: 3, name: '详情页', code: 'detail', sort: 3, status: 'enabled' },
  { id: 4, name: '单页', code: 'single', sort: 4, status: 'disabled' }
])

const tags = ref([
  { id: 1, name: '营销', color: '#EF4444', usageCount: 128 },
  { id: 2, name: '首页', color: '#3B82F6', usageCount: 86 },
  { id: 3, name: '列表', color: '#10B981', usageCount: 64 },
  { id: 4, name: '导航', color: '#F59E0B', usageCount: 42 },
  { id: 5, name: '产品', color: '#8B5CF6', usageCount: 38 }
])

const filteredTags = computed(() => {
  if (!tagSearch.value) return tags.value
  return tags.value.filter(t => t.name.includes(tagSearch.value))
})

function addCategory() {
  categories.value.push({
    id: Date.now(),
    ...newCategory.value,
    status: 'enabled'
  })
  showCategoryModal.value = false
  newCategory.value = { name: '', code: '', sort: 0 }
}

function addTag() {
  tags.value.push({
    id: Date.now(),
    ...newTag.value,
    usageCount: 0
  })
  showTagModal.value = false
  newTag.value = { name: '', color: '#3B82F6' }
}
</script>
