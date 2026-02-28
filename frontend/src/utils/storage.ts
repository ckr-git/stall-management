const TOKEN_KEY = 'stall_token'
const USER_KEY = 'stall_user'

export function getToken(): string | null {
  return localStorage.getItem(TOKEN_KEY)
}

export function setToken(token: string): void {
  localStorage.setItem(TOKEN_KEY, token)
}

export function removeToken(): void {
  localStorage.removeItem(TOKEN_KEY)
}

export function getUser<T>(): T | null {
  const user = localStorage.getItem(USER_KEY)
  return user ? JSON.parse(user) : null
}

export function setUser<T>(user: T): void {
  localStorage.setItem(USER_KEY, JSON.stringify(user))
}

export function removeUser(): void {
  localStorage.removeItem(USER_KEY)
}

export function clearAuth(): void {
  removeToken()
  removeUser()
}
