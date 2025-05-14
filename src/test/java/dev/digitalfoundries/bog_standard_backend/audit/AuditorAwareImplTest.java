package dev.digitalfoundries.bog_standard_backend.audit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AuditorAwareImplTest {

    private final AuditorAwareImpl auditorAware = new AuditorAwareImpl();

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void whenNoAuthentication_thenReturnsEmpty() {
        SecurityContextHolder.clearContext();

        Optional<String> result = auditorAware.getCurrentAuditor();

        assertTrue(result.isEmpty(), "Expected empty Optional when no authentication present");
    }

    @Test
    void whenNotAuthenticated_thenReturnsEmpty() {
        var unauthenticated = new UsernamePasswordAuthenticationToken("user", "password");
        unauthenticated.setAuthenticated(false);

        SecurityContextHolder.getContext().setAuthentication(unauthenticated);

        Optional<String> result = auditorAware.getCurrentAuditor();

        assertTrue(result.isEmpty(), "Expected empty Optional when user is not authenticated");
    }

    @Test
    void whenSystemPrincipal_thenReturnsEmpty() {
        var auth = new UsernamePasswordAuthenticationToken(
                "system",
                "password",
                List.of(new SimpleGrantedAuthority("ROLE_SYSTEM"))
        );

        SecurityContextHolder.getContext().setAuthentication(auth);

        Optional<String> result = auditorAware.getCurrentAuditor();

        assertTrue(result.isEmpty(), "Expected empty Optional when principal is 'system'");
    }


    @Test
    void whenAuthenticated_thenReturnsUsername() {
        var auth = new UsernamePasswordAuthenticationToken(
                "john.doe",
                "password",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        SecurityContextHolder.getContext().setAuthentication(auth);

        Optional<String> result = auditorAware.getCurrentAuditor();

        assertTrue(result.isPresent(), "Expected Optional with username when authenticated");
        assertEquals("john.doe", result.get());
    }
}
