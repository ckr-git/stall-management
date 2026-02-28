import request from './index'
import type { ApiResponse, PageResult, Announcement, PageQuery } from '@/types'

// 公开接口
export function getPublishedAnnouncements(params?: { type?: number }): Promise<ApiResponse<any>> {
  return request.get('/announcement/list', { params })
}

export function getAnnouncementById(id: number): Promise<ApiResponse<Announcement>> {
  return request.get(`/announcement/${id}`)
}

// 管理员
export function getAnnouncementList(params: PageQuery & {
  status?: number
  type?: number
}): Promise<ApiResponse<PageResult<Announcement>>> {
  return request.get('/announcement/admin/list', { params })
}

export function createAnnouncement(data: Partial<Announcement>): Promise<ApiResponse<void>> {
  return request.post('/announcement/admin', data)
}

export function updateAnnouncement(id: number, data: Partial<Announcement>): Promise<ApiResponse<void>> {
  return request.put(`/announcement/admin/${id}`, data)
}

export function publishAnnouncement(id: number): Promise<ApiResponse<void>> {
  return request.put(`/announcement/admin/${id}/status`, null, { params: { status: 1 } })
}

export function unpublishAnnouncement(id: number): Promise<ApiResponse<void>> {
  return request.put(`/announcement/admin/${id}/status`, null, { params: { status: 2 } })
}

export function deleteAnnouncement(id: number): Promise<ApiResponse<void>> {
  return request.delete(`/announcement/admin/${id}`)
}
