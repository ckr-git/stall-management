<template>
  <div class="stall-detail">
    <div class="container">
      <a-spin :spinning="loading">
        <a-card :bordered="false" v-if="stall">
          <a-row :gutter="24">
            <a-col :span="10">
              <div class="stall-cover" :class="'status-' + stall.status">
                <span class="stall-number">{{ stall.stallNo }}</span>
              </div>
            </a-col>
            <a-col :span="14">
              <h2>{{ stall.stallNo }}</h2>
              <a-tag :color="statusColors[stall.status]">{{ statusTexts[stall.status] }}</a-tag>
              <a-descriptions :column="1" class="info">
                <a-descriptions-item label="摊位类型">{{ stall.typeName }}</a-descriptions-item>
                <a-descriptions-item label="位置">{{ stall.location }}</a-descriptions-item>
                <a-descriptions-item label="面积">{{ stall.area }}㎡</a-descriptions-item>
                <a-descriptions-item label="月租金">
                  <span class="price">¥{{ stall.rentPrice }}</span>
                </a-descriptions-item>
                <a-descriptions-item label="描述">{{ stall.description || '暂无描述' }}</a-descriptions-item>
              </a-descriptions>
              <div class="actions">
                <a-button type="primary" size="large" :disabled="stall.status !== 0" @click="handleApply">
                  {{ stall.status === 0 ? '申请租赁' : '暂不可申请' }}
                </a-button>
                <a-button size="large" @click="router.back()">返回</a-button>
              </div>
            </a-col>
          </a-row>
        </a-card>
      </a-spin>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getStallById } from '@/api/stall'
import { useUserStore } from '@/stores/user'
import type { Stall } from '@/types'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const stall = ref<Stall | null>(null)
const loading = ref(false)

const statusTexts: Record<number, string> = { 0: '空闲', 1: '已租', 2: '维护中' }
const statusColors: Record<number, string> = { 0: 'green', 1: 'blue', 2: 'orange' }

onMounted(async () => {
  loading.value = true
  try {
    const res = await getStallById(Number(route.params.id))
    stall.value = res.data
  } finally {
    loading.value = false
  }
})

function handleApply() {
  if (!userStore.isLoggedIn()) {
    router.push({ path: '/login', query: { redirect: route.fullPath } })
    return
  }
  router.push(`/application/submit/${stall.value?.id}`)
}
</script>

<style scoped>
.container { max-width: 1200px; margin: 0 auto; padding: 24px; }
.stall-cover { height: 300px; display: flex; align-items: center; justify-content: center; font-size: 48px; font-weight: bold; color: #fff; border-radius: 8px; }
.status-0 { background: linear-gradient(135deg, #52c41a, #389e0d); }
.status-1 { background: linear-gradient(135deg, #1890ff, #096dd9); }
.status-2 { background: linear-gradient(135deg, #faad14, #d48806); }
.stall-number { background: rgba(255,255,255,0.2); padding: 16px 32px; border-radius: 8px; }
h2 { margin-bottom: 8px; }
.info { margin-top: 24px; }
.price { color: #f5222d; font-size: 24px; font-weight: bold; }
.actions { margin-top: 32px; display: flex; gap: 12px; }
</style>
