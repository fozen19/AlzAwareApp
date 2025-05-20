package AlzAware.AlzAware_App.payload.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GeofenceRequest {
    @NotNull
    private Long patientId;

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;

    @NotNull
    private Double radius;

    private String name;
}
