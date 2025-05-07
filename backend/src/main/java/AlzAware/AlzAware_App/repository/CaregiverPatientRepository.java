package AlzAware.AlzAware_App.repository;

import AlzAware.AlzAware_App.models.CaregiverPatient;
import AlzAware.AlzAware_App.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CaregiverPatientRepository extends JpaRepository<CaregiverPatient, Long> {
    boolean existsByCaregiverAndPatient(User caregiver, User patient);
    List<CaregiverPatient> findByCaregiver(User caregiver);
    @Query("SELECT u FROM User u WHERE u.id IN (SELECT cp.patient.id FROM CaregiverPatient cp WHERE cp.caregiver.id = :caregiverId)")
    List<User> findPatientsByCaregiverId(@Param("caregiverId") Long caregiverId);

}
