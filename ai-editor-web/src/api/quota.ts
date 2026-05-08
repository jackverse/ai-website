import request from './request'

export function getQuotaStats() {
  return request({
    url: '/ai/quota/stats',
    method: 'get'
  })
}

export function updateDailyLimit(dailyLimit: number) {
  return request({
    url: '/ai/quota/daily-limit',
    method: 'post',
    data: { dailyLimit }
  })
}
