<template>
  <div class="h-100 overflow-y-auto custom-scrollbar pb-4 pe-2">
    <div class="d-flex flex-column flex-sm-row justify-content-between align-items-sm-center mb-4 gap-3">
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
        <button class="btn btn-dark-peel rounded-3 shadow-sm px-3 fw-semibold text-nowrap">
          <i class="bi bi-cloud-download me-1"></i> Xuất báo cáo
        </button>
      </div>
    </div>

    <div class="row g-4 mb-4">
      <div class="col-12 col-sm-6 col-md-3" v-for="(stat, idx) in dashboardStats" :key="idx">
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
            <div v-if="isLoading" class="spinner-grow spinner-grow-sm text-slate-300 mt-2" role="status"></div>
            <h3 v-else class="fw-black text-slate-800 mb-0 tracking-tight transition-all">{{ stat.value }}</h3>
          </div>
        </div>
      </div>
    </div>

    <div class="row g-4 flex-grow-1">
      <div class="col-12 col-lg-7 col-xl-8 d-flex flex-column">
        <div class="bg-white rounded-4 shadow-sm border border-slate-100 p-4 h-100 d-flex flex-column">
          <div class="d-flex justify-content-between align-items-center mb-4">
            <h6 class="fw-bolder text-slate-800 mb-0">Biểu đồ doanh thu ({{ currentChartTitle }})</h6>
            <div class="text-slate-400 small fw-medium">Đơn vị: VNĐ</div>
          </div>
          
          <div class="flex-grow-1 position-relative d-flex flex-column" style="min-height: 350px;">
            <div v-if="isLoading" class="position-absolute top-0 start-0 w-100 h-100 d-flex flex-column justify-content-center align-items-center bg-white bg-opacity-75 z-2" style="backdrop-filter: blur(2px);">
              <div class="spinner-border mb-2 text-slate-300" role="status"></div>
              <span class="fw-semibold text-slate-500 small">Đang đồng bộ máy chủ...</span>
            </div>
            
            <div v-else-if="!hasChartData" class="h-100 w-100 d-flex flex-column align-items-center justify-content-center text-slate-400">
              <i class="bi bi-bar-chart fs-1 mb-2 opacity-50"></i>
              <span class="small fw-medium">Chưa có giao dịch nào trong thời gian này</span>
            </div>

            <apexchart 
              v-else 
              type="area" 
              height="100%" 
              width="100%"
              :options="chartOptions" 
              :series="chartSeries"
            />
          </div>
        </div>
      </div>

      <div class="col-12 col-lg-5 col-xl-4 d-flex flex-column">
        <div class="bg-white rounded-4 shadow-sm border border-slate-100 h-100 d-flex flex-column overflow-hidden">
          <div class="p-4 border-bottom border-slate-100 d-flex justify-content-between align-items-center bg-slate-50">
            <h6 class="fw-bolder text-slate-800 mb-0">Top Bán Chạy Nhất</h6>
            <i class="bi bi-trophy-fill text-warning fs-5"></i>
          </div>
          
          <div class="flex-grow-1 overflow-y-auto custom-scrollbar p-3 position-relative" style="min-height: 350px;">
            <div v-if="isLoading" class="position-absolute top-50 start-50 translate-middle text-slate-300">
              <div class="spinner-border" role="status"></div>
            </div>
            
            <div v-else-if="topProducts.length === 0" class="h-100 d-flex flex-column align-items-center justify-content-center text-slate-400">
              <i class="bi bi-inbox fs-1 mb-2 opacity-50"></i>
              <span class="small fw-medium">Chưa có dữ liệu bán hàng</span>
            </div>

            <div v-else v-for="(product, index) in topProducts" :key="index" class="d-flex align-items-center p-2 mb-2 rounded-3 hover-bg-slate transition-all border border-transparent hover-border cursor-pointer">
              <div class="fw-black text-slate-300 fs-5 me-3 text-center" style="width: 24px;">{{ index + 1 }}</div>
              <div class="bg-white rounded-3 p-1 border border-slate-200 me-3 shadow-sm flex-shrink-0" style="width: 48px; height: 48px;">
                <img :src="product.image || defaultImage" @error="handleImageError" class="w-100 h-100 object-fit-contain rounded-2" alt="product">
              </div>
              <div class="flex-grow-1 pe-2 overflow-hidden">
                <div class="fw-bold text-slate-800 text-truncate" style="font-size: 0.9rem;" :title="product.name">{{ product.name }}</div>
                <div class="text-slate-500 mt-1" style="font-size: 0.8rem;">
                  Đã bán: <span class="fw-black text-dark-peel bg-slate-100 px-2 py-0.5 rounded-pill">{{ product.soldCount }}</span>
                </div>
              </div>
              <div class="fw-black text-slate-900 text-end text-nowrap flex-shrink-0" style="font-size: 0.95rem;">
                {{ formatMoneyFull(product.revenue) }}
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
import request from '@/common/services/request'
import type { ApexOptions } from 'apexcharts' 

const apexchart = VueApexCharts;

