package org.example.features.contract.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.features.company.entity.Company;
import org.example.features.company.entity.User;
import org.example.features.company.entity.UserRole;
import org.example.features.company.repository.UserRepository;
import org.example.features.contract.dto.ContractConfirmResponseDTO;
import org.example.features.contract.dto.ContractResponseDTO;
import org.example.features.contract.dto.ContractTermsUpdateDTO;
import org.example.features.contract.entity.ContractStatus;
import org.example.features.contract.entity.OrderContract;
import org.example.features.contract.repository.OrderContractRepository;
import org.example.features.notification.entity.NotificationType;
import org.example.features.notification.service.UserNotificationService;
import org.example.features.order.entity.ItemReviewStatus;
import org.example.features.order.entity.Order;
import org.example.features.order.entity.OrderEventType;
import org.example.features.order.entity.OrderItem;
import org.example.features.order.entity.OrderStatus;
import org.example.features.order.repository.OrderRepository;
import org.example.features.order.service.OrderAuditService;
import org.example.features.payment.entity.PaymentMilestone;
import org.example.features.payment.service.PaymentMilestoneService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContractService {

    private static final TypeReference<List<String>> STRING_LIST_TYPE = new TypeReference<>() {
    };
    private static final DateTimeFormatter CONTRACT_NUMBER_DATE = DateTimeFormatter.ofPattern("yyyyMMdd");

    private static final List<String> DEFAULT_QUALITY_TERMS = List.of(
            "Sản phẩm phải đáp ứng đúng thông số kỹ thuật trong bản vẽ đính kèm. Dung sai không quá mức đã thỏa thuận.",
            "Bên B có quyền kiểm tra mẫu sản phẩm trước khi nhận lô hàng. Nếu không đạt yêu cầu, Bên A có 7 ngày làm lại hoặc bổ sung.",
            "Bên A chịu trách nhiệm về sản phẩm lỗi do gia công trong vòng 30 ngày kể từ ngày giao hàng.");

    private static final List<String> DEFAULT_CANCEL_TERMS = List.of(
            "Bên B hủy hợp đồng sau khi đã cọc: mất toàn bộ tiền cọc nếu Bên A đã tiến hành gia công.",
            "Bên A không thể giao hàng đúng hạn mà không có lý do hợp lệ: hoàn trả tiền cọc và bồi thường 2% giá trị hợp đồng/tuần trễ.",
            "Trường hợp bất khả kháng (thiên tai, dịch bệnh...): hai bên thương lượng gia hạn, không áp dụng phạt.");

    private final OrderContractRepository orderContractRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final PaymentMilestoneService paymentMilestoneService;
    private final UserNotificationService userNotificationService;
    private final OrderAuditService orderAuditService;
    private final ObjectMapper objectMapper;

    @Transactional
    public OrderContract createContractForQuotedOrder(Order order) {
        if (order == null || order.getId() == null) {
            throw new IllegalArgumentException("Order is required");
        }

        orderContractRepository.findTopByOrderIdOrderByCreatedAtDesc(order.getId())
                .filter(contract -> contract.getStatus() == ContractStatus.PENDING_CONFIRMATION)
                .ifPresent(previous -> {
                    previous.setStatus(ContractStatus.CANCELLED);
                    orderContractRepository.save(previous);
                });

        OrderContract contract = new OrderContract();
        contract.setOrder(order);
        contract.setContractNumber(generateContractNumber());
        contract.setStatus(ContractStatus.PENDING_CONFIRMATION);
        contract.setQualityTerms(toJson(DEFAULT_QUALITY_TERMS));
        contract.setCancelTerms(toJson(DEFAULT_CANCEL_TERMS));
        contract.setExtraNotes(null);
        return orderContractRepository.save(contract);
    }

    @Transactional(readOnly = true)
    public ContractResponseDTO getContractByOrderId(Long orderId, Long actorUserId, boolean actorAdmin) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        enforceOrderAccess(order, actorUserId, actorAdmin);

        OrderContract contract = orderContractRepository.findTopByOrderIdOrderByCreatedAtDesc(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Contract not found"));

        List<PaymentMilestone> milestones = paymentMilestoneService.getMilestones(orderId);
        return toContractResponse(contract, order, milestones);
    }

    @Transactional(readOnly = true)
    public ContractResponseDTO getContractById(Long contractId, Long actorUserId, boolean actorAdmin) {
        OrderContract contract = orderContractRepository.findById(contractId)
                .orElseThrow(() -> new IllegalArgumentException("Contract not found"));
        Order order = contract.getOrder();
        enforceOrderAccess(order, actorUserId, actorAdmin);

        List<PaymentMilestone> milestones = paymentMilestoneService.getMilestones(order.getId());
        return toContractResponse(contract, order, milestones);
    }

    @Transactional
    public OrderContract updateTerms(Long contractId, ContractTermsUpdateDTO request) {
        OrderContract contract = orderContractRepository.findById(contractId)
                .orElseThrow(() -> new IllegalArgumentException("Contract not found"));

        if (contract.getStatus() != ContractStatus.PENDING_CONFIRMATION) {
            throw new IllegalStateException("Không thể sửa điều khoản khi hợp đồng đã được xác nhận.");
        }

        if (request.getQualityTerms() != null) {
            contract.setQualityTerms(toJson(request.getQualityTerms()));
        }
        if (request.getCancelTerms() != null) {
            contract.setCancelTerms(toJson(request.getCancelTerms()));
        }
        if (request.getExtraNotes() != null) {
            contract.setExtraNotes(request.getExtraNotes());
        }

        return orderContractRepository.save(contract);
    }

    @Transactional
    public ContractConfirmResponseDTO confirmContract(Long contractId, Long confirmerUserId, String confirmedIp) {
        OrderContract contract = orderContractRepository.findById(contractId)
                .orElseThrow(() -> new IllegalArgumentException("Contract not found"));

        if (contract.getStatus() != ContractStatus.PENDING_CONFIRMATION) {
            throw new IllegalStateException("Contract is not pending confirmation");
        }

        Order order = contract.getOrder();
        if (order.getUser() == null || !order.getUser().getId().equals(confirmerUserId)) {
            throw new SecurityException("Only order owner can confirm contract");
        }

        User confirmer = userRepository.findById(confirmerUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        contract.setStatus(ContractStatus.CONFIRMED);
        contract.setConfirmedAt(LocalDateTime.now());
        contract.setConfirmedBy(confirmer);
        contract.setConfirmedIp(confirmedIp);
        orderContractRepository.save(contract);

        OrderStatus previousStatus = order.getStatus();
        order.setStatus(OrderStatus.AWAITING_PAYMENT);
        orderRepository.save(order);

        PaymentMilestone activeMilestone = paymentMilestoneService.activateFirstMilestone(order.getId());

        orderAuditService.recordStatusEvent(
                order,
                OrderEventType.STATUS_CHANGED,
                previousStatus,
                OrderStatus.AWAITING_PAYMENT,
                confirmerUserId,
                "CUSTOMER",
                "Khách xác nhận hợp đồng");

        userNotificationService.pushOrderNotification(
                order,
                NotificationType.ORDER,
                "Hợp đồng đã được xác nhận",
                "Đơn " + order.getOrderNumber() + " đã xác nhận hợp đồng. Vui lòng thanh toán tiền cọc trước hạn.",
                "contract-confirmed-" + contract.getId());

        ContractConfirmResponseDTO response = new ContractConfirmResponseDTO();
        response.setId(contract.getId());
        response.setContractNumber(contract.getContractNumber());
        response.setStatus(contract.getStatus().name());
        response.setConfirmedAt(contract.getConfirmedAt());

        ContractConfirmResponseDTO.ConfirmedByDTO confirmedByDTO = new ContractConfirmResponseDTO.ConfirmedByDTO();
        confirmedByDTO.setUserId(confirmer.getId());
        confirmedByDTO.setFullName(confirmer.getFullName());
        confirmedByDTO.setEmail(confirmer.getEmail());
        response.setConfirmedBy(confirmedByDTO);

        response.setConfirmedIp(contract.getConfirmedIp());
        response.setOrderStatus(order.getStatus().name());

        ContractConfirmResponseDTO.ActiveMilestoneDTO milestoneDTO = new ContractConfirmResponseDTO.ActiveMilestoneDTO();
        milestoneDTO.setMilestoneOrder(activeMilestone.getMilestoneOrder());
        milestoneDTO.setMilestoneName(activeMilestone.getMilestoneName());
        milestoneDTO.setAmount(activeMilestone.getAmount());
        milestoneDTO.setDueDate(activeMilestone.getDueDate());
        milestoneDTO.setStatus(activeMilestone.getStatus().name());
        milestoneDTO.setPaymentQrUrl(activeMilestone.getPaymentQrUrl());
        response.setActiveMilestone(milestoneDTO);

        response.setMessage("Hợp đồng đã được xác nhận. Vui lòng thanh toán tiền cọc đúng hạn.");
        return response;
    }

    @Transactional
    public ContractRejectResult rejectContract(Long contractId, Long userId, String reason) {
        OrderContract contract = orderContractRepository.findById(contractId)
                .orElseThrow(() -> new IllegalArgumentException("Contract not found"));

        if (contract.getStatus() != ContractStatus.PENDING_CONFIRMATION) {
            throw new IllegalStateException("Contract is not pending confirmation");
        }

        String normalizedReason = normalizeReason(reason);

        Order order = contract.getOrder();
        if (order.getUser() == null || !order.getUser().getId().equals(userId)) {
            throw new SecurityException("Only order owner can reject contract");
        }

        contract.setStatus(ContractStatus.REJECTED);
        contract.setRejectedAt(LocalDateTime.now());
        contract.setRejectionReason(normalizedReason);
        orderContractRepository.save(contract);

        OrderStatus previousStatus = order.getStatus();
        order.setStatus(OrderStatus.PENDING_QUOTE);
        order.setPaymentDeadline(null);
        order.setPaymentQrUrl(null);
        orderRepository.save(order);

        paymentMilestoneService.cancelAllMilestones(order);

        orderAuditService.recordStatusEvent(
                order,
                OrderEventType.STATUS_CHANGED,
                previousStatus,
                OrderStatus.PENDING_QUOTE,
                userId,
                "CUSTOMER",
                "Khách từ chối hợp đồng: " + normalizedReason);

        userRepository.findByRole(UserRole.ADMIN).forEach(admin ->
                userNotificationService.pushNotificationToUser(
                        admin.getId(),
                        order,
                        NotificationType.ORDER,
                        "Hợp đồng bị từ chối",
                        "Đơn " + order.getOrderNumber() + " bị từ chối hợp đồng. Lý do: " + normalizedReason,
                        "contract-rejected-" + contract.getId() + "-" + admin.getId()));

        return new ContractRejectResult(
                contract.getId(),
                contract.getContractNumber(),
                contract.getStatus().name(),
                contract.getRejectedAt(),
                contract.getRejectionReason(),
                order.getStatus().name(),
                "Hợp đồng đã bị từ chối. Đơn hàng quay về trạng thái chờ báo giá.");
    }

    private void enforceOrderAccess(Order order, Long actorUserId, boolean actorAdmin) {
        if (actorAdmin) {
            return;
        }
        Long ownerId = order.getUser() != null ? order.getUser().getId() : null;
        if (actorUserId == null || ownerId == null || !ownerId.equals(actorUserId)) {
            throw new SecurityException("Unauthorized access to contract");
        }
    }

    private ContractResponseDTO toContractResponse(OrderContract contract,
                                                   Order order,
                                                   List<PaymentMilestone> milestones) {
        ContractResponseDTO dto = new ContractResponseDTO();
        dto.setId(contract.getId());
        dto.setContractNumber(contract.getContractNumber());
        dto.setStatus(contract.getStatus().name());
        dto.setCreatedAt(contract.getCreatedAt());
        dto.setConfirmedAt(contract.getConfirmedAt());

        if (contract.getConfirmedBy() != null) {
            ContractConfirmResponseDTO.ConfirmedByDTO confirmedBy = new ContractConfirmResponseDTO.ConfirmedByDTO();
            confirmedBy.setUserId(contract.getConfirmedBy().getId());
            confirmedBy.setFullName(contract.getConfirmedBy().getFullName());
            confirmedBy.setEmail(contract.getConfirmedBy().getEmail());
            dto.setConfirmedBy(confirmedBy);
        }

        dto.setSupplierInfo(buildSupplierInfo());
        dto.setBuyerInfo(buildBuyerInfo(order));
        dto.setOrderInfo(buildOrderInfo(order));
        dto.setItems(buildItems(order));
        dto.setMilestones(buildMilestones(milestones));
        dto.setQualityTerms(fromJsonList(contract.getQualityTerms()));
        dto.setCancelTerms(fromJsonList(contract.getCancelTerms()));
        dto.setExtraNotes(contract.getExtraNotes());
        return dto;
    }

    private ContractResponseDTO.CompanyInfoDTO buildSupplierInfo() {
        ContractResponseDTO.CompanyInfoDTO dto = new ContractResponseDTO.CompanyInfoDTO();
        Company supplier = resolveSupplierCompany();
        if (supplier == null) {
            return dto;
        }

        dto.setCompanyName(supplier.getCompanyName());
        dto.setTaxCode(supplier.getTaxCode());
        dto.setAddress(supplier.getAddress());
        dto.setRepresentative(supplier.getRepresentative());
        dto.setEmail(supplier.getEmail());
        dto.setPhone(supplier.getPhone());
        return dto;
    }

    private ContractResponseDTO.CompanyInfoDTO buildBuyerInfo(Order order) {
        ContractResponseDTO.CompanyInfoDTO dto = new ContractResponseDTO.CompanyInfoDTO();

        if (order.getCompany() != null) {
            dto.setCompanyName(order.getCompany().getCompanyName());
            dto.setTaxCode(order.getCompany().getTaxCode());
            dto.setAddress(order.getCompany().getAddress());
            dto.setRepresentative(order.getCompany().getRepresentative());
        }

        if (order.getUser() != null) {
            dto.setEmail(order.getUser().getEmail());
            dto.setPhone(order.getUser().getPhone());
        }

        return dto;
    }

    private ContractResponseDTO.OrderInfoDTO buildOrderInfo(Order order) {
        ContractResponseDTO.OrderInfoDTO info = new ContractResponseDTO.OrderInfoDTO();
        info.setOrderId(order.getId());
        info.setOrderNumber(order.getOrderNumber());
        info.setTotalPrice(order.getTotalPrice());
        info.setDepositAmount(order.getDepositAmount());
        info.setDeliveryDate(order.getDeliveryDate());
        return info;
    }

    private List<ContractResponseDTO.ItemDTO> buildItems(Order order) {
        return order.getItems().stream()
                .filter(item -> item.getReviewStatus() == ItemReviewStatus.APPROVED)
                .map(this::toItemDTO)
                .toList();
    }

    private ContractResponseDTO.ItemDTO toItemDTO(OrderItem item) {
        ContractResponseDTO.ItemDTO dto = new ContractResponseDTO.ItemDTO();
        dto.setItemCode(item.getItemCode());
        dto.setDrawingNumber(item.getDrawingNumber());
        dto.setItemName(item.getItemName());
        dto.setSpecification(item.getSpecification());
        dto.setMaterial(item.getMaterialType());
        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getUnitPrice());

        if (item.getUnitPrice() != null && item.getQuantity() != null) {
            dto.setLineTotal(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        return dto;
    }

    private List<ContractResponseDTO.MilestoneDTO> buildMilestones(List<PaymentMilestone> milestones) {
        List<ContractResponseDTO.MilestoneDTO> result = new ArrayList<>();
        for (PaymentMilestone milestone : milestones) {
            ContractResponseDTO.MilestoneDTO dto = new ContractResponseDTO.MilestoneDTO();
            dto.setMilestoneOrder(milestone.getMilestoneOrder());
            dto.setMilestoneName(milestone.getMilestoneName());
            dto.setPercentage(milestone.getPercentage());
            dto.setAmount(milestone.getAmount());
            dto.setStatus(milestone.getStatus().name());
            dto.setDueDate(milestone.getDueDate());
            dto.setDescription(milestoneDescription(milestone.getMilestoneOrder()));
            result.add(dto);
        }
        return result;
    }

    private String milestoneDescription(Integer milestoneOrder) {
        if (milestoneOrder != null && milestoneOrder == 1) {
            return "Thanh toán trong vòng 7 ngày kể từ ngày xác nhận hợp đồng. Gia công chỉ bắt đầu sau khi nhận được khoản này.";
        }
        return "Thanh toán sau khi gia công hoàn tất và trước khi nhận hàng.";
    }

    private Company resolveSupplierCompany() {
        return userRepository.findByRole(UserRole.ADMIN)
                .stream()
                .map(User::getCompany)
                .filter(company -> company != null)
                .findFirst()
                .orElse(null);
    }

    private String generateContractNumber() {
        LocalDateTime start = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        long seq = orderContractRepository.countCreatedBetween(start, end) + 1;

        return "HD-" + start.format(CONTRACT_NUMBER_DATE) + "-" + String.format("%03d", seq);
    }

    private String toJson(List<String> values) {
        try {
            return objectMapper.writeValueAsString(values != null ? values : List.of());
        } catch (Exception ex) {
            throw new IllegalStateException("Cannot serialize contract terms", ex);
        }
    }

    private List<String> fromJsonList(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }

        try {
            return objectMapper.readValue(json, STRING_LIST_TYPE);
        } catch (Exception ex) {
            log.warn("Cannot parse contract terms JSON: {}", ex.getMessage());
            return List.of();
        }
    }

    private String normalizeReason(String reason) {
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Lý do từ chối là bắt buộc");
        }

        String normalized = reason.trim();
        if (normalized.length() > 1000) {
            throw new IllegalArgumentException("Lý do từ chối không được vượt quá 1000 ký tự");
        }
        return normalized;
    }

    public record ContractRejectResult(
            Long id,
            String contractNumber,
            String status,
            LocalDateTime rejectedAt,
            String rejectionReason,
            String orderStatus,
            String message) {
    }
}
