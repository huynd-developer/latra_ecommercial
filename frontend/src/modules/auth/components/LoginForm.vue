<template>
  <div class="latra-auth-card">
    <div class="auth-header">
      <h2 class="auth-title">Sign in</h2>
      <p class="auth-subtitle">Sign in to track your orders and manage your profile.</p>
    </div>

    <Transition name="toast-slide">
      <div v-if="errorMessage" class="modern-toast-error">
        <div class="toast-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <circle cx="12" cy="12" r="10"></circle>
            <line x1="12" y1="8" x2="12" y2="12"></line>
            <line x1="12" y1="16" x2="12.01" y2="16"></line>
          </svg>
        </div>
        <div class="toast-content">{{ errorMessage }}</div>
        <button type="button" @click="clearError" class="toast-close">&times;</button>
      </div>
    </Transition>

    <form @submit.prevent="onLogin" class="auth-form">
      <div class="input-wrapper">
        <input
          v-model="credentials.loginId"
          type="text"
          placeholder="Tên đăng nhập hoặc Email"
          required
          class="auth-input"
          :disabled="isLoading"
        />
      </div>

      <div class="input-wrapper position-relative">
        <input
          v-model="credentials.password"
          :type="showPassword ? 'text' : 'password'"
          placeholder="Mật khẩu"
          required
          class="auth-input password-input"
          :disabled="isLoading"
        />
        
        <button
          type="button"
          class="toggle-password-btn"
          @click="showPassword = !showPassword"
          tabindex="-1"
        >
          <svg v-if="!showPassword" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
            <circle cx="12" cy="12" r="3"></circle>
          </svg>
          <svg v-else width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"></path>
            <line x1="1" y1="1" x2="23" y2="23"></line>
          </svg>
        </button>

        <button
          type="submit"
          class="auth-submit-btn"
          :disabled="isLoading"
        >
          <span v-if="isLoading" class="spinner">⏳</span>
          <span v-else>➔</span>
        </button>
      </div>

      <div class="auth-meta-row">
        <label class="remember-me">
          <input type="checkbox" class="latra-checkbox" />
          <span>Keep me signed in</span>
        </label>
        <a href="#" class="auth-link">Forgot password?</a>
      </div>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import { useAuthStore } from '../stores/authStore';
import { useRouter, useRoute } from 'vue-router';

const authStore = useAuthStore();
const router = useRouter();
const route = useRoute();

const credentials = reactive({
  loginId: '',
  password: ''
});

const isLoading = ref(false);
const errorMessage = ref('');
const showPassword = ref(false); // BIẾN QUẢN LÝ TRẠNG THÁI ẨN/HIỆN MẬT KHẨU
let errorTimeout: ReturnType<typeof setTimeout> | null = null; 

const showError = (msg: string) => {
  errorMessage.value = msg;
  if (errorTimeout) clearTimeout(errorTimeout);
  errorTimeout = setTimeout(() => {
    clearError();
  }, 4000); 
};

const clearError = () => {
  errorMessage.value = '';
};

const onLogin = async () => {
  if (!credentials.loginId || !credentials.password) {
    showError('Vui lòng điền đầy đủ thông tin.');
    return;
  }

  isLoading.value = true;
  clearError();

  const result = await authStore.login(credentials);
  isLoading.value = false;

  if (result.success) {
    const redirectPath = route.query.redirect as string;
    
    if (redirectPath) {
      router.push(redirectPath);
    } else {
      const userRawData = localStorage.getItem('currentUser');
      let userRole = 'USER';

      if (userRawData) {
        try {
          const parsedUser = JSON.parse(userRawData);
          userRole = parsedUser.role ? parsedUser.role.toUpperCase() : 'USER';
        } catch (e) {
          console.error("Lỗi đọc dữ liệu role tại LoginForm:", e);
        }
      }

      const isInternalTeam = ['ADMIN', 'MANAGER', 'STAFF'].includes(userRole);
      
      if (isInternalTeam) {
        router.push('/admin/dashboard');
      } else {
        router.push('/'); 
      }
    }
  } else {
    showError(result.message); 
  }
};
</script>

<style scoped>
.latra-auth-card {
  width: 100%;
  max-width: 420px;
  margin: 40px auto;
  padding: 30px;
  background-color: #ffffff;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
  position: relative; 
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

/* Toast CSS giữ nguyên */
.modern-toast-error {
  position: fixed;
  top: 24px;
  right: 24px;
  z-index: 9999;
  display: flex;
  align-items: center;
  gap: 12px;
  background-color: #ffffff;
  border-left: 4px solid #ef4444; 
  padding: 16px 20px;
  border-radius: 8px;
  box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.1), 0 8px 10px -6px rgba(0, 0, 0, 0.1);
  min-width: 300px;
  max-width: 400px;
}
.toast-icon {
  width: 24px;
  height: 24px;
  color: #ef4444;
  flex-shrink: 0;
}
.toast-content {
  font-size: 14px;
  font-weight: 500;
  color: #1f2937;
  flex-grow: 1;
  line-height: 1.4;
}
.toast-close {
  background: none;
  border: none;
  color: #9ca3af;
  font-size: 20px;
  cursor: pointer;
  padding: 0;
  line-height: 1;
  transition: color 0.2s;
}
.toast-close:hover {
  color: #1f2937;
}
.toast-slide-enter-active,
.toast-slide-leave-active {
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}
.toast-slide-enter-from {
  opacity: 0;
  transform: translateX(100px); 
}
.toast-slide-leave-to {
  opacity: 0;
  transform: translateX(100px) scale(0.9);
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

/* CHỪA CHỖ CHO 2 NÚT (MẮT & SUBMIT) KHÔNG BỊ CHỮ ĐÈ LÊN */
.password-input {
  padding-right: 80px; 
}

/* CSS CHO NÚT CON MẮT */
.toggle-password-btn {
  position: absolute;
  right: 48px; /* Nằm ngay sát nút Submit */
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  color: #9ca3af;
  cursor: pointer;
  padding: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: color 0.2s;
}
.toggle-password-btn:hover {
  color: #111827;
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
.auth-meta-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 13px;
  color: #4b5563;
  margin-top: 4px;
}
.remember-me {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  user-select: none;
}
.latra-checkbox {
  accent-color: #000000;
  width: 15px;
  height: 15px;
}
.auth-link {
  color: #4b5563;
  text-decoration: none;
}
.auth-link:hover {
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

@media (max-width: 600px) {
  .modern-toast-error {
    top: 16px;
    right: 16px;
    left: 16px;
    min-width: auto;
  }
}
</style>