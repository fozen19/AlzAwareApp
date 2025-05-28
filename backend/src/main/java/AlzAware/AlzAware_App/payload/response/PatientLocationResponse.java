package AlzAware.AlzAware_App.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PatientLocationResponse {
    private Long patientId;
    private Double latitude;
    private Double longitude;
    private String firstName;
    private String lastName;
    private String timestamp;
}
