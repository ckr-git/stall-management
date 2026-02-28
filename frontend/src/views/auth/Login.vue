<template>
  <div class="login-container">
    <!-- 暴风雪粒子 -->
    <div class="snow-container">
      <div v-for="n in 50" :key="n" class="snowflake" :style="snowflakeStyle()"></div>
    </div>
    
    <!-- 废墟剑影背景 -->
    <div class="ruins-silhouette"></div>
    
    <!-- 登录框 -->
    <div class="login-box">
      <div class="concrete-texture"></div>
      <div class="crack crack-1"></div>
      <div class="crack crack-2"></div>
      
      <div class="warning-stripe"></div>
      
      <div class="box-content">
        <div class="title-section">
          <div class="icon-warning">⚠</div>
          <h2 class="title">摊位管理平台</h2>
          <p class="subtitle">STALL MANAGEMENT SYSTEM</p>
          <div class="divider">
            <span class="divider-text">身份认证</span>
          </div>
        </div>
        
        <a-form :model="form" :rules="rules" ref="formRef" @finish="handleLogin" class="login-form">
          <a-form-item name="username">
            <a-input v-model:value="form.username" placeholder="输入用户名" size="large">
              <template #prefix><UserOutlined style="color: var(--rust-orange)" /></template>
            </a-input>
          </a-form-item>
          <a-form-item name="password">
            <a-input-password v-model:value="form.password" placeholder="输入密码" size="large">
              <template #prefix><LockOutlined style="color: var(--rust-orange)" /></template>
            </a-input-password>
          </a-form-item>
          <a-form-item>
            <a-button type="primary" html-type="submit" size="large" block :loading="loading" class="login-btn">
              <span class="btn-text">登 录</span>
            </a-button>
          </a-form-item>
        </a-form>
        
        <div class="footer">
          <span class="footer-text">还没有账号？</span>
          <router-link to="/register" class="register-link">立即注册</router-link>
        </div>
        
        <div class="bottom-code">
          <span>SYS-AUTH-v2.1</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { message } from 'ant-design-vue'
import { UserOutlined, LockOutlined } from '@ant-design/icons-vue'
import { login } from '@/api/auth'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)

const form = reactive({ username: '', password: '' })

