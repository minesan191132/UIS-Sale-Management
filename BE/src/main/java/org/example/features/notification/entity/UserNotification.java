package org.example.features.notification.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.features.company.entity.User;
import org.example.features.order.entity.Order;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_notifications",
        indexes = {
                @Index(name = "idx_user_notifications_user_created", columnList = "user_id, created_at"),
                @Index(name = "idx_user_notifications_user_read", columnList = "user_id, is_read")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_notifications_user_key", columnNames = {"user_id", "notification_key"})
        })
@Getter
@Setter
@NoArgsConstructor
public class UserNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private NotificationType type = NotificationType.SYSTEM;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "body", nullable = false, columnDefinition = "TEXT")
    private String body;

    @Column(name = "notification_key", length = 120)
    private String notificationKey;

    @Column(name = "is_read", nullable = false)
    private Boolean read = false;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (read == null) {
            read = false;
        }
    }
}
