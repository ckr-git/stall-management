<template>
  <a-layout class="user-layout">
    <a-layout-header class="header">
      <div class="header-content">
        <!-- Logo -->
        <div class="logo" @click="router.push('/')">
          <span class="logo-icon">⚠</span>
          <span class="logo-text">摊位管理平台</span>
        </div>
        
        <!-- 导航菜单 -->
        <nav class="nav-menu">
          <a 
            v-for="item in navItems" 
            :key="item.key"
            :class="['nav-item', { active: selectedKeys.includes(item.key) }]"
            @click="router.push(item.path)"
          >
            <span class="nav-text">{{ item.label }}</span>
          </a>
        </nav>
        
        <!-- 右侧操作区 -->
        <div class="header-right">
          <template v-if="userStore.isLoggedIn()">
            <a-dropdown>
              <a class="user-dropdown">
                <div class="avatar-wrapper">
                  <span class="avatar-text">{{ userStore.user?.nickname?.charAt(0) || 'U' }}</span>
                </div>
                <span class="username">{{ userStore.user?.nickname }}</span>
                <span class="dropdown-arrow">▼</span>
              </a>
              <template #overlay>
                <a-menu class="user-menu">
                  <a-menu-item @click="router.push('/application')">
                    <span class="menu-icon">📋</span> 我的申请
                  </a-menu-item>
                  <a-menu-item @click="router.push('/rental')">
                    <span class="menu-icon">🏠</span> 我的租赁
                  </a-menu-item>
                  <a-menu-item @click="router.push('/feedback')">
                    <span class="menu-icon">📝</span> 我的反馈
                  </a-menu-item>
                  <a-menu-item @click="router.push('/profile')">
                    <span class="menu-icon">👤</span> 个人中心
                  </a-menu-item>
                  <a-menu-divider />
                  <a-menu-item v-if="userStore.isAdmin()" @click="router.push('/admin')">
                    <span class="menu-icon">⚙️</span> 管理后台
                  </a-menu-item>
                  <a-menu-item @click="handleLogout" class="logout-item">
                    <span class="menu-icon">🚪</span> 退出登录
                  </a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>
          </template>
          <template v-else>
            <a-button class="btn-login" @click="router.push('/login')">登录</a-button>
            <a-button type="primary" class="btn-register" @click="router.push('/register')">注册</a-button>
          </template>
        </div>
      </div>
    </a-layout-header>
    
    <a-layout-content class="content">
      <router-view />
    </a-layout-content>
    
    <a-layout-footer class="footer">
      <div class="footer-content">
        <div class="footer-line"></div>
        <div class="footer-text">
          <span class="footer-icon">⚠</span>
          摊位管理平台 © {{ new Date().getFullYear() }} | WASTELAND MARKET SYSTEM
        </div>
        <div class="footer-code">SYS-v2.1.0</div>
      </div>
    </a-layout-footer>
  </a-layout>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const navItems = [
  { key: 'home', label: '首页', path: '/' },
  { key: 'stall', label: '摊位浏览', path: '/stall' },
  { key: 'announcement', label: '公告通知', path: '/announcement' }
]

const selectedKeys = computed(() => {
  const path = route.path
  if (path === '/') return ['home']
  if (path.startsWith('/stall')) return ['stall']
  if (path.startsWith('/announcement')) return ['announcement']
  return []
})

async function handleLogout() {
  await userStore.logout()
  router.push('/')
}
</script>

<style scoped>
.user-layout {
  min-height: 100vh;
  background: var(--bg-color);
}

