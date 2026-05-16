<template>
  <div class="pos-container">
    <div class="h-100 row g-4 p-3 bg-slate-50 d-print-none">
      <div class="col-xl-8 col-lg-7 d-flex flex-column h-100">
        
        <div class="d-flex justify-content-between align-items-center mb-3">
          <div>
            <h4 class="fw-bolder text-slate-800 mb-1">Điểm bán hàng (POS)</h4>
            <div class="text-slate-500 small">Chọn máy hoặc quét IMEI để lập đơn hàng</div>
          </div>
          <div class="position-relative" style="width: 350px;">
            <i class="bi bi-upc-scan position-absolute top-50 translate-middle-y text-slate-800 ms-3 fs-5"></i>
            <input 
              v-model="scannedCode" 
              @keyup.enter="handleScanImei"
              type="text" 
              class="form-control form-control-lg bg-white border-0 shadow-sm rounded-pill ps-5 fs-6 fw-bold text-slate-800 focus-ring-peel" 
              placeholder="Nhập hoặc quét mã IMEI..."
              autofocus
            >
          </div>
        </div>

        <div class="d-flex gap-3 mb-3">
          <div class="position-relative flex-grow-1">
            <i class="bi bi-search position-absolute top-50 translate-middle-y text-slate-400 ms-3"></i>
            <input 
              v-model="searchQuery" 
              type="text" 
              class="form-control bg-white border-0 shadow-sm rounded-pill ps-5 focus-ring-peel text-slate-700" 
              placeholder="Tìm tên máy, màu sắc, dung lượng..."
            >
          </div>
          <select v-model="filterStock" class="form-select bg-white border-0 shadow-sm rounded-pill px-4 focus-ring-peel fw-medium text-slate-700" style="width: 200px; cursor: pointer;">
            <option value="ALL">Tất cả sản phẩm</option>
            <option value="IN_STOCK">Chỉ máy còn hàng</option>
          </select>
        </div>

        <div class="flex-grow-1 overflow-auto custom-scrollbar pe-2 pb-2">
          <div v-if="displayProducts.length === 0" class="h-100 d-flex flex-column justify-content-center align-items-center text-slate-400">
            <div class="spinner-border text-slate-300 mb-3" role="status"></div>
            <span class="fw-medium">Đang tải dữ liệu kho...</span>
          </div>
          <div v-else-if="filteredProducts.length === 0" class="h-100 d-flex flex-column justify-content-center align-items-center text-slate-400">
            <i class="bi bi-search fs-1 mb-2 opacity-25"></i>
            <span class="fw-medium">Không tìm thấy sản phẩm phù hợp</span>
          </div>
          <div v-else class="row g-3">
            <div v-for="item in filteredProducts" :key="item.variantId" class="col-xl-4 col-md-6">
              <div class="card product-card border-0 shadow-sm rounded-4 h-100 d-flex flex-column p-0 transition-all overflow-hidden bg-white" :class="{'hover-lift cursor-pointer': item.stock > 0, 'opacity-75 grayscale': item.stock <= 0}" @click="item.stock > 0 ? autoPickImei(item.variantId) : null">
                <div class="d-flex justify-content-center align-items-center border-bottom border-slate-50 position-relative" style="height: 160px; background: #fcfdfe;">
                  <img :src="item.image" class="img-fluid object-fit-contain h-100 w-100 p-3 transition-all product-img" alt="Product Image" @error="handleImageError">
                  <span class="badge rounded-pill fw-bold position-absolute top-0 end-0 m-2 shadow-sm" :class="item.stock > 0 ? 'bg-slate-800 text-white' : 'bg-danger text-white'">
                    {{ item.stock > 0 ? `Tồn: ${item.stock}` : 'Hết hàng' }}
                  </span>
                </div>
                <div class="p-3 d-flex flex-column flex-grow-1">
                  <h6 class="fw-bold text-slate-800 mb-1 text-truncate" :title="item.name">{{ item.name }}</h6>
                  <div class="text-slate-500 small fw-medium mb-3">{{ item.color }} <span class="mx-1 opacity-50">•</span> {{ item.storage }}</div>
                  <div class="mt-auto d-flex justify-content-between align-items-center pt-3 border-top border-slate-50">
                    <div class="fw-black text-slate-900 fs-5 text-nowrap">{{ formatMoney(item.price) }}</div>
                    <button class="btn btn-light-peel btn-sm rounded-circle d-flex align-items-center justify-content-center transition-all" style="width: 32px; height: 32px;" :disabled="item.stock <= 0">
                      <i class="bi" :class="item.stock > 0 ? 'bi-plus-lg' : 'bi-x-lg'"></i>
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="col-xl-4 col-lg-5 h-100 d-flex flex-column">
        <div class="bg-white rounded-4 shadow-sm d-flex flex-column h-100 overflow-hidden border border-slate-200">
          <div class="p-3 border-bottom border-slate-100 d-flex justify-content-between align-items-center bg-white">
            <div class="d-flex align-items-center gap-2">
              <div class="bg-slate-100 p-2 rounded-3 border border-slate-200"><i class="bi bi-cart-check-fill text-slate-700"></i></div>
              <h6 class="fw-bolder text-slate-800 mb-0">ĐƠN HÀNG TẠM</h6>
            </div>
            <button class="btn btn-sm btn-light text-danger fw-bold rounded-pill" @click="clearCart" :disabled="cart.length === 0">Xóa hết</button>
          </div>
          <div class="flex-grow-1 overflow-auto custom-scrollbar p-3 bg-slate-50">
            <div v-if="cart.length === 0" class="h-100 d-flex flex-column justify-content-center align-items-center text-slate-400">
              <i class="bi bi-qr-code-scan fs-1 mb-2 opacity-25"></i>
              <span class="small fw-medium">Chưa có máy nào được chọn</span>
            </div>
            <transition-group name="list">
              <div v-for="(item, index) in cart" :key="item.imeiCode" class="bg-white p-3 rounded-4 shadow-sm mb-3 border border-slate-100 position-relative cart-item-card transition-all">
                <button class="btn-remove" @click.stop="removeScannedImei(index)"><i class="bi bi-x-lg"></i></button>
                <div class="d-flex gap-3">
                  <div class="bg-slate-50 rounded-3 p-1 border border-slate-100" style="width: 55px; height: 55px;">
                    <img :src="item.image || defaultImage" class="w-100 h-100 object-fit-contain" alt="mini">
                  </div>
                  <div class="flex-grow-1 pe-3">
                    <div class="fw-bold text-slate-800" style="font-size: 0.9rem;">{{ item.name }}</div>
                    <div class="text-slate-500" style="font-size: 0.75rem;">Màu: {{ item.color }} • B/N: {{ item.storage }}</div>
                    <div class="badge bg-slate-100 text-slate-700 border border-slate-200 font-monospace mt-1 mb-2" style="font-size: 0.7rem;">IMEI: {{ item.imeiCode }}</div>
                    
                    <select v-model.number="item.warrantyFee" @change="updateWarrantyName(item)" class="form-select form-select-sm bg-slate-50 border-slate-200 shadow-none fw-medium text-slate-600 mb-2" style="font-size: 0.75rem;">
                      <option :value="0">Mặc định: Bảo hành 6 tháng (0đ)</option>
                      <option :value="500000">Gói Bảo hành VIP 12 tháng (+500k)</option>
                      <option :value="800000">Gói Rơi vỡ, Vào nước (+800k)</option>
                    </select>
                  </div>
                </div>
                <div class="d-flex justify-content-between align-items-center mt-2 pt-2 border-top border-slate-50">
                  <div class="text-slate-500 fw-bold" style="font-size: 0.85rem;">SL: 1</div>
                  <div class="fw-black text-slate-800 fs-6 lh-1 text-nowrap">{{ formatMoney(item.price + item.warrantyFee) }}</div>
                </div>
              </div>
            </transition-group>
          </div>
          <div class="p-4 bg-white border-top border-slate-200 z-1">
            <div class="d-flex justify-content-between mb-3">
              <span class="text-slate-500 fw-bold small">TẠM TÍNH ({{ cart.length }} máy):</span>
              <span class="fw-black text-slate-800 fs-4 lh-1 text-nowrap">{{ formatMoney(cartTotal) }}</span>
            </div>
            <button @click="openPaymentModal" class="btn btn-dark-peel w-100 py-3 rounded-3 fw-black fs-5 shadow-sm" :disabled="cart.length === 0">THANH TOÁN (F9)</button>
          </div>
        </div>
      </div>

      <Teleport to="body">
        <div v-if="showPaymentModal" class="modal-overlay d-flex align-items-center justify-content-center p-3 d-print-none" style="z-index: 1050; background: rgba(15, 23, 42, 0.75); position: fixed; inset: 0; backdrop-filter: blur(8px);">
          <div class="bg-white rounded-5 shadow-2xl overflow-hidden animate-pop-in d-flex flex-column border border-slate-200" style="width: 1000px; max-width: 95vw; height: 85vh;">
            <div class="bg-slate-900 p-4 text-white d-flex justify-content-between align-items-center">
              <div class="d-flex align-items-center gap-3">
                <i class="bi bi-shield-check fs-2"></i>
                <div>
                  <h4 class="mb-0 fw-black tracking-tight">Xác nhận thanh toán</h4>
                  <div class="small opacity-75 fw-medium">Vui lòng nhập thông tin để kích hoạt bảo hành điện tử</div>
                </div>
              </div>
              <button @click="showPaymentModal = false" class="btn-close btn-close-white shadow-none"></button>
            </div>
            
            <div class="row g-0 flex-grow-1 overflow-hidden">
              <div class="col-md-7 p-4 border-end border-slate-200 d-flex flex-column h-100 overflow-auto custom-scrollbar">
                
                <div class="section-label mb-3"><i class="bi bi-person-lines-fill me-2"></i>Thông tin khách hàng</div>
                <div class="bg-slate-50 p-4 rounded-4 mb-4 border border-slate-100 shadow-sm">
                  <div class="row g-3">
                    <div class="col-sm-6">
                      <label class="form-label small fw-bold text-slate-600 mb-1">Số điện thoại <span class="text-danger">*</span></label>
                      <input type="text" class="form-control form-control-lg border-slate-200 rounded-3 shadow-none focus-ring-peel fs-6" v-model="customer.phone" placeholder="09xx xxx xxx" autofocus>
                    </div>
                    <div class="col-sm-6">
                      <label class="form-label small fw-bold text-slate-600 mb-1">Tên khách hàng</label>
                      <input type="text" class="form-control form-control-lg border-slate-200 rounded-3 shadow-none focus-ring-peel fs-6" v-model="customer.name" placeholder="Nguyễn Văn A">
                    </div>
                  </div>
                </div>
                
                <div class="section-label mb-3"><i class="bi bi-credit-card-2-front me-2"></i>Hình thức thanh toán</div>
                <div class="d-flex gap-2 mb-4">
                  <div class="pay-method-card" :class="{active: paymentForm.method === 'CASH'}" @click="paymentForm.method = 'CASH'">
                    <i class="bi bi-cash-stack mb-1 d-block"></i> <span style="font-size: 0.9rem;">Tiền mặt</span>
                  </div>
                  <div class="pay-method-card" :class="{active: paymentForm.method === 'TRANSFER'}" @click="paymentForm.method = 'TRANSFER'">
                    <i class="bi bi-qr-code-scan mb-1 d-block"></i> <span style="font-size: 0.9rem;">Chuyển khoản</span>
                  </div>
                  <div class="pay-method-card" :class="{active: paymentForm.method === 'SPLIT'}" @click="paymentForm.method = 'SPLIT'">
                    <i class="bi bi-pie-chart mb-1 d-block"></i> <span style="font-size: 0.9rem;">Kết hợp</span>
                  </div>
                </div>

                <div v-if="paymentForm.method === 'CASH' || paymentForm.method === 'SPLIT'">
                  <label class="form-label small fw-bold text-slate-600 mb-2">
                    {{ paymentForm.method === 'SPLIT' ? 'Tiền mặt khách đưa (VNĐ)' : 'Tiền khách đưa (VNĐ)' }}
                  </label>
                  <input type="text" class="form-control form-control-lg fw-black text-end rounded-4 border-2 border-slate-200 focus-ring-peel py-3 mb-3 fs-3 text-slate-900" v-model="displayCustomerGiven" @input="onInputCash" @focus="handleFocusInput">
                  
                  <div class="d-flex flex-wrap gap-2 mb-4">
                    <button @click="setQuickCash(paymentForm.method === 'SPLIT' ? finalTotal / 2 : finalTotal)" class="btn btn-sm btn-dark-peel rounded-pill px-3 fw-bold shadow-sm">
                      {{ paymentForm.method === 'SPLIT' ? '50% Giá trị' : 'Đưa đủ' }}
                    </button>
                    <button v-if="paymentForm.method === 'CASH'" v-for="suggestion in cashSuggestions" :key="suggestion" @click="setQuickCash(suggestion)" class="btn btn-sm btn-outline-dark rounded-pill px-3 fw-bold bg-white">{{ formatMoney(suggestion) }}</button>
                  </div>

                  <div v-if="paymentForm.method === 'CASH'" class="d-flex justify-content-between align-items-center p-3 bg-slate-100 border border-slate-200 rounded-4 shadow-sm">
                    <span class="fw-bold small text-slate-600 tracking-wider">TIỀN THỪA TRẢ KHÁCH:</span>
                    <span class="fs-4 fw-black text-nowrap" :class="changeAmount >= 0 ? 'text-success' : 'text-danger'">{{ changeAmount >= 0 ? formatMoney(changeAmount) : 'Khách đưa thiếu' }}</span>
                  </div>
                </div>

                <div v-if="paymentForm.method === 'TRANSFER' || paymentForm.method === 'SPLIT'" class="text-center bg-slate-50 p-4 rounded-4 border-2 border-dashed border-slate-200 mt-auto">
                  <div v-if="paymentForm.method === 'SPLIT'" class="mb-3 p-3 bg-white rounded-4 border border-slate-200 shadow-sm d-flex justify-content-between align-items-center">
                    <span class="fw-bold text-slate-600 small tracking-wider">CẦN CHUYỂN KHOẢN:</span>
                    <div class="fs-3 fw-black text-primary">{{ formatMoney(customerTransferAmount) }}</div>
                  </div>

                  <div class="mb-3 text-start">
                    <label class="form-label small fw-bold text-slate-600 mb-1">Chọn tài khoản nhận tiền</label>
                    <select v-model="selectedBankId" class="form-select border-slate-300 shadow-none rounded-3 focus-ring-peel fw-bold">
                      <option v-for="bank in supportedBanks" :key="bank.id" :value="bank.id">{{ bank.name }} - {{ bank.accNo }}</option>
                    </select>
                  </div>

                  <div class="bg-white p-2 d-inline-block rounded-4 shadow-sm mb-3 border border-slate-100 cursor-pointer" title="Click đúp để giả lập khách đã chuyển khoản">
                    <img :src="qrCodeUrl" @dblclick="simulateBankTransfer" class="qr-img" :class="{'opacity-25 grayscale': paymentForm.method === 'SPLIT' && customerTransferAmount <= 0}" alt="QR Payment">
                  </div>
                  <div class="small fw-black text-slate-800">{{ selectedBank.name }}</div>
                  <div class="small fw-bold text-slate-500">{{ selectedBank.accName }}</div>
                </div>
              </div>

              <div class="col-md-5 bg-slate-50 p-4 d-flex flex-column h-100">
                <div class="section-label mb-3"><i class="bi bi-receipt me-2"></i>Tóm tắt đơn hàng</div>
                <div class="flex-grow-1 overflow-auto custom-scrollbar pe-2 mb-3">
                  <div v-for="item in cart" :key="item.imeiCode" class="d-flex justify-content-between mb-3 pb-3 border-bottom border-slate-200 align-items-center">
                    <div class="text-slate-700">
                      <div class="fw-bold text-slate-800" style="font-size: 0.9rem;">{{ item.name }}</div>
                      <div class="font-monospace text-slate-500 mt-1 mb-1" style="font-size: 0.75rem;">IMEI: {{ item.imeiCode }}</div>
                      <div v-if="item.warrantyFee > 0" class="text-primary small fw-bold"><i class="bi bi-shield-check"></i> +{{ item.warrantyName }}</div>
                    </div>
                    <div class="fw-black text-slate-800 text-nowrap">{{ formatMoney(item.price + item.warrantyFee) }}</div>
                  </div>
                </div>
                
                <div class="mt-auto">
                  <div class="mb-3">
                    <label class="form-label small fw-bold text-slate-600 mb-1">Mã giảm giá (Voucher)</label>
                    <div class="input-group">
                      <input type="text" class="form-control rounded-start-3 border-slate-200 shadow-none focus-ring-peel text-uppercase fw-bold text-slate-700" v-model="voucherCode" placeholder="Nhập mã giảm giá..." :disabled="appliedDiscount > 0">
                      <button class="btn btn-outline-dark fw-bold px-3 border-slate-200" style="background: white;" @click="handleApplyVoucher" :disabled="!voucherCode || appliedDiscount > 0">Áp dụng</button>
                    </div>
                    <div v-if="appliedDiscount > 0" class="text-success small fw-bold mt-1 d-flex justify-content-between align-items-center bg-success bg-opacity-10 p-2 rounded-2 border border-success border-opacity-25 mt-2">
                      <span><i class="bi bi-check-circle-fill me-1"></i> Đã giảm: {{ formatMoney(appliedDiscount) }}</span>
                      <span class="cursor-pointer text-danger text-decoration-underline" @click="removeVoucher">Hủy mã</span>
                    </div>
                  </div>

                  <div class="mb-3">
                    <label class="form-label small fw-bold text-slate-600 mb-1">Ghi chú thêm (Tùy chọn)</label>
                    <input type="text" class="form-control rounded-3 border-slate-200 shadow-none focus-ring-peel bg-white" v-model="paymentForm.note" placeholder="Ví dụ: Giao hàng tận nơi...">
                  </div>

                  <div class="bg-white p-3 rounded-4 border border-slate-200 shadow-sm mb-4">
                    <div class="d-flex justify-content-between mb-2">
                      <span class="small fw-bold text-slate-500">Tạm tính:</span>
                      <span class="small fw-bold text-slate-800 text-nowrap">{{ formatMoney(cartTotal) }}</span>
                    </div>
                    <div class="d-flex justify-content-between mb-3 pb-3 border-bottom border-dashed">
                      <span class="small fw-bold text-slate-500">Giảm giá (Voucher):</span>
                      <span class="small fw-bold text-success text-nowrap">- {{ formatMoney(appliedDiscount) }}</span>
                    </div>
                    <div class="d-flex justify-content-between align-items-end">
                      <div class="small fw-black text-slate-500 tracking-wider">TỔNG CỘNG</div>
                      <div class="fs-2 fw-black text-slate-900 lh-1 text-nowrap">{{ formatMoney(finalTotal) }}</div>
                    </div>
                  </div>
                  <button @click="handleCheckout" class="btn btn-dark-peel w-100 py-3 rounded-pill fw-black shadow-sm fs-5 d-flex align-items-center justify-content-center gap-2" :disabled="paymentForm.method === 'CASH' && changeAmount < 0">
                    <i class="bi bi-check-circle-fill"></i> HOÀN TẤT THANH TOÁN
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </Teleport>
    </div>

    <Teleport to="body">
      <div id="print-receipt" class="d-none">
        <div class="receipt-header">
          <h2 class="store-name">LATRA STORE</h2>
          <p>ĐC: Trịnh Văn Bô, Nam Từ Liêm, Hà Nội</p>
          <p>Hotline: 0988.888.888</p>
          <div class="divider"></div>
          <h3 class="receipt-title">HÓA ĐƠN BÁN HÀNG</h3>
          <p>Số: HD-{{ Math.floor(Math.random() * 10000) }}</p>
          <p>Ngày: {{ new Date().toLocaleString('vi-VN') }}</p>
          <div class="divider"></div>
          <div class="customer-info">
            <p>Khách hàng: {{ printData.customerName || 'Khách lẻ' }}</p>
            <p>SĐT: {{ printData.customerPhone }}</p>
          </div>
          <div class="divider"></div>
        </div>

        <table class="receipt-table">
          <thead>
            <tr>
              <th class="text-start">Tên sản phẩm</th>
              <th class="text-end">Thành tiền</th>
            </tr>
          </thead>
          <tbody>
            <template v-for="(item, index) in printData.items" :key="index">
              <tr>
                <td colspan="2" class="item-name">{{ item.name }} ({{ item.color }} - {{ item.storage }})</td>
              </tr>
              <tr>
                <td class="item-desc">IMEI: {{ item.imeiCode }}</td>
                <td class="item-price text-end">{{ formatMoney(item.price) }}</td>
              </tr>
              <tr v-if="item.warrantyFee > 0">
                <td class="item-desc fst-italic text-secondary" style="padding-left: 10px;">+ {{ item.warrantyName }}</td>
                <td class="item-price text-end fst-italic">{{ formatMoney(item.warrantyFee) }}</td>
              </tr>
            </template>
          </tbody>
        </table>

        <div class="divider"></div>

        <div class="receipt-summary">
          <div class="summary-line">
            <span>Tổng số lượng:</span>
            <span>{{ printData.items.length }}</span>
          </div>
          <div class="summary-line">
            <span>Tạm tính:</span>
            <span>{{ formatMoney(printData.subtotal) }}</span>
          </div>
          <div class="summary-line" v-if="printData.discount > 0">
            <span>Giảm giá (Voucher):</span>
            <span>- {{ formatMoney(printData.discount) }}</span>
          </div>
          
          <div class="summary-line total-line">
            <span>TỔNG CỘNG:</span>
            <span>{{ formatMoney(printData.total) }}</span>
          </div>
          
          <div class="summary-line mt-1">
            <span>Hình thức:</span>
            <span>{{ printData.method === 'CASH' ? 'Tiền mặt' : (printData.method === 'TRANSFER' ? 'Chuyển khoản' : 'Kết hợp (TM + CK)') }}</span>
          </div>
          
          <template v-if="printData.method === 'CASH'">
            <div class="summary-line">
              <span>Tiền khách đưa:</span>
              <span>{{ formatMoney(printData.customerGiven) }}</span>
            </div>
            <div class="summary-line">
              <span>Tiền thừa trả khách:</span>
              <span>{{ formatMoney(printData.customerGiven - printData.total) }}</span>
            </div>
          </template>

          <template v-if="printData.method === 'SPLIT'">
            <div class="summary-line">
              <span>Tiền mặt:</span>
              <span>{{ formatMoney(printData.customerGiven) }}</span>
            </div>
            <div class="summary-line">
              <span>Đã chuyển khoản:</span>
              <span>{{ formatMoney(printData.transferAmount) }}</span>
            </div>
          </template>
        </div>

        <div class="divider"></div>

        <div class="receipt-footer text-center">
          <p class="fw-bold mt-2">CẢM ƠN QUÝ KHÁCH HÀNG!</p>
          <p class="policy">Lưu ý: Quý khách vui lòng giữ lại hóa đơn này để được hỗ trợ bảo hành máy tại hệ thống LATRA.</p>
          <div class="barcode-fake">*LATRA-{{ new Date().getFullYear() }}*</div>
        </div>
      </div>
    </Teleport>

  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import axios from 'axios'
