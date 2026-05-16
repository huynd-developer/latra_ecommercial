// backend/src/main/java/trungdevcode/latra/modules/promotion/service/FlashSaleService.java
package trungdevcode.latra.modules.promotion.service;

import trungdevcode.latra.entity.FlashSale;
import trungdevcode.latra.modules.promotion.dto.request.FlashSaleRequestDTO;

import java.util.List;

public interface FlashSaleService {
    FlashSale createFlashSale(FlashSaleRequestDTO request);
    List<FlashSale> getAllFlashSales();
    FlashSale updateFlashSale(Long id, FlashSaleRequestDTO request);
    void deleteFlashSale(Long id);
}