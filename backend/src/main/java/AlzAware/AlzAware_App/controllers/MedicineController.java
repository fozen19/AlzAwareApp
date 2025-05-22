package AlzAware.AlzAware_App.controllers;

import AlzAware.AlzAware_App.models.Medicine;
import AlzAware.AlzAware_App.security.services.MedicineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medicines")
public class MedicineController {

    @Autowired
    private MedicineService medicineService;

    @PostMapping("/create")
    public ResponseEntity<Medicine> createMedicine(@RequestBody Medicine medicine) {
        Medicine createdMedicine = medicineService.createMedicine(medicine);
        return new ResponseEntity<>(createdMedicine, HttpStatus.CREATED);
    }

    @GetMapping("/getMedicines/{patientId}")
    public ResponseEntity<List<Medicine>> getMedicinesByPatient(@PathVariable Long patientId) {
        List<Medicine> medicines = medicineService.getMedicinesByPatient(patientId);
        return new ResponseEntity<>(medicines, HttpStatus.OK);
    }

    @GetMapping("/getAMedicine/{id}")
    public ResponseEntity<Medicine> getMedicineById(@PathVariable Long id) {
        Medicine medicine = medicineService.getMedicineById(id);
        return new ResponseEntity<>(medicine, HttpStatus.OK);
    }

    @PutMapping("/updateMedicine/{id}")
    public ResponseEntity<Medicine> updateMedicine(@PathVariable Long id, @RequestBody Medicine medicine) {
        Medicine updatedMedicine = medicineService.updateMedicine(id, medicine);
        return new ResponseEntity<>(updatedMedicine, HttpStatus.OK);
    }

    @DeleteMapping("/deleteMedicine/{id}")
    public ResponseEntity<Void> deleteMedicine(@PathVariable Long id) {
        medicineService.deleteMedicine(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
