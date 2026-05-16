<template>
  <div class="h-100 overflow-y-auto custom-scrollbar pb-4 pe-2">
    <!-- HEADER & BỘ LỌC THỜI GIAN -->
    <div class="d-flex justify-content-between align-items-center mb-4">
      <div>
        <h4 class="fw-bolder text-slate-800 mb-1">Tổng quan Kinh Doanh</h4>
        <div class="text-slate-500 small">Phân tích dòng tiền và hiệu suất bán hàng thực tế</div>
      </div>
      <div class="d-flex gap-2">
        <select 
          v-model="selectedTimeRange" 
          class="form-select bg-white border-slate-200 shadow-sm rounded-3 fw-semibold text-slate-700 focus-ring-peel" 
          style="width: 180px; cursor: pointer;"
        >
          <option value="today">Hôm nay</option>
          <option value="7days">7 ngày qua</option>
          <option value="thisMonth">Tháng này</option>
          <option value="thisYear">Năm nay</option>
        </select>
        <button class="btn btn-dark-peel rounded-3 shadow-sm px-3 fw-semibold">
          <i class="bi bi-cloud-download me-1"></i> Xuất file
        </button>
      </div>
    </div>

    <!-- 4 THẺ THỐNG KÊ (KPI) -->
    <div class="row g-4 mb-4">
      <div class="col-md-3" v-for="(stat, idx) in dashboardStats" :key="idx">
        <div class="bg-white p-4 rounded-4 shadow-sm border border-slate-100 h-100 d-flex flex-column hover-lift transition-all cursor-pointer">
          <div class="d-flex justify-content-between align-items-start mb-3">
            <div class="rounded-circle d-flex align-items-center justify-content-center bg-slate-100 text-dark-peel" style="width: 48px; height: 48px;">
              <i :class="stat.icon" class="fs-5"></i>
            </div>
            <span class="badge rounded-pill fw-bold transition-all" :class="stat.isUp ? 'bg-success-light text-success' : 'bg-danger-light text-danger'">
              <i class="bi" :class="stat.isUp ? 'bi-arrow-up-right' : 'bi-arrow-down-right'"></i> {{ stat.percent }}%
            </span>
          </div>
          <div>
            <div class="text-slate-400 fw-bold small text-uppercase tracking-wider mb-1">{{ stat.title }}</div>
            <!-- Hiện loading thay vì số nếu đang tải -->
            <div v-if="isLoading" class="spinner-grow spinner-grow-sm text-slate-300 mt-2" role="status"></div>
            <h3 v-else class="fw-black text-slate-800 mb-0 tracking-tight transition-all">{{ stat.value }}</h3>
          </div>
        </div>
      </div>
    </div>

    <!-- KHU VỰC BIỂU ĐỒ & TOP SẢN PHẨM -->
    <div class="row g-4 flex-grow-1">
      
      <!-- BIỂU ĐỒ DOANH THU -->
      <div class="col-xl-8 col-lg-7 d-flex flex-column">
        <div class="bg-white rounded-4 shadow-sm border border-slate-100 p-4 h-100 d-flex flex-column">
          <div class="d-flex justify-content-between align-items-center mb-4">
            <h6 class="fw-bolder text-slate-800 mb-0">Biểu đồ doanh thu ({{ currentChartTitle }})</h6>
            <div class="text-slate-400 small fw-medium">Đơn vị: VNĐ</div>
          </div>
          
          <div class="flex-grow-1 position-relative" style="min-height: 350px;">
            <!-- Lớp phủ Loading đè lên biểu đồ -->
            <div v-if="isLoading" class="position-absolute top-0 start-0 w-100 h-100 d-flex flex-column justify-content-center align-items-center bg-white bg-opacity-75 z-2" style="backdrop-filter: blur(1px);">
              <div class="spinner-border mb-2 text-slate-300" role="status"></div>
              <span class="fw-semibold text-slate-500 small">Đang đồng bộ máy chủ...</span>
            </div>
            
            <apexchart v-if="chartSeries.length > 0" type="area" height="100%" :options="chartOptions" :series="chartSeries"></apexchart>
          </div>
        </div>
      </div>

      <!-- TOP MÁY BÁN CHẠY -->
      <div class="col-xl-4 col-lg-5 d-flex flex-column">
        <div class="bg-white rounded-4 shadow-sm border border-slate-100 h-100 d-flex flex-column overflow-hidden">
          <div class="p-4 border-bottom border-slate-100 d-flex justify-content-between align-items-center bg-slate-50">
            <h6 class="fw-bolder text-slate-800 mb-0">Top Bán Chạy Nhất</h6>
            <i class="bi bi-trophy text-warning"></i>
          </div>
          
          <div class="flex-grow-1 overflow-y-auto custom-scrollbar p-3 position-relative">
            <div v-if="isLoading" class="position-absolute top-50 start-50 translate-middle text-slate-300">
              <div class="spinner-border" role="status"></div>
            </div>
            
            <div v-else-if="topProducts.length === 0" class="h-100 d-flex flex-column align-items-center justify-content-center text-slate-400">
              <i class="bi bi-inbox fs-1 mb-2 opacity-50"></i>
              <span class="small fw-medium">Chưa có dữ liệu bán hàng</span>
            </div>

            <div v-else v-for="(product, index) in topProducts" :key="index" class="d-flex align-items-center p-2 mb-2 rounded-3 hover-bg-slate transition-all border border-transparent hover-border">
              <div class="fw-black text-slate-300 fs-5 me-3" style="width: 20px;">{{ index + 1 }}</div>
              <div class="bg-white rounded-3 p-1 border border-slate-200 me-3 shadow-sm" style="width: 45px; height: 45px;">
                <img :src="product.image" @error="handleImageError" class="w-100 h-100 object-fit-contain" alt="img">
              </div>
              <div class="flex-grow-1 pe-2">
                <div class="fw-bold text-slate-800 text-truncate" style="font-size: 0.85rem; max-width: 140px;" :title="product.name">{{ product.name }}</div>
                <div class="text-slate-500 font-monospace mt-1" style="font-size: 0.75rem;">Đã bán: <span class="fw-black text-dark-peel">{{ product.soldCount }}</span></div>
              </div>
              <div class="fw-black text-slate-900 text-end text-nowrap" style="font-size: 0.9rem;">
                {{ formatMoneyCompact(product.revenue) }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import VueApexCharts from 'vue3-apexcharts'
import axios from 'axios'
import type { ApexOptions } from 'apexcharts' 

const apexchart = VueApexCharts;

const isLoading = ref(true);
const selectedTimeRange = ref('7days');

// 1. Đổi sang link ảnh mặc định xịn xò, không bao giờ bị chặn (Placehold.co)
const defaultImage = 'https://placehold.co/100x100/f1f5f9/94a3b8?text=No+Image';

// 2. Nâng cấp hàm xử lý lỗi: Bóp cổ vòng lặp vô hạn!
const handleImageError = (e: any) => {
  e.target.onerror = null; // 👉 BÍ QUYẾT: Hủy luôn bộ lắng nghe lỗi sau lần tạch đầu tiên!
  e.target.src = defaultImage;
}

const dashboardStats = ref<any[]>([]);
const topProducts = ref<any[]>([]);
const chartSeries = ref<any[]>([]);
const chartOptions = ref<ApexOptions>({} as any);

const currentChartTitle = computed(() => {
  switch (selectedTimeRange.value) {
    case 'today': return 'Hôm nay';
    case '7days': return '7 ngày qua';
    case 'thisMonth': return 'Tháng này';
    case 'thisYear': return 'Năm nay';
    default: return '';
  }
});

// GỌI API THẬT TỪ DATABASE SPRING BOOT
const fetchDashboardData = async () => {
  isLoading.value = true;
  try {
    const res = await axios.get(`http://localhost:8080/api/v1/dashboard?timeRange=${selectedTimeRange.value}`);
    const data = res.data;

    dashboardStats.value = data.kpi;
    topProducts.value = data.products || [];
    chartSeries.value = [{ name: 'Doanh thu', data: data.chartData }];
    
    chartOptions.value = {
      chart: { type: 'area', fontFamily: 'inherit', toolbar: { show: false }, zoom: { enabled: false } },
      colors: ['#1e293b'],
      fill: {
        type: 'gradient',
        gradient: { shadeIntensity: 1, opacityFrom: 0.3, opacityTo: 0.05, stops: [0, 90, 100] }
      },
      dataLabels: { enabled: false },
      stroke: { curve: 'smooth', width: 3 },
      xaxis: {
        categories: data.chartCategories, // Lấy mốc thời gian từ Server trả về
        axisBorder: { show: false },
        axisTicks: { show: false },
        labels: { style: { colors: '#94a3b8', fontWeight: 600 } }
      },
      yaxis: {
        labels: {
          style: { colors: '#94a3b8', fontWeight: 600 },
          formatter: (value: number) => { 
            if (value >= 1000000000) return (value / 1000000000).toFixed(1) + ' Tỷ';
            if (value >= 1000000) return (value / 1000000).toFixed(0) + ' Tr'; 
            return value.toString();
          }
        }
      },
      grid: { borderColor: '#f1f5f9', strokeDashArray: 4, yaxis: { lines: { show: true } } },
      tooltip: {
        theme: 'light',
        y: { formatter: (val: number) => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val) }
      }
    } as any;
  } catch (error) {
    // In thẳng lỗi từ Backend ra (nếu có), không thì in lỗi mặc định của Axios
    console.error("Lỗi lấy dữ liệu Dashboard:", error.response?.data || error.message); 
} finally {
    isLoading.value = false;
}
}

