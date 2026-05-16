// backend/src/main/java/trungdevcode/latra/entity/Order.java
package trungdevcode.latra.entity;

import lombok.EqualsAndHashCode;
import trungdevcode.latra.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import trungdevcode.latra.enums.OrderStatus;
import trungdevcode.latra.enums.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@EqualsAndHashCode(callSuper = true)
public class Order extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private User employee;

    private BigDecimal totalAmount;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30)
    private OrderStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", length = 30)
    private PaymentMethod paymentMethod;//"VNPAY", "Vnpay",
    @Column(name = "payment_deadline")
    private LocalDateTime paymentDeadline;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL) private List<OrderVoucher> appliedVouchers;

    private BigDecimal cashAmount;     // Số tiền mặt khách đưa
    private BigDecimal transferAmount; // Số tiền khách chuyển khoản
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String note;
    
    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Shipment shipment;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderDetail> orderDetails;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<PaymentTransaction> transactions;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<RefundRequest> refundRequests;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucher_id")
    private Voucher voucher;
}