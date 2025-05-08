package AlzAware.AlzAware_App.controllers;

import AlzAware.AlzAware_App.models.CaregiverPatient;
import AlzAware.AlzAware_App.models.User;
import AlzAware.AlzAware_App.payload.request.CaregiverPatientMatchRequest;
import AlzAware.AlzAware_App.payload.response.MessageResponse;
import AlzAware.AlzAware_App.repository.CaregiverPatientRepository;
import AlzAware.AlzAware_App.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/caregiver-patient")
public class CaregiverPatientController {

    @Autowired
    private CaregiverPatientRepository caregiverPatientRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/assign")
    public ResponseEntity<MessageResponse> assignCaregiverToPatient(@Valid @RequestBody CaregiverPatientMatchRequest caregiverPatientMatchRequest) {
        User caregiver = userRepository.findById(caregiverPatientMatchRequest.getCaregiverId())
                .orElseThrow(() -> new RuntimeException("Caregiver not found"));
        User patient = userRepository.findById(caregiverPatientMatchRequest.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        // Aynı eşleşme varsa tekrar eklemeyelim
        boolean exists = caregiverPatientRepository.existsByCaregiverAndPatient(caregiver, patient);
        if (exists) {
            return ResponseEntity
                    .status(409)
                    .body(new MessageResponse("Patient is already assigned to this caregiver."));
        }

        CaregiverPatient caregiverPatient = new CaregiverPatient();
        caregiverPatient.setCaregiver(caregiver);
        caregiverPatient.setPatient(patient);

        caregiverPatientRepository.save(caregiverPatient);

        return ResponseEntity.ok(new MessageResponse("Caregiver assigned to patient successfully"));
    }

    // CaregiverPatientController.java
    @GetMapping("/patients/{caregiverId}")
    public ResponseEntity<List<User>> getPatientsByCaregiver(@PathVariable Long caregiverId) {
        List<User> patients = caregiverPatientRepository.findPatientsByCaregiverId(caregiverId);
        return ResponseEntity.ok(patients);
    }





}
