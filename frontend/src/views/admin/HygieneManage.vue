<template>
  <div class="hygiene-manage">
    <div class="toolbar">
      <a-space>
        <a-select v-model:value="query.result" placeholder="整改状态" style="width: 120px" allowClear @change="fetchData">
          <a-select-option value="合格">合格</a-select-option>
          <a-select-option value="不合格">不合格</a-select-option>
          <a-select-option value="优秀">优秀</a-select-option>
          <a-select-option value="良好">良好</a-select-option>
        </a-select>
        <a-button type="primary" @click="handleAdd">新增检查</a-button>
      </a-space>
    </div>
    <a-table :columns="columns" :data-source="records" :loading="loading" :pagination="pagination" @change="handleTableChange" row-key="id">
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'stall'">{{ record.stallNo }}</template>
        <template v-if="column.key === 'score'">
          <a-tag :color="record.score >= 80 ? 'success' : record.score >= 60 ? 'warning' : 'error'">
            {{ record.score }}分
          </a-tag>
        </template>
        <template v-if="column.key === 'rectificationStatus'">
          <a-tag :color="rectColors[record.status]">{{ rectTexts[record.status] }}</a-tag>
        </template>
        <template v-if="column.key === 'action'">
          <a-space>
            <a @click="handleEdit(record)">编辑</a>
            <a-dropdown v-if="record.status === 0">
              <a>整改状态</a>
              <template #overlay>
                <a-menu @click="handleRect(record.id, Number($event.key))">
                  <a-menu-item key="1">已整改</a-menu-item>
                  <a-menu-item key="2">无需整改</a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>
          </a-space>
        </template>
      </template>
    </a-table>
    <a-modal v-model:open="modalVisible" :title="editForm.id ? '编辑检查' : '新增检查'" @ok="handleSave">
      <a-form :model="editForm" layout="vertical">
        <a-form-item label="摊位" v-if="!editForm.id">
          <a-select v-model:value="editForm.stallId" placeholder="选择摊位">
            <a-select-option v-for="s in stalls" :key="s.id" :value="s.id">{{ s.stallNo }}</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="检查日期">
          <a-date-picker v-model:value="editForm.inspectionDate" style="width: 100%" />
        </a-form-item>
        <a-form-item label="评分">
          <a-input-number v-model:value="editForm.score" :min="0" :max="100" style="width: 100%" />
        </a-form-item>
        <a-form-item label="问题描述">
          <a-textarea v-model:value="editForm.problems" :rows="3" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'
import { getHygieneList, createHygiene, updateHygiene, updateRectificationStatus } from '@/api/hygiene'
import { formatDate } from '@/utils/format'
import { getStallList } from '@/api/stall'
import type { Hygiene, Stall } from '@/types'

const records = ref<Hygiene[]>([])
const stalls = ref<Stall[]>([])
const loading = ref(false)
const modalVisible = ref(false)
const pagination = ref({ current: 1, pageSize: 10, total: 0 })
const query = reactive({ result: undefined as string | undefined })
const editForm = reactive({ id: 0, stallId: undefined as number | undefined, inspectionDate: null as any, score: 80, problems: '' })

const rectTexts: Record<number, string> = { 0: '待整改', 1: '已整改', 2: '无需整改' }
const rectColors: Record<number, string> = { 0: 'warning', 1: 'success', 2: 'default' }

const columns = [
  { title: '摊位号', key: 'stall' },
  { title: '检查日期', dataIndex: 'inspectionDate', customRender: ({ text }: any) => formatDate(text) },
  { title: '评分', key: 'score' },
  { title: '问题', dataIndex: 'problems', ellipsis: true },
  { title: '整改状态', key: 'rectificationStatus' },
  { title: '操作', key: 'action' }
]

async function fetchData() {
  loading.value = true
  try {
    const res = await getHygieneList({ ...query, pageNum: pagination.value.current, pageSize: pagination.value.pageSize })
    records.value = res.data.records
    pagination.value.total = res.data.total
  } finally { loading.value = false }
}

function handleTableChange(pag: any) { pagination.value.current = pag.current; fetchData() }

function handleAdd() {
  Object.assign(editForm, { id: 0, stallId: undefined, inspectionDate: dayjs(), score: 80, problems: '' })
  modalVisible.value = true
}

function handleEdit(record: Hygiene) {
  Object.assign(editForm, {
    id: record.id, stallId: record.stallId,
    inspectionDate: record.inspectionDate ? dayjs(record.inspectionDate) : null,
    score: record.score, problems: record.problems || ''
  })
  modalVisible.value = true
}

async function handleSave() {
  const data = {
    stallId: editForm.stallId!,
    inspectionDate: dayjs(editForm.inspectionDate).format('YYYY-MM-DD'),
    score: editForm.score,
    problems: editForm.problems,
    suggestions: ''
  }
  if (editForm.id) {
    await updateHygiene(editForm.id, data)
  } else {
    await createHygiene(data)
  }
  message.success('保存成功')
  modalVisible.value = false
  fetchData()
}

async function handleRect(id: number, status: number) {
  await updateRectificationStatus(id, status)
  message.success('已更新')
  fetchData()
}

onMounted(async () => {
  const res = await getStallList({ pageNum: 1, pageSize: 100 })
  stalls.value = res.data.records
  fetchData()
})
</script>

<style scoped>
.toolbar { margin-bottom: 16px; }
</style>
