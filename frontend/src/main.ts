import { createApp } from 'vue'
import App from './App.vue'
import router from './router'

// Import Bootstrap CSS
import 'bootstrap/dist/css/bootstrap.min.css'

// Import Bootstrap JS (Bắt buộc phải có để Dropdown Menu xổ xuống được)
import 'bootstrap/dist/js/bootstrap.bundle.min.js'

// Import Bootstrap Icons (Để hiển thị các icon giỏ hàng, user, mũi tên)
import 'bootstrap-icons/font/bootstrap-icons.css'

const app = createApp(App)
app.use(router);
app.mount('#app')