import Swal from 'sweetalert2'

// --- INTERFACES ---
interface Bank { id: string; name: string; accNo: string; accName: string; }
interface DisplayProduct { variantId: string; name: string; color: string; storage: string; price: number; stock: number; image: string; }
interface CartItem { imeiCode: string; name: string; color: string; storage: string; price: number; image: string; warrantyFee: number; warrantyName: string; }

const defaultBank: Bank = { id: 'TCB', name: 'Techcombank', accNo: '3714082007', accName: 'NGUYEN DINH HUY' };
const supportedBanks: Bank[] = [
  defaultBank,
  { id: 'MB', name: 'MB Bank', accNo: '0901000001', accName: 'NGUYEN DINH HUY' },
  { id: 'VCB', name: 'Vietcombank', accNo: '1012345678', accName: 'NGUYEN DINH HUY' }
];

const defaultImage = 'https://placehold.co/400x400?text=No+Image';

// --- STATE ---
const scannedCode = ref<string>('') 
const dbImeis = ref<any[]>([]) 
const displayProducts = ref<DisplayProduct[]>([]) 
const cart = ref<CartItem[]>([]) 
const scannedImeiList = ref<string[]>([]) 

const searchQuery = ref<string>('')
const filterStock = ref<string>('ALL')

const customer = ref({ name: '', phone: '' });
const selectedBankId = ref<string>(defaultBank.id)
const showPaymentModal = ref<boolean>(false)
const displayCustomerGiven = ref<string>('0') 

