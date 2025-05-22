package AlzAware.AlzAware_App.security.services;

import AlzAware.AlzAware_App.models.Medicine;
import AlzAware.AlzAware_App.repository.MedicineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicineService {

    @Autowired
    private MedicineRepository medicineRepository;

    public Medicine createMedicine(Medicine medicine) {
        return medicineRepository.save(medicine);
    }

    public List<Medicine> getMedicinesByPatient(Long patientId) {
        return medicineRepository.findByPatientId(patientId);
    }

    public Medicine getMedicineById(Long id) {
        return medicineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medicine not found"));
    }

    public Medicine updateMedicine(Long id, Medicine medicine) {
        Medicine existingMedicine = medicineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medicine not found"));

        existingMedicine.setName(medicine.getName());
        existingMedicine.setWhichDayParts(medicine.getWhichDayParts());
        existingMedicine.setUsage(medicine.getUsage());
        existingMedicine.setCount(medicine.getCount());

        return medicineRepository.save(existingMedicine);
    }

    public void deleteMedicine(Long id) {
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medicine not found"));
        medicineRepository.delete(medicine);
    }
}
