package trungdevcode.latra.common.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    NOT_FOUND(404, "Không tìm thấy dữ liệu yêu cầu"),
    UNCATEGORIZED_EXCEPTION(500, "Lỗi hệ thống không xác định, vui lòng liên hệ Admin!"),
    INVALID_DATA(400, "Dữ liệu đầu vào không hợp lệ!"),

    // 2. Nhóm lỗi Brand (Thương hiệu)
    BRAND_NOT_FOUND(404, "Không tìm thấy thương hiệu này!"),
    BRAND_ALREADY_EXISTS(400, "Tên thương hiệu đã tồn tại trong hệ thống!"),
    BRAND_IN_USE(400, "Không thể xóa! Thương hiệu này đang có sản phẩm mở bán."),
    // Lỗi Inventory (Kho)
    CART_EMPTY(400, "Giỏ hàng đang trống"),
    OUT_OF_STOCK(400, "Sản phẩm đã hết hàng hoặc không đủ số lượng"),
    IMEI_NOT_AVAILABLE(400, "Mã IMEI không khả dụng hoặc đã bị bán"),

    // Lỗi Voucher
    VOUCHER_NOT_FOUND(404, "Mã giảm giá không tồn tại hoặc đã bị tắt"),
    VOUCHER_EXPIRED(400, "Mã giảm giá đã hết hạn hoặc chưa đến lúc dùng"),
    VOUCHER_OUT_OF_USAGE(400, "Mã giảm giá đã hết lượt sử dụng"),
    VOUCHER_MIN_ORDER_NOT_MET(400, "Đơn hàng chưa đạt giá trị tối thiểu để áp dụng mã"),

    // Lỗi Logic Order
    PRICE_MISMATCH(400, "Có sự sai lệch về giá trị thanh toán, vui lòng tải lại trang"),
    INVALID_STATUS_TRANSITION(400, "Không thể chuyển đổi trạng thái đơn hàng này");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}