package org.example.features.order.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.features.company.entity.User;
import org.example.features.company.repository.UserRepository;
import org.example.features.order.dto.OrderHistoryEventDTO;
import org.example.features.order.dto.OrderResponseDTO;
import org.example.features.order.dto.OrderRevisionSummaryDTO;
import org.example.features.order.entity.Order;
import org.example.features.order.entity.OrderEvent;
import org.example.features.order.entity.OrderEventType;
import org.example.features.order.entity.OrderImportBatch;
import org.example.features.order.entity.OrderRevision;
import org.example.features.order.entity.OrderRevisionSource;
import org.example.features.order.entity.OrderStatus;
import org.example.features.order.repository.OrderEventRepository;
import org.example.features.order.repository.OrderRepository;
import org.example.features.order.repository.OrderRevisionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderAuditService {

    private final OrderRepository orderRepository;
    private final OrderRevisionRepository orderRevisionRepository;
    private final OrderEventRepository orderEventRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public int recordImportApprovedRevision(
            Order order,
            OrderImportBatch importBatch,
            OrderRevisionSource sourceType,
            Object snapshot,
            OrderStatus fromStatus,
            OrderStatus toStatus,
            Long actorUserId,
            String actorRole,
            String note) {
        if (order == null || order.getId() == null) {
            return 0;
        }

        int current = safeCurrentRevision(order);
        boolean hasAnyRevision = orderRevisionRepository.existsByOrderId(order.getId());
        int nextRevision = hasAnyRevision ? current + 1 : current;

        order.setCurrentRevisionNo(nextRevision);
        orderRepository.save(order);

        OrderRevision revision = new OrderRevision();
        revision.setOrder(order);
        revision.setRevisionNo(nextRevision);
        revision.setSourceType(sourceType != null ? sourceType : OrderRevisionSource.MANUAL_UPDATE);
        revision.setImportBatch(importBatch);
        revision.setSnapshotJson(toJson(snapshot));
        revision.setCreatedByUser(resolveActor(actorUserId));
        orderRevisionRepository.save(revision);

        OrderEvent event = buildEvent(order, nextRevision, OrderEventType.IMPORT_APPROVED, fromStatus, toStatus, actorUserId,
                actorRole, note);
        orderEventRepository.save(event);

        return nextRevision;
    }

    @Transactional
    public void recordStatusEvent(
            Order order,
            OrderEventType eventType,
            OrderStatus fromStatus,
            OrderStatus toStatus,
            Long actorUserId,
            String actorRole,
            String note) {
        if (order == null || order.getId() == null || eventType == null) {
            return;
        }

        Integer revisionNo = safeCurrentRevision(order);
        OrderEvent event = buildEvent(order, revisionNo, eventType, fromStatus, toStatus, actorUserId, actorRole, note);
        orderEventRepository.save(event);
    }

    @Transactional(readOnly = true)
    public List<OrderHistoryEventDTO> getOrderHistory(Long orderId) {
        if (orderId == null) {
            return Collections.emptyList();
        }
        return orderEventRepository.findByOrderIdOrderByCreatedAtDescIdDesc(orderId).stream()
                .map(this::toHistoryDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OrderRevisionSummaryDTO> getOrderRevisions(Long orderId) {
        if (orderId == null) {
            return Collections.emptyList();
        }
        return orderRevisionRepository.findByOrderIdOrderByRevisionNoDesc(orderId).stream()
                .map(this::toRevisionDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderResponseDTO getOrderRevisionSnapshot(Long orderId, Integer revisionNo) {
        OrderRevision revision = orderRevisionRepository.findByOrderIdAndRevisionNo(orderId, revisionNo)
                .orElseThrow(() -> new IllegalArgumentException("Revision not found"));

        try {
            return objectMapper.readValue(revision.getSnapshotJson(), OrderResponseDTO.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Cannot parse revision snapshot", e);
        }
    }

    private int safeCurrentRevision(Order order) {
        if (order.getCurrentRevisionNo() == null || order.getCurrentRevisionNo() < 1) {
            order.setCurrentRevisionNo(1);
            orderRepository.save(order);
        }
        return order.getCurrentRevisionNo();
    }

    private User resolveActor(Long actorUserId) {
        if (actorUserId == null) {
            return null;
        }
        return userRepository.findById(actorUserId).orElse(null);
    }

    private String normalizeActorRole(String actorRole) {
        if (actorRole == null || actorRole.isBlank()) {
            return "SYSTEM";
        }
        return actorRole.trim().toUpperCase();
    }

    private String toJson(Object snapshot) {
        if (snapshot == null) {
            return "{}";
        }
        try {
            return objectMapper.writeValueAsString(snapshot);
        } catch (JsonProcessingException e) {
            log.warn("Failed to serialize snapshot for order audit: {}", e.getMessage());
            return "{}";
        }
    }

    private OrderEvent buildEvent(
            Order order,
            Integer revisionNo,
            OrderEventType eventType,
            OrderStatus fromStatus,
            OrderStatus toStatus,
            Long actorUserId,
            String actorRole,
            String note) {
        OrderEvent event = new OrderEvent();
        event.setOrder(order);
        event.setRevisionNo(revisionNo);
        event.setEventType(eventType);
        event.setFromStatus(fromStatus);
        event.setToStatus(toStatus);
        event.setActorUser(resolveActor(actorUserId));
        event.setActorRole(normalizeActorRole(actorRole));
        event.setNote(note);
        return event;
    }

    private OrderHistoryEventDTO toHistoryDto(OrderEvent event) {
        OrderHistoryEventDTO dto = new OrderHistoryEventDTO();
        dto.setId(event.getId());
        dto.setRevisionNo(event.getRevisionNo());
        dto.setEventType(event.getEventType());
        dto.setFromStatus(event.getFromStatus());
        dto.setToStatus(event.getToStatus());
        dto.setActorUserId(event.getActorUser() != null ? event.getActorUser().getId() : null);
        dto.setActorName(event.getActorUser() != null ? event.getActorUser().getFullName() : null);
        dto.setActorRole(event.getActorRole());
        dto.setNote(event.getNote());
        dto.setCreatedAt(event.getCreatedAt());
        return dto;
    }

    private OrderRevisionSummaryDTO toRevisionDto(OrderRevision revision) {
        OrderRevisionSummaryDTO dto = new OrderRevisionSummaryDTO();
        dto.setId(revision.getId());
        dto.setRevisionNo(revision.getRevisionNo());
        dto.setSourceType(revision.getSourceType());
        dto.setImportBatchId(revision.getImportBatch() != null ? revision.getImportBatch().getId() : null);
        dto.setCreatedByUserId(revision.getCreatedByUser() != null ? revision.getCreatedByUser().getId() : null);
        dto.setCreatedByName(revision.getCreatedByUser() != null ? revision.getCreatedByUser().getFullName() : null);
        dto.setCreatedAt(revision.getCreatedAt());
        return dto;
    }
}
