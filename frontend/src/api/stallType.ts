import request from './index'
import type { ApiResponse, StallType } from '@/types'

export function getStallTypeList(): Promise<ApiResponse<StallType[]>> {
  return request.get('/stall-type/list')
}

export function getAllStallTypes(): Promise<ApiResponse<StallType[]>> {
  return getStallTypeList()
}

export function getStallTypeById(id: number): Promise<ApiResponse<StallType>> {
  return request.get(`/stall-type/${id}`)
}

export function createStallType(data: Partial<StallType>): Promise<ApiResponse<void>> {
  return request.post('/stall-type/admin', data)
}

export function updateStallType(id: number, data: Partial<StallType>): Promise<ApiResponse<void>> {
  return request.put(`/stall-type/admin/${id}`, data)
}

export function deleteStallType(id: number): Promise<ApiResponse<void>> {
  return request.delete(`/stall-type/admin/${id}`)
}
