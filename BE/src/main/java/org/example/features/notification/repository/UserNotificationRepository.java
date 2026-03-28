package org.example.features.notification.repository;

import org.example.features.notification.entity.UserNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserNotificationRepository extends JpaRepository<UserNotification, Long> {

    List<UserNotification> findByUserIdOrderByCreatedAtDesc(Long userId);

    long countByUserIdAndReadFalse(Long userId);

    boolean existsByUserIdAndNotificationKey(Long userId, String notificationKey);

    long deleteByUserIdAndReadTrueAndCreatedAtBefore(Long userId, LocalDateTime createdAt);

    long deleteByReadTrueAndCreatedAtBefore(LocalDateTime createdAt);
}
