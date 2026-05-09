<template>
  <div
    class="bg-white h-100 d-flex flex-column overflow-hidden rounded-4 shadow-sm border border-slate-100"
  >
    <div
      class="p-4 border-bottom border-slate-100 d-flex flex-column gap-3 z-2"
    >
      <div class="d-flex justify-content-between align-items-center">
        <h5
          class="fw-bolder text-slate-800 mb-0 tracking-tight text-capitalize"
        >
          Quản lý Kho IMEI (WMS)
        </h5>

        <div class="position-relative" style="width: 350px">
          <i
            class="bi bi-search position-absolute top-50 translate-middle-y text-slate-400 ms-3"
          ></i>
          <input
            v-if="activeTab === 'import'"
            v-model="searchQuery"
            type="text"
            class="form-control bg-slate-50 border border-slate-200 text-slate-700 ps-5 py-2 shadow-none rounded-pill fw-medium focus-ring-peel"
            placeholder="Tìm Sản phẩm cần nhập kho..."
          />
          <input
            v-else
            v-model="imeiSearchQuery"
            type="text"
            class="form-control bg-slate-50 border border-slate-200 text-slate-700 ps-5 py-2 shadow-none rounded-pill fw-medium focus-ring-peel"
            placeholder="Tra cứu nhanh mã IMEI..."
          />
        </div>
      </div>

      <div
        class="d-flex gap-2 p-1 bg-slate-50 rounded-pill border border-slate-200"
        style="width: fit-content"
      >
        <button
          @click="activeTab = 'import'"
          class="btn rounded-pill px-4 py-2 fw-semibold text-decoration-none transition-all"
          :class="
            activeTab === 'import'
              ? 'bg-white text-dark-peel shadow-sm border border-slate-200'
              : 'btn-link text-slate-500 hover-text-dark'
          "
        >
          <i class="bi bi-box-arrow-in-down me-2"></i>Nhập Hàng
        </button>
        <button
          @click="activeTab = 'list'"
          class="btn rounded-pill px-4 py-2 fw-semibold text-decoration-none transition-all"
          :class="
            activeTab === 'list'
              ? 'bg-white text-dark-peel shadow-sm border border-slate-200'
              : 'btn-link text-slate-500 hover-text-dark'
          "
        >
          <i class="bi bi-upc-scan me-2"></i>Tồn Kho IMEI
        </button>
      </div>
    </div>

    <div
      class="p-0 flex-grow-1 overflow-auto custom-scrollbar position-relative"
    >
      <transition name="fade-slide" mode="out-in">
        <!-- ============================================== -->
        <!-- TAB 1: NHẬP HÀNG -->
        <!-- ============================================== -->
        <div v-if="activeTab === 'import'" class="h-100 d-flex flex-column">
          <table class="table align-middle mb-0 modern-table flex-grow-1">
            <thead
              class="bg-white text-slate-400 small fw-bold tracking-wider text-uppercase sticky-top"
            >
              <tr>
                <th class="ps-4 py-3 border-bottom border-slate-200">
                  Sản Phẩm
                </th>
                <th class="py-3 border-bottom border-slate-200">
                  Phiên Bản (Màu - DL)
                </th>
                <!-- THÊM CỘT TÌNH TRẠNG -->
                <th class="py-3 border-bottom border-slate-200 text-center">
                  Tình Trạng
                </th>
                <th class="py-3 border-bottom border-slate-200">Mã SKU</th>
                <th class="py-3 border-bottom border-slate-200 text-center">
                  Tồn Kho
                </th>
                <th class="text-end pe-4 py-3 border-bottom border-slate-200">
                  Thao tác
                </th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="paginatedVariants.length === 0">
                <td
                  colspan="6"
                  class="text-center py-5 text-slate-400 fw-medium"
                >
                  Không tìm thấy phiên bản nào.
                </td>
              </tr>
              <tr
                v-for="item in paginatedVariants"
                :key="item.variantId"
                class="transition-all hover-bg-slate"
              >
                <td
                  class="ps-4 py-3 border-bottom border-slate-50 text-slate-800 fw-bold"
                >
                  {{ item.productName }}
                </td>
                <td
                  class="py-3 border-bottom border-slate-50 text-slate-700 fw-medium"
                >
                  <span class="badge bg-light text-dark border px-2 py-1"
                    >{{ item.color }} - {{ item.storage }}</span
                  >
                </td>
                
                <!-- DỮ LIỆU TÌNH TRẠNG -->
                <td class="py-3 border-bottom border-slate-50 text-center">
                  <span :class="getConditionClass(item.condition)" class="badge-condition">
                    {{ item.condition }}
                  </span>
                </td>

                <td
                  class="py-3 border-bottom border-slate-50 text-slate-500 fw-bold font-monospace"
                >
                  {{ item.sku }}
                </td>
                <td class="py-3 border-bottom border-slate-50 text-center">
                  <span
                    class="badge px-3 py-2 fs-6"
                    :class="
                      item.stock > 0
                        ? 'bg-success bg-opacity-10 text-success'
                        : 'bg-danger bg-opacity-10 text-danger'
                    "
                  >
                    {{ item.stock }} máy
                  </span>
                </td>
                <td class="text-end pe-4 py-3 border-bottom border-slate-50">
                  <button
                    @click="openImportModal(item)"
                    class="btn btn-sm btn-dark-peel fw-semibold px-3 py-1 rounded-pill shadow-peel hover-lift"
                  >
                    <i class="bi bi-box-arrow-in-down me-1"></i> Nhập Kho
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
          <div
            class="p-3 border-top border-slate-100 bg-white d-flex justify-content-between align-items-center mt-auto"
          >
            <span class="text-slate-500 small fw-medium"
              >Hiển thị {{ paginatedVariants.length }} /
              {{ filteredVariants.length }}</span
            >
            <div class="d-flex gap-2 align-items-center">
              <button
                class="btn btn-sm btn-white border-slate-200 px-3 rounded-pill hover-bg-slate"
                :disabled="currentPage === 1"
                @click="currentPage--"
              >
                Trước
              </button>
              <span class="fw-bold text-slate-700 mx-2 small"
                >{{ currentPage }} / {{ totalPages }}</span
              >
              <button
                class="btn btn-sm btn-white border-slate-200 px-3 rounded-pill hover-bg-slate"
                :disabled="currentPage === totalPages || totalPages === 0"
                @click="currentPage++"
              >
                Sau
              </button>
            </div>
          </div>
        </div>

        <!-- ============================================== -->
        <!-- TAB 2: DANH SÁCH IMEI TỒN KHO -->
        <!-- ============================================== -->
        <div v-else-if="activeTab === 'list'" class="h-100 d-flex flex-column">
          <table class="table align-middle mb-0 modern-table flex-grow-1">
            <thead
              class="bg-white text-slate-400 small fw-bold tracking-wider text-uppercase sticky-top"
            >
              <tr>
                <th class="ps-4 py-3 border-bottom border-slate-200">
                  Mã IMEI / Serial
                </th>
                <th class="py-3 border-bottom border-slate-200">Sản Phẩm</th>
                <th class="py-3 border-bottom border-slate-200">Phiên Bản</th>
                <!-- THÊM CỘT TÌNH TRẠNG -->
                <th class="py-3 border-bottom border-slate-200 text-center">Tình Trạng</th>
                <th class="py-3 border-bottom border-slate-200 text-center">Ngày Nhập</th>
                <th class="py-3 border-bottom border-slate-200 text-center">
                  Trạng Thái
                </th>
                <th class="text-end pe-4 py-3 border-bottom border-slate-200">
                  Thao tác
                </th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="paginatedImeis.length === 0">
                <td
                  colspan="7"
                  class="text-center py-5 text-slate-400 fw-medium"
                >
                  Kho chưa có mã IMEI nào.
                </td>
              </tr>
              <tr
                v-for="imei in paginatedImeis"
                :key="imei.id"
                class="transition-all hover-bg-slate"
              >
                <td
                  class="ps-4 py-3 border-bottom border-slate-50 text-dark-peel fw-bolder font-monospace fs-6"
                >
                  <i class="bi bi-upc-scan text-slate-400 me-2"></i
                  >{{ imei.imeiCode }}
                </td>
                <td
                  class="py-3 border-bottom border-slate-50 text-slate-800 fw-bold"
                >
                  {{ imei.productName }}
                </td>
                <td
                  class="py-3 border-bottom border-slate-50 text-slate-700 fw-medium"
                >
                  {{ imei.color }} - {{ imei.storage }}
                </td>

                <!-- DỮ LIỆU TÌNH TRẠNG -->
                <td class="py-3 border-bottom border-slate-50 text-center">
                  <span :class="getConditionClass(imei.condition)" class="badge-condition">
                    {{ imei.condition }}
                  </span>
                </td>

                <td
                  class="py-3 border-bottom border-slate-50 text-slate-500 small text-center"
                >
                  {{ formatDate(imei.createdAt) }}
                </td>
                <td class="py-3 border-bottom border-slate-50 text-center">
                  <span
                    v-if="imei.status === 'AVAILABLE'"
                    class="badge bg-success bg-opacity-10 text-success px-2 py-1"
                    ><i class="bi bi-box-seam me-1"></i>Trong kho</span
                  >
                  <span
                    v-else-if="imei.status === 'SOLD'"
                    class="badge bg-danger bg-opacity-10 text-danger px-2 py-1"
                    ><i class="bi bi-cart-check me-1"></i>Đã bán</span
                  >
                  <span
                    v-else
                    class="badge bg-warning bg-opacity-10 text-warning px-2 py-1"
                    >Lỗi/BH</span
                  >
                </td>
                <td class="text-end pe-4 py-3 border-bottom border-slate-50">
                  <button
                    v-if="imei.status === 'AVAILABLE'"
                    @click="deleteImei(imei)"
                    class="btn btn-sm btn-light text-danger rounded-circle p-1 btn-action"
                    title="Xóa mã"
                  >
                    <i class="bi bi-trash3-fill"></i>
                  </button>
                  <button
                    v-else
                    disabled
                    class="btn btn-sm btn-light text-slate-300 rounded-circle p-1 btn-action"
                  >
                    <i class="bi bi-trash3-fill"></i>
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
          <div
            class="p-3 border-top border-slate-100 bg-white d-flex justify-content-between align-items-center mt-auto"
          >
            <span class="text-slate-500 small fw-medium"
              >Hiển thị {{ paginatedImeis.length }} /
              {{ filteredImeis.length }} mã</span
            >
            <div class="d-flex gap-2 align-items-center">
              <button
                class="btn btn-sm btn-white border-slate-200 px-3 rounded-pill hover-bg-slate"
                :disabled="imeiPage === 1"
                @click="imeiPage--"
              >
                Trước
              </button>
              <span class="fw-bold text-slate-700 mx-2 small"
                >{{ imeiPage }} / {{ imeiTotalPages }}</span
              >
              <button
                class="btn btn-sm btn-white border-slate-200 px-3 rounded-pill hover-bg-slate"
                :disabled="imeiPage === imeiTotalPages || imeiTotalPages === 0"
                @click="imeiPage++"
              >
                Sau
              </button>
            </div>
          </div>
        </div>
      </transition>
    </div>

    <!-- MODAL NHẬP KHO -->
    <Teleport to="body">
      <transition name="fade-scale">
        <div
          v-if="showModal"
          class="modal-overlay d-flex align-items-center justify-content-center"
          @mousedown.self="closeModal"
        >
          <div
            class="bg-white rounded-4 shadow-lg modal-content-custom d-flex flex-column overflow-hidden"
            style="width: 600px; max-width: 95vw"
          >
            <div
              class="p-4 border-bottom border-slate-100 d-flex justify-content-between align-items-center bg-slate-50"
            >
              <h5 class="mb-0 fw-bolder text-slate-800">
                <i class="bi bi-upc-scan me-2 text-primary"></i>Quét Mã Vạch
                Nhập Kho
              </h5>
              <button
                @click="closeModal"
                class="btn-close shadow-none"
              ></button>
            </div>

            <div class="p-4">
              <div class="mb-3 p-3 bg-light rounded-3 border border-slate-200">
                <div class="small text-slate-500 mb-1">Đang nhập hàng cho:</div>
                <div class="fw-bold text-slate-800 fs-5">
                  {{ selectedItem?.productName }}
                </div>
                <div class="fw-medium text-primary">
                  {{ selectedItem?.color }} - {{ selectedItem?.storage }}
                  <span class="mx-1">•</span> 
                  <span class="badge bg-primary rounded-pill">{{ selectedItem?.condition }}</span> 
                  (SKU: {{ selectedItem?.sku }})
                </div>
              </div>

              <div class="mb-2 d-flex justify-content-between align-items-end">
                <label class="form-label fw-bold text-slate-700 mb-0"
                  >Mã IMEI / Serial Number
                  <span class="text-danger">*</span></label
                >
                <span class="badge bg-primary rounded-pill px-3"
                  >{{ parsedImeis.length }} mã hợp lệ</span
                >
              </div>

              <div
                class="alert alert-info py-2 small mb-3 border-0 bg-info bg-opacity-10"
              >
                <i class="bi bi-info-circle-fill me-1"></i> Sử dụng súng quét mã
                vạch để quét liên tục. Mỗi mã sẽ tự động xuống dòng.
              </div>

              <textarea
                v-model="rawImeiInput"
                class="form-control shadow-none border-slate-300 font-monospace"
                rows="8"
                placeholder="Ví dụ:&#10;358500000000001&#10;358500000000002"
                style="resize: none"
              ></textarea>

              <div
                v-if="duplicateLines.length > 0"
                class="text-danger small mt-2 fw-medium"
              >
                <i class="bi bi-shield-x"></i> Phát hiện
                {{ duplicateLines.length }} mã bị trùng lặp. Hệ thống tự động
                lọc bỏ.
              </div>

              <div
                class="d-flex justify-content-end gap-2 mt-4 pt-3 border-top border-slate-100"
              >
                <button
                  type="button"
                  @click="closeModal"
                  class="btn btn-white border border-slate-200 px-4 py-2 rounded-pill fw-semibold text-slate-600 hover-bg-slate"
                >
                  Hủy
                </button>
                <button
                  type="button"
                  @click="submitImport"
                  class="btn btn-primary px-4 py-2 rounded-pill fw-bold shadow-sm hover-lift"
                  :disabled="parsedImeis.length === 0 || isLoading"
                >
                  <span
                    v-if="isLoading"
                    class="spinner-border spinner-border-sm me-2"
                  ></span>
                  Xác Nhận Nhập {{ parsedImeis.length }} Máy
                </button>
              </div>
            </div>
          </div>
        </div>
      </transition>
    </Teleport>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from "vue";
