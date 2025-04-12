package AlzAware.AlzAware_App.config;

import AlzAware.AlzAware_App.models.ERole;
import AlzAware.AlzAware_App.models.Role;
import AlzAware.AlzAware_App.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        // Initialize roles if they don't exist
        for (ERole role : ERole.values()) {
            if (!roleRepository.existsById(role.ordinal() + 1)) {
                Role newRole = new Role();
                newRole.setName(role);
                roleRepository.save(newRole);
                System.out.println("Created role: " + role.name());
            }
        }
    }
}