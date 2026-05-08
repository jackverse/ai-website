<template>
  <div class="p-6">
    <!-- 欢迎信息 -->
    <div class="mb-8">
      <h1 class="text-2xl font-bold text-gray-900">欢迎回来！管理员</h1>
      <p class="text-gray-500 mt-1">祝您有愉快的工作体验</p>
    </div>

    <!-- 统计卡片 -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
      <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-6 hover:shadow-md transition-shadow">
        <div class="flex items-center gap-4">
          <div class="w-14 h-14 bg-blue-50 rounded-xl flex items-center justify-center">
            <span class="text-2xl">📁</span>
          </div>
          <div>
            <div class="text-sm text-gray-500">项目</div>
            <div class="text-3xl font-bold text-gray-900">12</div>
          </div>
        </div>
        <div class="mt-4 pt-4 border-t border-gray-100">
          <router-link to="/project" class="text-sm text-blue-600 hover:text-blue-700 font-medium">去看看 →</router-link>
        </div>
      </div>

      <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-6 hover:shadow-md transition-shadow">
        <div class="flex items-center gap-4">
          <div class="w-14 h-14 bg-emerald-50 rounded-xl flex items-center justify-center">
            <span class="text-2xl">📄</span>
          </div>
          <div>
            <div class="text-sm text-gray-500">模板</div>
            <div class="text-3xl font-bold text-gray-900">48</div>
          </div>
        </div>
        <div class="mt-4 pt-4 border-t border-gray-100">
          <router-link to="/template" class="text-sm text-emerald-600 hover:text-emerald-700 font-medium">去看看 →</router-link>
        </div>
      </div>

      <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-6 hover:shadow-md transition-shadow">
        <div class="flex items-center gap-4">
          <div class="w-14 h-14 bg-purple-50 rounded-xl flex items-center justify-center">
            <span class="text-2xl">🤖</span>
          </div>
          <div>
            <div class="text-sm text-gray-500">AI生成</div>
            <div class="text-3xl font-bold text-gray-900">156</div>
          </div>
        </div>
        <div class="mt-4 pt-4 border-t border-gray-100">
          <router-link to="/generate" class="text-sm text-purple-600 hover:text-purple-700 font-medium">开始 →</router-link>
        </div>
      </div>

      <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-6 hover:shadow-md transition-shadow">
        <div class="flex items-center gap-4">
          <div class="w-14 h-14 bg-amber-50 rounded-xl flex items-center justify-center">
            <span class="text-2xl">📊</span>
          </div>
          <div>
            <div class="text-sm text-gray-500">配额使用</div>
            <div class="text-3xl font-bold text-gray-900">85%</div>
          </div>
        </div>
        <div class="mt-4 pt-4 border-t border-gray-100">
          <router-link to="/quota" class="text-sm text-amber-600 hover:text-amber-700 font-medium">查看 →</router-link>
        </div>
      </div>
    </div>

    <!-- 最近操作 -->
    <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-6 mb-6">
      <h2 class="text-lg font-semibold text-gray-900 mb-4">最近操作</h2>
      <div class="divide-y divide-gray-100">
        <div v-for="(op, index) in recentOperations" :key="index" class="flex items-center gap-4 py-3 first:pt-0 last:pb-0">
          <span class="text-gray-400 w-16">🕒 {{ op.time }}</span>
          <span class="flex-1 text-gray-700">{{ op.action }}</span>
          <span :class="op.status === '成功' ? 'text-emerald-600 bg-emerald-50' : 'text-red-600 bg-red-50'" class="px-2 py-1 rounded-full text-xs font-medium">
            {{ op.status }}
          </span>
          <span v-if="op.model" class="text-gray-400 text-sm bg-gray-50 px-2 py-1 rounded">{{ op.model }}</span>
        </div>
      </div>
    </div>

    <!-- 快速开始 -->
    <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
      <h2 class="text-lg font-semibold text-gray-900 mb-4">快速开始</h2>
      <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
        <router-link to="/page">
          <button class="w-full px-6 py-4 bg-blue-50 text-blue-600 rounded-xl hover:bg-blue-100 transition-colors font-medium text-left flex items-center gap-3">
            <span class="text-xl">+</span>
            <span>新建页面</span>
          </button>
        </router-link>
        <router-link to="/generate">
          <button class="w-full px-6 py-4 bg-purple-50 text-purple-600 rounded-xl hover:bg-purple-100 transition-colors font-medium text-left flex items-center gap-3">
            <span class="text-xl">🤖</span>
            <span>AI生成</span>
          </button>
        </router-link>
        <router-link to="/template">
          <button class="w-full px-6 py-4 bg-emerald-50 text-emerald-600 rounded-xl hover:bg-emerald-100 transition-colors font-medium text-left flex items-center gap-3">
            <span class="text-xl">📄</span>
            <span>从模板创建</span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
const recentOperations = [
  { time: '14:32', action: '生成页面"产品列表"', status: '成功', model: 'GPT-4' },
  { time: '14:15', action: '发布页面"关于我们"', status: '成功', model: '' },
  { time: '13:20', action: '创建项目"官网v2.0"', status: '成功', model: '' },
  { time: '11:55', action: '使用模板"企业官网"', status: '成功', model: '模板' }
]
</script>
