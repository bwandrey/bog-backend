package dev.digitalfoundries.bog_standard_backend.security.service;

import dev.digitalfoundries.bog_standard_backend.security.entity.RoleEntity;
import dev.digitalfoundries.bog_standard_backend.security.entity.UserEntity;
import dev.digitalfoundries.bog_standard_backend.security.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

    private UserRepository userRepository;
    private CustomUserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userDetailsService = new CustomUserDetailsService(userRepository);
    }

    @Test
    void loadUserByUsername_userExists_returnsUserDetails() {
        // Given
        RoleEntity role = new RoleEntity();
        role.setName("ROLE_USER");

        UserEntity user = new UserEntity();
        user.setUsername("john");
        user.setPassword("password");
        user.setEnabled(true);
        user.setRoles(Set.of(role));

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername("john");

        // Then
        assertEquals("john", userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
        assertTrue(userDetails.isEnabled());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void loadUserByUsername_userNotFound_throwsException() {
        when(userRepository.findByUsername("doesnotexist")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("doesnotexist");
        });
    }
}
