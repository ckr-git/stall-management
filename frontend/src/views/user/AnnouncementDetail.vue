<template>
  <div class="announcement-detail">
    <div class="container">
      <a-spin :spinning="loading">
        <a-card :bordered="false" v-if="announcement">
          <template #title>
            {{ announcement.title }}
            <a-tag v-if="announcement.type === 1" color="red" style="margin-left: 8px">重要</a-tag>
          </template>
          <div class="meta">发布时间：{{ formatDateTime(announcement.publishTime) }}</div>
          <a-divider />
          <div class="content" v-html="announcement.content"></div>
          <a-divider />
          <a-button @click="router.back()">返回</a-button>
        </a-card>
      </a-spin>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getAnnouncementById } from '@/api/announcement'
import { formatDateTime } from '@/utils/format'
import type { Announcement } from '@/types'

const router = useRouter()
const route = useRoute()
const announcement = ref<Announcement | null>(null)
const loading = ref(false)

onMounted(async () => {
  loading.value = true
  try {
    const res = await getAnnouncementById(Number(route.params.id))
    announcement.value = res.data
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.container { max-width: 1200px; margin: 0 auto; padding: 24px; }
.meta { color: #999; font-size: 14px; }
.content { line-height: 1.8; min-height: 200px; }
</style>
