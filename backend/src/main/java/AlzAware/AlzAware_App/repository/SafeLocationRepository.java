package AlzAware.AlzAware_App.repository;

import AlzAware.AlzAware_App.models.SafeLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SafeLocationRepository extends JpaRepository<SafeLocation, Long> {
    List<SafeLocation> findByPatientId(Long patientId);
}
