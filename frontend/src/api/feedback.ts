import request from './index'
import type { ApiResponse, PageResult, Feedback, PageQuery } from '@/types'

// 管理员
export function getFeedbackList(params: PageQuery & {
  status?: number
  type?: number
}): Promise<ApiResponse<PageResult<Feedback>>> {
  return request.get('/feedback/admin/list', { params })
}

// 用户
export function getMyFeedbacks(params: PageQuery & { type?: number; status?: number }): Promise<ApiResponse<PageResult<Feedback>>> {
  return request.get('/feedback/my', { params })
}

export function getFeedbackById(id: number): Promise<ApiResponse<Feedback>> {
  return request.get(`/feedback/${id}`)
}

export function createFeedback(data: {
  stallId?: number
  type: number
  title: string
  content: string
}): Promise<ApiResponse<void>> {
  return request.post('/feedback/submit', data)
}

// 管理员操作
export function replyFeedback(id: number, reply: string): Promise<ApiResponse<void>> {
  return request.put(`/feedback/admin/${id}/reply`, { reply })
}

export function updateFeedbackStatus(id: number, status: number): Promise<ApiResponse<void>> {
  return request.put(`/feedback/admin/${id}/status`, null, { params: { status } })
}
