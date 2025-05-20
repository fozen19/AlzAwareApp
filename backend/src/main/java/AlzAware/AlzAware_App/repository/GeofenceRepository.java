package AlzAware.AlzAware_App.repository;

import AlzAware.AlzAware_App.models.Geofence;
import AlzAware.AlzAware_App.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GeofenceRepository extends JpaRepository<Geofence, Long> {
    Optional<Geofence> findByPatient(User patient);
    Optional<Geofence> findByPatientId(Long patientId);
}
