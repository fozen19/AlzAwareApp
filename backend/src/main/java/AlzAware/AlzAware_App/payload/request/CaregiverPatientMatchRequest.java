package AlzAware.AlzAware_App.payload.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CaregiverPatientMatchRequest {
    @NotNull
    private Long caregiverId;

    @NotNull
    private Long patientId;
}
