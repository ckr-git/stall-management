<template>
  <a-layout class="admin-layout">
    <a-layout-sider
      v-model:collapsed="collapsed"
      collapsible
      breakpoint="lg"
      :collapsed-width="isMobile ? 0 : 80"
      class="sider"
      @breakpoint="onBreakpoint"
    >
      <div class="logo">
        <span v-if="!collapsed">摊位管理后台</span>
        <span v-else>摊位</span>
      </div>
      <a-menu mode="inline" :selected-keys="selectedKeys" theme="dark">
        <a-menu-item key="dashboard" @click="router.push('/admin')">
          <template #icon><DashboardOutlined /></template>
          控制台
        </a-menu-item>
        <a-menu-item key="user" @click="router.push('/admin/user')">
          <template #icon><UserOutlined /></template>
          用户管理
        </a-menu-item>
        <a-sub-menu key="stall-sub">
          <template #icon><ShopOutlined /></template>
          <template #title>摊位管理</template>
          <a-menu-item key="stall" @click="router.push('/admin/stall')">摊位列表</a-menu-item>
          <a-menu-item key="stall-type" @click="router.push('/admin/stall/type')">类型管理</a-menu-item>
        </a-sub-menu>
        <a-menu-item key="application" @click="router.push('/admin/application')">
          <template #icon><FileTextOutlined /></template>
          申请审核
        </a-menu-item>
        <a-menu-item key="rental" @click="router.push('/admin/rental')">
          <template #icon><ContainerOutlined /></template>
          租赁管理
        </a-menu-item>
        <a-menu-item key="hygiene" @click="router.push('/admin/hygiene')">
          <template #icon><SafetyOutlined /></template>
          卫生检查
        </a-menu-item>
        <a-menu-item key="feedback" @click="router.push('/admin/feedback')">
          <template #icon><MessageOutlined /></template>
          反馈处理
        </a-menu-item>
        <a-menu-item key="announcement" @click="router.push('/admin/announcement')">
          <template #icon><NotificationOutlined /></template>
          公告管理
        </a-menu-item>
      </a-menu>
    </a-layout-sider>
    <div v-if="isMobile && !collapsed" class="sider-mask" @click="collapsed = true"></div>
    <a-layout>
      <a-layout-header class="header">
        <div class="header-left">
          <MenuOutlined v-if="isMobile" class="mobile-trigger" @click="collapsed = !collapsed" />
          <a-breadcrumb>
            <a-breadcrumb-item>
              <router-link to="/admin">首页</router-link>
            </a-breadcrumb-item>
            <a-breadcrumb-item v-if="currentTitle">{{ currentTitle }}</a-breadcrumb-item>
          </a-breadcrumb>
        </div>
        <div class="header-right">
          <a-dropdown>
            <a class="user-dropdown">
              <a-avatar :size="32" style="background-color: #1890ff">
                {{ userStore.user?.nickname?.charAt(0) || 'A' }}
              </a-avatar>
              <span class="username">{{ userStore.user?.nickname }}</span>
            </a>
            <template #overlay>
              <a-menu>
                <a-menu-item @click="router.push('/')">返回前台</a-menu-item>
                <a-menu-item @click="router.push('/admin/profile')">个人中心</a-menu-item>
                <a-menu-divider />
                <a-menu-item @click="handleLogout">退出登录</a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>
        </div>
      </a-layout-header>
      <a-layout-content class="content">
        <router-view />
      </a-layout-content>
    </a-layout>
  </a-layout>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import {
  DashboardOutlined, UserOutlined, ShopOutlined, FileTextOutlined,
  ContainerOutlined, SafetyOutlined, MessageOutlined, NotificationOutlined,
  MenuOutlined
} from '@ant-design/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const collapsed = ref(false)
const isMobile = ref(false)

function onBreakpoint(broken: boolean) {
  isMobile.value = broken
  collapsed.value = broken
}

const titleMap: Record<string, string> = {
  '/admin/user': '用户管理',
  '/admin/stall': '摊位列表',
  '/admin/stall/type': '类型管理',
  '/admin/application': '申请审核',
  '/admin/rental': '租赁管理',
  '/admin/hygiene': '卫生检查',
  '/admin/feedback': '反馈处理',
  '/admin/announcement': '公告管理',
  '/admin/profile': '个人中心'
}

