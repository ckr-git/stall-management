<template>
  <div class="submit-application">
    <div class="container">
      <a-card title="提交申请" :bordered="false">
        <a-spin :spinning="loading">
          <div v-if="stall" class="stall-info">
            <a-descriptions :column="3" bordered size="small">
              <a-descriptions-item label="摊位号">{{ stall.stallNo }}</a-descriptions-item>
              <a-descriptions-item label="类型">{{ stall.typeName }}</a-descriptions-item>
              <a-descriptions-item label="月租金">¥{{ stall.rentPrice }}</a-descriptions-item>
            </a-descriptions>
          </div>
          <a-form :model="form" :rules="rules" ref="formRef" layout="vertical" @finish="handleSubmit" style="max-width: 600px; margin-top: 24px">
            <a-form-item label="经营类型" name="businessType">
              <a-input v-model:value="form.businessType" placeholder="如：餐饮、服装、日用品等" />
            </a-form-item>
            <a-form-item label="经营描述" name="businessDescription">
              <a-textarea v-model:value="form.businessDescription" :rows="4" placeholder="请描述您的经营计划" />
            </a-form-item>
            <a-form-item label="期望开始日期" name="expectedStartDate">
              <a-date-picker v-model:value="form.expectedStartDate" style="width: 100%" />
            </a-form-item>
            <a-form-item label="租赁时长(月)" name="leaseDuration">
              <a-input-number v-model:value="form.leaseDuration" :min="1" :max="36" style="width: 100%" />
            </a-form-item>
            <a-form-item>
              <a-space>
                <a-button type="primary" html-type="submit" :loading="submitting">提交申请</a-button>
                <a-button @click="router.back()">取消</a-button>
              </a-space>
            </a-form-item>
          </a-form>
        </a-spin>
      </a-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'
import { getStallById } from '@/api/stall'
import { createApplication } from '@/api/application'
import type { Stall } from '@/types'

const router = useRouter()
const route = useRoute()
const formRef = ref()
const stall = ref<Stall | null>(null)
const loading = ref(false)
const submitting = ref(false)

const form = reactive({
  businessType: '',
  businessDescription: '',
  expectedStartDate: null as any,
  leaseDuration: 12
})

const rules = {
  businessType: [{ required: true, message: '请输入经营类型' }],
  businessDescription: [{ required: true, message: '请输入经营描述' }],
  expectedStartDate: [{ required: true, message: '请选择期望开始日期' }],
  leaseDuration: [{ required: true, message: '请输入租赁时长' }]
}

onMounted(async () => {
  loading.value = true
  try {
    const res = await getStallById(Number(route.params.stallId))
    stall.value = res.data
  } finally {
    loading.value = false
  }
})

async function handleSubmit() {
  submitting.value = true
  try {
    const startDate = dayjs(form.expectedStartDate).format('YYYY-MM-DD')
    const endDate = dayjs(form.expectedStartDate).add(form.leaseDuration, 'month').format('YYYY-MM-DD')
    await createApplication({
      stallId: Number(route.params.stallId),
      businessType: form.businessType,
      reason: form.businessDescription,
      startDate,
      endDate
    })
    message.success('申请提交成功')
    router.push('/application')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.container { max-width: 1200px; margin: 0 auto; padding: 24px; }
.stall-info { margin-bottom: 24px; }
</style>
