import { ref, computed, watch, nextTick } from "vue";
// ⚠️ CHÚ Ý: SỬA LẠI ĐƯỜNG DẪN NÀY ĐẾN FILE request.ts CỦA MÀY
import request from "@/common/services/request";
import Swal from "sweetalert2";

// --- INTERFACES ---
export interface DisplayProduct {
  variantId: string;
  name: string;
  color: string;
  storage: string;
  price: number;
  stock: number;
  image: string;
}
export interface CartItem {
  imeiCode: string;
  name: string;
  color: string;
  storage: string;
  price: number;
  image: string;
  warrantyFee: number;
  warrantyName: string;
}

const defaultImage = "https://placehold.co/400x400?text=No+Image";

// --- SHARED STATE ---
const scannedCode = ref<string>("");
const dbImeis = ref<any[]>([]);
const displayProducts = ref<DisplayProduct[]>([]);
const cart = ref<CartItem[]>([]);
const scannedImeiList = ref<string[]>([]);

const searchQuery = ref<string>("");
const filterStock = ref<string>("ALL");

const customer = ref({ name: "", phone: "" });
const showPaymentModal = ref<boolean>(false);
const displayCustomerGiven = ref<string>("0");

// 🌟 TRẠNG THÁI QR VNPAY
const showVnPayQrModal = ref<boolean>(false);
const vnPayQrUrl = ref<string>("");

const voucherCode = ref<string>("");
const appliedDiscount = ref<number>(0);

const paymentForm = ref({
  method: "CASH" as "CASH" | "VNPAY" | "SPLIT", // Chỉ giữ lại 3 chuẩn này
  customerGiven: 0,
  note: "",
});

const printData = ref({
  items: [] as CartItem[],
  subtotal: 0,
  discount: 0,
  total: 0,
  customerName: "",
  customerPhone: "",
  method: "",
  customerGiven: 0,
  transferAmount: 0,
});

// --- SHARED COMPUTED ---
const cartTotal = computed<number>(() =>
  cart.value.reduce((sum, item) => sum + item.price + Number(item.warrantyFee), 0)
);

const finalTotal = computed<number>(() => {
  const total = cartTotal.value - appliedDiscount.value;
  return total > 0 ? total : 0;
});

const changeAmount = computed<number>(
  () => paymentForm.value.customerGiven - finalTotal.value
);

// Số tiền còn thiếu cần thanh toán qua VNPay (Dành cho phương thức SPLIT)
const customerTransferAmount = computed<number>(() => {
  if (paymentForm.value.method !== "SPLIT") return 0;
  const remain = finalTotal.value - paymentForm.value.customerGiven;
  return remain > 0 ? remain : 0;
});

const cashSuggestions = computed(() => {
  const total = finalTotal.value;
  if (total === 0) return [500000, 1000000, 2000000];
  const roundedBase = Math.ceil(total / 1000000) * 1000000;
  return [roundedBase, roundedBase + 500000, roundedBase + 1000000].filter(
    (v) => v > total
  );
});

const filteredProducts = computed(() => {
  return displayProducts.value.filter((item) => {
    const q = searchQuery.value.toLowerCase();
    const matchSearch =
      item.name.toLowerCase().includes(q) ||
      item.color.toLowerCase().includes(q) ||
      item.storage.toLowerCase().includes(q);
    const matchStock =
      filterStock.value === "ALL" ||
      (filterStock.value === "IN_STOCK" && item.stock > 0);
    return matchSearch && matchStock;
  });
});

// --- WATCHERS ---
const removeVoucher = () => {
  voucherCode.value = "";
  appliedDiscount.value = 0;
  setQuickCash(finalTotal.value);
};

watch(cartTotal, (newVal) => {
  if (newVal === 0) removeVoucher();
});

// --- HELPER FUNCTIONS ---
const setQuickCash = (amount: number) => {
  paymentForm.value.customerGiven = amount;
  displayCustomerGiven.value = amount.toLocaleString("en-US");
};

const formatMoney = (val: any) =>
  `${(Number(val) || 0).toLocaleString("vi-VN")} ₫`;