const isLoading = ref(true);
const selectedTimeRange = ref('7days');
const defaultImage = 'https://placehold.co/100x100/f1f5f9/94a3b8?text=No+Image';

const dashboardStats = ref<any[]>([]);
const topProducts = ref<any[]>([]);
const chartSeries = ref<{ name: string; data: number[] }[]>([]);
const chartOptions = ref<ApexOptions>({} as ApexOptions);

const currentChartTitle = computed(() => {
  switch (selectedTimeRange.value) {
    case 'today': return 'Hôm nay';
    case '7days': return '7 ngày qua';
    case 'thisMonth': return 'Tháng này';
    case 'thisYear': return 'Năm nay';
    default: return '';
  }
});

// FIX ERROR TYPESCRIPT: Xử lý triệt để Object is possibly undefined
const hasChartData = computed(() => {
  return chartSeries.value.length > 0 && (chartSeries.value[0]?.data?.length || 0) > 0;
});

const handleImageError = (e: Event) => {
  const target = e.target as HTMLImageElement;
  target.onerror = null; 
  target.src = defaultImage;
}

const formatNumberCompact = (val: number): string => {
  if (val >= 1000000000) return (val / 1000000000).toFixed(1) + ' Tỷ';
  if (val >= 1000000) return (val / 1000000).toFixed(1) + ' Tr';
  return val.toString();
}

const formatMoneyFull = (val: number): string => {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val);
}

const fetchDashboardData = async () => {
  isLoading.value = true;
  try {
    const data: any = await request.get(`/dashboard?timeRange=${selectedTimeRange.value}`);

    dashboardStats.value = data.kpi || [];
    topProducts.value = data.products || [];
    chartSeries.value = [{ name: 'Doanh thu', data: data.chartData || [] }];
    
    chartOptions.value = {
      chart: { type: 'area', fontFamily: 'inherit', toolbar: { show: false }, zoom: { enabled: false } },
      colors: ['#1e293b'],
      fill: {
        type: 'gradient',
        gradient: { shadeIntensity: 1, opacityFrom: 0.3, opacityTo: 0.02, stops: [0, 90, 100] }
      },
      dataLabels: { enabled: false },
      stroke: { curve: 'smooth', width: 3 },
      xaxis: {
        categories: data.chartCategories || [], 
        axisBorder: { show: false },
        axisTicks: { show: false },
        labels: { style: { colors: '#94a3b8', fontWeight: 600 } }
      },
      yaxis: {
        labels: {
          style: { colors: '#94a3b8', fontWeight: 600 },
          formatter: formatNumberCompact 
        }
      },
      grid: { borderColor: '#f1f5f9', strokeDashArray: 4, yaxis: { lines: { show: true } } },
      tooltip: {
        theme: 'light',
        y: { formatter: (val: number) => formatMoneyFull(val) } 
      }
    };
  } catch (error: any) {
    console.error("Lỗi lấy dữ liệu Dashboard:", error.response?.data || error.message); 
  } finally {
    isLoading.value = false;
  }
}

watch(selectedTimeRange, () => {
  fetchDashboardData();
});

onMounted(() => {
  fetchDashboardData();
});
</script>

<style scoped>
/* Màu sắc chủ đạo đồng bộ */
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

/* Trạng thái */
.bg-success-light { background-color: #dcfce7; }
.bg-danger-light { background-color: #fee2e2; }
.text-success { color: #16a34a !important; }
.text-danger { color: #dc2626 !important; }

/* Nút bấm Custom */
.btn-dark-peel { background-color: #111111; color: white; border: none; transition: 0.2s; }
.btn-dark-peel:hover { background-color: #000000; transform: translateY(-2px); box-shadow: 0 4px 12px rgba(0,0,0,0.15) !important; }
.focus-ring-peel:focus { border-color: #111111 !important; box-shadow: 0 0 0 3px rgba(17, 17, 17, 0.1) !important; outline: none; }

/* Typography */
.fw-black { font-weight: 900 !important; }
.fw-semibold { font-weight: 600 !important; }
.tracking-tight { letter-spacing: -0.025em; }
.tracking-wider { letter-spacing: 0.05em; }

/* Utilities & Animations */
.cursor-pointer { cursor: pointer; }
.transition-all { transition: all 0.3s ease-in-out; }
.hover-lift { transition: transform 0.25s cubic-bezier(0.4, 0, 0.2, 1), box-shadow 0.25s cubic-bezier(0.4, 0, 0.2, 1); }
.hover-lift:hover { transform: translateY(-4px); box-shadow: 0 12px 20px -5px rgba(0,0,0,0.08); }

.border-transparent { border-color: transparent !important; }
.hover-bg-slate:hover { background-color: #f8fafc; }
.hover-border:hover { border-color: #e2e8f0 !important; }

/* Scrollbar tinh tế */
.custom-scrollbar::-webkit-scrollbar { width: 4px; height: 4px; }
.custom-scrollbar::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 10px; }
.custom-scrollbar::-webkit-scrollbar-thumb:hover { background: #94a3b8; }
.custom-scrollbar::-webkit-scrollbar-track { background: transparent; }
</style>