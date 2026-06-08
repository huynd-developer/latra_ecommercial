package trungdevcode.latra.modules.order.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import trungdevcode.latra.entity.Order;
import trungdevcode.latra.modules.order.dto.request.CheckoutRequestDTO;
import trungdevcode.latra.modules.order.dto.request.WebCheckoutRequestDTO;
import trungdevcode.latra.modules.order.dto.response.OrderResponse;

public interface OrderService {
    Page<OrderResponse.DanhSach> getOrders(String status, Pageable pageable);
    OrderResponse.OrderDetail getOrderDetails(Long orderId);
    void updateOrderStatus(Long orderId, OrderResponse.UpdateStatus request);

    // 🔥 LUỒNG WEB: Khách tự đặt hàng trên Web
    Order createWebOrder(WebCheckoutRequestDTO request);

    // 🔥 LUỒNG POS: Nhân viên dùng súng tít mã vạch tại quầy
    // 🛠️ ĐÃ SỬA FULL: Xóa hàm String cũ, chốt kiểu trả về là Order phục vụ cho VNPay QR
    Order checkoutPOS(CheckoutRequestDTO request);
}