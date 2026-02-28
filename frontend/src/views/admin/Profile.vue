<template>
  <div class="admin-profile">
    <a-row :gutter="24">
      <a-col :span="12">
        <a-card title="个人信息" :bordered="false">
          <a-form :model="userForm" layout="vertical" @finish="handleUpdateInfo">
            <a-form-item label="用户名">
              <a-input :value="userStore.user?.username" disabled />
            </a-form-item>
            <a-form-item label="昵称" name="nickname">
              <a-input v-model:value="userForm.nickname" />
            </a-form-item>
            <a-form-item label="手机号" name="phone">
              <a-input v-model:value="userForm.phone" />
            </a-form-item>
            <a-form-item>
              <a-button type="primary" html-type="submit" :loading="infoLoading">保存</a-button>
            </a-form-item>
          </a-form>
        </a-card>
      </a-col>
      <a-col :span="12">
        <a-card title="修改密码" :bordered="false">
          <a-form :model="pwdForm" :rules="pwdRules" layout="vertical" @finish="handleUpdatePwd">
            <a-form-item label="原密码" name="oldPassword">
              <a-input-password v-model:value="pwdForm.oldPassword" />
            </a-form-item>
            <a-form-item label="新密码" name="newPassword">
              <a-input-password v-model:value="pwdForm.newPassword" />
            </a-form-item>
            <a-form-item label="确认密码" name="confirmPassword">
              <a-input-password v-model:value="pwdForm.confirmPassword" />
            </a-form-item>
            <a-form-item>
              <a-button type="primary" html-type="submit" :loading="pwdLoading">修改</a-button>
            </a-form-item>
          </a-form>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watchEffect } from 'vue'
import { message } from 'ant-design-vue'
import { useUserStore } from '@/stores/user'
import { updatePassword } from '@/api/auth'
import { updateProfile } from '@/api/user'

const userStore = useUserStore()
const infoLoading = ref(false)
const pwdLoading = ref(false)

const userForm = reactive({ nickname: '', phone: '' })
const pwdForm = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })

watchEffect(() => {
  if (userStore.user) {
    userForm.nickname = userStore.user.nickname
    userForm.phone = userStore.user.phone
  }
})

const pwdRules = {
  oldPassword: [{ required: true, message: '请输入原密码' }],
  newPassword: [{ required: true, message: '请输入新密码' }, { min: 6, message: '密码至少6位' }],
  confirmPassword: [
    { required: true, message: '请确认密码' },
    { validator: (_: any, value: string) => value === pwdForm.newPassword ? Promise.resolve() : Promise.reject('两次密码不一致') }
  ]
}

async function handleUpdateInfo() {
  infoLoading.value = true
  try {
    await updateProfile(userForm)
    await userStore.fetchUserInfo()
    message.success('保存成功')
  } finally {
    infoLoading.value = false
  }
}

async function handleUpdatePwd() {
  pwdLoading.value = true
  try {
    await updatePassword({ oldPassword: pwdForm.oldPassword, newPassword: pwdForm.newPassword })
    message.success('密码修改成功')
    pwdForm.oldPassword = ''
    pwdForm.newPassword = ''
    pwdForm.confirmPassword = ''
  } finally {
    pwdLoading.value = false
  }
}
</script>
