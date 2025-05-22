package AlzAware.AlzAware_App.repository;

import AlzAware.AlzAware_App.models.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicineRepository extends JpaRepository<Medicine, Long> {
    List<Medicine> findByPatientId(Long patientId);
}
