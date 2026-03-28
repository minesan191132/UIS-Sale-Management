package org.example.features.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.features.company.entity.User;
import org.example.features.company.repository.UserRepository;
import org.example.features.notification.dto.UserNotificationResponseDTO;
import org.example.features.notification.entity.NotificationType;
import org.example.features.notification.entity.UserNotification;
import org.example.features.notification.repository.UserNotificationRepository;
import org.example.features.order.entity.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserNotificationService {

    private final UserNotificationRepository userNotificationRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<UserNotificationResponseDTO> getMyNotifications(Long userId) {
        return userNotificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public long countUnread(Long userId) {
        return userNotificationRepository.countByUserIdAndReadFalse(userId);
    }

    @Transactional
    public void markRead(Long userId, Long notificationId) {
        UserNotification notification = userNotificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));

        if (notification.getUser() == null || !notification.getUser().getId().equals(userId)) {
            throw new SecurityException("Unauthorized access to notification");
        }

        if (!Boolean.TRUE.equals(notification.getRead())) {
            notification.setRead(true);
            notification.setReadAt(LocalDateTime.now());
            userNotificationRepository.save(notification);
        }
    }

    @Transactional
    public void markAllRead(Long userId) {
        List<UserNotification> notifications = userNotificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        LocalDateTime now = LocalDateTime.now();
        boolean changed = false;

        for (UserNotification notification : notifications) {
            if (!Boolean.TRUE.equals(notification.getRead())) {
                notification.setRead(true);
                notification.setReadAt(now);
                changed = true;
            }
        }

        if (changed) {
            userNotificationRepository.saveAll(notifications);
        }
    }

    @Transactional
    public long cleanupMyOldReadNotifications(Long userId, int retentionDays) {
        int safeDays = Math.max(1, retentionDays);
        LocalDateTime cutoff = LocalDateTime.now().minusDays(safeDays);
        return userNotificationRepository.deleteByUserIdAndReadTrueAndCreatedAtBefore(userId, cutoff);
    }

    @Transactional
    public long cleanupOldReadNotifications(int retentionDays) {
        int safeDays = Math.max(1, retentionDays);
        LocalDateTime cutoff = LocalDateTime.now().minusDays(safeDays);
        return userNotificationRepository.deleteByReadTrueAndCreatedAtBefore(cutoff);
    }

    @Transactional
    public void pushNotificationToUser(Long userId,
                                       Order order,
                                       NotificationType type,
                                       String title,
                                       String body,
                                       String notificationKey) {
        if (userId == null) {
            return;
        }

        if (notificationKey != null && !notificationKey.isBlank()
                && userNotificationRepository.existsByUserIdAndNotificationKey(userId, notificationKey)) {
            return;
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return;
        }

        UserNotification notification = new UserNotification();
        notification.setUser(user);
        notification.setOrder(order);
        notification.setType(type);
        notification.setTitle(title);
        notification.setBody(body);
        notification.setNotificationKey(notificationKey);
        notification.setRead(false);

        userNotificationRepository.save(notification);
        log.info("Created user notification for user {} / order {} / key {}",
                userId,
                order != null ? order.getOrderNumber() : null,
                notificationKey);
    }

    @Transactional
    public void pushOrderNotification(Order order,
                                      NotificationType type,
                                      String title,
                                      String body,
                                      String notificationKey) {
        if (order == null || order.getUser() == null) {
            return;
        }

        pushNotificationToUser(order.getUser().getId(), order, type, title, body, notificationKey);
    }

    private UserNotificationResponseDTO toDTO(UserNotification notification) {
        UserNotificationResponseDTO dto = new UserNotificationResponseDTO();
        dto.setId(notification.getId());
        dto.setOrderId(notification.getOrder() != null ? notification.getOrder().getId() : null);
        dto.setOrderNumber(notification.getOrder() != null ? notification.getOrder().getOrderNumber() : null);
        dto.setType(notification.getType());
        dto.setTitle(notification.getTitle());
        dto.setBody(notification.getBody());
        dto.setRead(notification.getRead());
        dto.setReadAt(notification.getReadAt());
        dto.setCreatedAt(notification.getCreatedAt());
        return dto;
    }
}