/* 头部导航 - 金属质感 */
.header {
  background: linear-gradient(180deg, #2d3139 0%, #252830 100%) !important;
  border-bottom: 2px solid var(--border-rust);
  box-shadow: 
    0 4px 16px rgba(0, 0, 0, 0.4),
    inset 0 1px 0 rgba(255, 255, 255, 0.05);
  position: fixed;
  width: 100%;
  z-index: 100;
  padding: 0;
  height: 64px;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  padding: 0 24px;
  height: 100%;
}

/* Logo */
.logo {
  cursor: pointer;
  margin-right: 48px;
  display: flex;
  align-items: center;
  gap: 10px;
  transition: all 0.3s;
}

.logo:hover {
  transform: translateY(-1px);
}

.logo-icon {
  font-size: 24px;
  color: var(--warning-color);
  text-shadow: 0 0 10px var(--glow-warning);
}

.logo-text {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-bright);
  letter-spacing: 2px;
  font-family: var(--font-military);
}

/* 导航菜单 */
.nav-menu {
  flex: 1;
  display: flex;
  gap: 8px;
}

.nav-item {
  padding: 8px 20px;
  color: var(--text-color);
  font-family: var(--font-military);
  font-size: 13px;
  letter-spacing: 1px;
  text-transform: uppercase;
  cursor: pointer;
  position: relative;
  transition: all 0.3s;
  border: 1px solid transparent;
}

.nav-item:hover {
  color: var(--rust-orange);
  background: rgba(139, 115, 85, 0.1);
  border-color: var(--border-color);
}

.nav-item.active {
  color: var(--rust-orange);
  background: linear-gradient(180deg, rgba(139, 115, 85, 0.2) 0%, transparent 100%);
  border-color: var(--border-rust);
  border-bottom: 2px solid var(--rust-orange);
}

.nav-item.active::before {
  content: '▸';
  margin-right: 6px;
  font-size: 10px;
}

/* 右侧操作区 */
.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-dropdown {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 6px 12px;
  border: 1px solid var(--border-color);
  border-radius: 2px;
  background: rgba(45, 49, 57, 0.8);
  transition: all 0.3s;
}

.user-dropdown:hover {
  border-color: var(--rust-orange);
  background: rgba(139, 115, 85, 0.1);
}

.avatar-wrapper {
  width: 32px;
  height: 32px;
  border-radius: 2px;
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-active) 100%);
  border: 2px solid var(--border-rust);
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-text {
  color: var(--text-bright);
  font-weight: 600;
  font-size: 14px;
}

.username {
  color: var(--text-color);
  font-family: var(--font-military);
  font-size: 13px;
}

.dropdown-arrow {
  font-size: 8px;
  color: var(--text-muted);
}

.btn-login {
  color: var(--rust-orange) !important;
  border: 1px solid var(--border-color) !important;
  background: transparent !important;
}

.btn-login:hover {
  border-color: var(--rust-orange) !important;
  background: rgba(139, 115, 85, 0.1) !important;
}

.btn-register {
  background: linear-gradient(180deg, var(--primary-color) 0%, var(--primary-active) 100%) !important;
  border: 1px solid var(--border-rust) !important;
}

/* 内容区 */
.content {
  margin-top: 64px;
  min-height: calc(100vh - 64px - 80px);
  background: var(--bg-color);
}

/* 页脚 - 废墟风格 */
.footer {
  background: linear-gradient(180deg, var(--bg-medium) 0%, var(--bg-dark) 100%) !important;
  padding: 0 !important;
  height: auto !important;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px 24px;
  text-align: center;
}

.footer-line {
  height: 3px;
  background: linear-gradient(90deg, 
    transparent 0%, 
    var(--border-rust) 20%, 
    var(--rust-orange) 50%, 
    var(--border-rust) 80%, 
    transparent 100%);
  margin-bottom: 16px;
  opacity: 0.6;
}

.footer-text {
  color: var(--text-muted);
  font-family: var(--font-military);
  font-size: 12px;
  letter-spacing: 2px;
}

.footer-icon {
  color: var(--warning-color);
  margin-right: 8px;
}

.footer-code {
  margin-top: 8px;
  font-family: var(--font-military);
  font-size: 10px;
  color: var(--text-muted);
  opacity: 0.5;
}

/* 用户菜单样式 */
.menu-icon {
  margin-right: 8px;
}

.logout-item {
  color: var(--error-color) !important;
}
</style>
