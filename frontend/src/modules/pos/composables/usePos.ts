// src/modules/pos/composables/usePos.ts
import { ref, computed, watch } from "vue";
// CHÚ Ý SỬA LẠI ĐƯỜNG DẪN NÀY ĐẾN FILE request.ts CỦA MÀY
import request from "@/common/services/request";
import Swal from "sweetalert2";

// --- INTERFACES ---
export interface Bank {
  id: string;
  name: string;
  accNo: string;
  accName: string;
}
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

const defaultBank: Bank = {
  id: "TCB",
  name: "Techcombank",
  accNo: "3714082007",
  accName: "NGUYEN DINH HUY",
};
const supportedBanks: Bank[] = [
  defaultBank,
  {
    id: "MB",
    name: "MB Bank",
    accNo: "0901000001",
    accName: "NGUYEN DINH HUY",
  },
  {
    id: "VCB",
    name: "Vietcombank",
    accNo: "1012345678",
    accName: "NGUYEN DINH HUY",
  },
];

const defaultImage = "https://placehold.co/400x400?text=No+Image";

// --- SHARED STATE (Khai báo ngoài để dùng chung vùng nhớ) ---
const scannedCode = ref<string>("");
const dbImeis = ref<any[]>([]);
const displayProducts = ref<DisplayProduct[]>([]);
const cart = ref<CartItem[]>([]);
const scannedImeiList = ref<string[]>([]);

const searchQuery = ref<string>("");
const filterStock = ref<string>("ALL");

const customer = ref({ name: "", phone: "" });
const selectedBankId = ref<string>(defaultBank.id);
const showPaymentModal = ref<boolean>(false);
const displayCustomerGiven = ref<string>("0");

const voucherCode = ref<string>("");
const appliedDiscount = ref<number>(0);

