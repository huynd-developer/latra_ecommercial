package trungdevcode.latra.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import trungdevcode.latra.entity.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}