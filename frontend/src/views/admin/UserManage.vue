<template>
  <div class="user-manage">
    <div class="toolbar">
      <a-space>
        <a-input-search v-model:value="query.username" placeholder="搜索用户名" @search="fetchData" />
        <a-select v-model:value="query.status" placeholder="状态" style="width: 120px" allowClear @change="fetchData">
          <a-select-option :value="1">正常</a-select-option>
          <a-select-option :value="0">禁用</a-select-option>
        </a-select>
      </a-space>
    </div>
    <a-table :columns="columns" :data-source="users" :loading="loading" :pagination="pagination" @change="handleTableChange" row-key="id">
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'role'">
          <a-tag :color="record.role === 'ADMIN' ? 'red' : 'blue'">{{ record.role === 'ADMIN' ? '管理员' : '用户' }}</a-tag>
        </template>
        <template v-if="column.key === 'status'">
          <a-switch :checked="record.status === 1" @change="handleStatusChange(record)" />
        </template>
        <template v-if="column.key === 'action'">
          <a-space>
            <a @click="handleEdit(record)">编辑</a>
            <a-popconfirm title="确定重置密码为123456？" @confirm="handleResetPwd(record.id)">
              <a>重置密码</a>
            </a-popconfirm>
          </a-space>
        </template>
      </template>
    </a-table>
    <a-modal v-model:open="modalVisible" title="编辑用户" @ok="handleSave">
      <a-form :model="editForm" layout="vertical">
        <a-form-item label="昵称"><a-input v-model:value="editForm.nickname" /></a-form-item>
        <a-form-item label="手机号"><a-input v-model:value="editForm.phone" /></a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { getUserList, updateUser, updateUserStatus, resetPassword } from '@/api/user'
import { formatDateTime } from '@/utils/format'
import type { User } from '@/types'

const users = ref<User[]>([])
const loading = ref(false)
const modalVisible = ref(false)
const pagination = ref({ current: 1, pageSize: 10, total: 0 })
const query = reactive({ username: '', status: undefined as number | undefined })
const editForm = reactive({ id: 0, nickname: '', phone: '' })

const columns = [
  { title: '用户名', dataIndex: 'username' },
  { title: '昵称', dataIndex: 'nickname' },
  { title: '手机', dataIndex: 'phone' },
  { title: '角色', key: 'role' },
  { title: '状态', key: 'status' },
  { title: '注册时间', dataIndex: 'createTime', customRender: ({ text }: any) => formatDateTime(text) },
  { title: '操作', key: 'action' }
]

async function fetchData() {
  loading.value = true
  try {
    const res = await getUserList({ ...query, keyword: query.username, pageNum: pagination.value.current, pageSize: pagination.value.pageSize })
    users.value = res.data.records
    pagination.value.total = res.data.total
  } finally { loading.value = false }
}

function handleTableChange(pag: any) { pagination.value.current = pag.current; fetchData() }

async function handleStatusChange(record: User) {
  await updateUserStatus(record.id, record.status === 1 ? 0 : 1)
  message.success('状态已更新')
  fetchData()
}

function handleEdit(record: User) {
  Object.assign(editForm, { id: record.id, nickname: record.nickname, phone: record.phone })
  modalVisible.value = true
}

async function handleSave() {
  await updateUser(editForm.id, editForm)
  message.success('保存成功')
  modalVisible.value = false
  fetchData()
}

async function handleResetPwd(id: number) {
  await resetPassword(id)
  message.success('密码已重置为123456')
}

onMounted(fetchData)
</script>

<style scoped>
.toolbar { margin-bottom: 16px; }
</style>
