package AlzAware.AlzAware_App.controllers;

import AlzAware.AlzAware_App.models.User;
import AlzAware.AlzAware_App.payload.response.MessageResponse;
import AlzAware.AlzAware_App.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/patients")
@Tag(name = "Patient", description = "Patient profile operations")
public class PatientProfileController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/{id}/profile")
    @Operation(summary = "Get patient profile", description = "Returns profile information of a patient")
    public ResponseEntity<?> getPatientProfile(@PathVariable Long id) {
        return userRepository.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity
                        .status(404)
                        .body(new MessageResponse("Patient not found")));
    }



    @PutMapping("/{id}/profile")
    @Operation(summary = "Update patient profile", description = "Updates profile details of a patient")
    public ResponseEntity<?> updatePatientProfile(@PathVariable Long id, @Valid @RequestBody User updatedUser) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    existingUser.setFirstName(updatedUser.getFirstName());
                    existingUser.setLastName(updatedUser.getLastName());
                    existingUser.setUsername(updatedUser.getUsername());
                    existingUser.setEmail(updatedUser.getEmail());
                    existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
                    userRepository.save(existingUser);
                    return ResponseEntity.ok(new MessageResponse("Patient profile updated successfully"));
                })
                .orElseGet(() -> ResponseEntity
                        .status(404)
                        .body(new MessageResponse("Patient not found")));
    }
}
