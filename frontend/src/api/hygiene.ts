import request from './index'
import type { ApiResponse, PageResult, Hygiene, PageQuery } from '@/types'

export function getHygieneList(params: PageQuery & {
  stallId?: number
  result?: string
}): Promise<ApiResponse<PageResult<Hygiene>>> {
  return request.get('/hygiene/list', { params })
}

export function getHygieneById(id: number): Promise<ApiResponse<Hygiene>> {
  return request.get(`/hygiene/${id}`)
}

export function createHygiene(data: {
  stallId: number
  inspectionDate: string
  score: number
  problems?: string
  suggestions?: string
}): Promise<ApiResponse<void>> {
  return request.post('/hygiene/admin', data)
}

export function updateHygiene(id: number, data: Partial<Hygiene>): Promise<ApiResponse<void>> {
  return request.put(`/hygiene/admin/${id}`, data)
}

export function updateRectificationStatus(id: number, status: number): Promise<ApiResponse<void>> {
  return request.put(`/hygiene/admin/${id}/rectification`, null, { params: { status } })
}

export function deleteHygiene(id: number): Promise<ApiResponse<void>> {
  return request.delete(`/hygiene/admin/${id}`)
}