// TRẠNG THÁI VOUCHER MỚI THÊM
const voucherCode = ref<string>('');
const appliedDiscount = ref<number>(0);

const paymentForm = ref({ method: 'CASH' as 'CASH' | 'TRANSFER' | 'SPLIT', customerGiven: 0, note: '' })

const printData = ref({
  items: [] as CartItem[],
  subtotal: 0, // Đã thêm subtotal (Tạm tính)
  discount: 0, // Đã thêm discount (Giảm giá)
  total: 0,
  customerName: '',
  customerPhone: '',
  method: '',
  customerGiven: 0,
  transferAmount: 0 
});

// --- COMPUTED LẠI CÁCH TÍNH TIỀN THEO VOUCHER ---
const cartTotal = computed<number>(() => cart.value.reduce((sum, item) => sum + item.price + Number(item.warrantyFee), 0));

// TỔNG TIỀN CUỐI CÙNG (Đã trừ voucher)
const finalTotal = computed<number>(() => {
  const total = cartTotal.value - appliedDiscount.value;
  return total > 0 ? total : 0;
});

// Tính tiền thừa và tiền chuyển khoản dựa trên FINAL TOTAL
const changeAmount = computed<number>(() => paymentForm.value.customerGiven - finalTotal.value);
const selectedBank = computed<Bank>(() => supportedBanks.find(b => b.id === selectedBankId.value) || defaultBank);

