package AlzAware.AlzAware_App.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "geofences")
@Data
@NoArgsConstructor
public class Geofence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The center coordinates of the geofence
    private Double latitude;
    private Double longitude;

    // The radius of the geofence in meters
    private Double radius;

    // Optional name/description
    private String name;

    // One-to-one relationship with Patient (User)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false, unique = true)
    private User patient;
}
