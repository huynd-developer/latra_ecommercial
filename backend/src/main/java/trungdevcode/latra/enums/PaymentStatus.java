package trungdevcode.latra.enums;

public enum PaymentStatus {
    PENDING,    // Đang chờ khách quét mã
    SUCCESS,    // Thanh toán thành công (Webhook VNPAY báo về)
    FAILED,     // Thanh toán thất bại / Hủy giao dịch
    REFUNDED    // Đã hoàn tiền cho khách
}