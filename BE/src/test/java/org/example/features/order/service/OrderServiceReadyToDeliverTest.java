package org.example.features.order.service;

import org.example.features.company.repository.CompanyRepository;
import org.example.features.company.repository.UserRepository;
import org.example.features.contract.service.ContractService;
import org.example.features.order.dto.OrderResponseDTO;
import org.example.features.order.entity.Order;
import org.example.features.order.entity.OrderEventType;
import org.example.features.order.entity.OrderStatus;
import org.example.features.order.entity.OrderType;
import org.example.features.order.repository.OrderEventRepository;
import org.example.features.order.repository.OrderImportBatchRepository;
import org.example.features.order.repository.OrderItemRepository;
import org.example.features.order.repository.OrderRepository;
import org.example.features.notification.service.UserNotificationService;
import org.example.features.payment.service.PaymentMilestoneService;
import org.example.features.payment.service.PaymentService;
import org.example.features.productadmin.AdminProductService;
import org.example.features.warehouse.repository.DrawingMetaRepository;
import org.example.features.warehouse.service.QuotePricingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OrderServiceReadyToDeliverTest {

    private OrderRepository orderRepository;
    private OrderAuditService orderAuditService;
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepository.class);
        orderAuditService = mock(OrderAuditService.class);

        orderService = new OrderService(
                orderRepository,
                mock(OrderEventRepository.class),
                mock(OrderImportBatchRepository.class),
                mock(OrderItemRepository.class),
                mock(UserRepository.class),
                mock(CompanyRepository.class),
                mock(ContractService.class),
                mock(PaymentService.class),
                mock(PaymentMilestoneService.class),
                mock(QuotePricingService.class),
                mock(DrawingMetaRepository.class),
                mock(AdminProductService.class),
                mock(UserNotificationService.class),
                orderAuditService);
    }

    @Test
    void setDeliveryDateAndReadyToDeliver_shouldTransitionWhenReadyMadeProcessing() {
        Order order = new Order();
        order.setId(11L);
        order.setOrderNumber("ORD-READY-001");
        order.setOrderType(OrderType.READY_MADE);
        order.setStatus(OrderStatus.PROCESSING);

        LocalDate deliveryDate = LocalDate.now().plusDays(2);

        when(orderRepository.findById(11L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderResponseDTO result = orderService.setDeliveryDateAndReadyToDeliver(11L, deliveryDate);

        assertEquals(OrderStatus.AWAITING_DELIVERY, result.getStatus());
        assertEquals(deliveryDate, result.getDeliveryDate());
        assertEquals(OrderType.READY_MADE, result.getOrderType());
        assertEquals(OrderStatus.AWAITING_DELIVERY, order.getStatus());
        assertEquals(deliveryDate, order.getDeliveryDate());

        verify(orderRepository).save(order);
        verify(orderAuditService).recordStatusEvent(
                eq(order),
                eq(OrderEventType.STATUS_CHANGED),
                eq(OrderStatus.PROCESSING),
                eq(OrderStatus.AWAITING_DELIVERY),
                isNull(),
                eq("ADMIN"),
                eq("Admin đã đặt ngày giao và đánh dấu sẵn sàng giao hàng"));
    }

    @Test
    void setDeliveryDateAndReadyToDeliver_shouldRejectWhenDateIsInPast() {
        Order order = new Order();
        order.setId(12L);
        order.setOrderType(OrderType.READY_MADE);
        order.setStatus(OrderStatus.PROCESSING);

        when(orderRepository.findById(12L)).thenReturn(Optional.of(order));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> orderService.setDeliveryDateAndReadyToDeliver(12L, LocalDate.now().minusDays(1)));

        assertEquals("Ngày giao hàng không được ở trong quá khứ", ex.getMessage());
        verify(orderRepository, never()).save(any(Order.class));
        verify(orderAuditService, never()).recordStatusEvent(any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void setDeliveryDateAndReadyToDeliver_shouldRejectWhenOrderTypeIsNotReadyMade() {
        Order order = new Order();
        order.setId(13L);
        order.setOrderType(OrderType.CUSTOM_MANUFACTURING);
        order.setStatus(OrderStatus.PROCESSING);

        when(orderRepository.findById(13L)).thenReturn(Optional.of(order));

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> orderService.setDeliveryDateAndReadyToDeliver(13L, LocalDate.now().plusDays(1)));

        assertEquals("Chức năng này chỉ áp dụng cho đơn sản phẩm phôi (READY_MADE)", ex.getMessage());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void setDeliveryDateAndReadyToDeliver_shouldRejectWhenStatusIsNotProcessing() {
        Order order = new Order();
        order.setId(14L);
        order.setOrderType(OrderType.READY_MADE);
        order.setStatus(OrderStatus.DEPOSITED);

        when(orderRepository.findById(14L)).thenReturn(Optional.of(order));

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> orderService.setDeliveryDateAndReadyToDeliver(14L, LocalDate.now().plusDays(1)));

        assertEquals("Đơn hàng phải đang ở trạng thái 'Đang chuẩn bị' (PROCESSING)", ex.getMessage());
        verify(orderRepository, never()).save(any(Order.class));
    }
}
