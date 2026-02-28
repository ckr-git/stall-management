<template>
  <div class="announcement-list">
    <div class="container">
      <a-card title="公告通知" :bordered="false">
        <a-list :data-source="announcements" :loading="loading">
          <template #renderItem="{ item }">
            <a-list-item>
              <a-list-item-meta>
                <template #title>
                  <router-link :to="`/announcement/${item.id}`" class="announcement-link">{{ item.title }}</router-link>
                </template>
                <template #description>
                  <a-space>
                    <span>{{ formatDateTime(item.publishTime) }}</span>
                    <a-tag v-if="item.type === 1" color="red">重要</a-tag>
                  </a-space>
                </template>
              </a-list-item-meta>
            </a-list-item>
          </template>
        </a-list>
      </a-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getPublishedAnnouncements } from '@/api/announcement'
import { formatDateTime } from '@/utils/format'
import type { Announcement } from '@/types'
const announcements = ref<Announcement[]>([])
const loading = ref(false)
async function fetchData() {
  loading.value = true
  try {
    const res = await getPublishedAnnouncements()
    const data = res.data
    announcements.value = Array.isArray(data) ? data : (data.records || data)
  } finally {
    loading.value = false
  }
}

onMounted(fetchData)
</script>

<style scoped>
.container { max-width: 1200px; margin: 0 auto; padding: 24px; }
.announcement-link {
  color: var(--text-bright);
  text-decoration: none;
  transition: color 0.3s;
}
.announcement-link:hover,
.announcement-link:focus-visible {
  color: var(--rust-orange);
  outline: 1px solid var(--rust-orange);
  outline-offset: 2px;
}
</style>
