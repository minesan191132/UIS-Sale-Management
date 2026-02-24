package org.example.features.order.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "invoices")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "invoice_no", length = 50)
    private String invoiceNo; // Số phiếu (Có thể sinh tự động hoặc update sau ở trang 3)
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Một phiếu xuất chứa nhiều đơn hàng
    // LEGACY MAPPING - Disabled for new Order Management System
    // @OneToMany(mappedBy = "invoice")
    // private List<Order> orders;
}