const formatMoneyCompact = (val: number) => {
  if (val >= 1000000000) return (val / 1000000000).toFixed(1) + ' Tỷ';
  if (val >= 1000000) return (val / 1000000).toFixed(1) + 'M';
  return val.toLocaleString('vi-VN') + ' ₫';
}

// Bắt sự kiện mỗi khi đổi thẻ Select
watch(selectedTimeRange, () => {
  fetchDashboardData();
});

onMounted(() => {
  fetchDashboardData();
});
</script>

<style scoped>
.text-slate-300 { color: #cbd5e1 !important; }
.text-slate-400 { color: #94a3b8 !important; }
.text-slate-500 { color: #64748b !important; }
.text-slate-700 { color: #334155 !important; }
.text-slate-800 { color: #1e293b !important; }
.text-slate-900 { color: #0f172a !important; }
.bg-slate-50 { background-color: #f8fafc !important; }
.bg-slate-100 { background-color: #f1f5f9 !important; }
.border-slate-100 { border-color: #f1f5f9 !important; }
.border-slate-200 { border-color: #e2e8f0 !important; }
.text-dark-peel { color: #111111 !important; }

.bg-success-light { background-color: #dcfce7; }
.bg-danger-light { background-color: #fee2e2; }
.text-success { color: #16a34a !important; }
.text-danger { color: #dc2626 !important; }

.btn-dark-peel { background-color: #111111; color: white; border: none; transition: 0.2s; }
.btn-dark-peel:hover { background-color: #000000; transform: translateY(-1px); }
.focus-ring-peel:focus { border-color: #111111 !important; box-shadow: 0 0 0 3px rgba(17, 17, 17, 0.1) !important; }

.fw-black { font-weight: 900 !important; }
.fw-semibold { font-weight: 600 !important; }
.tracking-tight { letter-spacing: -0.025em; }
.tracking-wider { letter-spacing: 0.05em; }

.cursor-pointer { cursor: pointer; }
.transition-all { transition: all 0.3s ease-in-out; }
.hover-lift { transition: transform 0.2s, box-shadow 0.2s; }
.hover-lift:hover { transform: translateY(-3px); box-shadow: 0 10px 15px -3px rgba(0,0,0,0.05); }

.border-transparent { border-color: transparent !important; }
.hover-bg-slate:hover { background-color: #f1f5f9; }
.hover-border:hover { border-color: #e2e8f0 !important; }

.custom-scrollbar::-webkit-scrollbar { width: 5px; height: 5px; }
.custom-scrollbar::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 10px; }
.custom-scrollbar::-webkit-scrollbar-track { background: transparent; }
</style>