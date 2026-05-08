import request from './request'

export function getPromptLogs() {
  return request({
    url: '/ai/prompt/logs',
    method: 'get'
  })
}

export function getPromptStats() {
  return request({
    url: '/ai/prompt/stats',
    method: 'get'
  })
}