const customerTransferAmount = computed<number>(() => {
  if (paymentForm.value.method !== 'SPLIT') return 0;
  const remain = finalTotal.value - paymentForm.value.customerGiven;
  return remain > 0 ? remain : 0;
});

const qrCodeUrl = computed<string>(() => {
  const bank = selectedBank.value;
  const accName = bank.accName.replace(/\s+/g, '%20');
  const amount = paymentForm.value.method === 'SPLIT' ? customerTransferAmount.value : finalTotal.value;
  return `https://img.vietqr.io/image/${bank.id}-${bank.accNo}-compact2.png?amount=${amount}&addInfo=LATRA%20Thanh%20Toan&accountName=${accName}`;
});

const cashSuggestions = computed(() => {
  const total = finalTotal.value;
  if (total === 0) return [500000, 1000000, 2000000];
  const roundedBase = Math.ceil(total / 1000000) * 1000000;
  return [roundedBase, roundedBase + 500000, roundedBase + 1000000].filter(v => v > total);
});

const filteredProducts = computed(() => {
  return displayProducts.value.filter(item => {
    const q = searchQuery.value.toLowerCase();
    const matchSearch = item.name.toLowerCase().includes(q) || item.color.toLowerCase().includes(q) || item.storage.toLowerCase().includes(q);
    const matchStock = filterStock.value === 'ALL' || (filterStock.value === 'IN_STOCK' && item.stock > 0);
    return matchSearch && matchStock;
  });
});

