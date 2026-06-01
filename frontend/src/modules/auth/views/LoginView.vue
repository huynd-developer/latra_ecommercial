<template>
  <div class="auth-page-wrapper">
    <div class="auth-logo-header">
      <h1 class="latra-logo">LATRA</h1>
    </div>

    <div class="auth-content-container">
      <LoginForm />
      
      <div class="auth-footer">
        Chưa có tài khoản? 
        <router-link to="/register" class="auth-switch-link">
          Đăng ký ngay
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import LoginForm from '../components/LoginForm.vue';
</script>

<style scoped>
.auth-page-wrapper {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 48px 16px;
  background-color: #f9fafb; /* Màu nền xám nhạt */
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
}

.auth-logo-header {
  text-align: center;
  margin-bottom: 32px;
}

.latra-logo {
  font-size: 36px;
  font-weight: 700;
  letter-spacing: 0.1em;
  color: #111827;
  text-transform: uppercase;
  margin: 0;
}

.auth-content-container {
  width: 100%;
  max-width: 420px;
  margin: 0 auto;
}

.auth-footer {
  margin-top: 24px;
  text-align: center;
  font-size: 14px;
  color: #6b7280;
}

.auth-switch-link {
  font-weight: 500;
  color: #000000;
  text-decoration: none;
  transition: all 0.2s;
  margin-left: 4px;
}

.auth-switch-link:hover {
  text-decoration: underline;
}
</style>