<template>
  <div class="dashboard">
    <!-- 统计卡片 -->
    <a-row :gutter="[16, 16]">
      <a-col :xs="24" :sm="12" :lg="6">
        <div class="stat-card stat-users">
          <div class="stat-icon">👥</div>
          <div class="stat-content">
            <div class="stat-value">{{ stats.userCount }}</div>
            <div class="stat-label">USER COUNT</div>
          </div>
          <div class="stat-badge">用户总数</div>
        </div>
      </a-col>
      <a-col :xs="24" :sm="12" :lg="6">
        <div class="stat-card stat-stalls">
          <div class="stat-icon">🏪</div>
          <div class="stat-content">
            <div class="stat-value">{{ stats.stallCount }}</div>
            <div class="stat-label">STALL COUNT</div>
          </div>
          <div class="stat-badge">摊位总数</div>
        </div>
      </a-col>
      <a-col :xs="24" :sm="12" :lg="6">
        <div class="stat-card stat-pending">
          <div class="stat-icon">📋</div>
          <div class="stat-content">
            <div class="stat-value">{{ stats.pendingApplications }}</div>
            <div class="stat-label">PENDING</div>
          </div>
          <div class="stat-badge">待审核</div>
        </div>
      </a-col>
      <a-col :xs="24" :sm="12" :lg="6">
        <div class="stat-card stat-feedback">
          <div class="stat-icon">⚠️</div>
          <div class="stat-content">
            <div class="stat-value">{{ stats.pendingFeedbacks }}</div>
            <div class="stat-label">FEEDBACK</div>
          </div>
          <div class="stat-badge">待处理</div>
        </div>
      </a-col>
    </a-row>

    <!-- 图表区域 -->
    <a-row :gutter="16" style="margin-top: 20px">
      <a-col :xs="24" :lg="16">
        <div class="chart-card">
          <div class="chart-header">
            <span class="chart-title">▸ 摊位出租率趋势 / OCCUPANCY TREND</span>
          </div>
          <div class="chart-container">
            <v-chart class="chart" :option="occupancyOption" autoresize />
          </div>
        </div>
      </a-col>
      <a-col :xs="24" :lg="8">
        <div class="chart-card">
          <div class="chart-header">
            <span class="chart-title">▸ 摊位类型分布 / TYPE DIST</span>
          </div>
          <div class="chart-container">
            <v-chart class="chart" :option="typeDistOption" autoresize />
          </div>
        </div>
      </a-col>
    </a-row>

    <!-- 列表区域 -->
    <a-row :gutter="16" style="margin-top: 20px">
      <a-col :xs="24" :lg="12">
        <div class="list-card">
          <div class="list-header">
            <span class="list-title">▸ 最近申请 / RECENT APPLICATIONS</span>
          </div>
          <a-table :columns="appColumns" :data-source="recentApplications" :pagination="false" size="small">
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'status'">
                <span class="status-badge" :class="'status-' + record.status">
                  {{ statusTexts[record.status] }}
                </span>
              </template>
            </template>
          </a-table>
        </div>
      </a-col>
      <a-col :xs="24" :lg="12">
        <div class="list-card">
          <div class="list-header">
            <span class="list-title">▸ 最近反馈 / RECENT FEEDBACK</span>
          </div>
          <a-table :columns="fbColumns" :data-source="recentFeedbacks" :pagination="false" size="small">
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'status'">
                <span class="status-badge" :class="record.status === 0 ? 'status-0' : 'status-1'">
                  {{ record.status === 0 ? '待处理' : '已回复' }}
                </span>
              </template>
            </template>
          </a-table>
        </div>
      </a-col>
    </a-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart, PieChart } from 'echarts/charts'
import { GridComponent, TooltipComponent, LegendComponent, TitleComponent } from 'echarts/components'
import VChart from 'vue-echarts'
import { getUserList } from '@/api/user'
import { getStallList } from '@/api/stall'
import { getApplicationList } from '@/api/application'
import { getFeedbackList } from '@/api/feedback'
import { getAllStallTypes } from '@/api/stallType'

use([CanvasRenderer, LineChart, PieChart, GridComponent, TooltipComponent, LegendComponent, TitleComponent])

const stats = reactive({ userCount: 0, stallCount: 0, pendingApplications: 0, pendingFeedbacks: 0, rentedCount: 0 })
const recentApplications = ref<any[]>([])
const recentFeedbacks = ref<any[]>([])
const stallTypes = ref<any[]>([])

const statusTexts: Record<number, string> = { 0: '待审核', 1: '已通过', 2: '已拒绝', 3: '已取消' }

const appColumns = [
  { title: '摊位', dataIndex: 'stallNo' },
  { title: '申请人', dataIndex: 'username' },
  { title: '状态', key: 'status' }
]
const fbColumns = [
  { title: '标题', dataIndex: 'title' },
  { title: '用户', dataIndex: 'username' },
  { title: '状态', key: 'status' }
]