import axios from "axios";
import Swal from "sweetalert2";

const activeTab = ref<"import" | "list">("import");

// --- TAB 1 STATE ---
const allVariants = ref<any[]>([]);
const searchQuery = ref("");
const currentPage = ref(1);
const pageSize = ref(10);
const showModal = ref(false);
const selectedItem = ref<any>(null);
const rawImeiInput = ref("");
const isLoading = ref(false);

// --- TAB 2 STATE ---
const allImeis = ref<any[]>([]);
const imeiSearchQuery = ref("");
const imeiPage = ref(1);
const imeiPageSize = ref(15);

// HÀM FORMAT ĐỔI MÀU CHO TÌNH TRẠNG MÁY
const getConditionClass = (cond: string) => {
  if (!cond) return 'cond-default';
  const c = cond.toLowerCase();
  if (c.includes('new') || c.includes('100')) return 'cond-new';
  if (c.includes('99')) return 'cond-99';
  if (c.includes('98')) return 'cond-98';
  return 'cond-old';
};

const formatDate = (dateStr: string) => {
  if (!dateStr) return "";
  const d = new Date(dateStr);
  return `${d.getHours().toString().padStart(2, "0")}:${d
    .getMinutes()
    .toString()
    .padStart(2, "0")} - ${d.getDate().toString().padStart(2, "0")}/${(
    d.getMonth() + 1
  )
    .toString()
    .padStart(2, "0")}/${d.getFullYear()}`;
};

