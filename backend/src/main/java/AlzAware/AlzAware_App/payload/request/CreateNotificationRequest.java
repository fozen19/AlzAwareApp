package AlzAware.AlzAware_App.payload.request;

import AlzAware.AlzAware_App.models.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateNotificationRequest {
    @NotNull(message = "Notification type is required")
    private NotificationType type;

    @NotBlank(message = "Message is required")
    private String message;

    @NotNull(message = "Recipient ID is required")
    private Long recipientId;

    @NotNull(message = "Patient ID is required")
    private Long patientId;
}