// 出租率趋势图配置
const occupancyOption = computed(() => ({
  backgroundColor: 'transparent',
  tooltip: {
    trigger: 'axis',
    backgroundColor: '#2d3139',
    borderColor: '#4a4540',
    textStyle: { color: '#d4cfc5' }
  },
  grid: {
    left: '3%', right: '4%', bottom: '3%', containLabel: true
  },
  xAxis: {
    type: 'category',
    data: ['1月', '2月', '3月', '4月', '5月', '6月'],
    axisLine: { lineStyle: { color: '#4a4540' } },
    axisLabel: { color: '#9a9590' }
  },
  yAxis: {
    type: 'value',
    max: 100,
    axisLine: { lineStyle: { color: '#4a4540' } },
    axisLabel: { color: '#9a9590', formatter: '{value}%' },
    splitLine: { lineStyle: { color: '#3a3f4a', type: 'dashed' } }
  },
  series: [{
    name: '出租率',
    type: 'line',
    smooth: true,
    data: [65, 72, 78, 85, 82, 88],
    lineStyle: { color: '#cd853f', width: 3 },
    itemStyle: { color: '#cd853f' },
    areaStyle: {
      color: {
        type: 'linear', x: 0, y: 0, x2: 0, y2: 1,
        colorStops: [
          { offset: 0, color: 'rgba(205, 133, 63, 0.4)' },
          { offset: 1, color: 'rgba(205, 133, 63, 0.05)' }
        ]
      }
    }
  }]
}))

// 摊位类型分布图配置
const typeDistOption = computed(() => ({
  backgroundColor: 'transparent',
  tooltip: {
    trigger: 'item',
    backgroundColor: '#2d3139',
    borderColor: '#4a4540',
    textStyle: { color: '#d4cfc5' }
  },
  series: [{
    name: '摊位类型',
    type: 'pie',
    radius: ['40%', '70%'],
    center: ['50%', '55%'],
    avoidLabelOverlap: false,
    itemStyle: {
      borderRadius: 4,
      borderColor: '#1a1d21',
      borderWidth: 2
    },
    label: {
      show: true,
      position: 'outside',
      color: '#9a9590',
      fontSize: 11
    },
    data: stallTypes.value.length > 0 ? stallTypes.value.map((t, i) => ({
      value: Math.floor(Math.random() * 20) + 5,
      name: t.name,
      itemStyle: { color: ['#8b7355', '#5d7a5d', '#4a6785', '#c4a35a'][i % 4] }
    })) : [
      { value: 12, name: '餐饮', itemStyle: { color: '#8b7355' } },
      { value: 8, name: '零售', itemStyle: { color: '#5d7a5d' } },
      { value: 6, name: '娱乐', itemStyle: { color: '#4a6785' } },
      { value: 4, name: '手工艺', itemStyle: { color: '#c4a35a' } }
    ]
  }]
}))

onMounted(async () => {
  const [userRes, stallRes, appRes, fbRes, typeRes] = await Promise.all([
    getUserList({ pageNum: 1, pageSize: 1 }),
    getStallList({ pageNum: 1, pageSize: 1 }),
    getApplicationList({ pageNum: 1, pageSize: 5, status: 0 }),
    getFeedbackList({ pageNum: 1, pageSize: 5, status: 0 }),
    getAllStallTypes()
  ])
  stats.userCount = userRes.data.total
  stats.stallCount = stallRes.data.total
  stats.pendingApplications = appRes.data.total
  stats.pendingFeedbacks = fbRes.data.total
  recentApplications.value = appRes.data.records
  recentFeedbacks.value = fbRes.data.records
  stallTypes.value = typeRes.data || []
})
</script>

<style scoped>
.dashboard {
  padding: 8px;
}

/* 统计卡片 - 军事风格 */
.stat-card {
  background: var(--bg-medium);
  border: 1px solid var(--border-color);
  border-radius: 4px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  position: relative;
  overflow: hidden;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
}

.stat-users::before { background: #4a6785; }
.stat-stalls::before { background: #5d7a5d; }
.stat-pending::before { background: #c4a35a; }
.stat-feedback::before { background: #a83232; }

.stat-icon {
  font-size: 32px;
  opacity: 0.8;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: var(--text-bright);
  font-family: var(--font-military);
  line-height: 1;
}

.stat-label {
  font-size: 10px;
  color: var(--text-muted);
  font-family: var(--font-military);
  letter-spacing: 2px;
  margin-top: 4px;
}

.stat-badge {
  position: absolute;
  top: 8px;
  right: 8px;
  font-size: 10px;
  color: var(--text-secondary);
  font-family: var(--font-military);
  background: var(--bg-dark);
  padding: 2px 8px;
  border-radius: 2px;
}

/* 图表卡片 */
.chart-card, .list-card {
  background: var(--bg-medium);
  border: 1px solid var(--border-color);
  border-radius: 4px;
  overflow: hidden;
}

.chart-header, .list-header {
  padding: 12px 16px;
  background: linear-gradient(180deg, var(--bg-light) 0%, var(--bg-medium) 100%);
  border-bottom: 1px solid var(--border-rust);
}

.chart-title, .list-title {
  color: var(--rust-orange);
  font-family: var(--font-military);
  font-size: 12px;
  letter-spacing: 2px;
}

.chart-container {
  padding: 16px;
  width: 100%;
  min-width: 0;
  overflow: hidden;
}

.chart {
  height: 280px;
  min-height: 280px;
  width: 100%;
  display: block;
}

/* 状态徽章 */
.status-badge {
  display: inline-block;
  padding: 2px 8px;
  font-size: 10px;
  font-family: var(--font-military);
  text-transform: uppercase;
  letter-spacing: 1px;
  border-radius: 2px;
}

.status-0 {
  background: rgba(196, 163, 90, 0.2);
  color: var(--warning-color);
  border: 1px solid var(--warning-color);
}

.status-1 {
  background: rgba(93, 122, 93, 0.2);
  color: #8fbc8f;
  border: 1px solid var(--success-color);
}

.status-2 {
  background: rgba(168, 50, 50, 0.2);
  color: #e57373;
  border: 1px solid var(--error-color);
}

.status-3 {
  background: rgba(108, 117, 125, 0.2);
  color: var(--text-secondary);
  border: 1px solid var(--concrete-gray);
}
</style>
