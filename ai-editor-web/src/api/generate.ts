import request from './request'

export interface GenerateParams {
  prompt: string
  pageType: number
  style?: string
  platform?: string
  referenceImage?: string
  modelId?: number
}

export interface GenerateResult {
  success: boolean
  code: string
  model: string
  error?: string
}

export interface GenerateHistory {
  id: number
  prompt: string
  pageType: number
  style: string
  platform: string
  generatedCode: string
  status: number
  errorMsg?: string
  duration?: number
  createdAt: string
}

/**
 * 生成页面
 */
export function generatePage(data: GenerateParams) {
  return request({
    url: '/ai/generate/page',
    method: 'post',
    data
  })
}

/**
 * 重新生成
 */
export function regenerate(logId: number, additionalPrompt?: string) {
  return request({
    url: '/ai/generate/regenerate',
    method: 'post',
    data: { logId, additionalPrompt }
  })
}

/**
 * 获取生成历史
 */
export function getGenerateHistory(limit?: number) {
  return request({
    url: '/ai/generate/history',
    method: 'get',
    params: { limit }
  })
}
