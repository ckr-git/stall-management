<template>
  <div class="rental-manage">
    <div class="toolbar">
      <a-select v-model:value="query.status" placeholder="状态" style="width: 120px" allowClear @change="fetchData">
        <a-select-option :value="1">租赁中</a-select-option>
        <a-select-option :value="2">已到期</a-select-option>
        <a-select-option :value="3">提前终止</a-select-option>
      </a-select>
    </div>
    <a-table :columns="columns" :data-source="rentals" :loading="loading" :pagination="pagination" @change="handleTableChange" row-key="id">
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'stall'">{{ record.stallNo }}</template>
        <template v-if="column.key === 'user'">{{ record.username }}</template>
        <template v-if="column.key === 'period'">{{ formatDate(record.startDate) }} ~ {{ formatDate(record.endDate) }}</template>
        <template v-if="column.key === 'paymentStatus'">
          <a-tag :color="paymentColors[record.paymentStatus]">{{ paymentTexts[record.paymentStatus] }}</a-tag>
        </template>
        <template v-if="column.key === 'status'">
          <a-tag :color="statusColors[record.status]">{{ statusTexts[record.status] }}</a-tag>
        </template>
        <template v-if="column.key === 'action'">
          <a-space>
            <a-dropdown v-if="record.status === 1">
              <a>支付状态</a>
              <template #overlay>
                <a-menu @click="handlePayment(record.id, Number($event.key))">
                  <a-menu-item key="0">待支付</a-menu-item>
                  <a-menu-item key="1">已支付</a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>
            <a-popconfirm v-if="record.status === 1" title="确定终止？" @confirm="handleTerminate(record.id)">
              <a style="color: #ff4d4f">终止</a>
            </a-popconfirm>
          </a-space>
        </template>
      </template>
    </a-table>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { getRentalList, updatePaymentStatus, terminateRental } from '@/api/rental'
import { formatDate } from '@/utils/format'
import type { Rental } from '@/types'

const rentals = ref<Rental[]>([])
const loading = ref(false)
const pagination = ref({ current: 1, pageSize: 10, total: 0 })
const query = reactive({ status: undefined as number | undefined })

const paymentTexts: Record<number, string> = { 0: '待支付', 1: '已支付' }
const paymentColors: Record<number, string> = { 0: 'warning', 1: 'success' }
const statusTexts: Record<number, string> = { 1: '租赁中', 2: '已到期', 3: '提前终止' }
const statusColors: Record<number, string> = { 1: 'success', 2: 'default', 3: 'error' }

const columns = [
  { title: '摊位号', key: 'stall' },
  { title: '租户', key: 'user' },
  { title: '租赁周期', key: 'period' },
  { title: '租金', dataIndex: 'rentAmount' },
  { title: '押金', dataIndex: 'deposit' },
  { title: '支付状态', key: 'paymentStatus' },
  { title: '状态', key: 'status' },
  { title: '操作', key: 'action' }
]

async function fetchData() {
  loading.value = true
  try {
    const res = await getRentalList({ ...query, pageNum: pagination.value.current, pageSize: pagination.value.pageSize })
    rentals.value = res.data.records
    pagination.value.total = res.data.total
  } finally { loading.value = false }
}

function handleTableChange(pag: any) { pagination.value.current = pag.current; fetchData() }

async function handlePayment(id: number, status: number) {
  await updatePaymentStatus(id, status)
  message.success('已更新')
  fetchData()
}

async function handleTerminate(id: number) {
  await terminateRental(id)
  message.success('已终止')
  fetchData()
}

onMounted(fetchData)
</script>

<style scoped>
.toolbar { margin-bottom: 16px; }
</style>