// Reset Voucher nếu khách xóa hết sản phẩm
watch(cartTotal, (newVal) => {
  if (newVal === 0) removeVoucher();
});

// --- FUNCTIONS ---
const fetchWarehouseData = async () => {
  try {
    const res = await axios.get("http://localhost:8080/api/v1/imeis")
    dbImeis.value = res.data
    const grouped = new Map<string, DisplayProduct>()
    res.data.forEach((imei: any) => {
      if (imei.variant) {
        const key = imei.variant.product?.name + imei.variant.color
        const imgUrl = imei.variant.image || imei.variant.product?.image || defaultImage;
        if (!grouped.has(key)) {
          grouped.set(key, {
            variantId: key, name: imei.variant.product?.name || 'Sản phẩm',
            color: imei.variant.color || '', storage: imei.variant.storage || '',
            price: Number(imei.variant.price) || 0, stock: imei.status === 'AVAILABLE' ? 1 : 0, image: imgUrl
          })
        } else {
          const existing = grouped.get(key)
          if (existing && imei.status === 'AVAILABLE') existing.stock++ 
        }
      }
    })
    displayProducts.value = Array.from(grouped.values())
  } catch (error) { console.error("Lỗi lấy dữ liệu kho:", error) }
}

const handleImageError = (e: any) => { 
  if (!e.target.src.includes('placehold.co')) { e.target.src = defaultImage; }
}

