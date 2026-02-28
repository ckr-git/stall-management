import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import { message } from 'ant-design-vue'
import { useUserStore } from '@/stores/user'

const routes: RouteRecordRaw[] = [
  // 用户端路由
  {
    path: '/',
    component: () => import('@/components/layout/UserLayout.vue'),
    children: [
      { path: '', name: 'Home', component: () => import('@/views/user/Home.vue') },
      { path: 'stall', name: 'StallList', component: () => import('@/views/user/StallList.vue') },
      { path: 'stall/:id', name: 'StallDetail', component: () => import('@/views/user/StallDetail.vue') },
      { path: 'announcement', name: 'AnnouncementList', component: () => import('@/views/user/AnnouncementList.vue') },
      { path: 'announcement/:id', name: 'AnnouncementDetail', component: () => import('@/views/user/AnnouncementDetail.vue') },
      // 需要登录的页面
      { path: 'application', name: 'MyApplication', component: () => import('@/views/user/MyApplication.vue'), meta: { requiresAuth: true } },
      { path: 'application/submit/:stallId', name: 'SubmitApplication', component: () => import('@/views/user/SubmitApplication.vue'), meta: { requiresAuth: true } },
      { path: 'application/:id', name: 'ApplicationDetail', component: () => import('@/views/user/ApplicationDetail.vue'), meta: { requiresAuth: true } },
      { path: 'rental', name: 'MyRental', component: () => import('@/views/user/MyRental.vue'), meta: { requiresAuth: true } },
      { path: 'feedback', name: 'MyFeedback', component: () => import('@/views/user/MyFeedback.vue'), meta: { requiresAuth: true } },
      { path: 'feedback/submit', name: 'SubmitFeedback', component: () => import('@/views/user/SubmitFeedback.vue'), meta: { requiresAuth: true } },
      { path: 'profile', name: 'UserProfile', component: () => import('@/views/user/Profile.vue'), meta: { requiresAuth: true } }
    ]
  },
  // 认证页面
  { path: '/login', name: 'Login', component: () => import('@/views/auth/Login.vue') },
  { path: '/register', name: 'Register', component: () => import('@/views/auth/Register.vue') },
  // 管理端路由
  {
    path: '/admin',
    component: () => import('@/components/layout/AdminLayout.vue'),
    meta: { requiresAuth: true, requiresAdmin: true },
    children: [
      { path: '', name: 'Dashboard', component: () => import('@/views/admin/Dashboard.vue') },
      { path: 'user', name: 'UserManage', component: () => import('@/views/admin/UserManage.vue') },
      { path: 'stall', name: 'StallManage', component: () => import('@/views/admin/StallManage.vue') },
      { path: 'stall/type', name: 'StallTypeManage', component: () => import('@/views/admin/StallTypeManage.vue') },
      { path: 'application', name: 'ApplicationManage', component: () => import('@/views/admin/ApplicationManage.vue') },
      { path: 'rental', name: 'RentalManage', component: () => import('@/views/admin/RentalManage.vue') },
      { path: 'hygiene', name: 'HygieneManage', component: () => import('@/views/admin/HygieneManage.vue') },
      { path: 'feedback', name: 'FeedbackManage', component: () => import('@/views/admin/FeedbackManage.vue') },
      { path: 'announcement', name: 'AnnouncementManage', component: () => import('@/views/admin/AnnouncementManage.vue') },
      { path: 'profile', name: 'AdminProfile', component: () => import('@/views/admin/Profile.vue') }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach(async (to, _from, next) => {
  const userStore = useUserStore()

  if (to.meta.requiresAuth && !userStore.isLoggedIn()) {
    next({ path: '/login', query: { redirect: to.fullPath } })
    return
  }

  // 有 token 但用户信息丢失（如刷新页面），对所有需认证路由先重新获取
  if (to.meta.requiresAuth && userStore.isLoggedIn() && !userStore.user) {
    await userStore.fetchUserInfo()
  }

  if (to.meta.requiresAdmin) {
    if (!userStore.isAdmin()) {
      message.warning('您无权访问管理后台')
      next('/')
      return
    }
  }

  next()
})

export default router
