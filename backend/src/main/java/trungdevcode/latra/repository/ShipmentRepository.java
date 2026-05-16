package trungdevcode.latra.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import trungdevcode.latra.entity.Shipment;

import java.util.Optional;

public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
    Optional<Shipment> findByOrderId(Long orderId);

    // Quét xem mã vận đơn này đã được gán cho đơn nào chưa (Chống trùng lặp từ Carrier)
    boolean existsByTrackingCode(String trackingCode);
}
