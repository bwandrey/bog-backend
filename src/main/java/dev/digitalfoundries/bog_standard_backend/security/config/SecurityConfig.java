package dev.digitalfoundries.bog_standard_backend.security.config;

import dev.digitalfoundries.bog_standard_backend.security.entity.RoleEntity;
import dev.digitalfoundries.bog_standard_backend.security.entity.UserEntity;
import dev.digitalfoundries.bog_standard_backend.security.filter.JwtAuthFilter;
import dev.digitalfoundries.bog_standard_backend.security.repository.RoleRepository;
import dev.digitalfoundries.bog_standard_backend.security.repository.UserRepository;
import dev.digitalfoundries.bog_standard_backend.security.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;


    @Autowired
     public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.userDetailsService = customUserDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/authenticate",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/favicon.ico"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class); // JWT filter before auth filter

        return http.build();
    }
    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public CommandLineRunner createSuperUser(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            String adminUsername = "admin";
            String adminPassword = "admin123";
            String adminRoleName = "ADMIN";
            RoleEntity role = roleRepository.getByName(adminRoleName)
                    .orElseGet(() -> {
                        RoleEntity newRole = new RoleEntity();
                        newRole.setName(adminRoleName);
                        return roleRepository.save(newRole);
                    });


            if (userRepository.findByUsername(adminUsername).isEmpty()) {
                UserEntity superUser = new UserEntity();
                superUser.setUsername(adminUsername);
                superUser.setPassword(passwordEncoder.encode(adminPassword));
                superUser.setEnabled(true);
                Set<RoleEntity> roles = new HashSet<>();
                roles.add(role);
                superUser.setRoles(roles);

                userRepository.save(superUser);
                System.out.println("Super user created: " + adminUsername);
            }
        };
    }
}
