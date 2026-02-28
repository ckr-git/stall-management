<template>
  <div class="my-rental">
    <div class="container">
      <a-card title="我的租赁" :bordered="false">
        <a-table :columns="columns" :data-source="rentals" :loading="loading" :pagination="pagination" @change="handleTableChange" row-key="id">
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'stall'">
              {{ record.stallNo }}
            </template>
            <template v-if="column.key === 'period'">
              {{ formatDate(record.startDate) }} ~ {{ formatDate(record.endDate) }}
            </template>
            <template v-if="column.key === 'paymentStatus'">
              <a-tag :color="paymentColors[record.paymentStatus]">
                {{ paymentTexts[record.paymentStatus] }}
              </a-tag>
            </template>
            <template v-if="column.key === 'status'">
              <a-tag :color="statusColors[record.status]">
                {{ statusTexts[record.status] }}
              </a-tag>
            </template>
          </template>
        </a-table>
      </a-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getMyRentals } from '@/api/rental'
import { formatDate } from '@/utils/format'
import type { Rental } from '@/types'

const rentals = ref<Rental[]>([])
const loading = ref(false)
const pagination = ref({ current: 1, pageSize: 10, total: 0 })

const paymentTexts: Record<number, string> = { 0: '待支付', 1: '已支付' }
const paymentColors: Record<number, string> = { 0: 'warning', 1: 'success' }
const statusTexts: Record<number, string> = { 1: '租赁中', 2: '已到期', 3: '提前终止' }
const statusColors: Record<number, string> = { 1: 'success', 2: 'default', 3: 'error' }

const columns = [
  { title: '摊位号', key: 'stall' },
  { title: '租赁周期', key: 'period' },
  { title: '租金', dataIndex: 'rentAmount' },
  { title: '押金', dataIndex: 'deposit' },
  { title: '支付状态', key: 'paymentStatus' },
  { title: '状态', key: 'status' }
]

async function fetchData() {
  loading.value = true
  try {
    const res = await getMyRentals({ pageNum: pagination.value.current, pageSize: pagination.value.pageSize })
    rentals.value = res.data.records
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
