<template>
  <div class="my-application">
    <div class="container">
      <a-card title="我的申请" :bordered="false">
        <a-table :columns="columns" :data-source="applications" :loading="loading" :pagination="pagination" @change="handleTableChange" row-key="id">
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'stall'">
              {{ record.stallNo }}
            </template>
            <template v-if="column.key === 'period'">
              {{ formatDate(record.startDate) }} ~ {{ formatDate(record.endDate) }}
            </template>
            <template v-if="column.key === 'status'">
              <a-tag :color="statusColors[record.status]">{{ statusTexts[record.status] }}</a-tag>
            </template>
            <template v-if="column.key === 'action'">
              <a-space>
                <a @click="router.push(`/application/${record.id}`)">详情</a>
                <a-popconfirm v-if="record.status === 0" title="确定取消申请？" @confirm="handleCancel(record.id)">
                  <a style="color: #ff4d4f">取消</a>
                </a-popconfirm>
              </a-space>
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
import { message } from 'ant-design-vue'
import { getMyApplications, cancelApplication } from '@/api/application'
import { formatDateTime, formatDate } from '@/utils/format'
import type { Application } from '@/types'

const router = useRouter()
const applications = ref<Application[]>([])
const loading = ref(false)
const pagination = ref({ current: 1, pageSize: 10, total: 0 })

const statusTexts: Record<number, string> = { 0: '待审核', 1: '已通过', 2: '已拒绝', 3: '已取消' }
const statusColors: Record<number, string> = { 0: 'processing', 1: 'success', 2: 'error', 3: 'default' }

const columns = [
  { title: '摊位号', key: 'stall' },
  { title: '经营类型', dataIndex: 'businessType' },
  { title: '租赁周期', key: 'period' },
  { title: '状态', key: 'status' },
  { title: '申请时间', dataIndex: 'createTime', customRender: ({ text }: any) => formatDateTime(text) },
  { title: '操作', key: 'action' }
]

async function fetchData() {
  loading.value = true
  try {
    const res = await getMyApplications({ pageNum: pagination.value.current, pageSize: pagination.value.pageSize })
    applications.value = res.data.records
    pagination.value.total = res.data.total
  } finally {
    loading.value = false
  }
}

function handleTableChange(pag: any) {
  pagination.value.current = pag.current
  fetchData()
}

async function handleCancel(id: number) {
  await cancelApplication(id)
  message.success('已取消')
  fetchData()
}

onMounted(fetchData)
</script>

<style scoped>
.container { max-width: 1200px; margin: 0 auto; padding: 24px; }
</style>
