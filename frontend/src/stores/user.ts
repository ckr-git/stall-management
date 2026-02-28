import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { User, LoginResult } from '@/types'
import { getUserInfo, logout as logoutApi } from '@/api/auth'
import { getToken, setToken, setUser, getUser, clearAuth } from '@/utils/storage'

export const useUserStore = defineStore('user', () => {
  const token = ref<string | null>(getToken())
  const user = ref<User | null>(getUser<User>())

  const isLoggedIn = () => !!token.value

  const isAdmin = () => user.value?.role === 'ADMIN'

  async function login(loginResult: LoginResult) {
    token.value = loginResult.token
    setToken(loginResult.token)
    await fetchUserInfo()
  }

  async function fetchUserInfo() {
    try {
      const res = await getUserInfo()
      user.value = res.data
      setUser(res.data)
    } catch {
      clearAuth()
      token.value = null
      user.value = null
    }
  }

  async function logout() {
    try {
      await logoutApi()
    } finally {
      clearAuth()
      token.value = null
      user.value = null
    }
  }

  return { token, user, isLoggedIn, isAdmin, login, fetchUserInfo, logout }
})
