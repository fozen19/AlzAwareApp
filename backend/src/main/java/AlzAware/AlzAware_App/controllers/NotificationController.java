package AlzAware.AlzAware_App.controllers;


import AlzAware.AlzAware_App.dto.NotificationDTO;
import AlzAware.AlzAware_App.models.NotificationType;
import AlzAware.AlzAware_App.payload.request.CreateNotificationRequest;
import AlzAware.AlzAware_App.payload.response.MessageResponse;
import AlzAware.AlzAware_App.security.services.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notifications", description = "API for notification management")
@Slf4j
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/create")
    @Operation(summary = "Create a new notification", description = "Create a new notification for a recipient")
    public ResponseEntity<?> createNotification(@Valid @RequestBody CreateNotificationRequest request) {
        try {
            NotificationDTO notification = notificationService.createNotification(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(notification);
        } catch (RuntimeException e) {
            log.error("Error creating notification: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/recipient/{recipientId}")
    @Operation(summary = "Get all notifications for recipient", description = "Retrieve all notifications for a specific recipient")
    public ResponseEntity<List<NotificationDTO>> getNotificationsByRecipient(
            @Parameter(description = "Recipient user ID") @PathVariable Long recipientId) {
        try {
            List<NotificationDTO> notifications = notificationService.getNotificationsByRecipient(recipientId);
            return ResponseEntity.ok(notifications);
        } catch (RuntimeException e) {
            log.error("Error fetching notifications for recipient {}: {}", recipientId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/recipient/{recipientId}/unread")
    @Operation(summary = "Get unread notifications for recipient", description = "Retrieve unread notifications for a specific recipient")
    public ResponseEntity<List<NotificationDTO>> getUnreadNotificationsByRecipient(
            @Parameter(description = "Recipient user ID") @PathVariable Long recipientId) {
        try {
            List<NotificationDTO> notifications = notificationService.getUnreadNotificationsByRecipient(recipientId);
            return ResponseEntity.ok(notifications);
        } catch (RuntimeException e) {
            log.error("Error fetching unread notifications for recipient {}: {}", recipientId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/recipient/{recipientId}/type/{type}")
    @Operation(summary = "Get notifications by type", description = "Retrieve notifications of a specific type for a recipient")
    public ResponseEntity<List<NotificationDTO>> getNotificationsByType(
            @Parameter(description = "Recipient user ID") @PathVariable Long recipientId,
            @Parameter(description = "Notification type") @PathVariable NotificationType type) {
        try {
            List<NotificationDTO> notifications = notificationService.getNotificationsByTypeAndRecipient(recipientId, type);
            return ResponseEntity.ok(notifications);
        } catch (RuntimeException e) {
            log.error("Error fetching notifications by type for recipient {}: {}", recipientId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{notificationId}/mark-read")
    @Operation(summary = "Mark notification as read", description = "Mark a specific notification as read")
    public ResponseEntity<MessageResponse> markNotificationAsRead(
            @Parameter(description = "Notification ID") @PathVariable Long notificationId) {
        try {
            notificationService.markAsRead(notificationId);
            return ResponseEntity.ok(new MessageResponse("Notification marked as read successfully"));
        } catch (RuntimeException e) {
            log.error("Error marking notification {} as read: {}", notificationId, e.getMessage());
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @PutMapping("/mark-read")
    @Operation(summary = "Mark multiple notifications as read", description = "Mark multiple notifications as read")
    public ResponseEntity<MessageResponse> markNotificationsAsRead(@Valid @PathVariable Long notificationId) {
        try {
            notificationService.markAsRead(notificationId);
            return ResponseEntity.ok(new MessageResponse("Notifications marked as read successfully"));
        } catch (RuntimeException e) {
            log.error("Error marking notifications as read: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @DeleteMapping("/cleanup/{daysOld}")
    @Operation(summary = "Cleanup old notifications", description = "Delete notifications older than specified days")
    public ResponseEntity<MessageResponse> cleanupOldNotifications(
            @Parameter(description = "Days old threshold") @PathVariable int daysOld) {
        try {
            notificationService.cleanupOldNotifications(daysOld);
            return ResponseEntity.ok(new MessageResponse("Old notifications cleaned up successfully"));
        } catch (Exception e) {
            log.error("Error cleaning up old notifications: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }
}