const selectedKeys = computed(() => {
  const path = route.path
  if (path === '/admin') return ['dashboard']
  if (path === '/admin/stall/type') return ['stall-type']
  if (path.startsWith('/admin/stall')) return ['stall']
  const key = path.replace('/admin/', '')
  return [key]
})

const currentTitle = computed(() => titleMap[route.path] || '')

async function handleLogout() {
  await userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.admin-layout {
  min-height: 100vh;
  background: var(--bg-color);
}

/* 侧边栏 - 军营风格 */
.sider {
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  z-index: 100;
  background: linear-gradient(180deg, #1a1d21 0%, #252830 100%) !important;
  border-right: 2px solid var(--border-rust);
}

.sider :deep(.ant-layout-sider-children) {
  background: transparent;
}

.sider :deep(.ant-menu) {
  background: transparent !important;
}

.sider :deep(.ant-menu-item),
.sider :deep(.ant-menu-submenu-title) {
  color: var(--text-color) !important;
  font-family: var(--font-military);
  letter-spacing: 1px;
  margin: 4px 8px !important;
  border-radius: 2px !important;
}

.sider :deep(.ant-menu-item:hover),
.sider :deep(.ant-menu-submenu-title:hover) {
  background: rgba(139, 115, 85, 0.2) !important;
  color: var(--rust-orange) !important;
}

.sider :deep(.ant-menu-item-selected) {
  background: linear-gradient(90deg, rgba(139, 115, 85, 0.3) 0%, transparent 100%) !important;
  color: var(--rust-orange) !important;
  border-left: 3px solid var(--rust-orange) !important;
}

.sider :deep(.ant-menu-sub) {
  background: rgba(0, 0, 0, 0.2) !important;
}

/* Logo */
.logo {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--warning-color);
  font-size: 14px;
  font-weight: 600;
  font-family: var(--font-military);
  letter-spacing: 2px;
  background: linear-gradient(180deg, rgba(139, 115, 85, 0.2) 0%, transparent 100%);
  border-bottom: 1px solid var(--border-rust);
  text-shadow: 0 0 10px var(--glow-warning);
}

/* 头部 */
.header {
  background: linear-gradient(180deg, #2d3139 0%, #252830 100%) !important;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
  margin-left: 200px;
  border-bottom: 1px solid var(--border-color);
}

.header :deep(.ant-breadcrumb) {
  color: var(--text-secondary);
}

.header :deep(.ant-breadcrumb a) {
  color: var(--text-color) !important;
}

.header :deep(.ant-breadcrumb a:hover) {
  color: var(--rust-orange) !important;
}

.ant-layout-sider-collapsed + .ant-layout .header {
  margin-left: 80px;
}

/* 移动端汉堡触发器 */
.mobile-trigger {
  font-size: 18px;
  color: var(--rust-orange);
  margin-right: 16px;
  cursor: pointer;
}

/* 移动端遮罩层 */
.sider-mask {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.45);
  z-index: 999;
}

/* 移动端适配 */
@media (max-width: 992px) {
  .header {
    margin-left: 0 !important;
    padding: 0 16px;
  }
  .content {
    margin-left: 0 !important;
    margin: 16px;
    padding: 16px;
  }
  .sider {
    z-index: 1000;
  }
  .ant-layout-sider-collapsed + .ant-layout .header {
    margin-left: 0;
  }
  .ant-layout-sider-collapsed ~ .ant-layout .content {
    margin-left: 0;
  }
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

.username {
  color: var(--text-color);
  font-family: var(--font-military);
  font-size: 13px;
}

/* 内容区 */
.content {
  margin: 24px;
  margin-left: 224px;
  padding: 24px;
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: 4px;
  min-height: calc(100vh - 64px - 48px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
}

.ant-layout-sider-collapsed ~ .ant-layout .content {
  margin-left: 104px;
}

/* 头部左右区 */
.header-left {
  display: flex;
  align-items: center;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}
</style>
