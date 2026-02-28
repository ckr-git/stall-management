import request from './index'
import type { ApiResponse, LoginForm, LoginResult, RegisterForm, User } from '@/types'

export function login(data: LoginForm): Promise<ApiResponse<LoginResult>> {
  return request.post('/auth/login', data)
}

export function register(data: RegisterForm): Promise<ApiResponse<void>> {
  return request.post('/auth/register', data)
}

export function logout(): Promise<ApiResponse<void>> {
  return request.post('/auth/logout')
}

export function getUserInfo(): Promise<ApiResponse<User>> {
  return request.get('/auth/info')
}

export function updatePassword(data: { oldPassword: string; newPassword: string }): Promise<ApiResponse<void>> {
  return request.put('/auth/password', data)
}
