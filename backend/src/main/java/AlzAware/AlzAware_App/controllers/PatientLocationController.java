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
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/patients")
public class PatientLocationController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PatientLocationRepository locationRepository;

    @PostMapping("/{id}/location")
    public ResponseEntity<?> updatePatientLocation(@PathVariable Long id, @RequestBody LocationRequest request) {
        try {
            Optional<User> optionalPatient = userRepository.findById(id);
            if (optionalPatient.isEmpty()) {
                return ResponseEntity.status(404).body(new MessageResponse("Patient not found"));
            }

            User patient = optionalPatient.get();

            PatientLocation location = new PatientLocation();
            location.setPatient(patient);
            location.setLatitude(request.getLatitude());
            location.setLongitude(request.getLongitude());
            location.setTimestamp(LocalDateTime.now());

            locationRepository.save(location);

            return ResponseEntity.ok(new MessageResponse("Location saved successfully."));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new MessageResponse("Failed to save location: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}/location")
    public ResponseEntity<?> getLatestLocation(@PathVariable Long id) {
        try {
            Optional<User> optionalPatient = userRepository.findById(id);
            if (optionalPatient.isEmpty()) {
                return ResponseEntity.status(404).body(new MessageResponse("Patient not found"));
            }

            User patient = optionalPatient.get();

            Optional<PatientLocation> optionalLocation = locationRepository.findTopByPatientOrderByTimestampDesc(patient);
            if (optionalLocation.isEmpty()) {
                return ResponseEntity.status(404).body(new MessageResponse("No location data found"));
            }

            PatientLocation location = optionalLocation.get();

            PatientLocationResponse response = new PatientLocationResponse(
                    patient.getId(),
                    location.getLatitude(),
                    location.getLongitude(),
                    patient.getFirstName(),
                    patient.getLastName(),
                    location.getTimestamp().toString()
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new MessageResponse("Failed to retrieve location: " + e.getMessage()));
        }
    }
}
