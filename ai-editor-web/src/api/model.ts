import request from './request'

export interface AiModelConfig {
  id?: number
  tenantId?: number
  name: string
  displayName: string
  type: string
  endpoint: string
  apiKey: string
  model: string
  timeout?: number
  isDefault: number
  isEnabled: number
  sort?: number
  configJson?: string
  createdBy?: number
  createdAt?: string
  updatedAt?: string
}

export function getModelList() {
  return request({
    url: '/ai/model/list',
    method: 'get'
  })
}

export function getProviders() {
  return request({
    url: '/ai/model/providers',
    method: 'get'
  })
}

export function getDefaultModel() {
  return request({
    url: '/ai/model/default',
    method: 'get'
  })
}

export function getModelById(id: number) {
  return request({
    url: `/ai/model/${id}`,
    method: 'get'
  })
}

export function createModel(data: AiModelConfig) {
  return request({
    url: '/ai/model',
    method: 'post',
    data
  })
}

export function updateModel(id: number, data: AiModelConfig) {
  return request({
    url: `/ai/model/${id}`,
    method: 'put',
    data
  })
}

export function deleteModel(id: number) {
  return request({
    url: `/ai/model/${id}`,
    method: 'delete'
  })
}

export function setDefaultModel(id: number) {
  return request({
    url: `/ai/model/${id}/default`,
    method: 'post'
  })
}

export function setModelEnabled(id: number, enabled: boolean) {
  return request({
    url: `/ai/model/${id}/enabled`,
    method: 'post',
    params: { enabled }
  })
}

export function testModelConnection(id: number) {
  return request({
    url: `/ai/model/${id}/test`,
    method: 'post'
  })
}

export function chatWithModel(id: number, message: string, signal?: AbortSignal) {
  return request({
    url: `/ai/model/${id}/chat`,
    method: 'post',
    data: { message },
    signal
  })
}
