package org.example.features.order.entity;

public interface MasterInventoryDTO {
    String getItemCode(); // <-- MỚI: Mã sản phẩm

    String getDrawingNumber(); // Mã bản vẽ

    String getPartName(); // <-- MỚI: Tên sản phẩm (Tiếng Nhật/Việt)

    String getSpecification(); // <-- MỚI: Quy cách (Nên có để kho kiểm tra)

    String getMaterial(); // <-- MỚI: Vật liệu

    Long getTotalQuantity(); // Số lượng tổng
}
