package AlzAware.AlzAware_App.controllers;

import AlzAware.AlzAware_App.models.CaregiverPatient;
import AlzAware.AlzAware_App.models.User;
import AlzAware.AlzAware_App.payload.request.CaregiverPatientMatchRequest;
import AlzAware.AlzAware_App.repository.CaregiverPatientRepository;
import AlzAware.AlzAware_App.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/caregiver-patient")
public class CaregiverPatientController {

    @Autowired
    private CaregiverPatientRepository caregiverPatientRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/assign")
    public ResponseEntity<String> assignCaregiverToPatient(@Valid @RequestBody CaregiverPatientMatchRequest caregiverPatientMatchRequest) {
        User caregiver = userRepository.findById(caregiverPatientMatchRequest.getCaregiverId())
                .orElseThrow(() -> new RuntimeException("Caregiver not found"));
        User patient = userRepository.findById(caregiverPatientMatchRequest.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        CaregiverPatient caregiverPatient = new CaregiverPatient();
        caregiverPatient.setCaregiver(caregiver);
        caregiverPatient.setPatient(patient);

        caregiverPatientRepository.save(caregiverPatient);

        return ResponseEntity.ok("Caregiver assigned to patient successfully");
    }
}
