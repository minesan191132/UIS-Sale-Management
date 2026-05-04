package org.example.features.warehouse.service;

import lombok.RequiredArgsConstructor;
import org.example.features.order.entity.Order;
import org.example.features.order.entity.OrderItem;
import org.example.features.order.entity.OrderStatus;
import org.example.features.order.repository.OrderItemRepository;
import org.example.features.order.repository.OrderRepository;
import org.example.features.warehouse.dto.ShipmentPreviewDTO;
import org.example.features.warehouse.repository.DrawingMetaRepository;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ShipmentService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final DrawingMetaRepository drawingMetaRepository;

    /**
     * Preview shipment by order IDs (legacy - selects all items in orders)
     */
    public ShipmentPreviewDTO previewShipment(List<Long> orderIds) {
        List<Order> orders = orderRepository.findAllById(orderIds);
        if (orders.isEmpty()) {
            throw new IllegalArgumentException("No orders found for given IDs");
        }

        validateShipmentEligibleOrders(orders);

        List<OrderItem> allItems = new ArrayList<>();
        for (Order order : orders) {
            allItems.addAll(order.getItems());
        }

        return buildPreview(allItems);
    }

    /**
     * Preview shipment by individual item IDs (partial shipment)
     */
    public ShipmentPreviewDTO previewShipmentByItems(List<Long> itemIds) {
        List<OrderItem> items = orderItemRepository.findAllById(itemIds);
        if (items.isEmpty()) {
            throw new IllegalArgumentException("No items found for given IDs");
        }

        List<Order> orders = items.stream()
                .map(OrderItem::getOrder)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        validateShipmentEligibleOrders(orders);

        return buildPreview(items);
    }

    private void validateShipmentEligibleOrders(List<Order> orders) {
        for (Order order : orders) {
            OrderStatus status = order.getStatus();
            if (!isShipmentEligibleStatus(status)) {
                throw new IllegalStateException(
                        "Chỉ có thể xuất hóa đơn khi đơn đã cọc. Đơn " + order.getOrderNumber() + " đang ở trạng thái " + status);
            }
        }
    }

    private boolean isShipmentEligibleStatus(OrderStatus status) {
        return status == OrderStatus.DEPOSITED
                || status == OrderStatus.PROCESSING
                || status == OrderStatus.COMPLETED;
    }

    /**
     * Shared logic: build preview from a list of OrderItems
     */
    private ShipmentPreviewDTO buildPreview(List<OrderItem> items) {
        // Count distinct orders
        Set<Long> orderIds = new HashSet<>();
        items.forEach(oi -> {
            if (oi.getOrder() != null) orderIds.add(oi.getOrder().getId());
        });

        ShipmentPreviewDTO preview = new ShipmentPreviewDTO();
        preview.setShipmentCode("SHP-" + System.currentTimeMillis());
        preview.setTitle("Phiếu xuất kho — " + items.size() + " sản phẩm từ " + orderIds.size() + " đơn");

        Map<String, ShipmentPreviewDTO.ShipmentItem> itemMap = new LinkedHashMap<>();

        for (OrderItem oi : items) {
            String drawingNumber = oi.getDrawingNumber();
            if (drawingNumber == null || drawingNumber.isBlank()) continue;

            String orderNumber = oi.getOrder() != null ? oi.getOrder().getOrderNumber() : "—";

            // Use item's deliveryDate if available, else fallback to order's createdAt
            String deliveryDate = "";
            if (oi.getDeliveryDate() != null) {
                deliveryDate = oi.getDeliveryDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } else if (oi.getOrder() != null && oi.getOrder().getCreatedAt() != null) {
                deliveryDate = oi.getOrder().getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }

            ShipmentPreviewDTO.ShipmentItem shipItem = itemMap.computeIfAbsent(drawingNumber, k -> {
                ShipmentPreviewDTO.ShipmentItem si = new ShipmentPreviewDTO.ShipmentItem();
                si.setDrawingNumber(drawingNumber);
                si.setPartName(oi.getItemName());
                si.setSpecification(oi.getSpecification());
                si.setMaterial(oi.getMaterialType());
                si.setTotalQty(0);
                si.setOrderBreakdown(new ArrayList<>());

                return si;
            });

            int qty = oi.getQuantity() != null ? oi.getQuantity() : 0;
            shipItem.setTotalQty(shipItem.getTotalQty() + qty);

            ShipmentPreviewDTO.OrderBreakdown ob = new ShipmentPreviewDTO.OrderBreakdown();
            ob.setVnnNo(orderNumber);
            ob.setDeliveryDate(deliveryDate);
            ob.setQuantity(qty);
            shipItem.getOrderBreakdown().add(ob);
        }

        List<ShipmentPreviewDTO.ShipmentItem> shipItems = new ArrayList<>(itemMap.values());
        preview.setItems(shipItems);
        preview.setTotalItems(shipItems.size());
        preview.setTotalQty(shipItems.stream().mapToInt(ShipmentPreviewDTO.ShipmentItem::getTotalQty).sum());

        return preview;
    }
}
