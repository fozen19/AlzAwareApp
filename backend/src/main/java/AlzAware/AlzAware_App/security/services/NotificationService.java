package AlzAware.AlzAware_App.security.services;

import AlzAware.AlzAware_App.dto.NotificationDTO;
import AlzAware.AlzAware_App.models.Notification;
import AlzAware.AlzAware_App.models.NotificationType;
import AlzAware.AlzAware_App.models.User;
import AlzAware.AlzAware_App.payload.request.CreateNotificationRequest;
import AlzAware.AlzAware_App.repository.NotificationRepository;
import AlzAware.AlzAware_App.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    public NotificationDTO createNotification(CreateNotificationRequest request) {
        validateUser(request.getRecipientId(), "Recipient");
        validateUser(request.getPatientId(), "Patient");

        Notification notification = new Notification(
                request.getType(),
                request.getMessage(),
                request.getRecipientId(),
                request.getPatientId()
        );

        Notification savedNotification = notificationRepository.save(notification);
        log.info("Created notification with ID: {} for recipient: {}", savedNotification.getId(), request.getRecipientId());

        return convertToDTO(savedNotification);
    }

    public NotificationDTO createNotification(NotificationType type, String message, Long recipientId, Long patientId) {
        CreateNotificationRequest request = new CreateNotificationRequest(type, message, recipientId, patientId);
        return createNotification(request);
    }

    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotificationsByRecipient(Long recipientId) {
        List<Notification> notifications = notificationRepository.findByRecipientIdOrderByCreatedAtDesc(recipientId);
        return notifications.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<NotificationDTO> getUnreadNotificationsByRecipient(Long recipientId) {
        List<Notification> notifications = notificationRepository.findByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(recipientId);
        return notifications.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotificationsByTypeAndRecipient(Long recipientId, NotificationType type) {
        List<Notification> notifications = notificationRepository.findByRecipientIdAndTypeOrderByCreatedAtDesc(recipientId, type);
        return notifications.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public void markAsRead(Long notificationId) {
        Optional<Notification> notificationOpt = notificationRepository.findById(notificationId);
        if (notificationOpt.isPresent()) {
            Notification notification = notificationOpt.get();
            notification.markAsRead();
            notificationRepository.save(notification);
            log.info("Marked notification {} as read", notificationId);
        } else {
            throw new RuntimeException("Notification not found with ID: " + notificationId);
        }
    }

    public void markAsRead(List<Long> notificationIds) {
        LocalDateTime readAt = LocalDateTime.now();
        notificationRepository.markAsReadByIds(notificationIds, readAt);
        log.info("Marked {} notifications as read", notificationIds.size());
    }


    public void deleteNotification(Long notificationId) {
        if (notificationRepository.existsById(notificationId)) {
            notificationRepository.deleteById(notificationId);
            log.info("Deleted notification with ID: {}", notificationId);
        } else {
            throw new RuntimeException("Notification not found with ID: " + notificationId);
        }
    }

    public void cleanupOldNotifications(int daysOld) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysOld);
        notificationRepository.deleteOldNotifications(cutoffDate);
        log.info("Cleaned up notifications older than {} days", daysOld);
    }

    private NotificationDTO convertToDTO(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setType(notification.getType());
        dto.setMessage(notification.getMessage());
        dto.setRecipientId(notification.getRecipientId());
        dto.setPatientId(notification.getPatientId());
        dto.setIsRead(notification.getIsRead());
        dto.setCreatedAt(notification.getCreatedAt());
        dto.setReadAt(notification.getReadAt());
        dto.setTypeDisplayName(notification.getType().getDisplayName());

        Optional<User> patient = userRepository.findById(notification.getPatientId());
        if (patient.isPresent()) {
            dto.setPatientName(patient.get().getFirstName() + " " + patient.get().getLastName());
        }

        return dto;
    }

    private void validateUser(Long userId, String userType) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException(userType + " not found with ID: " + userId);
        }
    }
}