const updateWarrantyName = (item: CartItem) => {
  if (item.warrantyFee === 0) item.warrantyName = 'Bảo hành tiêu chuẩn 6 tháng';
  else if (item.warrantyFee === 500000) item.warrantyName = 'Gói Bảo hành VIP 12 tháng';
  else if (item.warrantyFee === 800000) item.warrantyName = 'Gói Rơi vỡ, Vào nước';
}

const onInputCash = (e: Event) => {
  const target = e.target as HTMLInputElement;
  const numValue = parseInt(target.value.replace(/\D/g, ''), 10) || 0;
  paymentForm.value.customerGiven = numValue;
  displayCustomerGiven.value = numValue.toLocaleString('en-US'); 
}

const setQuickCash = (amount: number) => {
  paymentForm.value.customerGiven = amount;
  displayCustomerGiven.value = amount.toLocaleString('en-US');
}

const handleFocusInput = (event: Event) => {
  const target = event.target as HTMLInputElement;
  if (target) target.select();
};

const autoPickImei = (variantIdKey: string) => {
  const availableImei = dbImeis.value.find((i: any) => {
    const key = i.variant?.product?.name + i.variant?.color;
    return i.status === 'AVAILABLE' && key === variantIdKey && !scannedImeiList.value.includes(i.imeiCode);
  });
  if (availableImei) {
    scannedCode.value = availableImei.imeiCode; handleScanImei();
  } else {
    Swal.fire({ icon: 'error', title: 'Hết hàng!', text: 'Phiên bản này đã hết máy để bán!', timer: 2000, showConfirmButton: false });
  }
};

const handleScanImei = () => {
  const code = scannedCode.value.trim()
  if (!code) return
  if (scannedImeiList.value.includes(code)) {
    Swal.fire({ icon: 'error', title: 'Lỗi', text: 'Máy này đã quét rồi!', timer: 1500, showConfirmButton: false });
    scannedCode.value = ''; return;
  }
  const foundImei = dbImeis.value.find((i: any) => i.imeiCode === code && i.status === 'AVAILABLE')
  if (!foundImei) {
    Swal.fire({ icon: 'warning', title: 'Lỗi', text: 'IMEI không hợp lệ hoặc đã bán!', timer: 1500, showConfirmButton: false });
    scannedCode.value = ''; return;
  }
  scannedImeiList.value.push(code)
  
  cart.value.unshift({
    imeiCode: foundImei.imeiCode, name: foundImei.variant?.product?.name || 'Điện thoại',
    color: foundImei.variant?.color || '', storage: foundImei.variant?.storage || '',
    price: Number(foundImei.variant?.price) || 0, image: foundImei.variant?.image || foundImei.variant?.product?.image || defaultImage,
    warrantyFee: 0, warrantyName: 'Bảo hành tiêu chuẩn 6 tháng'
  })
  scannedCode.value = ''
}

const removeScannedImei = (index: number) => { scannedImeiList.value.splice(index, 1); cart.value.splice(index, 1); }

const clearCart = () => {
  Swal.fire({ title: 'Làm mới giỏ hàng?', text: "Tất cả máy đã quét sẽ bị xóa!", icon: 'warning', showCancelButton: true, confirmButtonText: 'Đồng ý xóa', confirmButtonColor: '#1e293b' }).then((res) => {
    if (res.isConfirmed) { cart.value = []; scannedImeiList.value = []; removeVoucher(); }
  })
}

// LOGIC XỬ LÝ VOUCHER 
// --- LOGIC XỬ LÝ VOUCHER GỌI API THẬT ---
// --- LOGIC XỬ LÝ VOUCHER GỌI API THẬT ---
const handleApplyVoucher = async () => {
  const code = voucherCode.value.trim().toUpperCase();
  if (!code) return;

  try {
    // 1. Gọi thẳng xuống API Spring Boot mà ông vừa code
    const res = await axios.get(`http://localhost:8080/api/v1/vouchers/admin/check?code=${code}&total=${cartTotal.value}`);
    
    // 2. Lấy số tiền giảm giá Backend trả về đập vào màn hình
    appliedDiscount.value = res.data.discountAmount;
    setQuickCash(finalTotal.value); 
    
    Swal.fire({ icon: 'success', title: 'Áp dụng mã thành công!', toast: true, position: 'top-end', showConfirmButton: false, timer: 2000 });
  } catch (error: any) {
    // 3. Nếu Java chửi lỗi (hết hạn, chưa đủ điều kiện...), thì hứng câu chửi đó hiện lên
    let errorMsg = "Mã không tồn tại hoặc lỗi kết nối!";
    if (error.response?.data?.message) {
      errorMsg = error.response.data.message; 
    }
    Swal.fire({ icon: 'error', title: 'Lỗi Voucher', text: errorMsg, toast: true, position: 'top-end', showConfirmButton: false, timer: 3000 });
    voucherCode.value = '';
    appliedDiscount.value = 0;
  }
}

