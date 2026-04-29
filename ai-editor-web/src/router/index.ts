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
    path: '/template',
    name: 'Template',
    component: () => import('@/views/Template.vue'),
    meta: { title: '模板库' }
  },
  {
    path: '/generate',
    name: 'Generate',
    component: () => import('@/views/Generate.vue'),
    meta: { title: 'AI生成' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
