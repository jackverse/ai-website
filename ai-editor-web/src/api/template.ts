import request from './request'

export interface Template {
  id: number
  tenantId: number
  name: string
  description?: string
  type: number
  code: string
  structure?: string
  tags?: string
  isOfficial: number
  isPublished: number
  usageCount: number
  qualityScore: number
  createdBy: number
  createdAt: string
  updatedAt: string
}

/**
 * 获取模板列表
 */
export function getTemplateList(type?: number) {
  return request({
    url: '/ai/template/list',
    method: 'get',
    params: type !== undefined ? { type } : {}
  })
}

/**
 * 获取官方模板
 */
export function getOfficialTemplates() {
  return request({
    url: '/ai/template/official',
    method: 'get'
  })
}

/**
 * 获取模板详情
 */
export function getTemplate(id: number) {
  return request({
    url: `/ai/template/${id}`,
    method: 'get'
  })
}

/**
 * 创建模板
 */
export function createTemplate(data: Partial<Template>) {
  return request({
    url: '/ai/template',
    method: 'post',
    data
  })
}

/**
 * 更新模板
 */
export function updateTemplate(data: Partial<Template>) {
  return request({
    url: '/ai/template',
    method: 'put',
    data
  })
}

/**
 * 删除模板
 */
export function deleteTemplate(id: number) {
  return request({
    url: `/ai/template/${id}`,
    method: 'delete'
  })
}

/**
 * 从生成结果保存为模板
 */
export function saveTemplateFromGenerate(name: string, code: string) {
  return request({
    url: '/ai/template/save-from-generate',
    method: 'post',
    data: { name, code }
  })
}

/**
 * 使用模板
 */
export function useTemplate(id: number) {
  return request({
    url: `/ai/template/${id}/use`,
    method: 'post'
  })
}
