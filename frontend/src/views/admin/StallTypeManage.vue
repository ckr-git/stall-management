<template>
  <div class="stall-type-manage">
    <div class="toolbar">
      <a-button type="primary" @click="handleAdd">新增类型</a-button>
    </div>
    <a-table :columns="columns" :data-source="types" :loading="loading" :pagination="false" row-key="id">
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'action'">
          <a-space>
            <a @click="handleEdit(record)">编辑</a>
            <a-popconfirm title="确定删除？" @confirm="handleDelete(record.id)">
              <a style="color: #ff4d4f">删除</a>
            </a-popconfirm>
          </a-space>
        </template>
      </template>
    </a-table>
    <a-modal v-model:open="modalVisible" :title="editForm.id ? '编辑类型' : '新增类型'" @ok="handleSave">
      <a-form :model="editForm" layout="vertical">
        <a-form-item label="类型名称"><a-input v-model:value="editForm.name" /></a-form-item>
        <a-form-item label="排序"><a-input-number v-model:value="editForm.sortOrder" :min="0" style="width: 100%" /></a-form-item>
        <a-form-item label="描述"><a-textarea v-model:value="editForm.description" /></a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { getStallTypeList, createStallType, updateStallType, deleteStallType } from '@/api/stallType'
import { formatDateTime } from '@/utils/format'
import type { StallType } from '@/types'

const types = ref<StallType[]>([])
const loading = ref(false)
const modalVisible = ref(false)
const editForm = reactive({ id: 0, name: '', sortOrder: 0, description: '' })

const columns = [
  { title: '类型名称', dataIndex: 'name' },
  { title: '排序', dataIndex: 'sortOrder' },
  { title: '描述', dataIndex: 'description' },
  { title: '创建时间', dataIndex: 'createTime', customRender: ({ text }: any) => formatDateTime(text) },
  { title: '操作', key: 'action' }
]

async function fetchData() {
  loading.value = true
  try {
    const res = await getStallTypeList()
    types.value = Array.isArray(res.data) ? res.data : []
  } finally { loading.value = false }
}

function handleAdd() {
  Object.assign(editForm, { id: 0, name: '', sortOrder: 0, description: '' })
  modalVisible.value = true
}

function handleEdit(record: StallType) {
  Object.assign(editForm, record)
  modalVisible.value = true
}

async function handleSave() {
  if (editForm.id) {
    await updateStallType(editForm.id, editForm)
  } else {
    await createStallType(editForm)
  }
  message.success('保存成功')
  modalVisible.value = false
  fetchData()
}

async function handleDelete(id: number) {
  await deleteStallType(id)
  message.success('已删除')
  fetchData()
}

onMounted(fetchData)
</script>

<style scoped>
.toolbar { margin-bottom: 16px; }
</style>
