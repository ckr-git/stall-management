<template>
  <div class="announcement-manage">
    <div class="toolbar">
      <a-space>
        <a-select v-model:value="query.status" placeholder="状态" style="width: 120px" allowClear @change="fetchData">
          <a-select-option :value="0">草稿</a-select-option>
          <a-select-option :value="1">已发布</a-select-option>
        </a-select>
        <a-button type="primary" @click="handleAdd">新增公告</a-button>
      </a-space>
    </div>
    <a-table :columns="columns" :data-source="announcements" :loading="loading" :pagination="pagination" @change="handleTableChange" row-key="id">
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'type'">
          <a-tag :color="record.type === 1 ? 'red' : 'blue'">{{ record.type === 1 ? '重要' : '普通' }}</a-tag>
        </template>
        <template v-if="column.key === 'status'">
          <a-tag :color="record.status === 1 ? 'success' : 'default'">{{ record.status === 1 ? '已发布' : '草稿' }}</a-tag>
        </template>
        <template v-if="column.key === 'action'">
          <a-space>
            <a @click="handleEdit(record)">编辑</a>
            <a v-if="record.status === 0" @click="handlePublish(record.id)">发布</a>
            <a v-else @click="handleUnpublish(record.id)">下架</a>
            <a-popconfirm title="确定删除？" @confirm="handleDelete(record.id)">
              <a style="color: #ff4d4f">删除</a>
            </a-popconfirm>
          </a-space>
        </template>
      </template>
    </a-table>
    <a-modal v-model:open="modalVisible" :title="editForm.id ? '编辑公告' : '新增公告'" @ok="handleSave" width="700px">
      <a-form :model="editForm" layout="vertical">
        <a-form-item label="标题"><a-input v-model:value="editForm.title" /></a-form-item>
        <a-form-item label="类型">
          <a-radio-group v-model:value="editForm.type">
            <a-radio :value="0">普通</a-radio>
            <a-radio :value="1">重要</a-radio>
          </a-radio-group>
        </a-form-item>
        <a-form-item label="内容"><a-textarea v-model:value="editForm.content" :rows="8" /></a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { getAnnouncementList, createAnnouncement, updateAnnouncement, publishAnnouncement, unpublishAnnouncement, deleteAnnouncement } from '@/api/announcement'
import { formatDateTime } from '@/utils/format'
import type { Announcement } from '@/types'

const announcements = ref<Announcement[]>([])
const loading = ref(false)
const modalVisible = ref(false)
const pagination = ref({ current: 1, pageSize: 10, total: 0 })
const query = reactive({ status: undefined as number | undefined })
const editForm = reactive({ id: 0, title: '', type: 0, content: '' })

const columns = [
  { title: '标题', dataIndex: 'title' },
  { title: '类型', key: 'type' },
  { title: '状态', key: 'status' },
  { title: '发布时间', dataIndex: 'publishTime', customRender: ({ text }: any) => formatDateTime(text) },
  { title: '操作', key: 'action' }
]

async function fetchData() {
  loading.value = true
  try {
    const res = await getAnnouncementList({ ...query, pageNum: pagination.value.current, pageSize: pagination.value.pageSize })
    announcements.value = res.data.records
    pagination.value.total = res.data.total
  } finally { loading.value = false }
}

function handleTableChange(pag: any) { pagination.value.current = pag.current; fetchData() }

function handleAdd() {
  Object.assign(editForm, { id: 0, title: '', type: 0, content: '' })
  modalVisible.value = true
}

function handleEdit(record: Announcement) {
  Object.assign(editForm, { id: record.id, title: record.title, type: record.type, content: record.content })
  modalVisible.value = true
}

async function handleSave() {
  if (editForm.id) {
    await updateAnnouncement(editForm.id, editForm)
  } else {
    await createAnnouncement(editForm)
  }
  message.success('保存成功')
  modalVisible.value = false
  fetchData()
}

async function handlePublish(id: number) {
  await publishAnnouncement(id)
  message.success('已发布')
  fetchData()
}

async function handleUnpublish(id: number) {
  await unpublishAnnouncement(id)
  message.success('已下架')
  fetchData()
}

async function handleDelete(id: number) {
  await deleteAnnouncement(id)
  message.success('已删除')
  fetchData()
}

onMounted(fetchData)
</script>

<style scoped>
.toolbar { margin-bottom: 16px; }
</style>
