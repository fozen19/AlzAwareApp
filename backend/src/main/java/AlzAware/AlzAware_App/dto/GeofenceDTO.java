package AlzAware.AlzAware_App.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GeofenceDTO {
    private Long id;
    private Double latitude;
    private Double longitude;
    private Double radius;
    private String name;
    private Long patientId;
}


