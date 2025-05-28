package AlzAware.AlzAware_App.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "patient_locations")
@Data
public class PatientLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "patient_id")
    private User patient;

    private Double latitude;
    private Double longitude;

    private LocalDateTime timestamp;
}
