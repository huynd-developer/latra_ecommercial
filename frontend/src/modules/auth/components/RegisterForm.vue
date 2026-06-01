<template>
  <div class="latra-auth-card">
    <div class="auth-header">
      <h2 class="auth-title">Create account</h2>
      <p class="auth-subtitle">Join LATRA Store to experience premium services.</p>
    </div>

    <div v-if="errorMessage" class="auth-alert alert-error">
      ⚠️ {{ errorMessage }}
    </div>
    
    <div v-if="successMessage" class="auth-alert alert-success">
      ✅ {{ successMessage }}
    </div>

    <form @submit.prevent="onRegister" class="auth-form">
      <div class="input-wrapper">
        <input
          v-model="registerData.fullName"
          type="text"
          placeholder="Họ và tên"
          required
          class="auth-input"
          :disabled="isLoading"
        />
      </div>

      <div class="input-wrapper">
        <input
          v-model="registerData.username"
          type="text"
          placeholder="Tên đăng nhập (viết liền không dấu)"
          required
          class="auth-input"
          :disabled="isLoading"
        />
      </div>

      <div class="input-wrapper">
        <input
          v-model="registerData.email"
          type="email"
          placeholder="Email address"
          required
          class="auth-input"
          :disabled="isLoading"
        />
      </div>

      <div class="input-wrapper">
        <input
          v-model="registerData.phone"
          type="tel"
          placeholder="Số điện thoại"
          required
          class="auth-input"
          :disabled="isLoading"
        />
      </div>

      <div class="input-wrapper position-relative">
        <input
          v-model="registerData.password"
          type="password"
          placeholder="Mật khẩu (ít nhất 6 ký tự)"
          required
          class="auth-input"
          :disabled="isLoading"
        />
        
        <button
          type="submit"
          class="auth-submit-btn"
          :disabled="isLoading"
        >
          <span v-if="isLoading" class="spinner">⏳</span>
          <span v-else>➔</span>
        </button>
      </div>

      <div class="terms-text">
        By continuing, you agree to our <a href="#" class="terms-link">Terms of service</a> and <a href="#" class="terms-link">Privacy policy</a>.
      </div>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import { useAuthStore } from '../stores/authStore';

const emit = defineEmits(['register-success']);
const authStore = useAuthStore();

// Khớp hoàn toàn với BE RegisterRequest
const registerData = reactive({
  fullName: '',
  username: '',
  email: '',
  phone: '',
  password: ''
});

const isLoading = ref(false);
const errorMessage = ref('');
const successMessage = ref('');

const onRegister = async () => {
  // Validate sơ bộ ở FE
  if (!registerData.fullName || !registerData.username || !registerData.email || !registerData.phone || !registerData.password) {
    errorMessage.value = 'Vui lòng nhập đầy đủ các trường thông tin.';
    return;
  }

  isLoading.value = true;
  errorMessage.value = '';
  successMessage.value = '';

  const result = await authStore.register(registerData);
  isLoading.value = false;

  if (result.success) {
    successMessage.value = result.message;
    
    // Reset form
    registerData.fullName = '';
    registerData.username = '';
    registerData.email = '';
    registerData.phone = '';
    registerData.password = '';
    
    setTimeout(() => {
      emit('register-success');
    }, 1500);
  } else {
    // Sẽ in ra: "Tên đăng nhập đã tồn tại!", "Email không đúng định dạng", v.v...
    errorMessage.value = result.message; 
  }
};
</script>

<style scoped>
/* Kế thừa và đồng bộ hóa UI với LoginForm */
.latra-auth-card {
  width: 100%;
  max-width: 420px;
  margin: 40px auto;
  padding: 30px;
  background-color: #ffffff;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
}
.auth-header {
  text-align: left;
  margin-bottom: 28px;
}
.auth-title {
  font-size: 32px;
  font-weight: 400;
  color: #111827;
  margin-bottom: 6px;
}
.auth-subtitle {
  font-size: 14px;
  color: #6b7280;
  line-height: 1.4;
}
.auth-alert {
  margin-bottom: 20px;
  padding: 14px;
  border-radius: 6px;
  font-size: 14px;
}
.alert-error {
  background-color: #fef2f2;
  border: 1px solid #fecaca;
  color: #dc2626;
}
.alert-success {
  background-color: #f0fdf4;
  border: 1px solid #bbf7d0;
  color: #16a34a;
}
.auth-form {
  display: flex;
  flex-direction: column;
  gap: 18px;
}
.input-wrapper {
  position: relative;
  width: 100%;
}
.auth-input {
  width: 100%;
  padding: 14px 16px;
  border: 1px solid #d1d5db;
  border-radius: 4px;
  font-size: 15px;
  color: #111827;
  transition: border-color 0.2s;
  box-sizing: border-box;
}
.auth-input:focus {
  outline: none;
  border-color: #000000;
}
.auth-submit-btn {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  font-size: 22px;
  color: #9ca3af;
  cursor: pointer;
  padding: 4px 8px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.auth-submit-btn:hover:not(:disabled) {
  color: #000000;
}
.terms-text {
  font-size: 11px;
  color: #9ca3af;
  line-height: 1.6;
  text-align: center;
  margin-top: 8px;
}
.terms-link {
  color: #4b5563;
  text-decoration: underline;
}
.spinner {
  display: inline-block;
  animation: spin 1s linear infinite;
  font-size: 14px;
}
@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>