const paymentForm = ref({
  method: "CASH" as "CASH" | "TRANSFER" | "SPLIT",
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
  cart.value.reduce(
    (sum, item) => sum + item.price + Number(item.warrantyFee),
    0
  )
);

const finalTotal = computed<number>(() => {
  const total = cartTotal.value - appliedDiscount.value;
  return total > 0 ? total : 0;
});

const changeAmount = computed<number>(
  () => paymentForm.value.customerGiven - finalTotal.value
);
const selectedBank = computed<Bank>(
  () => supportedBanks.find((b) => b.id === selectedBankId.value) || defaultBank
);

const customerTransferAmount = computed<number>(() => {
  if (paymentForm.value.method !== "SPLIT") return 0;
  const remain = finalTotal.value - paymentForm.value.customerGiven;
  return remain > 0 ? remain : 0;
});

const qrCodeUrl = computed<string>(() => {
  const bank = selectedBank.value;
  const accName = bank.accName.replace(/\s+/g, "%20");
  const amount =
    paymentForm.value.method === "SPLIT"
      ? customerTransferAmount.value
      : finalTotal.value;
  return `https://img.vietqr.io/image/${bank.id}-${bank.accNo}-compact2.png?amount=${amount}&addInfo=LATRA%20Thanh%20Toan&accountName=${accName}`;
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

// --- WATCHERS (Chạy tự động khi import) ---
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
  // --- CORE FUNCTIONS ---
  const fetchWarehouseData = async () => {
    try {
      const res: any = await request.get("/imeis");

      // BẮT QUẢ TANG 1: Xem Backend trả về cái cục gì
      console.log("👉 [BƯỚC 1] Dữ liệu gốc Backend trả về:", res);

      const imeiList = Array.isArray(res)
        ? res
        : res?.data || res?.content || [];

      // BẮT QUẢ TANG 2: Xem danh sách sau khi bóc vỏ
      console.log("👉 [BƯỚC 2] Danh sách IMEI rút trích được:", imeiList);

      if (!Array.isArray(imeiList) || imeiList.length === 0) {
        console.warn(
          "⚠️ API không có dữ liệu hoặc bóc sai lớp data rồi ông giáo ạ!"
        );
        return;
      }

      dbImeis.value = imeiList;
      const grouped = new Map<string, DisplayProduct>();

      imeiList.forEach((imei: any, index: number) => {
        // BẮT QUẢ TANG 3: In thử 1 thằng đầu tiên ra xem nó có cái chữ "variant" như mày viết không
        if (index === 0) {
          console.log("👉 [BƯỚC 3] Cấu trúc của 1 phần tử nè:", imei);
        }

        // Tạm thời TẮT cái if (imei.variant) đi để xem có lên hình không nhé.
        // Tao giả sử BE của mày trả thẳng dữ liệu không lồng qua variant
        const variantObj = imei.variant || imei; // <--- Sửa linh hoạt chỗ này

        const productName =
          variantObj.product?.name ||
          variantObj.productName ||
          variantObj.name ||
          "Tên SP";
        const color = variantObj.color || variantObj.mauSac || "";
        const key = productName + color;
        const imgUrl =
          variantObj.image || variantObj.product?.image || defaultImage;

        if (!grouped.has(key)) {
          grouped.set(key, {
            variantId: key,
            name: productName,
            color: color,
            storage: variantObj.storage || variantObj.dungLuong || "",
            price: Number(variantObj.price || variantObj.giaBan) || 0,
            stock: imei.status === "AVAILABLE" ? 1 : imei.soLuong || 0,
            image: imgUrl,
          });
        } else {
          const existing = grouped.get(key);
          if (existing && imei.status === "AVAILABLE") existing.stock++;
        }
      });

      displayProducts.value = Array.from(grouped.values());
      console.log(
        "👉 [BƯỚC 4] Danh sách ép thành công để hiển thị:",
        displayProducts.value
      );
    } catch (error) {
      console.error("Lỗi kết nối Backend lấy dữ liệu kho:", error);
    }
  };

  const handleImageError = (e: any) => {
    if (!e.target.src.includes("placehold.co")) {
      e.target.src = defaultImage;
    }
  };

  const updateWarrantyName = (item: CartItem) => {
    if (item.warrantyFee === 0)
      item.warrantyName = "Bảo hành tiêu chuẩn 6 tháng";
    else if (item.warrantyFee === 500000)
      item.warrantyName = "Gói Bảo hành VIP 12 tháng";
    else if (item.warrantyFee === 800000)
      item.warrantyName = "Gói Rơi vỡ, Vào nước";
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
      const key = i.variant?.product?.name + i.variant?.color;
      return (
        i.status === "AVAILABLE" &&
        key === variantIdKey &&
        !scannedImeiList.value.includes(i.imeiCode)
      );
    });
    if (availableImei) {
      scannedCode.value = availableImei.imeiCode;
      handleScanImei();
    } else {
      Swal.fire({
        icon: "error",
        title: "Hết hàng!",
        text: "Phiên bản này đã hết máy để bán!",
        timer: 2000,
        showConfirmButton: false,
      });
    }
  };

  const handleScanImei = () => {
    const code = scannedCode.value.trim();
    if (!code) return;
    if (scannedImeiList.value.includes(code)) {
      Swal.fire({
        icon: "error",
        title: "Lỗi",
        text: "Máy này đã quét rồi!",
        timer: 1500,
        showConfirmButton: false,
      });
      scannedCode.value = "";
      return;
    }
    const foundImei = dbImeis.value.find(
      (i: any) => i.imeiCode === code && i.status === "AVAILABLE"
    );
    if (!foundImei) {
      Swal.fire({
        icon: "warning",
        title: "Lỗi",
        text: "IMEI không hợp lệ hoặc đã bán!",
        timer: 1500,
        showConfirmButton: false,
      });
      scannedCode.value = "";
      return;
    }
    scannedImeiList.value.push(code);

    cart.value.unshift({
      imeiCode: foundImei.imeiCode,
      name: foundImei.variant?.product?.name || "Điện thoại",
      color: foundImei.variant?.color || "",
      storage: foundImei.variant?.storage || "",
      price: Number(foundImei.variant?.price) || 0,
      image:
        foundImei.variant?.image ||
        foundImei.variant?.product?.image ||
        defaultImage,
      warrantyFee: 0,
      warrantyName: "Bảo hành tiêu chuẩn 6 tháng",
    });
    scannedCode.value = "";
  };

  const removeScannedImei = (index: number) => {
    scannedImeiList.value.splice(index, 1);
    cart.value.splice(index, 1);
  };

  const clearCart = () => {
    Swal.fire({
      title: "Làm mới giỏ hàng?",
      text: "Tất cả máy đã quét sẽ bị xóa!",
      icon: "warning",
      showCancelButton: true,
      confirmButtonText: "Đồng ý xóa",
      confirmButtonColor: "#1e293b",
    }).then((res) => {
      if (res.isConfirmed) {
        cart.value = [];
        scannedImeiList.value = [];
        removeVoucher();
      }
    });
  };

  const handleApplyVoucher = async () => {
    const code = voucherCode.value.trim().toUpperCase();
    if (!code) return;

    try {
      // ĐÃ SỬA: Gọi request tự chèn Token
      const res: any = await request.get(
        `/vouchers/admin/check?code=${code}&total=${cartTotal.value}`
      );
      const voucherData = res?.data || res;

      appliedDiscount.value = voucherData?.discountAmount || 0;
      setQuickCash(finalTotal.value);

      Swal.fire({
        icon: "success",
        title: "Áp dụng mã thành công!",
        toast: true,
        position: "top-end",
        showConfirmButton: false,
        timer: 2000,
      });
    } catch (error: any) {
      let errorMsg = "Mã không tồn tại hoặc lỗi kết nối!";
      if (error.response?.data?.message) {
        errorMsg = error.response.data.message;
      }
      Swal.fire({
        icon: "error",
        title: "Lỗi Voucher",
        text: errorMsg,
        toast: true,
        position: "top-end",
        showConfirmButton: false,
        timer: 3000,
      });
      voucherCode.value = "";
      appliedDiscount.value = 0;
    }
  };

  const openPaymentModal = () => {
    if (cart.value.length === 0) {
      Swal.fire({ icon: "warning", title: "Giỏ hàng trống" });
      return;
    }
    setQuickCash(finalTotal.value);
    paymentForm.value.method = "CASH";
    showPaymentModal.value = true;
  };

  const simulateBankTransfer = () => {
    const amountToPay =
      paymentForm.value.method === "SPLIT"
        ? customerTransferAmount.value
        : finalTotal.value;
    if (amountToPay <= 0) return;

    Swal.fire({
      title: "Đang kiểm tra giao dịch...",
      html: "Hệ thống đang kết nối với Napas để xác nhận luồng tiền.",
      timer: 1500,
      timerProgressBar: true,
      didOpen: () => {
        Swal.showLoading();
      },
    }).then(() => {
      Swal.fire({
        icon: "success",
        title: "Ting Ting! 💸",
        html: `Tài khoản <b>${
          selectedBank.value.name
        }</b> vừa nhận được <br><b class="text-success fs-4">${formatMoney(
          amountToPay
        )}</b>`,
        showConfirmButton: true,
        confirmButtonText: "Tuyệt vời",
        confirmButtonColor: "#10b981",
      });
    });
  };

  const handleCheckout = async () => {
    if (!customer.value.phone || customer.value.phone.trim() === "") {
      Swal.fire({
        icon: "error",
        title: "Thiếu thông tin",
        text: "Vui lòng nhập SĐT khách hàng để lưu dữ liệu bảo hành!",
      });
      return;
    }
    if (paymentForm.value.method === "CASH" && changeAmount.value < 0) return;

    try {
      const warrantyNotes = cart.value
        .filter((item) => item.warrantyFee > 0)
        .map((item) => `[${item.imeiCode}] Mua thêm: ${item.warrantyName}`)
        .join(" | ");

      const finalNote = paymentForm.value.note
        ? `${paymentForm.value.note} ${
            warrantyNotes ? " | " + warrantyNotes : ""
          }`
        : warrantyNotes;

      const payload = {
        userId: 1,
        totalAmount: finalTotal.value,
        scannedImeis: scannedImeiList.value,
        customerPhone: customer.value.phone,
        customerName: customer.value.name,
        note: finalNote,
        voucherCode: appliedDiscount.value > 0 ? voucherCode.value : null,
        paymentMethod: paymentForm.value.method,
        cashAmount:
          paymentForm.value.method === "CASH"
            ? finalTotal.value
            : paymentForm.value.method === "SPLIT"
            ? paymentForm.value.customerGiven
            : 0,
        transferAmount:
          paymentForm.value.method === "TRANSFER"
            ? finalTotal.value
            : paymentForm.value.method === "SPLIT"
            ? customerTransferAmount.value
            : 0,
      };

      printData.value = {
        items: [...cart.value],
        subtotal: cartTotal.value,
        discount: appliedDiscount.value,
        total: finalTotal.value,
        customerName: customer.value.name,
        customerPhone: customer.value.phone,
        method: paymentForm.value.method,
        customerGiven:
          paymentForm.value.method === "CASH"
            ? paymentForm.value.customerGiven
            : paymentForm.value.method === "SPLIT"
            ? paymentForm.value.customerGiven
            : 0,
        transferAmount:
          paymentForm.value.method === "TRANSFER"
            ? finalTotal.value
            : paymentForm.value.method === "SPLIT"
            ? customerTransferAmount.value
            : 0,
      };

      // ĐÃ SỬA: Gọi request tự chèn Token
      await request.post("/orders/checkout-pos", payload);

      showPaymentModal.value = false;

      Swal.fire({
        icon: "success",
        title: "Thanh toán thành công!",
        text: "Đơn hàng đã được lưu vào hệ thống.",
        showCancelButton: true,
        confirmButtonColor: "#1e293b",
        cancelButtonColor: "#94a3b8",
        confirmButtonText: '<i class="bi bi-printer me-1"></i> In hóa đơn',
        cancelButtonText: "Không in",
      }).then((result) => {
        if (result.isConfirmed) {
          setTimeout(() => {
            window.print();
          }, 300);
        }
        cart.value = [];
        scannedImeiList.value = [];
        customer.value = { name: "", phone: "" };
        removeVoucher();
        fetchWarehouseData();
      });
    } catch (error: any) {
      Swal.fire({
        icon: "error",
        title: "Lỗi hệ thống",
        text: error.response?.data?.message || error.message,
      });
    }
  };

  // --- TRẢ VỀ TOÀN BỘ BIẾN VÀ HÀM ---
  return {
    scannedCode,
    dbImeis,
    displayProducts,
    cart,
    scannedImeiList,
    searchQuery,
    filterStock,
    customer,
    selectedBankId,
    showPaymentModal,
    displayCustomerGiven,
    voucherCode,
    appliedDiscount,
    paymentForm,
    printData,
    supportedBanks,
    defaultBank,
    defaultImage,

    cartTotal,
    finalTotal,
    changeAmount,
    selectedBank,
    customerTransferAmount,
    qrCodeUrl,
    cashSuggestions,
    filteredProducts,

    fetchWarehouseData,
    handleImageError,
    updateWarrantyName,
    onInputCash,
    setQuickCash,
    handleFocusInput,
    autoPickImei,
    handleScanImei,
    removeScannedImei,
    clearCart,
    handleApplyVoucher,
    removeVoucher,
    openPaymentModal,
    simulateBankTransfer,
    handleCheckout,
    formatMoney,
  };
}
