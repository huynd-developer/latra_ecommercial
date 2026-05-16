// backend/src/main/java/trungdevcode/latra/modules/promotion/service/impl/VoucherServiceImpl.java
package trungdevcode.latra.modules.promotion.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trungdevcode.latra.common.exception.BusinessException;
import trungdevcode.latra.common.exception.ErrorCode;
import trungdevcode.latra.entity.Voucher;
import trungdevcode.latra.enums.DiscountType;
import trungdevcode.latra.enums.VoucherCategory;
import trungdevcode.latra.modules.promotion.dto.request.VoucherRequestDTO;
import trungdevcode.latra.modules.promotion.dto.response.VoucherAnalyticsDTO;
import trungdevcode.latra.modules.promotion.dto.response.VoucherStackingResponseDTO;
import trungdevcode.latra.modules.promotion.service.VoucherService;
import trungdevcode.latra.repository.OrderRepository;
import trungdevcode.latra.repository.VoucherRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class VoucherServiceImpl implements VoucherService {

    private final VoucherRepository voucherRepository;
    private final OrderRepository orderRepository;

    @Override
    public List<Voucher> getAllVouchers() {
        return voucherRepository.findAll();
    }

    private void mapDtoToEntity(Voucher voucher, VoucherRequestDTO request, boolean isUpdate) {
        LocalDateTime now = LocalDateTime.now();
        if (!request.getStartDate().isBefore(request.getExpiredAt())) {
            throw new BusinessException(ErrorCode.INVALID_DATA, "Ngày bắt đầu phải trước ngày kết thúc");
        }
        if (!isUpdate && request.getExpiredAt().isBefore(now)) {
            throw new BusinessException(ErrorCode.INVALID_DATA, "Ngày kết thúc phải ở tương lai");
        }

        // LƯU Ý: Frontend truyền chuỗi "PERCENTAGE", backend map sang Enum
        DiscountType reqDiscountType;
        VoucherCategory reqCategory;
        try {
            reqDiscountType = DiscountType.valueOf(request.getDiscountType().toUpperCase());
            reqCategory = request.getCategory() != null ? VoucherCategory.valueOf(request.getCategory().toUpperCase()) : VoucherCategory.DISCOUNT;
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.INVALID_DATA, "Loại giảm giá hoặc danh mục không hợp lệ");
        }

        if (reqDiscountType == DiscountType.PERCENTAGE) {
            if (request.getValue().doubleValue() > 100) {
                throw new BusinessException(ErrorCode.INVALID_DATA, "Giảm theo % không vượt quá 100%");
            }
            if (request.getMaxDiscountAmount() == null || request.getMaxDiscountAmount().doubleValue() <= 0) {
                throw new BusinessException(ErrorCode.INVALID_DATA, "Phải quy định số tiền giảm tối đa");
            }
        }

        voucher.setCode(request.getCode().trim().toUpperCase()); // Chuẩn hóa mã
        voucher.setDiscountType(reqDiscountType);
        voucher.setCategory(reqCategory);
        voucher.setValue(request.getValue());
        voucher.setUsageLimit(request.getUsageLimit());
        voucher.setMinOrderValue(request.getMinOrderValue());

        if (reqDiscountType == DiscountType.FIXED_AMOUNT) {
            voucher.setMaxDiscountAmount(request.getValue());
        } else {
            voucher.setMaxDiscountAmount(request.getMaxDiscountAmount());
        }
        voucher.setStartDate(request.getStartDate());
        voucher.setExpiredAt(request.getExpiredAt());
        voucher.setStatus(1); // Mặc định Active
    }

    @Override
    @Transactional
    public Voucher createVoucher(VoucherRequestDTO request) {
        // 🔥 FIX LỖI "cannot find symbol": Thay isUpdate=false bằng gọi hàm logic
        if (voucherRepository.existsByCode(request.getCode().trim().toUpperCase())) {
            throw new BusinessException(ErrorCode.INVALID_DATA, "Mã Voucher đã tồn tại");
        }
        Voucher voucher = new Voucher();
        mapDtoToEntity(voucher, request, false);
        return voucherRepository.save(voucher);
    }

    @Override
    @Transactional
    public Voucher updateVoucher(Long id, VoucherRequestDTO request) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.VOUCHER_NOT_FOUND));

        // Cần đảm bảo có hàm existsByCodeAndIdNot trong repo
        if (voucherRepository.existsByCodeAndIdNot(request.getCode().trim().toUpperCase(), id)) {
            throw new BusinessException(ErrorCode.INVALID_DATA, "Mã bị trùng với voucher khác");
        }
        mapDtoToEntity(voucher, request, true);
        return voucherRepository.save(voucher);
    }

    @Override
    @Transactional
    public void deleteVoucher(Long id) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.VOUCHER_NOT_FOUND));
        // Soft delete bằng cách set status = 0 thay vì lùi expired_at
        voucher.setStatus(0);
        voucherRepository.save(voucher);
    }

    @Override
    public Map<String, Object> checkVoucher(String code, BigDecimal orderTotal, Long userId) {
        Voucher voucher = voucherRepository.findByCodeAndStatus((code.trim().toUpperCase()), 1)
                .orElseThrow(() -> new BusinessException(ErrorCode.VOUCHER_NOT_FOUND));

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(voucher.getStartDate())) throw new BusinessException(ErrorCode.VOUCHER_EXPIRED, "Chưa đến thời gian");
        if (now.isAfter(voucher.getExpiredAt())) throw new BusinessException(ErrorCode.VOUCHER_EXPIRED);
        if (voucher.getUsageLimit() <= 0) throw new BusinessException(ErrorCode.VOUCHER_OUT_OF_USAGE);
        if (orderTotal.compareTo(voucher.getMinOrderValue()) < 0) {
            throw new BusinessException(ErrorCode.VOUCHER_MIN_ORDER_NOT_MET);
        }

        if (userId != null) {
            long usageCount = orderRepository.countVoucherUsageByUser(userId, voucher.getId());
            if (usageCount >= 1) {
                throw new BusinessException(ErrorCode.INVALID_DATA, "Bạn đã dùng mã này rồi");
            }
        }

        BigDecimal discountAmount = BigDecimal.ZERO;
        if (voucher.getDiscountType() == DiscountType.FIXED_AMOUNT) {
            discountAmount = voucher.getValue();
        } else if (voucher.getDiscountType() == DiscountType.PERCENTAGE) {
            discountAmount = orderTotal.multiply(voucher.getValue()).divide(new BigDecimal("100"));
            if (voucher.getMaxDiscountAmount() != null && discountAmount.compareTo(voucher.getMaxDiscountAmount()) > 0) {
                discountAmount = voucher.getMaxDiscountAmount();
            }
        }

        // Không giảm quá tổng tiền
        if (discountAmount.compareTo(orderTotal) > 0) discountAmount = orderTotal;

        Map<String, Object> response = new HashMap<>();
        response.put("discountAmount", discountAmount);
        response.put("voucherCode", voucher.getCode());
        return response;
    }

    @Override
    public VoucherStackingResponseDTO calculateMultipleVouchers(List<String> codes, BigDecimal orderTotal, Long userId) {
        if (codes == null || codes.isEmpty()) {
            return VoucherStackingResponseDTO.builder()
                    .originalTotal(orderTotal).finalTotal(orderTotal).totalDiscount(BigDecimal.ZERO).build();
        }

        BigDecimal remainingTotal = orderTotal;
        BigDecimal totalDiscount = BigDecimal.ZERO;
        Map<String, BigDecimal> discountDetails = new HashMap<>();
        Set<VoucherCategory> appliedCategories = new HashSet<>();

        for (String code : codes) {
            this.checkVoucher(code, orderTotal, userId);

            Voucher voucher = voucherRepository.findByCodeAndStatus(code.trim().toUpperCase(), 1).get();

            if (!appliedCategories.add(voucher.getCategory())) {
                throw new BusinessException(ErrorCode.INVALID_DATA, "Chỉ dùng 1 mã loại " + voucher.getCategory().name());
            }

            BigDecimal discountAmount = BigDecimal.ZERO;
            if (voucher.getDiscountType() == DiscountType.FIXED_AMOUNT) {
                discountAmount = voucher.getValue();
            } else if (voucher.getDiscountType() == DiscountType.PERCENTAGE) {
                discountAmount = remainingTotal.multiply(voucher.getValue()).divide(new BigDecimal("100"));
                if (voucher.getMaxDiscountAmount() != null && discountAmount.compareTo(voucher.getMaxDiscountAmount()) > 0) {
                    discountAmount = voucher.getMaxDiscountAmount();
                }
            }

            if (discountAmount.compareTo(remainingTotal) > 0) discountAmount = remainingTotal;

            remainingTotal = remainingTotal.subtract(discountAmount);
            totalDiscount = totalDiscount.add(discountAmount);
            discountDetails.put(code, discountAmount);
        }

        return VoucherStackingResponseDTO.builder()
                .originalTotal(orderTotal).finalTotal(remainingTotal)
                .totalDiscount(totalDiscount).discountDetails(discountDetails)
                .build();
    }

    @Override
    public List<VoucherAnalyticsDTO> getVoucherAnalytics() {
        return voucherRepository.getVoucherAnalytics();
    }
    @Override
    public List<Voucher> getActiveVouchers() {
        LocalDateTime now = LocalDateTime.now();
        // Trả về các mã đang Active (status = 1) và còn hạn sử dụng
        return voucherRepository.findByStatus(1).stream()
                .filter(v -> v.getUsageLimit() > 0
                        && now.isAfter(v.getStartDate())
                        && now.isBefore(v.getExpiredAt()))
                .toList();
    }
}