<template>
  <div class="h-100 d-flex flex-column g-4 p-3 bg-slate-50">
    <!-- HEADER & TÌM KIẾM (Giữ nguyên) -->
    <div class="d-flex justify-content-between align-items-center mb-4">
      <div>
        <h4 class="fw-bolder text-slate-800 mb-1">Quản lý Hóa Đơn</h4>
        <div class="text-slate-500 small">Tra cứu và xem chi tiết các đơn hàng đã bán</div>
      </div>
      <div class="position-relative" style="width: 320px;">
        <i class="bi bi-search position-absolute top-50 translate-middle-y text-slate-400 ms-3"></i>
        <input 
          v-model="searchQuery"
          type="text" 
          class="form-control form-control-lg bg-white border-0 shadow-sm rounded-pill ps-5 fs-6 focus-ring-peel text-slate-800" 
          placeholder="Tìm mã đơn, tên khách..."
        >
      </div>
    </div>

    <!-- BẢNG DANH SÁCH (Giữ nguyên logic cũ) -->
    <div class="card border border-slate-200 shadow-sm rounded-4 flex-grow-1 overflow-hidden d-flex flex-column bg-white">
      <div class="table-responsive flex-grow-1 custom-scrollbar">
        <table class="table table-hover align-middle mb-0 modern-table">
          <thead class="bg-slate-50 sticky-top z-1 border-bottom border-slate-200">
            <tr>
              <th class="py-3 ps-4 text-slate-500 fw-bold tracking-wider text-uppercase" style="font-size: 0.75rem;">MÃ ĐƠN</th>
              <th class="py-3 text-slate-500 fw-bold tracking-wider text-uppercase" style="font-size: 0.75rem;">KHÁCH HÀNG</th>
              <th class="py-3 text-slate-500 fw-bold tracking-wider text-uppercase" style="font-size: 0.75rem;">NGÀY TẠO</th>
              <th class="py-3 text-slate-500 fw-bold tracking-wider text-uppercase" style="font-size: 0.75rem;">TỔNG TIỀN</th>
              <th class="py-3 text-slate-500 fw-bold tracking-wider text-uppercase" style="font-size: 0.75rem;">TRẠNG THÁI</th>
              <th class="py-3 pe-4 text-end text-slate-500 fw-bold tracking-wider text-uppercase" style="font-size: 0.75rem;">THAO TÁC</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="filteredOrders.length === 0">
              <td colspan="6" class="text-center py-5 text-slate-400 fw-medium">Không tìm thấy hóa đơn.</td>
            </tr>
            <tr v-for="order in filteredOrders" :key="order.id" class="cursor-pointer transition-all hover-bg-slate-50 border-bottom border-slate-100" @click="showDetail(order.id)">
              <td class="ps-4 fw-black text-slate-800">#{{ order.id }}</td>
              <td class="fw-bold text-slate-700">
                <!-- Logic Fix Lỗi: Đổi Quản trị viên thành Khách lẻ -->
                {{ formatCustomerName(order.customerName) }}
                <div class="text-slate-400 fw-medium font-monospace mt-1" style="font-size: 0.7rem;">
                  {{ order.customerPhone || 'Không có SĐT' }}
                </div>
              </td>
              <td class="text-slate-500 fw-medium small">{{ formatDate(order.createdAt) }}</td>
              <td class="fw-black text-slate-900 fs-6">{{ formatMoney(order.totalAmount) }}</td>
              <td>
                <span class="badge rounded-pill px-3 py-2 fw-bold" :class="order.status === 'COMPLETED' ? 'bg-success text-white' : 'bg-slate-200 text-slate-600'">
                  {{ order.status === 'COMPLETED' ? 'Đã thanh toán' : (order.status || 'Chờ xử lý') }}
                </span>
              </td>
              <td class="pe-4 text-end">
                <button class="btn btn-sm btn-outline-dark rounded-pill px-3 fw-bold shadow-sm" @click.stop="showDetail(order.id)">
                  Chi tiết
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- MODAL CHI TIẾT (ĐÃ FIX FULL LOGIC KẾ TOÁN & THỰC TẾ) -->
    <Teleport to="body">
      <div v-if="isModalOpen" class="modal-overlay d-flex align-items-center justify-content-center" @mousedown.self="closeModal" style="z-index: 1050; background: rgba(15, 23, 42, 0.75); position: fixed; inset: 0; backdrop-filter: blur(4px);">
        <div class="bg-white rounded-4 shadow-lg d-flex flex-column overflow-hidden animate-fade-in" style="width: 850px; max-width: 95vw; max-height: 90vh;">
          
          <div class="bg-slate-900 text-white p-4 d-flex justify-content-between align-items-center">
            <h5 class="mb-0 fw-black tracking-wide d-flex align-items-center gap-2">
              <i class="bi bi-receipt-cutoff"></i> Hóa Đơn <span class="text-slate-300">#{{ selectedOrder?.id }}</span>
            </h5>
            <button @click="closeModal" class="btn-close btn-close-white shadow-none"></button>
          </div>

          <div v-if="isLoadingDetails" class="p-5 d-flex flex-column align-items-center justify-content-center text-slate-400">
            <div class="spinner-border mb-3" role="status"></div>
            <span class="fw-bold">Đang tải dữ liệu từ máy chủ...</span>
          </div>

          <div v-else class="p-0 overflow-auto custom-scrollbar flex-grow-1">
            <!-- Khu vực thông tin chung -->
            <div class="row g-0 border-bottom border-slate-200 bg-slate-50">
              <!-- KHÁCH HÀNG -->
              <div class="col-md-4 p-4 border-end border-slate-200">
                <div class="text-slate-500 small fw-bold text-uppercase mb-1 tracking-wider">Khách Hàng</div>
                <div class="fw-black text-slate-800 fs-6">{{ formatCustomerName(selectedOrder?.customerName) }}</div>
                <div class="text-slate-600 small mt-1 font-monospace"><i class="bi bi-telephone me-1"></i>{{ selectedOrder?.customerPhone || 'N/A' }}</div>
              </div>
              <!-- GIAO DỊCH (Đã fix thêm ngày giờ) -->
              <div class="col-md-4 p-4 border-end border-slate-200">
                <div class="text-slate-500 small fw-bold text-uppercase mb-1 tracking-wider">Giao Dịch</div>
                <div class="fw-bold text-slate-800">{{ formatDate(selectedOrder?.createdAt) }}</div>
                <div class="text-slate-600 small mt-1"><i class="bi bi-person-badge me-1"></i>Thu ngân: {{ selectedOrder?.employeeName }}</div>
              </div>
              <!-- THANH TOÁN -->
              <div class="col-md-4 p-4">
                <div class="text-slate-500 small fw-bold text-uppercase mb-1 tracking-wider">Thanh Toán</div>
                <div class="fw-bold text-slate-800">
                  <span v-if="selectedOrder?.paymentMethod === 'TRANSFER'"><i class="bi bi-qr-code-scan me-1"></i> Chuyển khoản</span>
                  <span v-else><i class="bi bi-cash-stack me-1"></i> Tiền mặt</span>
                </div>
              </div>
            </div>

            <!-- BẢNG SẢN PHẨM -->
            <div class="p-4">
              <h6 class="fw-black text-slate-800 mb-3"><i class="bi bi-box-seam me-2 text-slate-500"></i>Danh sách máy đã bán</h6>
              <div class="border border-slate-200 rounded-4 overflow-hidden shadow-sm">
                <table class="table mb-0 align-middle table-borderless">
                  <thead class="bg-slate-50 border-bottom border-slate-200">
                    <tr>
                      <th class="py-3 ps-4 text-slate-600 fw-bold small text-uppercase tracking-wider">Sản Phẩm</th>
                      <th class="py-3 text-slate-600 fw-bold small text-uppercase tracking-wider">Mã IMEI</th>
                      <th class="py-3 pe-4 text-end text-slate-600 fw-bold small text-uppercase tracking-wider">Đơn Giá</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="(item, index) in selectedOrder?.items" :key="index" class="border-bottom border-slate-100">
                      <td class="ps-4 py-3">
                        <div class="fw-bold text-slate-800">{{ item.productName || item.variant?.product?.name || item.name || 'Điện thoại' }}</div>
                        <div class="text-slate-500 small">{{ item.color || item.variant?.color }} • {{ item.storage || item.variant?.storage }}</div>
                      </td>
                      <td class="py-3">
                        <span class="badge bg-slate-100 text-slate-800 border border-slate-200 font-monospace p-2 shadow-sm">
                          {{ item.imeiCode || item.imei || item.serialNumber || 'N/A' }}
                        </span>
                      </td>
                      <td class="pe-4 py-3 text-end fw-black text-slate-800">
                        {{ formatMoney(item.price || item.unitPrice) }}
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>

            <!-- TỔNG KẾT KẾ TOÁN (ĐÃ FIX LOGIC CỘNG TRỪ) -->
            <div class="p-4 bg-white border-top border-slate-200">
              <div class="d-flex justify-content-end mb-2">
                <div class="text-end" style="width: 300px;">
                  <!-- Dòng Tạm tính (Cộng dồn tất cả giá trị hàng) -->
                  <div class="d-flex justify-content-between mb-2 text-slate-600">
                    <span class="fw-semibold">Tổng tiền hàng:</span>
                    <span class="fw-bold">{{ formatMoney(calculatedSubTotal) }}</span>
                  </div>
                  <!-- Dòng Giảm giá (Chỉ hiện nếu có giảm giá) -->
                  <div class="d-flex justify-content-between mb-3 text-success" v-if="calculatedDiscount > 0">
                    <span class="fw-semibold">Giảm giá/Voucher:</span>
                    <span class="fw-bold">- {{ formatMoney(calculatedDiscount) }}</span>
                  </div>
                  <hr class="border-slate-200 my-2">
                  <!-- Dòng Tổng cộng khách phải trả -->
                  <div class="d-flex justify-content-between align-items-center mt-2">
                    <span class="text-slate-800 fw-black text-uppercase tracking-wider">KHÁCH CẦN TRẢ:</span>
                    <span class="fs-2 fw-black text-slate-900 lh-1 text-nowrap">{{ formatMoney(selectedOrder?.totalAmount) }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Nút hành động -->
          <div class="p-3 border-top border-slate-200 bg-slate-50 d-flex justify-content-end gap-2">
            <button @click="printInvoice" class="btn btn-outline-dark fw-bold px-4 rounded-pill d-flex align-items-center gap-2 shadow-sm transition-all">
              <i class="bi bi-printer"></i> In Lại Hóa Đơn
            </button>
            <button @click="closeModal" class="btn btn-dark-peel fw-bold px-4 rounded-pill shadow-sm transition-all">
              Đóng Cửa Sổ
            </button>
          </div>

        </div>
      </div>
    </Teleport>
  </div>
</template>
<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'

// --- 1. INTERFACES ---
interface OrderItem {
  productName?: string; name?: string; variant?: any;
  color?: string; storage?: string;
  imeiCode?: string; imei?: string; serialNumber?: string;
  price?: number; unitPrice?: number;
}
interface Order {
  id: number;
  customerName?: string; customerPhone?: string;
  createdBy?: string; paymentMethod?: string;
  createdAt: string; totalAmount: number; status: string;
  items?: OrderItem[];
  
  // 👉 ĐÃ BỔ SUNG BIẾN NÀY ĐỂ DIỆT GẠCH ĐỎ CHO ÔNG:
  employeeName?: string; 
}

// --- 2. STATE ---
const orders = ref<Order[]>([])
const isModalOpen = ref<boolean>(false)
const isLoadingDetails = ref<boolean>(false)
const selectedOrder = ref<Order | null>(null)
const searchQuery = ref<string>('')

// --- 3. COMPUTED LOGIC (TÍNH TOÁN KẾ TOÁN) ---
// Tính tổng tiền gốc (Subtotal) của các món hàng
const calculatedSubTotal = computed(() => {
  if (!selectedOrder.value || !selectedOrder.value.items) return 0;
  return selectedOrder.value.items.reduce((sum, item) => {
    return sum + (Number(item.price) || Number(item.unitPrice) || 0);
  }, 0);
});

// Tính số tiền được giảm giá (Discount)
const calculatedDiscount = computed(() => {
  if (!selectedOrder.value) return 0;
  const subTotal = calculatedSubTotal.value;
  const totalPaid = Number(selectedOrder.value.totalAmount) || 0;
  const discount = subTotal - totalPaid;
  return discount > 0 ? discount : 0;
});

// Hàm lọc tìm kiếm
const filteredOrders = computed(() => {
  if (!searchQuery.value) return orders.value;
  const q = searchQuery.value.toLowerCase().trim();
  return orders.value.filter(order => {
    return order.id.toString().includes(q) || 
           (order.customerName || '').toLowerCase().includes(q) || 
           (order.customerPhone || '').includes(q);
  });
})

// --- 4. HÀM XỬ LÝ TEXT THÔNG MINH ---
// Nếu tên khách là Quản trị viên, ép về Khách mua lẻ
const formatCustomerName = (name?: string) => {
  if (!name || name.trim().toLowerCase() === 'quản trị viên' || name.trim().toLowerCase() === 'admin') {
    return 'Khách lẻ tại quầy';
  }
  return name;
};

// Nếu tên Thu ngân trống, đặt là Nhân viên bán hàng
const formatStaffName = (name?: string) => {
  if (!name || name.toLowerCase() === 'system') return 'Nhân viên bán hàng';
  return name;
}

// --- 5. HÀM API & UI ---
const fetchOrders = async () => {
  try {
    const res = await axios.get("http://localhost:8080/api/admin/orders")
    orders.value = res.data.content || res.data || []
  } catch (error) { console.error("Lỗi tải:", error) }
}

const showDetail = async (orderId: number) => {
  isModalOpen.value = true;
  isLoadingDetails.value = true;
  try {
    const res = await axios.get(`http://localhost:8080/api/admin/orders/${orderId}`);
    selectedOrder.value = res.data;
  } catch (error) {
    const fallbackOrder = orders.value.find(o => o.id === orderId);
    if (fallbackOrder) selectedOrder.value = fallbackOrder;
  } finally {
    isLoadingDetails.value = false;
  }
};

const closeModal = () => { isModalOpen.value = false; selectedOrder.value = null; }
const printInvoice = () => { alert("Chức năng in đang kết nối máy in nhiệt..."); }

// --- 6. FORMATTER ---
const formatMoney = (val: any) => `${(Number(val) || 0).toLocaleString('vi-VN')} ₫`
const formatDate = (dateStr: string | undefined) => {
  if (!dateStr) return 'Chưa ghi nhận thời gian';
  const d = new Date(dateStr);
  return `${d.getHours().toString().padStart(2, '0')}:${d.getMinutes().toString().padStart(2, '0')} - ${d.getDate().toString().padStart(2, '0')}/${(d.getMonth() + 1).toString().padStart(2, '0')}/${d.getFullYear()}`;
}

onMounted(() => { fetchOrders() })
</script>

<style scoped>
/* Toàn bộ style chuẩn thiết kế cũ của ông */
.text-slate-300 { color: #cbd5e1 !important; }
.text-slate-400 { color: #94a3b8 !important; }
.text-slate-500 { color: #64748b !important; }
.text-slate-600 { color: #475569 !important; }
.text-slate-700 { color: #334155 !important; }
.text-slate-800 { color: #1e293b !important; }
.text-slate-900 { color: #0f172a !important; }
.bg-slate-50 { background-color: #f8fafc !important; }
.bg-slate-100 { background-color: #f1f5f9 !important; }
.bg-slate-900 { background-color: #0f172a !important; }
.border-slate-50 { border-color: #f8fafc !important; }
.border-slate-100 { border-color: #f1f5f9 !important; }
.border-slate-200 { border-color: #e2e8f0 !important; }

.btn-dark-peel { background-color: #1e293b; color: white; border: none; }
.btn-dark-peel:hover { background-color: #0f172a; transform: translateY(-1px); }
.btn-outline-dark { color: #1e293b; border: 1px solid #cbd5e1; background: white; }
.btn-outline-dark:hover { background: #f8fafc; border-color: #1e293b; transform: translateY(-1px); }

.fw-black { font-weight: 900 !important; }
.tracking-wider { letter-spacing: 0.05em; }
.cursor-pointer { cursor: pointer; }
.transition-all { transition: all 0.2s ease-in-out; }
.hover-bg-slate-50:hover { background-color: #fcfdfe !important; }
.focus-ring-peel:focus-within, .focus-ring-peel:focus { border-color: #1e293b !important; box-shadow: 0 0 0 3px rgba(30, 41, 59, 0.1) !important; background-color: #ffffff !important; }

.custom-scrollbar::-webkit-scrollbar { width: 6px; }
.custom-scrollbar::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 10px; }
.modal-overlay { backdrop-filter: blur(6px); animation: fadeIn 0.25s ease-out; }
.animate-fade-in { animation: popIn 0.3s cubic-bezier(0.34, 1.56, 0.64, 1); }

@keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }
@keyframes popIn { from { transform: scale(0.95); opacity: 0; } to { transform: scale(1); opacity: 1; } }
</style>