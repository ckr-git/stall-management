<template>
  <div class="register-container">
    <!-- 暴风雪粒子 -->
    <div class="snow-container">
      <div v-for="n in 40" :key="n" class="snowflake" :style="snowflakeStyle()"></div>
    </div>
    
    <!-- 废墟剪影背景 -->
    <div class="ruins-silhouette"></div>
    
    <!-- 注册框 -->
    <div class="register-box">
      <div class="concrete-texture"></div>
      <div class="warning-stripe"></div>
      
      <div class="box-content">
        <div class="title-section">
          <div class="icon-register">📝</div>
          <h2 class="title">用户注册</h2>
          <p class="subtitle">NEW RECRUIT REGISTRATION</p>
          <div class="divider">
            <span class="divider-text">信息录入</span>
          </div>
        </div>
        
        <a-form :model="form" :rules="rules" ref="formRef" @finish="handleRegister" class="register-form">
          <a-form-item name="username">
            <a-input v-model:value="form.username" placeholder="用户名" size="large">
              <template #prefix><UserOutlined style="color: var(--rust-orange)" /></template>
            </a-input>
          </a-form-item>
          <a-form-item name="password">
            <a-input-password v-model:value="form.password" placeholder="密码（至少6位）" size="large">
              <template #prefix><LockOutlined style="color: var(--rust-orange)" /></template>
            </a-input-password>
          </a-form-item>
          <a-form-item name="nickname">
            <a-input v-model:value="form.nickname" placeholder="昵称" size="large">
              <template #prefix><IdcardOutlined style="color: var(--rust-orange)" /></template>
            </a-input>
          </a-form-item>
          <a-form-item name="phone">
            <a-input v-model:value="form.phone" placeholder="手机号" size="large">
              <template #prefix><PhoneOutlined style="color: var(--rust-orange)" /></template>
            </a-input>
          </a-form-item>
          <a-form-item>
            <a-button type="primary" html-type="submit" size="large" block :loading="loading" class="register-btn">
              <span>提 交 注 册</span>
            </a-button>
          </a-form-item>
        </a-form>
        
        <div class="footer">
          <span class="footer-text">已有账号？</span>
          <router-link to="/login" class="login-link">立即登录</router-link>
        </div>
        
        <div class="bottom-code">
          <span>REG-FORM-v1.2</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { UserOutlined, LockOutlined, IdcardOutlined, PhoneOutlined } from '@ant-design/icons-vue'
import { register } from '@/api/auth'

const router = useRouter()
const formRef = ref()
const loading = ref(false)

