<template>
  <div class="stall-list">
    <div class="container">
      <a-card :bordered="false">
        <div class="filter-bar">
          <a-space>
            <a-select v-model:value="query.typeId" placeholder="摊位类型" style="width: 150px" allowClear>
              <a-select-option v-for="t in stallTypes" :key="t.id" :value="t.id">{{ t.name }}</a-select-option>
            </a-select>
            <a-select v-model:value="query.status" placeholder="状态" style="width: 120px" allowClear>
              <a-select-option :value="0">空闲</a-select-option>
              <a-select-option :value="1">已租</a-select-option>
              <a-select-option :value="2">维护中</a-select-option>
            </a-select>
            <a-input-search v-model:value="query.keyword" placeholder="搜索摊位号" style="width: 200px" @search="fetchData" />
          </a-space>
        </div>
        <a-list :grid="{ gutter: 16, column: 3 }" :data-source="stalls" :loading="loading">
          <template #renderItem="{ item }">
            <a-list-item>
              <a-card hoverable @click="router.push(`/stall/${item.id}`)">
                <template #cover>
                  <div class="stall-cover" :class="'status-' + item.status">
                    <span class="stall-number">{{ item.stallNo }}</span>
                  </div>
                </template>
                <a-card-meta :title="item.typeName">
                  <template #description>
                    <p>位置：{{ item.location }}</p>
                    <p>面积：{{ item.area }}㎡</p>
                    <p class="price">¥{{ item.rentPrice }}/月</p>
                  </template>
                </a-card-meta>
                <a-tag :color="statusColors[item.status]" class="status-tag">
                  {{ statusTexts[item.status] }}
                </a-tag>
              </a-card>
            </a-list-item>
          </template>
        </a-list>
        <div class="pagination">
          <a-pagination v-model:current="query.pageNum" :total="total" :page-size="query.pageSize" @change="fetchData" />
        </div>
      </a-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { getStallList } from '@/api/stall'
import { getAllStallTypes } from '@/api/stallType'
import type { Stall, StallType } from '@/types'

const router = useRouter()
const stalls = ref<Stall[]>([])
const stallTypes = ref<StallType[]>([])
const loading = ref(false)
const total = ref(0)

const query = reactive({ pageNum: 1, pageSize: 9, typeId: undefined as number | undefined, status: undefined as number | undefined, keyword: '' })
const statusTexts: Record<number, string> = { 0: '空闲', 1: '已租', 2: '维护中' }
const statusColors: Record<number, string> = { 0: 'green', 1: 'blue', 2: 'orange' }

async function fetchData() {
  loading.value = true
  try {
    const res = await getStallList(query)
    stalls.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  const typeRes = await getAllStallTypes()
  stallTypes.value = typeRes.data
  fetchData()
})

watch(() => [query.typeId, query.status], () => { query.pageNum = 1; fetchData() })
</script>

<style scoped>
.stall-list {
  background: var(--bg-color);
  min-height: calc(100vh - 144px);
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 32px 24px;
}

.filter-bar {
  margin-bottom: 24px;
  padding: 16px;
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-left: 3px solid var(--rust-orange);
}

.filter-bar :deep(.ant-select-selector),
.filter-bar :deep(.ant-input) {
  background: var(--bg-medium) !important;
  border-color: var(--border-color) !important;
}

/* 摊位卡片 - 铁皮效果 */
:deep(.ant-card) {
  background: var(--bg-card) !important;
  border: 1px solid var(--border-color) !important;
  overflow: hidden;
  transition: all 0.3s;
}

:deep(.ant-card:hover) {
  border-color: var(--rust-orange) !important;
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.4);
}

:deep(.ant-card-body) {
  padding: 16px;
}

:deep(.ant-card-meta-title) {
  color: var(--text-bright) !important;
  font-family: var(--font-military);
}

:deep(.ant-card-meta-description) {
  color: var(--text-secondary) !important;
}

:deep(.ant-card-meta-description p) {
  margin: 4px 0;
  font-size: 13px;
}

/* 摊位封面 - 工业风格 */
.stall-cover {
  height: 100px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  font-weight: bold;
  font-family: var(--font-military);
  letter-spacing: 2px;
  color: var(--text-bright);
  position: relative;
}

.stall-cover::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: repeating-linear-gradient(
    45deg,
    transparent,
    transparent 10px,
    rgba(0, 0, 0, 0.1) 10px,
    rgba(0, 0, 0, 0.1) 20px
  );
}

/* 状态颜色 */
.status-0 {
  background: linear-gradient(135deg, #5d7a5d 0%, #4a6349 100%);
}

.status-1 {
  background: linear-gradient(135deg, #4a6785 0%, #3d5570 100%);
}

.status-2 {
  background: linear-gradient(135deg, #a08060 0%, #8b7355 100%);
}

/* 摊位编号 */
.stall-number {
  background: rgba(0, 0, 0, 0.4);
  padding: 8px 20px;
  border: 2px solid rgba(255, 255, 255, 0.2);
  position: relative;
  z-index: 1;
}

/* 价格 - 配给券风格 */
.price {
  color: var(--warning-color) !important;
  font-size: 18px;
  font-weight: bold;
  font-family: var(--font-military);
  text-shadow: 0 0 8px var(--glow-warning);
}

.price::before {
  content: '▸ ';
  font-size: 12px;
}

/* 状态标签 - 军用徽章 */
.status-tag {
  position: absolute;
  top: 8px;
  right: 8px;
  z-index: 10;
}

:deep(.ant-tag) {
  font-family: var(--font-military);
  text-transform: uppercase;
  letter-spacing: 1px;
  font-size: 10px;
  padding: 2px 8px;
  border-radius: 2px;
}

/* 分页 */
.pagination {
  text-align: center;
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid var(--border-color);
}
</style>
