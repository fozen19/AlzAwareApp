package AlzAware.AlzAware_App.controllers;

import AlzAware.AlzAware_App.models.CaregiverPatient;
import AlzAware.AlzAware_App.models.User;
import AlzAware.AlzAware_App.repository.CaregiverPatientRepository;
import AlzAware.AlzAware_App.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/caregiver-patient")
public class CaregiverPatientController {

    @Autowired
    private CaregiverPatientRepository caregiverPatientRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/assign")
    public ResponseEntity<String> assignCaregiverToPatient(@RequestParam Long caregiverId, @RequestParam Long patientId) {
        User caregiver = userRepository.findById(caregiverId)
                .orElseThrow(() -> new RuntimeException("Caregiver not found"));
        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        CaregiverPatient caregiverPatient = new CaregiverPatient();
        caregiverPatient.setCaregiver(caregiver);
        caregiverPatient.setPatient(patient);

        caregiverPatientRepository.save(caregiverPatient);

        return ResponseEntity.ok("Caregiver assigned to patient successfully");
    }
}
