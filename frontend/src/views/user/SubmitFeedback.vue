<template>
  <div class="submit-feedback">
    <div class="container">
      <a-card title="提交反馈" :bordered="false">
        <a-form :model="form" :rules="rules" ref="formRef" layout="vertical" @finish="handleSubmit" style="max-width: 600px">
          <a-form-item label="反馈类型" name="type">
            <a-radio-group v-model:value="form.type">
              <a-radio :value="1">投诉</a-radio>
              <a-radio :value="2">建议</a-radio>
              <a-radio :value="3">咨询</a-radio>
            </a-radio-group>
          </a-form-item>
          <a-form-item label="标题" name="title">
            <a-input v-model:value="form.title" placeholder="请输入反馈标题" />
          </a-form-item>
          <a-form-item label="内容" name="content">
            <a-textarea v-model:value="form.content" :rows="6" placeholder="请详细描述您的反馈内容" />
          </a-form-item>
          <a-form-item>
            <a-space>
              <a-button type="primary" html-type="submit" :loading="loading">提交</a-button>
              <a-button @click="router.back()">取消</a-button>
            </a-space>
          </a-form-item>
        </a-form>
      </a-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { createFeedback } from '@/api/feedback'

const router = useRouter()
const formRef = ref()
const loading = ref(false)

const form = reactive({ type: 2, title: '', content: '' })

const rules = {
  type: [{ required: true, message: '请选择反馈类型' }],
  title: [{ required: true, message: '请输入标题' }],
  content: [{ required: true, message: '请输入内容' }]
}

async function handleSubmit() {
  loading.value = true
  try {
    await createFeedback(form)
    message.success('反馈提交成功')
    router.push('/feedback')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.container { max-width: 1200px; margin: 0 auto; padding: 24px; }
</style>