const fetchVariants = async () => {
  try {
    const res = await axios.get("http://localhost:8080/api/v1/products");
    const products = res.data.products || [];
    let variantsFlattened: any[] = [];
    products.forEach((p: any) => {
      if (p.variants) {
        p.variants.forEach((v: any) => {
          variantsFlattened.push({
            productId: p.id,
            productName: p.name,
            variantId: v.variantId,
            color: v.color,
            storage: v.storage,
            sku: v.sku,
            stock: v.stock,
            // Hứng data tình trạng máy từ Backend
            condition: v.condition || "New Seal",
          });
        });
      }
    });
    allVariants.value = variantsFlattened;
  } catch (error) {
    console.error("Lỗi Variant:", error);
  }
};

const fetchImeis = async () => {
  try {
    const res = await axios.get("http://localhost:8080/api/v1/imeis");
    allImeis.value = res.data
      .map((i: any) => ({
        id: i.id,
        imeiCode: i.imeiCode,
        status: i.status,
        createdAt: i.createdAt,
        productName: i.variant?.product?.name || "---",
        color: i.variant?.color || "",
        storage: i.variant?.storage || "",
        // Hứng data tình trạng máy từ Backend (Thông qua Variant)
        condition: i.variant?.condition || "New Seal",
      }))
      .reverse();
  } catch (error: any) {
    console.error("Lỗi IMEI:", error);
    alert("KẾT QUẢ GIẢI MÃ:\n" + JSON.stringify(error.response?.data, null, 2));
  }
};

