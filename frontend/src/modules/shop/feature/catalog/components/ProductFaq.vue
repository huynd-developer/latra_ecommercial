<template>
  <section class="container faq-section py-5 my-5">
    <h2 class="text-center fw-bold text-dark mb-4 text-title-section">Câu Hỏi Thường Gặp</h2>
    <div class="row">
      <div class="col-12 col-md-8 mx-auto">
        <div class="faq-accordion-group d-flex flex-column gap-3">
          
          <div 
            v-for="(faq, index) in faqList" 
            :key="index" 
            class="faq-item border rounded-4 bg-white overflow-hidden"
            :class="{ 'faq-item-active': faq.isOpen }"
          >
            <div 
              class="faq-header d-flex align-items-center justify-content-between p-4 cursor-pointer user-select-none" 
              @click="toggleFaq(index)"
            >
              <span class="faq-question fw-semibold text-dark">{{ faq.question }}</span>
              <span class="faq-icon-status ms-3" :class="{ 'rotated': faq.isOpen }">
                <i class="bi bi-plus-lg"></i>
              </span>
            </div>
            
            <div class="faq-body-collapse" :class="{ 'show': faq.isOpen }">
              <div class="faq-content-inner">
                <div class="faq-content p-4 pt-0 text-muted lh-lg">
                  {{ faq.answer }}
                </div>
              </div>
            </div>

          </div>

        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { ref } from 'vue';

interface FAQItem {
  question: string;
  answer: string;
  isOpen: boolean;
}

const faqList = ref<FAQItem[]>([
  { 
    question: 'Sản phẩm tại cửa hàng có phải là hàng chính hãng không?', 
    answer: 'Hoàn toàn chính xác. Tất cả điện thoại đang bán đều là hàng mới nguyên seal, chính hãng 100% và được bảo hành trực tiếp tại các trung tâm ủy quyền của hãng trên toàn quốc.',
    isOpen: false 
  },
  { 
    question: 'Chính sách bảo hành và đổi trả của shop như thế nào?', 
    answer: 'Chúng tôi hỗ trợ chính sách "Lỗi là đổi mới" trong vòng 30 ngày đầu tiên nếu máy có lỗi phần cứng từ nhà sản xuất. Sau thời gian này, sản phẩm được bảo hành tiêu chuẩn 12 tháng.',
    isOpen: false 
  },
  { 
    question: 'Cửa hàng có hỗ trợ mua điện thoại trả góp không?', 
    answer: 'Có! Cửa hàng hỗ trợ trả góp 0% qua thẻ tín dụng của hơn 20 ngân hàng khác nhau. Ngoài ra, bạn cũng có thể làm hồ sơ trả góp qua các công ty tài chính với thủ tục nhanh gọn chỉ cần CCCD.',
    isOpen: false 
  },
  { 
    question: 'Thời gian giao hàng là bao lâu kể từ lúc đặt?', 
    answer: 'Đối với các đơn hàng nội thành, chúng tôi có dịch vụ giao hàng siêu tốc trong vòng 2 giờ. Đối với khách hàng ở tỉnh khác, thời gian giao hàng tiêu chuẩn sẽ từ 2 đến 4 ngày làm việc.',
    isOpen: false 
  }
]);

const toggleFaq = (index: number) => {
  faqList.value.forEach((item, i) => {
    if (i !== index) item.isOpen = false;
  });
  
  const targetFaq = faqList.value[index];
  if (targetFaq) {
    targetFaq.isOpen = !targetFaq.isOpen;
  }
};
</script>

<style scoped>
.text-title-section { font-size: 28px; letter-spacing: -0.5px; }
.cursor-pointer { cursor: pointer; }

/* Item Box */
.faq-item { 
  border: 1px solid #eaeaea !important; 
  border-radius: 12px !important; 
  transition: border-color 0.3s ease, background-color 0.3s ease; 
}
.faq-item:hover, .faq-item-active { 
  border-color: #1a1a1a !important; 
}

/* Kỹ thuật CSS Grid nâng cấp chuyển động mượt từ 0độ cao lên tự động */
.faq-body-collapse { 
  display: grid;
  grid-template-rows: 0fr;
  transition: grid-template-rows 0.35s cubic-bezier(0.25, 1, 0.5, 1); 
}
.faq-body-collapse.show { 
  grid-template-rows: 1fr;
}

/* Bắt buộc phải có overflow: hidden ở lớp thắt nút con này */
.faq-content-inner {
  overflow: hidden;
}

.faq-content { 
  font-size: 14px; 
}

/* Hiệu ứng xoay Icon chuyển từ dấu cộng sang dấu trừ cực chất */
.faq-icon-status {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.3s cubic-bezier(0.25, 1, 0.5, 1);
}
.faq-icon-status.rotated {
  transform: rotate(135deg); /* Xoay dấu + thành dấu x/ - để báo hiệu trạng thái đóng */
}
</style>