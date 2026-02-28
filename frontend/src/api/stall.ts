import request from './index'
import type { ApiResponse, PageResult, Stall, PageQuery } from '@/types'

// 公开接口
export function getStallList(params: PageQuery & {
  keyword?: string
  typeId?: number
  status?: number
}): Promise<ApiResponse<PageResult<Stall>>> {
  return request.get('/stall/list', { params })
}

export function getStallById(id: number): Promise<ApiResponse<Stall>> {
  return request.get(`/stall/${id}`)
}

export function getAvailableStalls(): Promise<ApiResponse<Stall[]>> {
  return request.get('/stall/available')
}

// 管理员操作
export function createStall(data: Partial<Stall>): Promise<ApiResponse<void>> {
  return request.post('/stall/admin', data)
}

export function updateStall(id: number, data: Partial<Stall>): Promise<ApiResponse<void>> {
  return request.put(`/stall/admin/${id}`, data)
}

export function updateStallStatus(id: number, status: number): Promise<ApiResponse<void>> {
  return request.put(`/stall/admin/${id}/status`, null, { params: { status } })
}

export function deleteStall(id: number): Promise<ApiResponse<void>> {
  return request.delete(`/stall/admin/${id}`)
}
