import request from './index'
import type { ApiResponse, PageResult, Application, PageQuery } from '@/types'

// 管理员
export function getApplicationList(params: PageQuery & {
  status?: number
}): Promise<ApiResponse<PageResult<Application>>> {
  return request.get('/application/admin/list', { params })
}

// 用户
export function getMyApplications(params: PageQuery & { status?: number }): Promise<ApiResponse<PageResult<Application>>> {
  return request.get('/application/my', { params })
}

export function getApplicationById(id: number): Promise<ApiResponse<Application>> {
  return request.get(`/application/${id}`)
}

export function createApplication(data: {
  stallId: number
  startDate: string
  endDate: string
  businessType: string
  reason?: string
}): Promise<ApiResponse<void>> {
  return request.post('/application/submit', data)
}

export function cancelApplication(id: number): Promise<ApiResponse<void>> {
  return request.delete(`/application/${id}`)
}

// 管理员审核 (status: 1=approve, 2=reject)
export function reviewApplication(id: number, data: { status: number; reviewOpinion?: string }): Promise<ApiResponse<void>> {
  return request.put(`/application/admin/${id}/review`, data)
}

// 兼容旧调用
export function approveApplication(id: number): Promise<ApiResponse<void>> {
  return reviewApplication(id, { status: 1 })
}

export function rejectApplication(id: number, reason: string): Promise<ApiResponse<void>> {
  return reviewApplication(id, { status: 2, reviewOpinion: reason })
}
