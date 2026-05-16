// backend/src/main/java/trungdevcode/latra/cronjob/CartCleanupJob.java
package trungdevcode.latra.cronjob;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import trungdevcode.latra.repository.CartItemRepository;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class CartCleanupJob {

    private final CartItemRepository cartItemRepository;

    // Chạy vào 02:00 sáng mỗi ngày
    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    public void cleanupAbandonedCartItems() {
        log.info("Bắt đầu dọn dẹp Giỏ hàng rác...");

        // Mốc thời gian: Trước đây 30 ngày
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30);

        int deletedCount = cartItemRepository.deleteOldCartItems(cutoffDate);

        log.info("Đã xóa thành công {} CartItem rác bị bỏ quên quá 30 ngày.", deletedCount);
    }
}