import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/dashboard'
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('@/views/Dashboard.vue'),
    meta: { title: '首页' }
  },
  {
    path: '/project',
    name: 'Project',
    component: () => import('@/views/Project.vue'),
    meta: { title: '项目管理' }
  },
  {
    path: '/project/:id/version',
    name: 'ProjectVersion',
    component: () => import('@/views/ProjectVersion.vue'),
    meta: { title: '项目版本' }
  },
  {
    path: '/template',
    name: 'Template',
    component: () => import('@/views/Template.vue'),
    meta: { title: '模板库' }
  },
  {
    path: '/page',
    name: 'Page',
    component: () => import('@/views/Page.vue'),
    meta: { title: '页面管理' }
  },
  {
    path: '/generate',
    name: 'Generate',
    component: () => import('@/views/Generate.vue'),
    meta: { title: 'AI生成' }
  },
  {
    path: '/component',
    name: 'Component',
    component: () => import('@/views/Component.vue'),
    meta: { title: '组件管理' }
  },
  {
    path: '/category',
    name: 'Category',
    component: () => import('@/views/Category.vue'),
    meta: { title: '分类标签' }
  },
  {
    path: '/quota',
    name: 'Quota',
    component: () => import('@/views/Quota.vue'),
    meta: { title: '成本控制' }
  },
  {
    path: '/prompt',
    name: 'Prompt',
    component: () => import('@/views/Prompt.vue'),
    meta: { title: '提示词优化' }
  },
  {
    path: '/model',
    name: 'Model',
    component: () => import('@/views/Model.vue'),
    meta: { title: '模型配置' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
