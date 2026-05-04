package org.example.features.warehouse.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.features.order.entity.Order;
import org.example.features.order.entity.OrderItem;
import org.example.features.order.repository.OrderRepository;
import org.example.features.warehouse.dto.WarehouseItemDTO;
import org.example.features.warehouse.entity.DrawingMeta;
import org.example.features.warehouse.repository.DrawingMetaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WarehouseService {

    private final OrderRepository orderRepository;
    private final DrawingMetaRepository drawingMetaRepository;

    public List<WarehouseItemDTO> getWarehouseItems(Long companyId, String search) {
        List<Order> orders;
        if (companyId != null) {
            orders = orderRepository.findByCompanyId(companyId);
        } else {
            orders = orderRepository.findAll();
        }

        // Group order items by drawing_number
        Map<String, List<OrderItemWithOrder>> groupedItems = new LinkedHashMap<>();

        for (Order order : orders) {
            for (OrderItem item : order.getItems()) {
                String key = item.getDrawingNumber();
                if (key == null || key.isBlank()) continue;

                groupedItems.computeIfAbsent(key, k -> new ArrayList<>())
                        .add(new OrderItemWithOrder(item, order));
            }
        }

        // Build DTOs
        List<WarehouseItemDTO> result = new ArrayList<>();
        for (Map.Entry<String, List<OrderItemWithOrder>> entry : groupedItems.entrySet()) {
            String drawingNumber = entry.getKey();
            List<OrderItemWithOrder> items = entry.getValue();

            WarehouseItemDTO dto = new WarehouseItemDTO();
            dto.setDrawingNumber(drawingNumber);

            // Use first item's data for partName, spec, material
            OrderItem firstItem = items.get(0).item;
            dto.setPartName(firstItem.getItemName());
            dto.setSpecification(firstItem.getSpecification());
            dto.setMaterial(firstItem.getMaterialType());

            // Totals
            dto.setTotalQty(items.stream().mapToInt(i -> i.item.getQuantity() != null ? i.item.getQuantity() : 0).sum());

            // Unique orders
            Set<Long> orderIds = items.stream().map(i -> i.order.getId()).collect(Collectors.toSet());
            dto.setOrderCount(orderIds.size());

            // Company name from first order
            if (items.get(0).order.getCompany() != null) {
                dto.setCompanyName(items.get(0).order.getCompany().getCompanyName());
            }

            // Order breakdown
            List<WarehouseItemDTO.OrderBreakdown> breakdowns = new ArrayList<>();
            for (OrderItemWithOrder itemWithOrder : items) {
                WarehouseItemDTO.OrderBreakdown ob = new WarehouseItemDTO.OrderBreakdown();
                ob.setOrderId(itemWithOrder.order.getId());
                ob.setOrderNumber(itemWithOrder.order.getOrderNumber());
                ob.setQuantity(itemWithOrder.item.getQuantity() != null ? itemWithOrder.item.getQuantity() : 0);
                ob.setCreatedAt(itemWithOrder.order.getCreatedAt());
                breakdowns.add(ob);
            }
            dto.setOrders(breakdowns);

            result.add(dto);
        }

        // Search filter
        if (search != null && !search.isBlank()) {
            String s = search.toLowerCase();
            result = result.stream().filter(item ->
                    (item.getDrawingNumber() != null && item.getDrawingNumber().toLowerCase().contains(s)) ||
                    (item.getPartName() != null && item.getPartName().toLowerCase().contains(s)) ||
                    (item.getMaterial() != null && item.getMaterial().toLowerCase().contains(s)) ||
                    (item.getSpecification() != null && item.getSpecification().toLowerCase().contains(s))
            ).collect(Collectors.toList());
        }

        return result;
    }



    public List<String> getCompanyNames() {
        return orderRepository.findAll().stream()
                .map(order -> order.getCompany() != null ? order.getCompany().getCompanyName() : null)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    private record OrderItemWithOrder(OrderItem item, Order order) {}
}
