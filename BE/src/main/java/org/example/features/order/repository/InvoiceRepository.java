package org.example.features.order.repository;

import org.example.features.order.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    // Tìm kiếm phiếu xuất theo số hóa đơn
    Optional<Invoice> findByInvoiceNo(String invoiceNO);
}