onMounted(() => {
  fetchVariants();
  fetchImeis();
});

const filteredVariants = computed(() => {
  if (!searchQuery.value) return allVariants.value;
  const q = searchQuery.value.toLowerCase();
  return allVariants.value.filter(
    (v) =>
      v.productName.toLowerCase().includes(q) || 
      v.sku.toLowerCase().includes(q) ||
      v.condition.toLowerCase().includes(q)
  );
});
const totalPages = computed(
  () => Math.ceil(filteredVariants.value.length / pageSize.value) || 1
);
const paginatedVariants = computed(() =>
  filteredVariants.value.slice(
    (currentPage.value - 1) * pageSize.value,
    currentPage.value * pageSize.value
  )
);
watch(searchQuery, () => {
  currentPage.value = 1;
});

const filteredImeis = computed(() => {
  if (!imeiSearchQuery.value) return allImeis.value;
  const q = imeiSearchQuery.value.toLowerCase();
  return allImeis.value.filter(
    (i) =>
      i.imeiCode.toLowerCase().includes(q) ||
      i.productName.toLowerCase().includes(q) ||
      i.condition.toLowerCase().includes(q)
  );
});
const imeiTotalPages = computed(
  () => Math.ceil(filteredImeis.value.length / imeiPageSize.value) || 1
);
const paginatedImeis = computed(() =>
  filteredImeis.value.slice(
    (imeiPage.value - 1) * imeiPageSize.value,
    imeiPage.value * imeiPageSize.value
  )
);
watch(imeiSearchQuery, () => {
  imeiPage.value = 1;
});

