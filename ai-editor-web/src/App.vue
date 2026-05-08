<template>
  <div id="app" class="flex h-screen bg-gray-50">
    <!-- 侧边导航 -->
    <aside class="w-64 bg-white border-r border-gray-200 flex flex-col flex-shrink-0">
      <!-- Logo区 -->
      <div class="h-16 flex items-center px-5 border-b border-gray-100">
        <div class="w-9 h-9 bg-gradient-to-br from-blue-500 to-purple-600 rounded-xl flex items-center justify-center text-white font-bold text-lg mr-3">
          A
        </div>
        <div>
          <h1 class="text-base font-bold text-gray-900">AI 建站编辑器</h1>
          <p class="text-xs text-gray-400">v1.0.0</p>
        </div>
      </div>

      <!-- 导航菜单 -->
      <nav class="flex-1 overflow-y-auto py-4 px-3">
        <div v-for="section in menu" :key="section.title" class="mb-5">
          <div class="px-3 text-xs font-semibold text-gray-400 uppercase tracking-wider mb-2">{{ section.title }}</div>
          <router-link
            v-for="item in section.items"
            :key="item.path"
            :to="item.path"
            class="flex items-center gap-3 px-3 py-2.5 rounded-lg text-gray-600 hover:bg-gray-50 hover:text-gray-900 transition-colors mb-1"
            active-class="bg-blue-50 text-blue-600 font-medium"
          >
            <span class="text-lg">{{ item.icon }}</span>
            <span class="text-sm">{{ item.name }}</span>
          </router-link>
        </div>
      </nav>

      <!-- 用户信息 -->
      <div class="p-4 border-t border-gray-100">
        <div class="flex items-center gap-3">
          <div class="w-10 h-10 bg-gradient-to-br from-gray-200 to-gray-300 rounded-full flex items-center justify-center text-gray-600">
            👤
          </div>
          <div class="flex-1 min-w-0">
            <div class="font-medium text-gray-900 text-sm truncate">管理员</div>
            <div class="text-xs text-gray-400 truncate">admin@example.com</div>
          </div>
          <button class="p-1.5 hover:bg-gray-100 rounded-lg transition-colors text-gray-400 hover:text-gray-600">
            ⚙️
          </button>
        </div>
      </div>
    </aside>

    <!-- 主内容区 -->
    <main class="flex-1 flex flex-col overflow-hidden">
      <!-- 顶部Header -->
      <header class="h-16 bg-white border-b border-gray-100 flex items-center justify-between px-6 flex-shrink-0">
        <div class="flex items-center gap-4">
          <h2 class="text-lg font-semibold text-gray-900">{{ currentTitle }}</h2>
        </div>
        <div class="flex items-center gap-3">
          <button class="relative p-2 hover:bg-gray-100 rounded-lg transition-colors">
            <span class="text-lg">🔔</span>
            <span class="absolute top-1 right-1 w-2 h-2 bg-red-500 rounded-full"></span>
          </button>
          <button class="p-2 hover:bg-gray-100 rounded-lg transition-colors">
            <span class="text-lg">🌐</span>
          </button>
        </div>
      </header>

      <!-- 内容区 -->
      <div class="flex-1 overflow-auto">
        <router-view />
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const menu = [
  {
    title: '主要',
    items: [
      { name: '首页', path: '/dashboard', icon: '🏠' },
      { name: '项目管理', path: '/project', icon: '📁' },
      { name: '模板库', path: '/template', icon: '📄' },
      { name: '页面管理', path: '/page', icon: '📑' },
      { name: 'AI生成', path: '/generate', icon: '🤖' }
    ]
  },
  {
    title: '组件',
    items: [
      { name: '组件管理', path: '/component', icon: '🧩' },
      { name: '分类标签', path: '/category', icon: '🏷️' }
    ]
  },
  {
    title: '配置',
    items: [
      { name: '成本控制', path: '/quota', icon: '💰' },
      { name: '提示词优化', path: '/prompt', icon: '📝' },
      { name: '模型配置', path: '/model', icon: '⚙️' }
    ]
  }
]

const currentTitle = computed(() => {
  return route.meta.title as string || 'AI 建站编辑器'
})
</script>

<style>
#app {
  width: 100%;
  height: 100vh;
}

/* 自定义滚动条 */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background: transparent;
}

::-webkit-scrollbar-thumb {
  background: #d1d5db;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: #9ca3af;
}
</style>
