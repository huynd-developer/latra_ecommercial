// backend/src/main/java/trungdevcode/latra/modules/order/dto/request/WebCheckoutRequestDTO.java
package trungdevcode.latra.modules.order.dto.request;

import lombok.Data;

@Data
public class WebCheckoutRequestDTO {
    private Long userId;
    private String paymentMethod;
    private String shippingAddress;
    private String note;

    // 👉 ĐÃ THÊM: Cho phép khách áp mã giảm giá
    private String voucherCode;
}