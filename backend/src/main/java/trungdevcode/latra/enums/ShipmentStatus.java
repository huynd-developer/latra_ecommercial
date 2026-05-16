// backend/src/main/java/trungdevcode/latra/enums/ShipmentStatus.java
package trungdevcode.latra.enums;

public enum ShipmentStatus {
    PREPARING,   // Đang chuẩn bị hàng
    PICKING,     // Shipper đang lấy hàng
    DELIVERING,  // Đang giao cho khách
    DELIVERED,   // Giao thành công
    RETURNED,    // Hoàn hàng (Khách bom)
    CANCELLED    // Hủy giao
}