const removeVoucher = () => {
  voucherCode.value = '';
  appliedDiscount.value = 0;
  setQuickCash(finalTotal.value); // Cập nhật lại số tiền gợi ý
}

const openPaymentModal = () => {
  if (cart.value.length === 0) { Swal.fire({ icon: 'warning', title: 'Giỏ hàng trống' }); return; }
  setQuickCash(finalTotal.value);
  paymentForm.value.method = 'CASH';
  showPaymentModal.value = true;
}

const simulateBankTransfer = () => {
  const amountToPay = paymentForm.value.method === 'SPLIT' ? customerTransferAmount.value : finalTotal.value;
  if (amountToPay <= 0) return;

  Swal.fire({
    title: 'Đang kiểm tra giao dịch...',
    html: 'Hệ thống đang kết nối với Napas để xác nhận luồng tiền.',
    timer: 1500, timerProgressBar: true, didOpen: () => { Swal.showLoading() }
  }).then(() => {
    Swal.fire({
      icon: 'success', title: 'Ting Ting! 💸',
      html: `Tài khoản <b>${selectedBank.value.name}</b> vừa nhận được <br><b class="text-success fs-4">${formatMoney(amountToPay)}</b>`,
      showConfirmButton: true, confirmButtonText: 'Tuyệt vời', confirmButtonColor: '#10b981'
    });
  });
}

const handleCheckout = async () => {
  if (!customer.value.phone || customer.value.phone.trim() === '') {
    Swal.fire({ icon: 'error', title: 'Thiếu thông tin', text: 'Vui lòng nhập SĐT khách hàng để lưu dữ liệu bảo hành!' }); return;
  }
  if (paymentForm.value.method === 'CASH' && changeAmount.value < 0) return; 

  try {
    const warrantyNotes = cart.value
      .filter(item => item.warrantyFee > 0)
      .map(item => `[${item.imeiCode}] Mua thêm: ${item.warrantyName}`)
      .join(' | ');

    const finalNote = paymentForm.value.note
      ? `${paymentForm.value.note} ${warrantyNotes ? ' | ' + warrantyNotes : ''}`
      : warrantyNotes;

    // Gắn thêm voucherCode vào Payload để gửi về Backend
    const payload = {
      userId: 1, 
      totalAmount: finalTotal.value, // Đã trừ voucher
      scannedImeis: scannedImeiList.value,
      customerPhone: customer.value.phone, 
      customerName: customer.value.name, 
      note: finalNote, 
      voucherCode: appliedDiscount.value > 0 ? voucherCode.value : null, // Gửi mã về DB lưu
      paymentMethod: paymentForm.value.method,
      cashAmount: paymentForm.value.method === 'CASH' ? finalTotal.value : (paymentForm.value.method === 'SPLIT' ? paymentForm.value.customerGiven : 0),
      transferAmount: paymentForm.value.method === 'TRANSFER' ? finalTotal.value : (paymentForm.value.method === 'SPLIT' ? customerTransferAmount.value : 0)
    }
    
    printData.value = {
      items: [...cart.value],
      subtotal: cartTotal.value, // Tiền gốc
      discount: appliedDiscount.value, // Tiền giảm
      total: finalTotal.value, // Tiền sau giảm
      customerName: customer.value.name,
      customerPhone: customer.value.phone,
      method: paymentForm.value.method,
      customerGiven: paymentForm.value.method === 'CASH' ? paymentForm.value.customerGiven : (paymentForm.value.method === 'SPLIT' ? paymentForm.value.customerGiven : 0),
      transferAmount: paymentForm.value.method === 'TRANSFER' ? finalTotal.value : (paymentForm.value.method === 'SPLIT' ? customerTransferAmount.value : 0)
    };

    const res = await axios.post("http://localhost:8080/api/v1/orders/checkout-pos", payload)
    
    showPaymentModal.value = false;
    
    Swal.fire({
      icon: 'success',
      title: 'Thanh toán thành công!',
      text: 'Đơn hàng đã được lưu vào hệ thống.',
      showCancelButton: true,
      confirmButtonColor: '#1e293b',
      cancelButtonColor: '#94a3b8',
      confirmButtonText: '<i class="bi bi-printer me-1"></i> In hóa đơn',
      cancelButtonText: 'Không in'
    }).then((result) => {
      if (result.isConfirmed) {
        setTimeout(() => { window.print(); }, 300);
      }
      cart.value = []; scannedImeiList.value = []; customer.value = { name: '', phone: '' }; removeVoucher();
      fetchWarehouseData();
    });

  } catch (error: any) {
    Swal.fire({ icon: 'error', title: 'Lỗi hệ thống', text: error.response?.data || error.message });
  }
}

const formatMoney = (val: any) => `${(Number(val) || 0).toLocaleString('vi-VN')} ₫`

onMounted(() => {
  fetchWarehouseData();
  window.addEventListener('keydown', (e) => { if (e.key === 'F9') { e.preventDefault(); openPaymentModal(); } });
})
</script>

