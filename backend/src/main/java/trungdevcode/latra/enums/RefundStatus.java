package trungdevcode.latra.enums;

public enum RefundStatus {
    REQUESTED,  // Khách gửi yêu cầu hoàn/trả
    APPROVED,   // Quản lý duyệt yêu cầu
    REJECTED,   // Quản lý từ chối
    COMPLETED   // Đã trả tiền về tài khoản khách & Nhập lại kho
}