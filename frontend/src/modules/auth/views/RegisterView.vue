<template>
  <div class="auth-page-wrapper">
    <div class="auth-logo-header">
      <h1 class="latra-logo">LATRA</h1>
    </div>

    <div class="auth-content-container">
      <RegisterForm @register-success="handleRegisterSuccess" />
      
      <div class="auth-footer">
        Đã có tài khoản? 
        <router-link to="/login" class="auth-switch-link">
          Đăng nhập
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router';
import RegisterForm from '../components/RegisterForm.vue';

const router = useRouter();

const handleRegisterSuccess = () => {
  router.push('/login');
};
</script>

<style scoped>
/* Dùng chung style với LoginView để đồng bộ */
.auth-page-wrapper {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 48px 16px;
  background-color: #f9fafb;
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