const openImportModal = (item: any) => {
  selectedItem.value = item;
  rawImeiInput.value = "";
  showModal.value = true;
};
const closeModal = () => {
  showModal.value = false;
  selectedItem.value = null;
};

const parsedImeis = computed(() => {
  if (!rawImeiInput.value) return [];
  const lines = rawImeiInput.value
    .split("\n")
    .map((line) => line.trim())
    .filter((line) => line !== "");
  return [...new Set(lines)];
});

const duplicateLines = computed(() => {
  if (!rawImeiInput.value) return [];
  const lines = rawImeiInput.value
    .split("\n")
    .map((line) => line.trim())
    .filter((line) => line !== "");
  return lines.length > new Set(lines).size ? lines : [];
});

const submitImport = async () => {
  if (parsedImeis.value.length === 0) return;
  const payload = {
    variantId: selectedItem.value.variantId,
    imeis: parsedImeis.value,
  };
  isLoading.value = true;
  try {
    const res = await axios.post(
      "http://localhost:8080/api/v1/imeis/import",
      payload
    );
    Swal.fire({
      icon: "success",
      title: "Nhập kho thành công!",
      text: res.data,
      timer: 2000,
      showConfirmButton: false,
    });
    closeModal();
    await fetchVariants();
    await fetchImeis();
  } catch (error: any) {
    let errorMsg = "Lỗi nhập kho.";
    if (error.response?.data?.message) errorMsg = error.response.data.message;
    else if (typeof error.response?.data === "string")
      errorMsg = error.response.data;
    Swal.fire({ icon: "error", title: "Lỗi!", text: errorMsg });
  } finally {
    isLoading.value = false;
  }
};

