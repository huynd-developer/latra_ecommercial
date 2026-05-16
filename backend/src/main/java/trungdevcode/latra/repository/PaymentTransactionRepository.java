// backend/src/main/java/trungdevcode/latra/repository/PaymentTransactionRepository.java
package trungdevcode.latra.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import trungdevcode.latra.entity.PaymentTransaction;
import java.util.Optional;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {
    // Find transaction by the unique ID sent to/from VNPAY (vnp_TxnRef)
    Optional<PaymentTransaction> findByGatewayTransactionNo(String gatewayTransactionNo);

    // Find successful transaction of an order to process Refund
    Optional<PaymentTransaction> findByOrderIdAndStatus(Long orderId, trungdevcode.latra.enums.PaymentStatus status);
}