const form = reactive({
  username: '',
  password: '',
  nickname: '',
  phone: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名' }],
  password: [{ required: true, message: '请输入密码' }, { min: 6, message: '密码至少6位' }],
  nickname: [{ required: true, message: '请输入昵称' }],
  phone: [{ required: true, message: '请输入手机号' }, { pattern: /^1\d{10}$/, message: '手机号格式不正确' }]
}

// 雪花随机样式
function snowflakeStyle() {
  return {
    left: Math.random() * 100 + '%',
    animationDuration: (Math.random() * 3 + 2) + 's',
    animationDelay: Math.random() * 5 + 's',
    opacity: Math.random() * 0.6 + 0.2,
    fontSize: (Math.random() * 8 + 4) + 'px'
  }
}

async function handleRegister() {
  loading.value = true
  try {
    await register(form)
    message.success('注册成功，请登录')
    router.push('/login')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(180deg, #1a1d21 0%, #252830 50%, #1a1d21 100%);
  position: relative;
  overflow: hidden;
}

/* 暴风雪效果 */
.snow-container {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
  overflow: hidden;
}

.snowflake {
  position: absolute;
  top: -20px;
  color: rgba(245, 245, 245, 0.6);
  animation: snowfall linear infinite;
}

.snowflake::before {
  content: '❄';
}

@keyframes snowfall {
  0% { transform: translateY(0) rotate(0deg) translateX(0); }
  100% { transform: translateY(100vh) rotate(360deg) translateX(50px); }
}

/* 废墟剪影 */
.ruins-silhouette {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 180px;
  background: 
    linear-gradient(to bottom, transparent 40%, rgba(26, 29, 33, 0.9) 100%),
    url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 1200 200'%3E%3Cpath fill='%231a1d21' d='M0,200 L0,150 L50,150 L50,100 L80,100 L80,80 L100,80 L100,120 L130,120 L130,90 L150,90 L150,110 L200,110 L200,60 L220,60 L220,100 L250,100 L250,140 L300,140 L300,80 L320,70 L340,80 L340,120 L400,120 L400,50 L420,50 L420,90 L450,90 L450,130 L500,130 L500,70 L550,70 L550,100 L600,100 L600,60 L650,60 L650,110 L700,110 L700,80 L750,80 L750,130 L800,130 L800,90 L850,90 L850,70 L880,70 L880,110 L920,110 L920,140 L960,140 L960,100 L1000,100 L1000,60 L1050,60 L1050,120 L1100,120 L1100,80 L1150,80 L1150,140 L1200,140 L1200,200 Z'/%3E%3C/svg%3E") center bottom no-repeat;
  background-size: cover, 100% 100%;
  opacity: 0.8;
}

/* 注册框 */
.register-box {
  width: 420px;
  position: relative;
  background: linear-gradient(180deg, #3a3f4a 0%, #2d3139 50%, #252830 100%);
  border: 2px solid #4a4540;
  border-radius: 4px;
  box-shadow: 
    0 20px 60px rgba(0, 0, 0, 0.5),
    inset 0 1px 0 rgba(255, 255, 255, 0.05);
  z-index: 10;
}

.concrete-texture {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: 
    radial-gradient(circle at 20% 30%, rgba(74, 69, 64, 0.3) 0%, transparent 50%),
    radial-gradient(circle at 80% 70%, rgba(74, 69, 64, 0.2) 0%, transparent 40%);
  pointer-events: none;
  border-radius: 4px;
}

.warning-stripe {
  height: 8px;
  background: repeating-linear-gradient(
    45deg,
    #5d7a5d,
    #5d7a5d 10px,
    #1a1d21 10px,
    #1a1d21 20px
  );
  border-radius: 4px 4px 0 0;
}

.box-content {
  padding: 32px 40px;
  position: relative;
}

.title-section {
  text-align: center;
  margin-bottom: 24px;
}

.icon-register {
  font-size: 32px;
  margin-bottom: 8px;
}

.title {
  color: #f5f0e8;
  font-size: 24px;
  font-weight: 600;
  margin: 0 0 8px 0;
  letter-spacing: 4px;
}

.subtitle {
  color: #9a9590;
  font-family: 'Courier New', monospace;
  font-size: 11px;
  letter-spacing: 2px;
  margin: 0;
}

.divider {
  margin-top: 16px;
  border-top: 1px solid #4a4540;
  position: relative;
}

.divider-text {
  position: absolute;
  top: -10px;
  left: 50%;
  transform: translateX(-50%);
  background: #2d3139;
  padding: 0 16px;
  color: #5d7a5d;
  font-size: 12px;
  font-family: 'Courier New', monospace;
  letter-spacing: 2px;
}

.register-form :deep(.ant-input-affix-wrapper) {
  background: rgba(26, 29, 33, 0.6) !important;
  border: 1px solid #4a4540 !important;
  border-radius: 2px !important;
}

.register-form :deep(.ant-input) {
  background: transparent !important;
  color: #d4cfc5 !important;
  font-family: 'Courier New', monospace;
}

.register-form :deep(.ant-form-item) {
  margin-bottom: 16px;
}

.register-btn {
  height: 44px !important;
  font-size: 14px !important;
  letter-spacing: 6px !important;
  background: linear-gradient(180deg, #5d7a5d 0%, #4a6349 100%) !important;
  border: 2px solid #5d7a5d !important;
}

.footer {
  text-align: center;
  padding-top: 16px;
  border-top: 1px solid #4a4540;
}

.footer-text {
  color: #666360;
  font-size: 13px;
}

.login-link {
  color: #cd853f !important;
  font-weight: 500;
  margin-left: 8px;
  transition: all 0.3s;
}

.login-link:hover {
  color: #c4a35a !important;
  text-shadow: 0 0 8px rgba(196, 163, 90, 0.5);
}

.bottom-code {
  position: absolute;
  bottom: 8px;
  right: 16px;
  font-family: 'Courier New', monospace;
  font-size: 10px;
  color: #4a4540;
}
</style>