<style scoped>
/* CSS GIỮ NGUYÊN HOÀN TOÀN TỪ BẢN TRƯỚC */
.bg-slate-50 { background-color: #f8fafc !important; }
.bg-slate-100 { background-color: #f1f5f9 !important; }
.bg-slate-800 { background-color: #1e293b !important; }
.bg-slate-900 { background-color: #0f172a !important; }
.text-slate-900 { color: #0f172a !important; }
.text-slate-800 { color: #1e293b !important; }
.text-slate-700 { color: #334155 !important; }
.text-slate-600 { color: #475569 !important; }
.text-slate-500 { color: #64748b !important; }
.text-slate-400 { color: #94a3b8 !important; }
.border-slate-50 { border-color: #f8fafc !important; }
.border-slate-100 { border-color: #f1f5f9 !important; }
.border-slate-200 { border-color: #e2e8f0 !important; }
.border-slate-300 { border-color: #cbd5e1 !important; }
.btn-dark-peel { background-color: #1e293b; color: white; border: none; transition: all 0.2s; }
.btn-dark-peel:hover:not(:disabled) { background-color: #0f172a; transform: translateY(-1px); }
.btn-outline-dark { color: #1e293b; border: 1px solid #cbd5e1; background: white; transition: 0.2s; }
.btn-outline-dark:hover { background: #f8fafc; border-color: #1e293b; }
.focus-ring-peel:focus, .focus-ring-peel:focus-within { border-color: #1e293b !important; box-shadow: 0 0 0 3px rgba(30, 41, 59, 0.1) !important; }
.product-card { border: 1px solid #f1f5f9; cursor: pointer; }
.hover-lift:hover { transform: translateY(-4px); border-color: #cbd5e1; box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1) !important; }
.product-img { transition: transform 0.3s ease; }
.hover-lift:hover .product-img { transform: scale(1.05); }
.grayscale { filter: grayscale(100%); }
.btn-light-peel { width: 32px; height: 32px; border-radius: 50%; background: #f1f5f9; color: #64748b; transition: all 0.2s; border: none; }
.product-card:hover .btn-light-peel { background: #1e293b; color: white; }
.btn-remove { position: absolute; right: 8px; top: 8px; border: none; background: none; color: #cbd5e1; font-size: 1.2rem; transition: 0.2s; padding: 0; }
.btn-remove:hover { color: #ef4444; transform: scale(1.1); }
.fw-black { font-weight: 900 !important; }
.tracking-tight { letter-spacing: -0.025em; }
.tracking-wider { letter-spacing: 0.05em; }
.section-label { font-size: 0.75rem; font-weight: 800; color: #64748b; text-transform: uppercase; letter-spacing: 0.05rem; }
.text-nowrap { white-space: nowrap !important; }
.border-dashed { border-style: dashed !important; }
.pay-method-card { flex: 1; border: 2px solid #e2e8f0; padding: 12px; border-radius: 12px; cursor: pointer; text-align: center; transition: 0.2s; background: white; color: #64748b; font-weight: 600; }
.pay-method-card i { font-size: 1.8rem; color: #94a3b8; transition: 0.2s; }
.pay-method-card.active { border-color: #1e293b; background: #f8fafc; color: #1e293b; box-shadow: 0 4px 6px -1px rgba(30, 41, 59, 0.1); }
.pay-method-card.active i { color: #1e293b; }
.qr-img { width: 200px; height: 200px; object-fit: contain; }
.custom-scrollbar::-webkit-scrollbar { width: 6px; }
.custom-scrollbar::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 10px; }
.modal-overlay { backdrop-filter: blur(8px); animation: fadeIn 0.25s ease-out; }
@keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }
.animate-pop-in { animation: popIn 0.3s cubic-bezier(0.34, 1.56, 0.64, 1); }
@keyframes popIn { from { transform: scale(0.95); opacity: 0; } to { transform: scale(1); opacity: 1; } }
</style>

<style>
@media print {
  body { background-color: white !important; margin: 0 !important; padding: 0 !important; }
  body > :not(#print-receipt) { display: none !important; }
  #print-receipt { display: block !important; width: 80mm !important; margin: 0 auto !important; padding: 5mm !important; position: relative; font-family: Arial, Helvetica, sans-serif; color: #000; }
  @page { margin: 0; size: 80mm auto; }
  #print-receipt .receipt-header { text-align: center; margin-bottom: 10px; }
  #print-receipt .store-name { font-size: 22px; font-weight: 900; margin: 0 0 5px 0; letter-spacing: 1px; }
  #print-receipt .receipt-title { font-size: 18px; font-weight: bold; margin: 10px 0; }
  #print-receipt p { margin: 3px 0; font-size: 12px; }
  #print-receipt .customer-info { text-align: left; margin: 10px 0; }
  #print-receipt .customer-info p { margin: 3px 0; font-size: 12px; font-weight: bold; }
  #print-receipt .receipt-table { width: 100%; border-collapse: collapse; margin-bottom: 10px; font-size: 12px; }
  #print-receipt .receipt-table th { border-bottom: 1px dashed #000; padding: 5px 0; }
  #print-receipt .receipt-table td { padding: 4px 0; }
  #print-receipt .item-name { font-weight: bold; padding-top: 8px !important; }
  #print-receipt .item-desc { font-size: 11px; color: #333; }
  #print-receipt .text-start { text-align: left; }
  #print-receipt .text-end { text-align: right; }
  #print-receipt .divider { border-bottom: 1px dashed #000; margin: 10px 0; }
  #print-receipt .receipt-summary { font-size: 12px; }
  #print-receipt .summary-line { display: flex; justify-content: space-between; margin-bottom: 4px; }
  #print-receipt .total-line { font-size: 16px; font-weight: 900; margin: 8px 0; border-top: 1px dashed #000; border-bottom: 1px dashed #000; padding: 8px 0; }
  #print-receipt .receipt-footer { margin-top: 20px; }
  #print-receipt .policy { font-size: 11px; font-style: italic; margin-top: 5px; }
  #print-receipt .barcode-fake { font-family: 'Courier New', Courier, monospace; font-size: 14px; margin-top: 15px; font-weight: bold; }
}
</style>