<template>
  <div class="home">
    <div class="banner">
      <div class="banner-content">
        <h1>欢迎来到摊位管理平台</h1>
        <p>便捷的摊位租赁服务，助力您的创业梦想</p>
        <a-button type="primary" size="large" @click="router.push('/stall')">
          浏览摊位
        </a-button>
      </div>
    </div>
    <div class="container">
      <a-row :gutter="[24, 24]">
        <a-col :xs="24" :lg="16">
          <a-card title="最新公告" :bordered="false">
            <a-list :data-source="announcements" :loading="loading">
              <template #renderItem="{ item }">
                <a-list-item>
                  <a-list-item-meta>
                    <template #title>
                      <router-link :to="`/announcement/${item.id}`">{{ item.title }}</router-link>
                    </template>
                    <template #description>{{ formatDateTime(item.publishTime) }}</template>
                  </a-list-item-meta>
                </a-list-item>
              </template>
              <template #loadMore>
                <div style="text-align: center; margin-top: 12px">
                  <a-button @click="router.push('/announcement')">查看更多</a-button>
                </div>
              </template>
            </a-list>
          </a-card>
        </a-col>
        <a-col :xs="24" :lg="8">
          <a-card title="快捷入口" :bordered="false">
            <div class="quick-links">
              <a-button block @click="router.push('/stall')">摊位浏览</a-button>
              <a-button block @click="router.push('/application')">我的申请</a-button>
              <a-button block @click="router.push('/feedback/submit')">意见反馈</a-button>
            </div>
          </a-card>
        </a-col>
      </a-row>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getPublishedAnnouncements } from '@/api/announcement'
import { formatDateTime } from '@/utils/format'
import type { Announcement } from '@/types'

const router = useRouter()
const announcements = ref<Announcement[]>([])
const loading = ref(false)

onMounted(async () => {
  loading.value = true
  try {
    const res = await getPublishedAnnouncements()
    const data = res.data
    announcements.value = Array.isArray(data) ? data.slice(0, 5) : (data.records || data)
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.home {
  background: var(--bg-color);
}

/* Banner - 废墟市集风格 */
.banner {
  background: 
    linear-gradient(180deg, rgba(26, 29, 33, 0.7) 0%, rgba(37, 40, 48, 0.9) 100%),
    linear-gradient(45deg, transparent 48%, rgba(74, 69, 64, 0.1) 49%, rgba(74, 69, 64, 0.1) 51%, transparent 52%);
  background-color: #252830;
  padding: 100px 0 80px;
  text-align: center;
  position: relative;
  border-bottom: 3px solid var(--border-rust);
  overflow: hidden;
}

.banner::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: repeating-linear-gradient(
    90deg,
    var(--warning-color) 0px,
    var(--warning-color) 20px,
    transparent 20px,
    transparent 40px
  );
}

.banner-content {
  position: relative;
  z-index: 1;
}

.banner h1 {
  font-size: 42px;
  margin-bottom: 16px;
  color: var(--text-bright);
  font-family: var(--font-military);
  letter-spacing: 6px;
  text-shadow: 0 4px 8px rgba(0, 0, 0, 0.5);
}

.banner p {
  font-size: 16px;
  margin-bottom: 36px;
  color: var(--text-secondary);
  font-family: var(--font-military);
  letter-spacing: 3px;
}

.banner :deep(.ant-btn-primary) {
  height: 48px;
  padding: 0 40px;
  font-size: 14px;
  letter-spacing: 4px;
  background: linear-gradient(180deg, var(--primary-color) 0%, var(--primary-active) 100%) !important;
  border: 2px solid var(--rust-orange) !important;
  box-shadow: 0 4px 16px rgba(139, 115, 85, 0.4);
}

.banner :deep(.ant-btn-primary:hover) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(139, 115, 85, 0.6);
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 32px 24px;
}

/* 卡片样式 */
.container :deep(.ant-card) {
  background: var(--bg-card) !important;
  border: 1px solid var(--border-color) !important;
}

.container :deep(.ant-card-head) {
  background: linear-gradient(180deg, var(--bg-light) 0%, var(--bg-card) 100%) !important;
  border-bottom: 1px solid var(--border-rust) !important;
}

.container :deep(.ant-card-head-title) {
  color: var(--text-bright) !important;
  font-family: var(--font-military);
  letter-spacing: 2px;
}

.container :deep(.ant-card-head-title::before) {
  content: '▸ ';
  color: var(--rust-orange);
}

/* 快捷入口 */
.quick-links {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.quick-links :deep(.ant-btn) {
  height: 44px;
  font-family: var(--font-military);
  letter-spacing: 2px;
  text-align: left;
  padding-left: 20px;
  background: var(--bg-medium) !important;
  border: 1px solid var(--border-color) !important;
  color: var(--text-color) !important;
}

.quick-links :deep(.ant-btn:hover) {
  border-color: var(--rust-orange) !important;
  color: var(--rust-orange) !important;
  background: rgba(139, 115, 85, 0.1) !important;
}

.quick-links :deep(.ant-btn::before) {
  content: '▸';
  margin-right: 12px;
  color: var(--rust-orange);
}

/* 列表样式 */
.container :deep(.ant-list-item) {
  border-bottom: 1px solid var(--border-color) !important;
  padding: 16px 0;
}

.container :deep(.ant-list-item-meta-title a) {
  color: var(--text-bright) !important;
  font-family: var(--font-military);
}

.container :deep(.ant-list-item-meta-title a:hover) {
  color: var(--rust-orange) !important;
}

.container :deep(.ant-list-item-meta-description) {
  color: var(--text-muted) !important;
  font-size: 12px;
}
</style>
