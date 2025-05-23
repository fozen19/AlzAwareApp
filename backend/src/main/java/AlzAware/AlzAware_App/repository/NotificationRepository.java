package AlzAware.AlzAware_App.repository;


import AlzAware.AlzAware_App.models.Notification;
import AlzAware.AlzAware_App.models.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByRecipientIdOrderByCreatedAtDesc(Long recipientId);

    List<Notification> findByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(Long recipientId);

    List<Notification> findByPatientIdOrderByCreatedAtDesc(Long patientId);

    // Find notifications by type and recipient
    List<Notification> findByRecipientIdAndTypeOrderByCreatedAtDesc(Long recipientId, NotificationType type);

    // Find notifications by patient and type
    List<Notification> findByPatientIdAndTypeOrderByCreatedAtDesc(Long patientId, NotificationType type);

    // Mark notifications as read by IDs
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true, n.readAt = :readAt WHERE n.id IN :ids")
    void markAsReadByIds(@Param("ids") List<Long> ids, @Param("readAt") LocalDateTime readAt);

    // Find notifications between dates
    List<Notification> findByRecipientIdAndCreatedAtBetweenOrderByCreatedAtDesc(
            Long recipientId, LocalDateTime startDate, LocalDateTime endDate);

    // Delete old notifications (for cleanup)
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.createdAt < :cutoffDate")
    void deleteOldNotifications(@Param("cutoffDate") LocalDateTime cutoffDate);

    // Find notifications by recipient and read status with pagination support
    @Query("SELECT n FROM Notification n WHERE n.recipientId = :recipientId AND (:isRead IS NULL OR n.isRead = :isRead) ORDER BY n.createdAt DESC")
    List<Notification> findByRecipientIdAndReadStatus(@Param("recipientId") Long recipientId, @Param("isRead") Boolean isRead);
}
