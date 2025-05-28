package AlzAware.AlzAware_App.repository;

import AlzAware.AlzAware_App.models.PatientLocation;
import AlzAware.AlzAware_App.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientLocationRepository extends JpaRepository<PatientLocation, Long> {
    Optional<PatientLocation> findTopByPatientOrderByTimestampDesc(User patient);
}
