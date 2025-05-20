package AlzAware.AlzAware_App.controllers;

import AlzAware.AlzAware_App.dto.GeofenceDTO;
import AlzAware.AlzAware_App.mapper.TransformService;
import AlzAware.AlzAware_App.models.Geofence;
import AlzAware.AlzAware_App.models.User;
import AlzAware.AlzAware_App.payload.request.GeofenceRequest;
import AlzAware.AlzAware_App.payload.response.MessageResponse;
import AlzAware.AlzAware_App.repository.GeofenceRepository;
import AlzAware.AlzAware_App.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/geofence")
@Tag(name = "Geofence", description = "API for geofence operations")
public class GeofenceController {

    @Autowired
    private GeofenceRepository geofenceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransformService<Geofence, GeofenceDTO> geofenceTransformService;

    @PostMapping("/save")
    @Operation(summary = "Save or update geofence", description = "Save a new geofence or update an existing one for a patient")
    public ResponseEntity<?> saveGeofence(@Valid @RequestBody GeofenceRequest geofenceRequest) {
        // Get the patient
        User patient = userRepository.findById(geofenceRequest.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        // Check if geofence already exists for this patient
        Optional<Geofence> existingGeofence = geofenceRepository.findByPatient(patient);

        Geofence geofence;
        if (existingGeofence.isPresent()) {
            // Update existing geofence
            geofence = existingGeofence.get();
        } else {
            // Create new geofence
            geofence = new Geofence();
            geofence.setPatient(patient);
        }

        // Set/update geofence properties
        geofence.setLatitude(geofenceRequest.getLatitude());
        geofence.setLongitude(geofenceRequest.getLongitude());
        geofence.setRadius(geofenceRequest.getRadius());
        geofence.setName(geofenceRequest.getName());

        // Save the geofence
        geofenceRepository.save(geofence);

        String message = existingGeofence.isPresent()
                ? "Geofence updated successfully"
                : "Geofence created successfully";

        return ResponseEntity.ok(new MessageResponse(message));
    }

    @GetMapping(value = "/{patientId}")
    public ResponseEntity<?> getGeofence(@PathVariable Long patientId) {
        // Check if patient exists
        if (!userRepository.existsById(patientId)) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Patient not found"));
        }

        // Get the geofence for this patient
        Optional<Geofence> geofence = geofenceRepository.findByPatientId(patientId);
        if (geofence.isPresent()) {
            GeofenceDTO geofenceDTO = geofenceTransformService.toDto(geofence.get());
            return ResponseEntity.ok(geofenceDTO);
        } else {
            return ResponseEntity
                    .status(404)
                    .body(new MessageResponse("No geofence found for this patient"));
        }
    }
}