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
@RequestMapping("/api/caregivers")
@Tag(name = "Caregiver", description = "Caregiver profil işlemleri")
public class CaregiverController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/{id}")
    @Operation(summary = "Get caregiver profile", description = "Get profile details for a caregiver")
    public ResponseEntity<?> getCaregiverProfile(@PathVariable Long id) {
        return userRepository.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity
                        .status(404)
                        .body(new MessageResponse("Caregiver not found")));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update caregiver profile", description = "Update profile details for a caregiver")
    public ResponseEntity<?> updateCaregiverProfile(@PathVariable Long id, @Valid @RequestBody User updatedUser) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    existingUser.setFirstName(updatedUser.getFirstName());
                    existingUser.setLastName(updatedUser.getLastName());
                    existingUser.setUsername(updatedUser.getUsername());
                    existingUser.setEmail(updatedUser.getEmail());
                    existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
                    userRepository.save(existingUser);
                    return ResponseEntity.ok(new MessageResponse("Profile updated successfully"));
                })
                .orElseGet(() -> ResponseEntity
                        .status(404)
                        .body(new MessageResponse("Caregiver not found")));
    }

}

