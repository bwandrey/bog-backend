package dev.digitalfoundries.bog_standard_backend.security.config;

import dev.digitalfoundries.bog_standard_backend.security.entity.RoleEntity;
import dev.digitalfoundries.bog_standard_backend.security.entity.UserEntity;
import dev.digitalfoundries.bog_standard_backend.security.repository.RoleRepository;
import dev.digitalfoundries.bog_standard_backend.security.repository.UserRepository;
import dev.digitalfoundries.bog_standard_backend.security.service.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.*;

class SecurityConfigTest {

    @Mock private CustomUserDetailsService userDetailsService;
    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        securityConfig = new SecurityConfig(userDetailsService);
    }

    @Test
    void commandLineRunner_shouldCreateRoleAndSuperUser_whenNotPresent() throws Exception {
        // Arrange
        String encodedPassword = "encoded_pass";
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setName("ADMIN");

        when(userRepository.findByUsername("admin")).thenReturn(Optional.empty());
        when(roleRepository.getByName("ADMIN")).thenReturn(Optional.empty());
        when(roleRepository.save(any(RoleEntity.class))).thenReturn(roleEntity);
        when(passwordEncoder.encode("admin123")).thenReturn(encodedPassword);

        // Act
        CommandLineRunner runner = securityConfig.createSuperUser(userRepository, roleRepository, passwordEncoder);
        runner.run();  // execute logic

        // Assert
        verify(roleRepository).save(any(RoleEntity.class));
        verify(userRepository).save(argThat(user ->
                user.getUsername().equals("admin") &&
                        user.getPassword().equals(encodedPassword) &&
                        user.getRoles().stream().anyMatch(r -> r.getName().equals("ADMIN"))
        ));
    }

    @Test
    void commandLineRunner_shouldSkipUserCreation_whenUserExists() throws Exception {
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(new UserEntity()));

        CommandLineRunner runner = securityConfig.createSuperUser(userRepository, roleRepository, passwordEncoder);
        runner.run();

        verify(userRepository, never()).save(any());
    }
}
