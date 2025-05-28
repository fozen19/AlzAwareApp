package AlzAware.AlzAware_App.controllers;

import AlzAware.AlzAware_App.models.PatientLocation;
import AlzAware.AlzAware_App.models.User;
import AlzAware.AlzAware_App.payload.request.LocationRequest;
import AlzAware.AlzAware_App.payload.response.MessageResponse;
import AlzAware.AlzAware_App.payload.response.PatientLocationResponse;
import AlzAware.AlzAware_App.repository.PatientLocationRepository;
import AlzAware.AlzAware_App.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/patients")
public class PatientLocationController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PatientLocationRepository locationRepository;

    @PostMapping("/{id}/location")
    public ResponseEntity<MessageResponse> updatePatientLocation(@PathVariable Long id, @RequestBody LocationRequest request) {
        User patient = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        PatientLocation location = new PatientLocation();
        location.setPatient(patient);
        location.setLatitude(request.getLatitude());
        location.setLongitude(request.getLongitude());
        location.setTimestamp(LocalDateTime.now());

        locationRepository.save(location);

        return ResponseEntity.ok(new MessageResponse("Location saved successfully."));
    }

    @GetMapping("/{id}/location")
    public ResponseEntity<PatientLocationResponse> getLatestLocation(@PathVariable Long id) {
        User patient = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        PatientLocation location = locationRepository.findTopByPatientOrderByTimestampDesc(patient)
                .orElseThrow(() -> new RuntimeException("No location data found"));

        PatientLocationResponse response = new PatientLocationResponse(
                patient.getId(),
                location.getLatitude(),
                location.getLongitude(),
                patient.getFirstName(),
                patient.getLastName(),
                location.getTimestamp().toString()
        );

        return ResponseEntity.ok(response);
    }
}
