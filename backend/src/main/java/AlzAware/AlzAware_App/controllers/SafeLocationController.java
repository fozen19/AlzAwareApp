package AlzAware.AlzAware_App.controllers;

import AlzAware.AlzAware_App.models.SafeLocation;
import AlzAware.AlzAware_App.models.User;
import AlzAware.AlzAware_App.payload.request.SafeLocationRequest;
import AlzAware.AlzAware_App.payload.response.MessageResponse;
import AlzAware.AlzAware_App.repository.SafeLocationRepository;
import AlzAware.AlzAware_App.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/safe-location")
public class SafeLocationController {

    @Autowired
    private SafeLocationRepository safeLocationRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/add")
    public ResponseEntity<MessageResponse> saveSafeLocation(@Valid @RequestBody SafeLocationRequest request) {
        User patient = userRepository.findById(request.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        SafeLocation location = new SafeLocation();
        location.setPatientId(patient.getId());
        location.setLocationName(request.getLocationName());
        location.setLatitude(request.getLatitude());
        location.setLongitude(request.getLongitude());

        safeLocationRepository.save(location);

        return ResponseEntity.ok(new MessageResponse("Konum başarıyla kaydedildi."));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<SafeLocation>> getSafeLocationsByPatient(@PathVariable Long patientId) {
        List<SafeLocation> safeLocations = safeLocationRepository.findByPatientId(patientId);
        return ResponseEntity.ok(safeLocations);
    }
}
