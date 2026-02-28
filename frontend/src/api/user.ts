import request from './index'
import type { ApiResponse, PageResult, User, PageQuery } from '@/types'

// 用户自身
export function getProfile(): Promise<ApiResponse<User>> {
  return request.get('/user/profile')
}

export function updateProfile(data: Partial<User>): Promise<ApiResponse<void>> {
  return request.put('/user/profile', data)
}

// 管理员操作
export function getUserList(params: PageQuery & { keyword?: string; role?: string }): Promise<ApiResponse<PageResult<User>>> {
  return request.get('/user/admin/list', { params })
}

export function getUserById(id: number): Promise<ApiResponse<User>> {
  return request.get(`/user/admin/${id}`)
}

export function updateUser(id: number, data: Partial<User>): Promise<ApiResponse<void>> {
  return request.put(`/user/admin/${id}`, data)
}

export function updateUserStatus(id: number, status: number): Promise<ApiResponse<void>> {
  return request.put(`/user/admin/${id}/status`, null, { params: { status } })
}

export function resetPassword(id: number): Promise<ApiResponse<void>> {
  return request.put(`/user/admin/${id}/reset-password`)
}

export function deleteUser(id: number): Promise<ApiResponse<void>> {
  return request.delete(`/user/admin/${id}`)
}
