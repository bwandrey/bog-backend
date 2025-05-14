package dev.digitalfoundries.bog_standard_backend.security.runner;

import dev.digitalfoundries.bog_standard_backend.security.entity.RoleEntity;
import dev.digitalfoundries.bog_standard_backend.security.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class RoleInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        if (!roleRepository.existsByName("ROLE_ADMIN")) {
            RoleEntity admin = new RoleEntity();
            admin.setName("ROLE_ADMIN");
            roleRepository.save(admin);
        }
        if (!roleRepository.existsByName("ROLE_USER")) {
            RoleEntity user = new RoleEntity();
            user.setName("ROLE_USER");
            roleRepository.save(user);
        }
    }
}