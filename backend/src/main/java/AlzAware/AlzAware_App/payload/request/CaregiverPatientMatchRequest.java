package AlzAware.AlzAware_App.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CaregiverPatientMatchRequest {
    @NotBlank
    private Long caregiverId;

    @NotBlank
    private Long patientId;
}
