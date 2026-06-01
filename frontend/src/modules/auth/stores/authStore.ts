import { ref, computed } from 'vue';
import { defineStore } from 'pinia';
import request from '@/common/services/request';

// 1. Interface
interface UserProfile {
  username: string;
  email?: string;
  phone?: string;
  fullName?: string;
  role?: string;
}

interface LoginPayload {
  loginId: string; 
  password: string;
}

interface RegisterPayload {
  username: string;  
  email: string;
  phone: string;     
  password: string;
  fullName: string;  
}

export const useAuthStore = defineStore('auth', () => {
  // 2. State
  const user = ref<UserProfile | null>(null);
  const token = ref<string | null>(localStorage.getItem('accessToken'));

  // 3. Getters
  const isLoggedIn = computed(() => !!token.value);

  // 🛠️ HÀM GIẢI MÃ JWT TOKEN ĐỂ "MÓC" ROLE TỪ BACKEND (Đã fix lỗi TypeScript)
  function getRoleFromJwt(tokenString: string | null): string {
    if (!tokenString) return 'USER';
    try {
      // Tách chuỗi Token ra làm 3 phần
      const parts = tokenString.split('.');
      if (parts.length < 2) return 'USER'; // Check an toàn: Nếu token không đúng định dạng JWT thì trả về USER

      const base64Url = parts[1]; // Lấy phần Payload
      if (!base64Url) return 'USER'; // Check an toàn: Hết lỗi gạch đỏ TypeScript (Object is possibly 'undefined')

      const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
      const jsonPayload = decodeURIComponent(
        window.atob(base64)
          .split('')
          .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
          .join('')
      );
      
      const payload = JSON.parse(jsonPayload);
      console.log("🔍 ĐÃ GIẢI MÃ JWT PAYLOAD:", payload);

      // Tìm mọi ngóc ngách chứa Role trong Token Payload của Spring Boot
      let rawRole = payload.role || payload.roles || payload.authorities || payload.auth;
      if (Array.isArray(rawRole) && rawRole.length > 0) {
        rawRole = rawRole[0].authority || rawRole[0];
      }

      if (rawRole && typeof rawRole === 'string') {
        return rawRole.toUpperCase().replace('ROLE_', '');
      }
    } catch (e) {
      console.error("⚠️ Lỗi không thể giải mã cấu trúc JWT Token:", e);
    }
    return 'USER';
  }

  // 4. Actions

  // -- Đăng Nhập --
  async function login(credentials: LoginPayload) {
    try {
      const res: any = await request.post('/auth/login', credentials);
      
      if (res && res.status === 200) {
        token.value = res.data.accessToken;
        localStorage.setItem('accessToken', res.data.accessToken);

        // 🔥 BƯỚC ĐỘT PHÁ: Lấy thẳng Role từ ruột của Token ra, chấp mọi lỗi 404 của API Profile!
        const extractedRole = getRoleFromJwt(res.data.accessToken);
        const displayName = res.data.username || 'Sếp Tổng LATRA';

        console.log(`=> Chốt hạ Role bóc từ Token thành công: ${extractedRole}`);

        user.value = { 
          username: res.data.username,
          role: extractedRole
        }; 

        // Lưu thông tin chuẩn xuống LocalStorage cho Router Guard check điều hướng
        localStorage.setItem('currentUser', JSON.stringify({ 
          fullName: displayName,
          role: extractedRole 
        }));
        
        window.dispatchEvent(new Event('user-logged-in'));

        return { success: true, message: res.message };
      }
      return { success: false, message: res?.message || 'Sai thông tin đăng nhập' };
    } catch (error: any) {
      return { success: false, message: error.response?.data?.message || 'Lỗi kết nối máy chủ' };
    }
  }

  // -- Đăng Ký --
  async function register(registerData: RegisterPayload) {
    try {
      const res: any = await request.post('/auth/register', registerData);
      
      if (res && res.status === 200) {
        return { success: true, message: res.message || 'Đăng ký thành công!' };
      }
      return { success: false, message: res?.message || 'Đăng ký thất bại' };
    } catch (error: any) {
      return { success: false, message: error.response?.data?.message || 'Lỗi hệ thống' };
    }
  }

  // -- Lấy thông tin User khi F5 trang --
  async function fetchProfile() {
    if (!token.value) return;
    try {
      const res: any = await request.get('/auth/me'); 
      if (res && res.status === 200) {
        user.value = res.data;
        const extractedRole = getRoleFromJwt(token.value);
        localStorage.setItem('currentUser', JSON.stringify({ 
          fullName: res.data.fullName || res.data.username || 'Thành viên',
          role: extractedRole 
        }));
      }
    } catch (error) {
      // Nếu API /auth/me bị 404, ta sử dụng luôn dữ liệu Token sẵn có chứ KHÔNG tự động logout bừa bãi nữa
      const extractedRole = getRoleFromJwt(token.value);
      user.value = {
        username: JSON.parse(localStorage.getItem('currentUser') || '{}').fullName || 'Admin',
        role: extractedRole
      };
      console.log("⚠️ Cơ chế bọc lót: BE lỗi 404 nhưng FE giữ lại phiên đăng nhập dựa vào Token.");
    }
  }

  // -- Đăng Xuất --
  function logout() {
    token.value = null;
    user.value = null;

    localStorage.removeItem('accessToken');
    localStorage.removeItem('currentUser'); 
    
    window.dispatchEvent(new Event('storage'));
    window.location.href = '/login'; 
  }

  return { 
    user, 
    token, 
    isLoggedIn, 
    login, 
    register, 
    logout, 
    fetchProfile 
  };
});