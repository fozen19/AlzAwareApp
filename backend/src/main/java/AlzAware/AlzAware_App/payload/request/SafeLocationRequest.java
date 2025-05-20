package AlzAware.AlzAware_App.payload.request;

import lombok.Data;

@Data
public class SafeLocationRequest {
    private Long patientId;
    private String locationName;
    private double latitude;
    private double longitude;
}
