<template>
  <div class="stall-manage">
    <div class="toolbar">
      <a-space>
        <a-input-search v-model:value="query.stallNo" placeholder="搜索摊位号" @search="fetchData" />
        <a-select v-model:value="query.typeId" placeholder="类型" style="width: 120px" allowClear @change="fetchData">
          <a-select-option v-for="t in stallTypes" :key="t.id" :value="t.id">{{ t.name }}</a-select-option>
        </a-select>
        <a-select v-model:value="query.status" placeholder="状态" style="width: 120px" allowClear @change="fetchData">
          <a-select-option :value="0">空闲</a-select-option>
          <a-select-option :value="1">已租</a-select-option>
          <a-select-option :value="2">维护中</a-select-option>
        </a-select>
        <a-button type="primary" @click="handleAdd">新增摊位</a-button>
      </a-space>
    </div>
    <a-table :columns="columns" :data-source="stalls" :loading="loading" :pagination="pagination" @change="handleTableChange" row-key="id">
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'type'">{{ record.typeName }}</template>
        <template v-if="column.key === 'status'">
          <a-tag :color="statusColors[record.status]">{{ statusTexts[record.status] }}</a-tag>
        </template>
        <template v-if="column.key === 'action'">
          <a-space>
            <a @click="handleEdit(record)">编辑</a>
            <a-dropdown>
              <a>状态 <DownOutlined /></a>
              <template #overlay>
                <a-menu @click="handleStatusChange(record.id, Number($event.key))">
                  <a-menu-item key="0">空闲</a-menu-item>
                  <a-menu-item key="1">已租</a-menu-item>
                  <a-menu-item key="2">维护中</a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>
            <a-popconfirm title="确定删除？" @confirm="handleDelete(record.id)">
              <a style="color: #ff4d4f">删除</a>
            </a-popconfirm>
          </a-space>
        </template>
      </template>
    </a-table>
    <a-modal v-model:open="modalVisible" :title="editForm.id ? '编辑摊位' : '新增摊位'" @ok="handleSave">
      <a-form :model="editForm" layout="vertical">
        <a-form-item label="摊位号"><a-input v-model:value="editForm.stallNo" /></a-form-item>
        <a-form-item label="摊位名称"><a-input v-model:value="editForm.name" /></a-form-item>
        <a-form-item label="类型">
          <a-select v-model:value="editForm.typeId">
            <a-select-option v-for="t in stallTypes" :key="t.id" :value="t.id">{{ t.name }}</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="面积(㎡)"><a-input-number v-model:value="editForm.area" :min="1" style="width: 100%" /></a-form-item>
        <a-form-item label="位置"><a-input v-model:value="editForm.location" /></a-form-item>
        <a-form-item label="月租金"><a-input-number v-model:value="editForm.rentPrice" :min="0" style="width: 100%" /></a-form-item>
        <a-form-item label="描述"><a-textarea v-model:value="editForm.description" /></a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { DownOutlined } from '@ant-design/icons-vue'
import { getStallList, createStall, updateStall, updateStallStatus, deleteStall } from '@/api/stall'
import { getAllStallTypes } from '@/api/stallType'
import type { Stall, StallType } from '@/types'

const stalls = ref<Stall[]>([])
const stallTypes = ref<StallType[]>([])
const loading = ref(false)
const modalVisible = ref(false)
const pagination = ref({ current: 1, pageSize: 10, total: 0 })
const query = reactive({ stallNo: '', typeId: undefined as number | undefined, status: undefined as number | undefined })
const editForm = reactive({ id: 0, stallNo: '', name: '', typeId: undefined as number | undefined, area: 10, location: '', rentPrice: 0, description: '' })

const statusTexts: Record<number, string> = { 0: '空闲', 1: '已租', 2: '维护中' }
const statusColors: Record<number, string> = { 0: 'green', 1: 'blue', 2: 'orange' }

const columns = [
  { title: '摊位号', dataIndex: 'stallNo' },
  { title: '名称', dataIndex: 'name' },
  { title: '类型', key: 'type' },
  { title: '面积(㎡)', dataIndex: 'area' },
  { title: '位置', dataIndex: 'location' },
  { title: '月租金', dataIndex: 'rentPrice' },
  { title: '状态', key: 'status' },
  { title: '操作', key: 'action' }
]

async function fetchData() {
  loading.value = true
  try {
    const res = await getStallList({ ...query, keyword: query.stallNo, pageNum: pagination.value.current, pageSize: pagination.value.pageSize })
    stalls.value = res.data.records
    pagination.value.total = res.data.total
  } finally { loading.value = false }
}

function handleTableChange(pag: any) { pagination.value.current = pag.current; fetchData() }

function handleAdd() {
  Object.assign(editForm, { id: 0, stallNo: '', name: '', typeId: stallTypes.value[0]?.id, area: 10, location: '', rentPrice: 0, description: '' })
  modalVisible.value = true
}

function handleEdit(record: Stall) {
  Object.assign(editForm, { id: record.id, stallNo: record.stallNo, name: (record as any).name || '', typeId: record.typeId, area: record.area, location: record.location, rentPrice: record.rentPrice, description: record.description || '' })
  modalVisible.value = true
}

async function handleSave() {
  if (editForm.id) {
    await updateStall(editForm.id, editForm)
  } else {
    await createStall(editForm as any)
  }
  message.success('保存成功')
  modalVisible.value = false
  fetchData()
}

async function handleStatusChange(id: number, status: number) {
  await updateStallStatus(id, status)
  message.success('状态已更新')
  fetchData()
}

async function handleDelete(id: number) {
  await deleteStall(id)
  message.success('已删除')
  fetchData()
}

onMounted(async () => {
  const res = await getAllStallTypes()
  stallTypes.value = res.data
  fetchData()
})
</script>

<style scoped>
.toolbar { margin-bottom: 16px; }
</style>