const deleteImei = async (imei: any) => {
  const result = await Swal.fire({
    title: "Xóa mã IMEI này?",
    text: `Tồn kho sẽ bị trừ đi 1.`,
    icon: "warning",
    showCancelButton: true,
    confirmButtonColor: "#ef4444",
    confirmButtonText: "Có, Xóa",
    cancelButtonText: "Hủy",
  });
  if (result.isConfirmed) {
    try {
      await axios.delete(`http://localhost:8080/api/v1/imeis/${imei.id}`);
      Swal.fire({
        icon: "success",
        title: "Đã xóa!",
        timer: 1500,
        showConfirmButton: false,
      });
      await fetchVariants();
      await fetchImeis();
    } catch (error) {
      Swal.fire({
        icon: "error",
        title: "Lỗi!",
        text: "Không thể xóa mã IMEI này.",
      });
    }
  }
};
</script>

<style scoped>
.text-slate-300 { color: #cbd5e1 !important; }
.text-slate-400 { color: #94a3b8 !important; }
.text-slate-500 { color: #64748b !important; }
.text-slate-700 { color: #334155 !important; }
.text-slate-800 { color: #1e293b !important; }
.bg-slate-50 { background-color: #f8fafc !important; }
.bg-slate-100 { background-color: #f1f5f9 !important; }
.border-slate-50 { border-color: #f8fafc !important; }
.border-slate-100 { border-color: #f1f5f9 !important; }
.border-slate-200 { border-color: #e2e8f0 !important; }
.border-slate-300 { border-color: #cbd5e1 !important; }
.text-dark-peel { color: #111111 !important; }
.btn-dark-peel { background-color: #111111; color: white; border: 1px solid #111111; }
.btn-dark-peel:hover { background-color: #000000; color: white; }
.btn-white { background-color: #ffffff; border-color: #e2e8f0; color: #475569; }
.btn-white:hover { background-color: #f8fafc; }
.hover-text-dark:hover { color: #1e293b !important; }
.fw-bolder { font-weight: 800 !important; }
.tracking-tight { letter-spacing: -0.025em; }
.tracking-wider { letter-spacing: 0.05em; }
.transition-all { transition: all 0.2s ease-in-out; }
.hover-lift { transition: transform 0.2s; }
.hover-lift:hover { transform: translateY(-2px); }
.hover-bg-slate:hover { background-color: #f8fafc !important; }
.focus-ring-peel:focus { border-color: #111111 !important; box-shadow: 0 0 0 1px #111111 !important; }
.font-monospace { font-family: "Courier New", Courier, monospace; letter-spacing: 1px; }

/* 👉 Thêm style cực đẹp cho Tình trạng máy */
.badge-condition { padding: 4px 10px; border-radius: 6px; font-size: 0.75rem; font-weight: 700; border: 1px solid transparent; display: inline-block; white-space: nowrap; }
.cond-new { background: #eff6ff; color: #2563eb; border-color: #dbeafe; } /* Xanh dương: New 100% */
.cond-99 { background: #fdf4ff; color: #a21caf; border-color: #fae8ff; } /* Tím: Like New 99% */
.cond-98 { background: #fff7ed; color: #c2410c; border-color: #ffedd5; } /* Cam: Cũ 98% */
.cond-old { background: #fef2f2; color: #dc2626; border-color: #fee2e2; } /* Đỏ: Dưới 95% */
.cond-default { background: #f1f5f9; color: #475569; }

.custom-scrollbar::-webkit-scrollbar { width: 5px; height: 5px; }
.custom-scrollbar::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 10px; }
.modal-overlay { position: fixed; top: 0; left: 0; width: 100vw; height: 100vh; background-color: rgba(15, 23, 42, 0.4); z-index: 1050; backdrop-filter: blur(2px); }
.modal-content-custom { margin: 0 auto; }
.fade-slide-enter-active, .fade-slide-leave-active { transition: opacity 0.2s, transform 0.2s; }
.fade-slide-enter-from { opacity: 0; transform: translateX(-10px); }
.fade-slide-leave-to { opacity: 0; transform: translateX(10px); }
.fade-scale-enter-active, .fade-scale-leave-active { transition: opacity 0.2s, transform 0.2s; }
.fade-scale-enter-from, .fade-scale-leave-to { opacity: 0; transform: scale(0.95); }
.btn-action { width: 32px; height: 32px; display: inline-flex; justify-content: center; align-items: center; }
</style>