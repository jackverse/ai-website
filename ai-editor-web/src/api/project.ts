import request from './request'

export interface Project {
  id: number
  tenantId: number
  name: string
  description?: string
  icon?: string
  status: number
  currentVersion?: string
  createdBy: number
  createdAt: string
  updatedAt: string
}

/**
 * 获取项目列表
 */
export function getProjectList() {
  return request({
    url: '/ai/project/list',
    method: 'get'
  })
}

/**
 * 获取项目详情
 */
export function getProject(id: number) {
  return request({
    url: `/ai/project/${id}`,
    method: 'get'
  })
}

/**
 * 创建项目
 */
export function createProject(data: Partial<Project>) {
  return request({
    url: '/ai/project',
    method: 'post',
    data
  })
}

/**
 * 更新项目
 */
export function updateProject(data: Partial<Project>) {
  return request({
    url: '/ai/project',
    method: 'put',
    data
  })
}

/**
 * 删除项目
 */
export function deleteProject(id: number) {
  return request({
    url: `/ai/project/${id}`,
    method: 'delete'
  })
}

// ============ 版本相关 ============

export interface ProjectVersion {
  id: number
  projectId: number
  version: string
  description?: string
  isCurrent: number
  pageCount: number
  createdBy: number
  createdAt: string
}

/**
 * 获取项目版本列表
 */
export function getProjectVersionList(projectId: number) {
  return request({
    url: `/ai/project/${projectId}/version/list`,
    method: 'get'
  })
}

/**
 * 创建项目版本
 */
export function createProjectVersion(projectId: number, data: Partial<ProjectVersion>) {
  return request({
    url: `/ai/project/${projectId}/version`,
    method: 'post',
    data
  })
}

/**
 * 切换当前版本
 */
export function switchProjectVersion(projectId: number, versionId: number) {
  return request({
    url: `/ai/project/${projectId}/version/${versionId}/switch`,
    method: 'put'
  })
}

/**
 * 删除项目版本
 */
export function deleteProjectVersion(projectId: number, versionId: number) {
  return request({
    url: `/ai/project/${projectId}/version/${versionId}`,
    method: 'delete'
  })
}
