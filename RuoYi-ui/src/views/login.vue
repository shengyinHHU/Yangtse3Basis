<template>
  <div class="login yangzi-login">
    <div class="brand-badge" aria-hidden="true">
      <span class="brand-mark">YZ</span>
      <span>扬子石化</span>
    </div>

    <div class="plant-lines" aria-hidden="true">
      <div class="tank tank-a"></div>
      <div class="tank tank-b"></div>
      <div class="tower tower-a"></div>
      <div class="tower tower-b"></div>
      <div class="pipe pipe-a"></div>
      <div class="pipe pipe-b"></div>
      <div class="flare"></div>
    </div>

    <el-form ref="loginRef" :model="loginForm" :rules="loginRules" class="login-form login-center">
      <h3 class="title">体系三基融合工作平台</h3>

      <el-form-item prop="username">
        <el-input
          v-model="loginForm.username"
          type="text"
          size="large"
          auto-complete="off"
          placeholder="登录账号"
        >
          <template #prefix><svg-icon icon-class="user" class="el-input__icon input-icon" /></template>
        </el-input>
      </el-form-item>

      <el-form-item prop="password">
        <el-input
          v-model="loginForm.password"
          type="password"
          size="large"
          auto-complete="off"
          placeholder="登录密码"
          show-password
          @keyup.enter="handleLogin"
        >
          <template #prefix><svg-icon icon-class="password" class="el-input__icon input-icon" /></template>
        </el-input>
      </el-form-item>

      <el-form-item prop="code" v-if="captchaEnabled" class="code-row">
        <el-input
          v-model="loginForm.code"
          size="large"
          auto-complete="off"
          placeholder="验证码"
          @keyup.enter="handleLogin"
        >
          <template #prefix><svg-icon icon-class="validCode" class="el-input__icon input-icon" /></template>
        </el-input>
        <button class="login-code" type="button" @click="getCode">
          <img :src="codeUrl" class="login-code-img" alt="验证码" />
        </button>
      </el-form-item>

      <el-form-item class="submit-row">
        <el-button :loading="loading" size="large" type="primary" @click.prevent="handleLogin">
          <span v-if="!loading">登录</span>
          <span v-else>登录中...</span>
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { getCodeImg } from '@/api/login'
import useUserStore from '@/store/modules/user'

const userStore = useUserStore()
const route = useRoute()
const router = useRouter()
const { proxy } = getCurrentInstance()

const loginForm = ref({
  username: 'admin',
  password: 'admin123',
  code: '',
  uuid: ''
})

const loginRules = {
  username: [{ required: true, trigger: 'blur', message: '请输入登录账号' }],
  password: [{ required: true, trigger: 'blur', message: '请输入登录密码' }],
  code: [{ required: true, trigger: 'change', message: '请输入验证码' }]
}

const codeUrl = ref('')
const loading = ref(false)
const captchaEnabled = ref(true)
const redirect = ref(undefined)

watch(route, (newRoute) => {
  redirect.value = newRoute.query && newRoute.query.redirect
}, { immediate: true })

function handleLogin() {
  proxy.$refs.loginRef.validate(valid => {
    if (!valid) return

    loading.value = true
    userStore.login(loginForm.value).then(() => {
      const query = route.query
      const otherQueryParams = Object.keys(query).reduce((acc, cur) => {
        if (cur !== 'redirect') {
          acc[cur] = query[cur]
        }
        return acc
      }, {})
      router.push({ path: redirect.value || '/threebase/leader-dashboard', query: otherQueryParams })
    }).catch(() => {
      loading.value = false
      if (captchaEnabled.value) {
        getCode()
      }
    })
  })
}

function getCode() {
  getCodeImg().then(res => {
    captchaEnabled.value = res.captchaEnabled === undefined ? true : res.captchaEnabled
    if (captchaEnabled.value) {
      codeUrl.value = 'data:image/gif;base64,' + res.img
      loginForm.value.uuid = res.uuid
    }
  })
}

getCode()
</script>