const rules = {
  username: [{ required: true, message: '请输入用户名' }],
  password: [{ required: true, message: '请输入密码' }]
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

async function handleLogin() {
  loading.value = true
  try {
    const res = await login(form)
    await userStore.login(res.data as any)
    message.success('登录成功')
    const redirect = route.query.redirect as string
    router.push(redirect || '/')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
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
  0% {
    transform: translateY(0) rotate(0deg) translateX(0);
  }
  100% {
    transform: translateY(100vh) rotate(360deg) translateX(50px);
  }
}

/* 废墟剪影 */
.ruins-silhouette {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 200px;
  background: 
    /* 建筑剪影 */
    linear-gradient(to bottom, transparent 40%, rgba(26, 29, 33, 0.9) 100%),
    url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 1200 200'%3E%3Cpath fill='%231a1d21' d='M0,200 L0,150 L50,150 L50,100 L80,100 L80,80 L100,80 L100,120 L130,120 L130,90 L150,90 L150,110 L200,110 L200,60 L220,60 L220,100 L250,100 L250,140 L300,140 L300,80 L320,70 L340,80 L340,120 L400,120 L400,50 L420,50 L420,90 L450,90 L450,130 L500,130 L500,70 L550,70 L550,100 L600,100 L600,60 L650,60 L650,110 L700,110 L700,80 L750,80 L750,130 L800,130 L800,90 L850,90 L850,70 L880,70 L880,110 L920,110 L920,140 L960,140 L960,100 L1000,100 L1000,60 L1050,60 L1050,120 L1100,120 L1100,80 L1150,80 L1150,140 L1200,140 L1200,200 Z'/%3E%3C/svg%3E") center bottom no-repeat;
  background-size: cover, 100% 100%;
  opacity: 0.8;
}

/* 登录框 - 混凝土质感 */
.login-box {
  width: 420px;
  position: relative;
  background: linear-gradient(180deg, #3a3f4a 0%, #2d3139 50%, #252830 100%);
  border: 2px solid #4a4540;
  border-radius: 4px;
  box-shadow: 
    0 20px 60px rgba(0, 0, 0, 0.5),
    inset 0 1px 0 rgba(255, 255, 255, 0.05),
    inset 0 -1px 0 rgba(0, 0, 0, 0.3);
  z-index: 10;
}

/* 混凝土纹理 */
.concrete-texture {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: 
    radial-gradient(circle at 20% 30%, rgba(74, 69, 64, 0.3) 0%, transparent 50%),
    radial-gradient(circle at 80% 70%, rgba(74, 69, 64, 0.2) 0%, transparent 40%),
    radial-gradient(circle at 50% 50%, rgba(0, 0, 0, 0.1) 0%, transparent 70%);
  pointer-events: none;
  border-radius: 4px;
}

/* 裂纹装饰 */
.crack {
  position: absolute;
  background: linear-gradient(45deg, transparent 40%, rgba(74, 69, 64, 0.4) 50%, transparent 60%);
  pointer-events: none;
}

.crack-1 {
  top: 20px;
  right: 30px;
  width: 60px;
  height: 80px;
  transform: rotate(15deg);
}

.crack-2 {
  bottom: 40px;
  left: 20px;
  width: 40px;
  height: 50px;
  transform: rotate(-20deg);
}

/* 警示条纹 */
.warning-stripe {
  height: 8px;
  background: repeating-linear-gradient(
    45deg,
    #c4a35a,
    #c4a35a 10px,
    #1a1d21 10px,
    #1a1d21 20px
  );
  border-radius: 4px 4px 0 0;
}

.box-content {
  padding: 40px;
  position: relative;
}

.title-section {
  text-align: center;
  margin-bottom: 32px;
}

.icon-warning {
  font-size: 36px;
  color: #c4a35a;
  text-shadow: 0 0 20px rgba(196, 163, 90, 0.5);
  margin-bottom: 12px;
}

.title {
  color: #f5f0e8;
  font-size: 26px;
  font-weight: 600;
  margin: 0 0 8px 0;
  letter-spacing: 4px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.subtitle {
  color: #9a9590;
  font-family: 'Courier New', monospace;
  font-size: 12px;
  letter-spacing: 3px;
  margin: 0;
}

.divider {
  margin-top: 20px;
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
  color: #cd853f;
  font-size: 12px;
  font-family: 'Courier New', monospace;
  letter-spacing: 2px;
}

.login-form {
  margin-bottom: 24px;
}

.login-form :deep(.ant-input-affix-wrapper) {
  background: rgba(26, 29, 33, 0.6) !important;
  border: 1px solid #4a4540 !important;
  border-radius: 2px !important;
}

.login-form :deep(.ant-input) {
  background: transparent !important;
  color: #d4cfc5 !important;
  font-family: 'Courier New', monospace;
}

.login-btn {
  height: 48px !important;
  font-size: 16px !important;
  letter-spacing: 8px !important;
  background: linear-gradient(180deg, #8b7355 0%, #705a40 100%) !important;
  border: 2px solid #6b5344 !important;
  position: relative;
  overflow: hidden;
}

.login-btn::after {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.1), transparent);
  animation: btnShine 3s infinite;
}

@keyframes btnShine {
  0% { left: -100%; }
  50% { left: 100%; }
  100% { left: 100%; }
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

.register-link {
  color: #cd853f !important;
  font-weight: 500;
  margin-left: 8px;
  text-decoration: none;
  transition: all 0.3s;
}

.register-link:hover {
  color: #c4a35a !important;
  text-shadow: 0 0 8px rgba(196, 163, 90, 0.5);
}

.bottom-code {
  position: absolute;
  bottom: 12px;
  right: 16px;
  font-family: 'Courier New', monospace;
  font-size: 10px;
  color: #4a4540;
  letter-spacing: 1px;
}
</style>
