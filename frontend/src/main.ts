// frontend/src/main.ts
import { createApp } from 'vue'
import { createPinia } from 'pinia' // 1. IMPORT PINIA THÊM VÀO ĐÂY
import App from './App.vue'
import router from './router'

// Import Bootstrap CSS
import 'bootstrap/dist/css/bootstrap.min.css'
// Import Bootstrap JS 
import 'bootstrap/dist/js/bootstrap.bundle.min.js'
// Import Bootstrap Icons 
import 'bootstrap-icons/font/bootstrap-icons.css'

const app = createApp(App)
const pinia = createPinia() // 2. KHỞI TẠO PINIA

app.use(pinia) // 3. CÀI PINIA VÀO APP (Bắt buộc phải nằm trước router)
app.use(router)

app.mount('#app')