<style lang="scss" scoped>
.login {
  position: relative;
  display: grid;
  place-items: center;
  min-height: 100%;
  padding: 24px;
  color: #172033;
  background:
    linear-gradient(90deg, rgba(74, 163, 255, 0.08) 1px, transparent 1px),
    linear-gradient(0deg, rgba(74, 163, 255, 0.06) 1px, transparent 1px),
    linear-gradient(145deg, #172236 0%, #101826 58%, #0b1220 100%);
  background-size: 44px 44px, 44px 44px, auto;
  overflow: hidden;
}

.login::before {
  content: '';
  position: absolute;
  inset: -20%;
  background: radial-gradient(circle at 50% 44%, rgba(31, 111, 235, 0.26), transparent 32%);
  pointer-events: none;
}

.brand-badge {
  position: absolute;
  top: 28px;
  left: 32px;
  z-index: 1;
  display: inline-flex;
  align-items: center;
  gap: 12px;
  color: #dbeafe;
  font-size: 15px;
  font-weight: 800;
}

.brand-mark {
  display: inline-grid;
  place-items: center;
  width: 38px;
  height: 28px;
  color: #1f6feb;
  background: #ffffff;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 900;
}

.plant-lines {
  position: absolute;
  left: 50%;
  bottom: 46px;
  width: min(720px, 72vw);
  height: 230px;
  opacity: 0.42;
  transform: translateX(-50%);
  pointer-events: none;
}

.tank,
.tower,
.pipe,
.flare {
  position: absolute;
  border: 1px solid rgba(123, 188, 255, 0.48);
  background: rgba(17, 25, 39, 0.56);
  box-shadow: 0 0 28px rgba(31, 111, 235, 0.16);
}

.tank {
  bottom: 10px;
  width: 118px;
  height: 62px;
  border-radius: 50% 50% 10px 10px / 34% 34% 10px 10px;
}

.tank-a { left: 46px; }
.tank-b { left: 184px; width: 104px; height: 56px; }

.tower {
  bottom: 10px;
  width: 42px;
  border-radius: 4px 4px 0 0;
}

.tower-a { right: 176px; height: 176px; }
.tower-b { right: 108px; height: 132px; }

.tower::before,
.tower::after {
  content: '';
  position: absolute;
  left: 8px;
  right: 8px;
  height: 1px;
  background: rgba(123, 188, 255, 0.5);
}

.tower::before { top: 35%; }
.tower::after { top: 68%; }

.pipe {
  height: 9px;
  border-radius: 99px;
  background: rgba(31, 111, 235, 0.34);
}

.pipe-a {
  left: 98px;
  right: 132px;
  bottom: 88px;
}

.pipe-b {
  left: 276px;
  width: 174px;
  bottom: 118px;
  transform: rotate(-24deg);
  transform-origin: left center;
}

.flare {
  right: 30px;
  bottom: 10px;
  width: 16px;
  height: 196px;
  border-radius: 10px 10px 0 0;
}

.flare::before {
  content: '';
  position: absolute;
  left: 50%;
  top: -34px;
  width: 38px;
  height: 38px;
  border-radius: 50% 50% 50% 12px;
  background: linear-gradient(135deg, #ffcf5c, #f97316 64%, #ef4444);
  transform: translateX(-50%) rotate(-45deg);
  box-shadow: 0 0 30px rgba(249, 115, 22, 0.45);
}

.login-form {
  position: relative;
  z-index: 2;
  width: 100%;
  max-width: 420px;
  padding: 34px 32px 32px;
  background: rgba(255, 255, 255, 0.95);
  border: 1px solid rgba(217, 228, 242, 0.78);
  border-top: 4px solid #1f6feb;
  border-radius: 6px;
  box-shadow: 0 30px 76px rgba(3, 10, 24, 0.42);
  backdrop-filter: blur(10px);

  :deep(.el-form-item) {
    margin-bottom: 22px;
  }

  :deep(.el-input__wrapper) {
    min-height: 48px;
    border-radius: 4px;
    background: rgba(255, 255, 255, 0.96);
    box-shadow: 0 0 0 1px #d9e4f2 inset;
  }

  :deep(.el-input__wrapper.is-focus) {
    box-shadow: 0 0 0 1px #1f6feb inset, 0 0 0 4px rgba(31, 111, 235, 0.1);
  }

  .input-icon {
    width: 15px;
    height: 44px;
    color: #64748b;
  }
}

.code-row {
  :deep(.el-form-item__content) {
    display: grid;
    grid-template-columns: minmax(0, 1fr) 128px;
    gap: 10px;
  }
}

.login-code {
  height: 48px;
  padding: 0;
  background: #eef3f8;
  border: 1px solid #d9e4f2;
  border-radius: 4px;
  cursor: pointer;
  overflow: hidden;
}

.login-code-img {
  display: block;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.title {
  margin: 0 0 26px;
  color: #172033;
  font-size: 26px;
  line-height: 1.3;
  text-align: center;
  font-weight: 900;
}

.submit-row {
  margin-bottom: 0 !important;

  .el-button {
    width: 100%;
    height: 46px;
    border-radius: 4px;
    background: #1f6feb;
    border-color: #1f6feb;
    font-weight: 800;
  }
}

html.dark .login-form {
  background: rgba(17, 25, 39, 0.92) !important;
  border-color: #27364f;

  .title {
    color: #fff;
  }
}

@media (max-width: 640px) {
  .login {
    padding: 16px;
  }

  .brand-badge {
    top: 18px;
    left: 18px;
  }

  .login-form {
    padding: 28px 22px 24px;
  }

  .title {
    font-size: 22px;
  }

  .plant-lines {
    width: 680px;
    opacity: 0.28;
  }
}

@media (max-width: 430px) {
  .code-row :deep(.el-form-item__content) {
    grid-template-columns: 1fr;
  }
}
</style>

