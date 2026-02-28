<template>
  <div class="my-feedback">
    <div class="container">
      <a-card title="我的反馈" :bordered="false">
        <template #extra>
          <a-button type="primary" @click="router.push('/feedback/submit')">提交反馈</a-button>
        </template>
        <a-table :columns="columns" :data-source="feedbacks" :loading="loading" :pagination="pagination" @change="handleTableChange" row-key="id">
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'type'">
              {{ typeTexts[record.type] }}
            </template>
            <template v-if="column.key === 'status'">
              <a-tag :color="statusColors[record.status]">{{ statusTexts[record.status] }}</a-tag>
            </template>
            <template v-if="column.key === 'action'">
              <a-popover title="回复内容" v-if="record.reply">
                <template #content>{{ record.reply }}</template>
                <a>查看回复</a>
              </a-popover>
            </template>
          </template>
        </a-table>
      </a-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getMyFeedbacks } from '@/api/feedback'
import { formatDateTime } from '@/utils/format'
import type { Feedback } from '@/types'

const router = useRouter()
const feedbacks = ref<Feedback[]>([])
const loading = ref(false)
const pagination = ref({ current: 1, pageSize: 10, total: 0 })

const typeTexts: Record<number, string> = { 1: '投诉', 2: '建议', 3: '咨询' }
const statusTexts: Record<number, string> = { 0: '待处理', 1: '处理中', 2: '已处理', 3: '已关闭' }
const statusColors: Record<number, string> = { 0: 'processing', 1: 'warning', 2: 'success', 3: 'default' }

const columns = [
  { title: '标题', dataIndex: 'title' },
  { title: '类型', key: 'type' },
  { title: '状态', key: 'status' },
  { title: '提交时间', dataIndex: 'createTime', customRender: ({ text }: any) => formatDateTime(text) },
  { title: '操作', key: 'action' }
]

async function fetchData() {
  loading.value = true
  try {
    const res = await getMyFeedbacks({ pageNum: pagination.value.current, pageSize: pagination.value.pageSize })
    feedbacks.value = res.data.records
    pagination.value.total = res.data.total
  } finally {
    loading.value = false
  }
}

function handleTableChange(pag: any) {
  pagination.value.current = pag.current
  fetchData()
}

onMounted(fetchData)
</script>

<style scoped>
.container { max-width: 1200px; margin: 0 auto; padding: 24px; }
</style>