export function usePos() {
  const fetchWarehouseData = async () => {
    try {
      const res: any = await request.get("/imeis");
      const imeiList = Array.isArray(res) ? res : res?.data || res?.content || [];
      if (!Array.isArray(imeiList) || imeiList.length === 0) return;

      dbImeis.value = imeiList;
      const grouped = new Map<string, DisplayProduct>();

      imeiList.forEach((imei: any) => {
        const productName = imei.productName || "Sản phẩm không tên";
        const variantName = imei.variantName || "";
        let color = "", storage = "";
        
        if (variantName.includes(" - ")) {
          const parts = variantName.split(" - ");
          color = parts[0]?.trim() || "";
          storage = parts[1]?.trim() || "";
        } else {
          color = variantName || "Tiêu chuẩn";
        }

        const key = productName + " @ " + variantName;
        const imgUrl = imei.imageUrl || defaultImage;

        if (!grouped.has(key)) {
          grouped.set(key, {
            variantId: key, name: productName, color: color, storage: storage,
            price: Number(imei.price) || 0, stock: imei.status === "AVAILABLE" ? 1 : 0, image: imgUrl,
          });
        } else {
          const existing = grouped.get(key);
          if (existing && imei.status === "AVAILABLE") existing.stock++;
        }
      });
      displayProducts.value = Array.from(grouped.values());
    } catch (error) {
      console.error("Lỗi kết nối Backend lấy dữ liệu kho:", error);
    }
  };

  const handleImageError = (e: any) => {
    if (!e.target.src.includes("placehold.co")) e.target.src = defaultImage;
  };

  const updateWarrantyName = (item: CartItem) => {
    if (item.warrantyFee === 0) item.warrantyName = "Bảo hành tiêu chuẩn 6 tháng";
    else if (item.warrantyFee === 500000) item.warrantyName = "Gói Bảo hành VIP 12 tháng";
    else if (item.warrantyFee === 800000) item.warrantyName = "Gói Rơi vỡ, Vào nước";
  };

  const onInputCash = (e: Event) => {
    const target = e.target as HTMLInputElement;
    const numValue = parseInt(target.value.replace(/\D/g, ""), 10) || 0;
    paymentForm.value.customerGiven = numValue;
    displayCustomerGiven.value = numValue.toLocaleString("en-US");
  };

  const handleFocusInput = (event: Event) => {
    const target = event.target as HTMLInputElement;
    if (target) target.select();
  };

  const autoPickImei = (variantIdKey: string) => {
    const availableImei = dbImeis.value.find((i: any) => {
      const key = i.productName + " @ " + i.variantName;
      return i.status === "AVAILABLE" && key === variantIdKey && !scannedImeiList.value.includes(i.imeiCode);
    });
    if (availableImei) {
      scannedCode.value = availableImei.imeiCode;
      handleScanImei();
    } else {
      Swal.fire({ icon: "error", title: "Hết hàng!", text: "Phiên bản này đã hết máy!", timer: 2000, showConfirmButton: false });
    }
  };

  const handleScanImei = () => {
    const code = scannedCode.value.trim();
    if (!code) return;
    if (scannedImeiList.value.includes(code)) {
      Swal.fire({ icon: "error", title: "Lỗi", text: "Máy này đã quét rồi!", timer: 1500, showConfirmButton: false });
      scannedCode.value = ""; return;
    }
    const foundImei = dbImeis.value.find((i: any) => i.imeiCode === code && i.status === "AVAILABLE");
    if (!foundImei) {
      Swal.fire({ icon: "warning", title: "Lỗi", text: "IMEI không hợp lệ hoặc đã bán!", timer: 1500, showConfirmButton: false });
      scannedCode.value = ""; return;
    }
    scannedImeiList.value.push(code);

    let color = "", storage = "";
    if (foundImei.variantName && foundImei.variantName.includes(" - ")) {
      const parts = foundImei.variantName.split(" - ");
      color = parts[0]?.trim() || "";
      storage = parts[1]?.trim() || "";
    } else {
      color = foundImei.variantName || "";
    }

    cart.value.unshift({
      imeiCode: foundImei.imeiCode, name: foundImei.productName || "Điện thoại", color: color, storage: storage,
      price: Number(foundImei.price) || 0, image: foundImei.imageUrl || defaultImage,
      warrantyFee: 0, warrantyName: "Bảo hành tiêu chuẩn 6 tháng",
    });
    scannedCode.value = "";
  };

  const removeScannedImei = (index: number) => {
    scannedImeiList.value.splice(index, 1);
    cart.value.splice(index, 1);
  };

  const clearCart = () => {
    Swal.fire({ title: "Làm mới giỏ hàng?", text: "Tất cả máy đã quét sẽ bị xóa!", icon: "warning", showCancelButton: true, confirmButtonText: "Đồng ý xóa", confirmButtonColor: "#1e293b"
    }).then((res) => {
      if (res.isConfirmed) { cart.value = []; scannedImeiList.value = []; removeVoucher(); }
    });
  };

  const handleApplyVoucher = async () => {
    const code = voucherCode.value.trim().toUpperCase();
    if (!code) return;
    try {
      const res: any = await request.get(`/vouchers/admin/check?code=${code}&total=${cartTotal.value}`);
      const voucherData = res?.data || res;
      appliedDiscount.value = voucherData?.discountAmount || 0;
      setQuickCash(finalTotal.value);
      Swal.fire({ icon: "success", title: "Áp dụng mã thành công!", toast: true, position: "top-end", showConfirmButton: false, timer: 2000 });
    } catch (error: any) {
      Swal.fire({ icon: "error", title: "Lỗi Voucher", text: error.response?.data?.message || "Mã không hợp lệ!", toast: true, position: "top-end", showConfirmButton: false, timer: 3000 });
      voucherCode.value = ""; appliedDiscount.value = 0;
    }
  };

  const openPaymentModal = () => {
    if (cart.value.length === 0) {
      Swal.fire({ icon: "warning", title: "Giỏ hàng trống" }); return;
    }
    setQuickCash(finalTotal.value);
    paymentForm.value.method = "CASH";
    showPaymentModal.value = true;
  };

  // 🌟 TÁCH HÀM IN HOÁ ĐƠN: Dùng chung cho Tiền mặt và sau khi xác nhận VNPay
  const executePrintAndReset = async () => {
    await nextTick(); 
    Swal.fire({
      icon: "success", title: "Giao dịch hoàn tất!", text: "Đơn hàng hệ thống POS đã xử lý thành công.",
      showCancelButton: true, confirmButtonColor: "#1e293b", cancelButtonColor: "#94a3b8",
      confirmButtonText: '<i class="bi bi-printer me-1"></i> In hóa đơn', cancelButtonText: "Không in",
    }).then((result) => {
      const resetDataAndRefresh = () => {
        cart.value = []; scannedImeiList.value = []; customer.value = { name: "", phone: "" };
        removeVoucher(); fetchWarehouseData();
      };
      if (result.isConfirmed) {
        setTimeout(() => { window.print(); resetDataAndRefresh(); }, 300);
      } else {
        resetDataAndRefresh();
      }
    });
  };

  // 🌟 HÀM KHI THU NGÂN XÁC NHẬN KHÁCH ĐÃ QUÉT VNPAY THÀNH CÔNG
  const confirmVnPayPaymentDone = async () => {
    showVnPayQrModal.value = false;
    await executePrintAndReset();
  };

  const handleCheckout = async () => {
    if (!customer.value.phone || customer.value.phone.trim() === "") {
      Swal.fire({ icon: "error", title: "Thiếu thông tin", text: "Vui lòng nhập SĐT khách hàng!" }); return;
    }
    if (paymentForm.value.method === "CASH" && changeAmount.value < 0) return;

    try {
      const warrantyNotes = cart.value
        .filter((item) => item.warrantyFee > 0)
        .map((item) => `[${item.imeiCode}] Mua thêm: ${item.warrantyName}`).join(" | ");

      const finalNote = paymentForm.value.note ? `${paymentForm.value.note} ${warrantyNotes ? " | " + warrantyNotes : ""}` : warrantyNotes;

      // 🌟 LOGIC TÍNH TIỀN GỬI LÊN BACKEND CHUẨN XÁC
      const cashAmt = paymentForm.value.method === "CASH" ? finalTotal.value : (paymentForm.value.method === "SPLIT" ? paymentForm.value.customerGiven : 0);
      const transferAmt = paymentForm.value.method === "VNPAY" ? finalTotal.value : (paymentForm.value.method === "SPLIT" ? customerTransferAmount.value : 0);

      const payload = {
        userId: 1, // ID Thu ngân (Sau này lấy từ Token)
        totalAmount: finalTotal.value,
        scannedImeis: scannedImeiList.value,
        customerPhone: customer.value.phone,
        customerName: customer.value.name,
        note: finalNote,
        voucherCode: appliedDiscount.value > 0 ? voucherCode.value : null,
        paymentMethod: paymentForm.value.method,
        cashAmount: cashAmt,
        transferAmount: transferAmt,
      };

      printData.value = {
        items: [...cart.value], subtotal: cartTotal.value, discount: appliedDiscount.value, total: finalTotal.value,
        customerName: customer.value.name, customerPhone: customer.value.phone, method: paymentForm.value.method,
        customerGiven: paymentForm.value.customerGiven, transferAmount: transferAmt,
      };

      // ⚠️ KIỂM TRA LẠI ENDPOINT BACKEND NẾU GẶP LỖI 404
      const res: any = await request.post("/orders/checkout-pos", payload);
      const responseData = res?.data || res;

      showPaymentModal.value = false;

      // 🌟 XỬ LÝ KẾT QUẢ: BẬT VNPAY QR HOẶC IN HOÁ ĐƠN
      if (responseData?.isVnPay && responseData?.paymentUrl) {
        vnPayQrUrl.value = responseData.paymentUrl;
        showVnPayQrModal.value = true; 
      } else {
        await executePrintAndReset();
      }
    } catch (error: any) {
      Swal.fire({ icon: "error", title: "Lỗi hệ thống", text: error.response?.data?.message || error.message });
    }
  };

  return {
    scannedCode, dbImeis, displayProducts, cart, scannedImeiList, searchQuery, filterStock, customer, showPaymentModal, displayCustomerGiven,
    showVnPayQrModal, vnPayQrUrl, voucherCode, appliedDiscount, paymentForm, printData, defaultImage,
    cartTotal, finalTotal, changeAmount, customerTransferAmount, cashSuggestions, filteredProducts,
    fetchWarehouseData, handleImageError, updateWarrantyName, onInputCash, setQuickCash, handleFocusInput, autoPickImei, handleScanImei, removeScannedImei, clearCart, handleApplyVoucher, removeVoucher, openPaymentModal, handleCheckout, confirmVnPayPaymentDone, formatMoney,
  };
}