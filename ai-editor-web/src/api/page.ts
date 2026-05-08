import request from './request'

export interface Page {
  id: number
  tenantId: number
  projectId?: number
  versionId?: number
  templateId?: number
  name: string
  description?: string
  code: string
  structure?: string
  pageType: number
  routePath?: string
  links?: string
  isPublished: number
  version: number
  createdBy: number
  createdAt: string
  updatedAt: string
}

/**
 * 获取页面列表
 */
export function getPageList(projectId?: number) {
  return request({
    url: '/ai/page/list',
    method: 'get',
    params: projectId !== undefined ? { projectId } : {}
  })
}

/**
 * 获取页面详情
 */
export function getPage(id: number) {
  return request({
    url: `/ai/page/${id}`,
    method: 'get'
  })
}

/**
 * 创建页面
 */
export function createPage(data: Partial<Page>) {
  return request({
    url: '/ai/page',
    method: 'post',
    data
  })
}

/**
 * 更新页面
 */
export function updatePage(data: Partial<Page>) {
  return request({
    url: '/ai/page',
    method: 'put',
    data
  })
}

/**
 * 删除页面
 */
export function deletePage(id: number) {
  return request({
    url: `/ai/page/${id}`,
    method: 'delete'
  })
}

/**
 * 发布页面
 */
export function publishPage(id: number) {
  return request({
    url: `/ai/page/${id}/publish`,
    method: 'put'
  })
}
