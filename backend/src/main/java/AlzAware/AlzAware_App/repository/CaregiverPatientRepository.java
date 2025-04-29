package AlzAware.AlzAware_App.repository;

import AlzAware.AlzAware_App.models.CaregiverPatient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CaregiverPatientRepository extends JpaRepository<CaregiverPatient, Long> {
}
