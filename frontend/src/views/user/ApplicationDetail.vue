<template>
  <div class="application-detail">
    <div class="container">
      <a-spin :spinning="loading">
        <a-card :bordered="false" v-if="application">
          <template #title>
            申请详情
            <a-tag :color="statusColors[application.status]" style="margin-left: 12px">
              {{ statusTexts[application.status] }}
            </a-tag>
          </template>
          <a-descriptions :column="2" bordered>
            <a-descriptions-item label="摊位号">{{ application.stallNo }}</a-descriptions-item>
            <a-descriptions-item label="摊位">{{ application.stallName }}</a-descriptions-item>
            <a-descriptions-item label="经营类型">{{ application.businessType }}</a-descriptions-item>
            <a-descriptions-item label="开始日期">{{ formatDate(application.startDate) }}</a-descriptions-item>
            <a-descriptions-item label="结束日期">{{ formatDate(application.endDate) }}</a-descriptions-item>
            <a-descriptions-item label="申请理由" :span="2">{{ application.reason }}</a-descriptions-item>
            <a-descriptions-item label="申请时间">{{ formatDateTime(application.createTime) }}</a-descriptions-item>
            <a-descriptions-item label="更新时间">{{ formatDateTime(application.updateTime) }}</a-descriptions-item>
            <a-descriptions-item label="拒绝原因" :span="2" v-if="application.status === 2">
              {{ application.reviewOpinion }}
            </a-descriptions-item>
          </a-descriptions>
          <div class="actions">
            <a-button @click="router.back()">返回</a-button>
          </div>
        </a-card>
      </a-spin>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getApplicationById } from '@/api/application'
import { formatDateTime, formatDate } from '@/utils/format'
import type { Application } from '@/types'

const router = useRouter()
const route = useRoute()
const application = ref<Application | null>(null)
const loading = ref(false)

const statusTexts: Record<number, string> = { 0: '待审核', 1: '已通过', 2: '已拒绝', 3: '已取消' }
const statusColors: Record<number, string> = { 0: 'processing', 1: 'success', 2: 'error', 3: 'default' }

onMounted(async () => {
  loading.value = true
  try {
    const res = await getApplicationById(Number(route.params.id))
    application.value = res.data
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.container { max-width: 1200px; margin: 0 auto; padding: 24px; }
.actions { margin-top: 24px; }
</style>
