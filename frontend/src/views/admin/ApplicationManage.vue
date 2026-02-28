<template>
  <div class="application-manage">
    <div class="toolbar">
      <a-space>
        <a-select v-model:value="query.status" placeholder="状态" style="width: 120px" allowClear @change="fetchData">
          <a-select-option :value="0">待审核</a-select-option>
          <a-select-option :value="1">已通过</a-select-option>
          <a-select-option :value="2">已拒绝</a-select-option>
          <a-select-option :value="3">已取消</a-select-option>
        </a-select>
      </a-space>
    </div>
    <a-table :columns="columns" :data-source="applications" :loading="loading" :pagination="pagination" @change="handleTableChange" row-key="id">
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'stall'">{{ record.stallNo }}</template>
        <template v-if="column.key === 'user'">{{ record.username }}</template>
        <template v-if="column.key === 'period'">
          {{ formatDate(record.startDate) }} ~ {{ formatDate(record.endDate) }}
        </template>
        <template v-if="column.key === 'status'">
          <a-tag :color="statusColors[record.status]">{{ statusTexts[record.status] }}</a-tag>
        </template>
        <template v-if="column.key === 'action'">
          <a-space v-if="record.status === 0">
            <a-popconfirm title="确定通过？" @confirm="handleApprove(record.id)">
              <a style="color: #52c41a">通过</a>
            </a-popconfirm>
            <a @click="openReject(record.id)">拒绝</a>
          </a-space>
          <span v-else>-</span>
        </template>
      </template>
    </a-table>
    <a-modal v-model:open="rejectVisible" title="拒绝申请" @ok="handleReject">
      <a-form-item label="拒绝原因">
        <a-textarea v-model:value="rejectReason" :rows="3" />
      </a-form-item>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { getApplicationList, approveApplication, rejectApplication } from '@/api/application'
import { formatDateTime, formatDate } from '@/utils/format'
import type { Application } from '@/types'

const applications = ref<Application[]>([])
const loading = ref(false)
const pagination = ref({ current: 1, pageSize: 10, total: 0 })
const query = reactive({ status: undefined as number | undefined })
const rejectVisible = ref(false)
const rejectId = ref(0)
const rejectReason = ref('')

const statusTexts: Record<number, string> = { 0: '待审核', 1: '已通过', 2: '已拒绝', 3: '已取消' }
const statusColors: Record<number, string> = { 0: 'processing', 1: 'success', 2: 'error', 3: 'default' }

const columns = [
  { title: '摊位号', key: 'stall' },
  { title: '申请人', key: 'user' },
  { title: '经营类型', dataIndex: 'businessType' },
  { title: '租赁周期', key: 'period' },
  { title: '状态', key: 'status' },
  { title: '申请时间', dataIndex: 'createTime', customRender: ({ text }: any) => formatDateTime(text) },
  { title: '操作', key: 'action' }
]

async function fetchData() {
  loading.value = true
  try {
    const res = await getApplicationList({ ...query, pageNum: pagination.value.current, pageSize: pagination.value.pageSize })
    applications.value = res.data.records
    pagination.value.total = res.data.total
  } finally { loading.value = false }
}

function handleTableChange(pag: any) { pagination.value.current = pag.current; fetchData() }

async function handleApprove(id: number) {
  await approveApplication(id)
  message.success('已通过')
  fetchData()
}

function openReject(id: number) {
  rejectId.value = id
  rejectReason.value = ''
  rejectVisible.value = true
}

async function handleReject() {
  await rejectApplication(rejectId.value, rejectReason.value)
  message.success('已拒绝')
  rejectVisible.value = false
  fetchData()
}

onMounted(fetchData)
</script>

<style scoped>
.toolbar { margin-bottom: 16px; }
</style>
