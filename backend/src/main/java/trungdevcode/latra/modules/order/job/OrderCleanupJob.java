// backend/src/main/java/trungdevcode/latra/modules/order/job/OrderCleanupJob.java
package trungdevcode.latra.modules.order.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import trungdevcode.latra.entity.Order;
import trungdevcode.latra.enums.OrderStatus;
import trungdevcode.latra.modules.order.dto.response.OrderResponse;
import trungdevcode.latra.modules.order.service.OrderService;
import trungdevcode.latra.repository.OrderRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCleanupJob {

    private final OrderRepository orderRepository;
    private final OrderService orderService;

    // Chạy tự động mỗi 60 giây (60000 ms)
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void cleanupAbandonedOrders() {
        // Tìm các đơn đã tạo quá 15 phút mà vẫn PENDING_PAYMENT
        LocalDateTime expireTime = LocalDateTime.now().minusMinutes(15);
        List<Order> abandonedOrders = orderRepository.findByStatusAndCreatedAtBefore((OrderStatus.PENDING_PAYMENT), expireTime);

        if (!abandonedOrders.isEmpty()) {
            log.info("Phát hiện {} đơn hàng quá hạn thanh toán. Đang tiến hành hủy...", abandonedOrders.size());

            for (Order order : abandonedOrders) {
                OrderResponse.UpdateStatus cancelRequest = new OrderResponse.UpdateStatus();
                cancelRequest.setStatus(OrderStatus.CANCELLED.name());

                orderService.updateOrderStatus(order.getId(), cancelRequest);
                log.info("Đã tự động hủy đơn hàng ID: {} và nhả tồn kho thành công.", order.getId());
            }
        }
    }
}