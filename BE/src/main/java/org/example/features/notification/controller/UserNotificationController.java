package org.example.features.notification.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.config.security.CustomUserDetails;
import org.example.features.notification.dto.UserNotificationResponseDTO;
import org.example.features.notification.service.UserNotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
public class UserNotificationController {

    private final UserNotificationService userNotificationService;

    @GetMapping("/my")
    public ResponseEntity<?> getMyNotifications(@AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            List<UserNotificationResponseDTO> notifications =
                    userNotificationService.getMyNotifications(userDetails.getUserId());
            long unreadCount = userNotificationService.countUnread(userDetails.getUserId());
            return ResponseEntity.ok(Map.of(
                    "notifications", notifications,
                    "unreadCount", unreadCount
            ));
        } catch (Exception e) {
            log.error("Error fetching notifications", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch notifications"));
        }
    }

    @GetMapping("/my/unread-count")
    public ResponseEntity<?> getUnreadCount(@AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            long unreadCount = userNotificationService.countUnread(userDetails.getUserId());
            return ResponseEntity.ok(Map.of("unreadCount", unreadCount));
        } catch (Exception e) {
            log.error("Error fetching unread notifications count", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch unread count"));
        }
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<?> markRead(@PathVariable Long id,
                                      @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            userNotificationService.markRead(userDetails.getUserId(), id);
            return ResponseEntity.ok(Map.of("message", "Notification marked as read"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error marking notification as read", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to mark notification as read"));
        }
    }

    @PutMapping("/read-all")
    public ResponseEntity<?> markAllRead(@AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            userNotificationService.markAllRead(userDetails.getUserId());
            return ResponseEntity.ok(Map.of("message", "All notifications marked as read"));
        } catch (Exception e) {
            log.error("Error marking all notifications as read", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to mark all notifications as read"));
        }
    }

    @DeleteMapping("/cleanup-old")
    public ResponseEntity<?> cleanupOldReadNotifications(
            @RequestParam(defaultValue = "90") int days,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            long deleted = userNotificationService.cleanupMyOldReadNotifications(userDetails.getUserId(), days);
            return ResponseEntity.ok(Map.of(
                    "message", "Old notifications cleaned up",
                    "deleted", deleted,
                    "days", Math.max(1, days)
            ));
        } catch (Exception e) {
            log.error("Error cleaning up old notifications", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to clean up notifications"));
        }
    }
}
