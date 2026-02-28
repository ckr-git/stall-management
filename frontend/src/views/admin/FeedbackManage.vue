<template>
  <div class="feedback-manage">
    <div class="toolbar">
      <a-space>
        <a-select v-model:value="query.status" placeholder="状态" style="width: 120px" allowClear @change="fetchData">
          <a-select-option :value="0">待处理</a-select-option>
          <a-select-option :value="1">处理中</a-select-option>
          <a-select-option :value="2">已处理</a-select-option>
          <a-select-option :value="3">已关闭</a-select-option>
        </a-select>
        <a-select v-model:value="query.type" placeholder="类型" style="width: 120px" allowClear @change="fetchData">
          <a-select-option :value="1">投诉</a-select-option>
          <a-select-option :value="2">建议</a-select-option>
          <a-select-option :value="3">咨询</a-select-option>
        </a-select>
      </a-space>
    </div>
    <a-table :columns="columns" :data-source="feedbacks" :loading="loading" :pagination="pagination" @change="handleTableChange" row-key="id">
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'user'">{{ record.username }}</template>
        <template v-if="column.key === 'type'">
          <a-tag>{{ typeTexts[record.type] }}</a-tag>
        </template>
        <template v-if="column.key === 'status'">
          <a-tag :color="statusColors[record.status]">
            {{ statusTexts[record.status] }}
          </a-tag>
        </template>
        <template v-if="column.key === 'action'">
          <a-space>
            <a @click="showDetail(record)">查看</a>
            <a v-if="record.status === 0 || record.status === 1" @click="openReply(record)">回复</a>
          </a-space>
        </template>
      </template>
    </a-table>
    <a-modal v-model:open="detailVisible" title="反馈详情" :footer="null">
      <a-descriptions :column="1" bordered size="small" v-if="currentFeedback">
        <a-descriptions-item label="标题">{{ currentFeedback.title }}</a-descriptions-item>
        <a-descriptions-item label="类型">{{ typeTexts[currentFeedback.type] }}</a-descriptions-item>
        <a-descriptions-item label="内容">{{ currentFeedback.content }}</a-descriptions-item>
        <a-descriptions-item label="提交时间">{{ formatDateTime(currentFeedback.createTime) }}</a-descriptions-item>
        <a-descriptions-item label="回复" v-if="currentFeedback.reply">{{ currentFeedback.reply }}</a-descriptions-item>
      </a-descriptions>
    </a-modal>
    <a-modal v-model:open="replyVisible" title="回复反馈" @ok="handleReply">
      <a-form-item label="回复内容">
        <a-textarea v-model:value="replyContent" :rows="4" />
      </a-form-item>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { getFeedbackList, replyFeedback } from '@/api/feedback'
import { formatDateTime } from '@/utils/format'
import type { Feedback } from '@/types'

const feedbacks = ref<Feedback[]>([])
const loading = ref(false)
const pagination = ref({ current: 1, pageSize: 10, total: 0 })
const query = reactive({ status: undefined as number | undefined, type: undefined as number | undefined })
const detailVisible = ref(false)
const replyVisible = ref(false)
const currentFeedback = ref<Feedback | null>(null)
const replyContent = ref('')

const typeTexts: Record<number, string> = { 1: '投诉', 2: '建议', 3: '咨询' }
const statusTexts: Record<number, string> = { 0: '待处理', 1: '处理中', 2: '已处理', 3: '已关闭' }
const statusColors: Record<number, string> = { 0: 'processing', 1: 'warning', 2: 'success', 3: 'default' }

const columns = [
  { title: '标题', dataIndex: 'title' },
  { title: '用户', key: 'user' },
  { title: '类型', key: 'type' },
  { title: '状态', key: 'status' },
  { title: '提交时间', dataIndex: 'createTime', customRender: ({ text }: any) => formatDateTime(text) },
  { title: '操作', key: 'action' }
]

async function fetchData() {
  loading.value = true
  try {
    const res = await getFeedbackList({ ...query, pageNum: pagination.value.current, pageSize: pagination.value.pageSize })
    feedbacks.value = res.data.records
    pagination.value.total = res.data.total
  } finally { loading.value = false }
}

function handleTableChange(pag: any) { pagination.value.current = pag.current; fetchData() }

function showDetail(record: Feedback) {
  currentFeedback.value = record
  detailVisible.value = true
}

function openReply(record: Feedback) {
  currentFeedback.value = record
  replyContent.value = ''
  replyVisible.value = true
}

async function handleReply() {
  await replyFeedback(currentFeedback.value!.id, replyContent.value)
  message.success('回复成功')
  replyVisible.value = false
  fetchData()
}

onMounted(fetchData)
</script>

<style scoped>
.toolbar { margin-bottom: 16px; }
</style>
