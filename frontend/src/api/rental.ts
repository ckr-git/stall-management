import request from './index'
import type { ApiResponse, PageResult, Rental, PageQuery } from '@/types'

// 管理员
export function getRentalList(params: PageQuery & {
  status?: number
  stallId?: number
}): Promise<ApiResponse<PageResult<Rental>>> {
  return request.get('/rental/admin/list', { params })
}

// 用户
export function getMyRentals(params: PageQuery): Promise<ApiResponse<PageResult<Rental>>> {
  return request.get('/rental/my', { params })
}

export function getRentalById(id: number): Promise<ApiResponse<Rental>> {
  return request.get(`/rental/${id}`)
}

// 管理员操作
export function updatePaymentStatus(id: number, paymentStatus: number): Promise<ApiResponse<void>> {
  return request.put(`/rental/admin/${id}/payment`, null, { params: { paymentStatus } })
}

export function terminateRental(id: number): Promise<ApiResponse<void>> {
  return request.put(`/rental/admin/${id}/terminate`)
}
