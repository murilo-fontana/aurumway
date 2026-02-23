package dev.murilofontana.aurumway.billing.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers("/actuator/**").hasRole("ADMIN")
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/error").permitAll()

                        .requestMatchers(HttpMethod.GET, "/customers/**").hasAnyRole("ADMIN", "FINANCE", "VIEWER")
                        .requestMatchers(HttpMethod.POST, "/customers/**").hasAnyRole("ADMIN", "FINANCE")

                        .requestMatchers(HttpMethod.GET, "/invoices/**").hasAnyRole("ADMIN", "FINANCE", "VIEWER")
                        .requestMatchers(HttpMethod.POST, "/invoices").hasAnyRole("ADMIN", "FINANCE")
                        .requestMatchers(HttpMethod.POST, "/invoices/*/issue").hasAnyRole("ADMIN", "FINANCE")
                        .requestMatchers(HttpMethod.POST, "/invoices/*/send").hasAnyRole("ADMIN", "FINANCE")
                        .requestMatchers(HttpMethod.POST, "/invoices/*/pay").hasAnyRole("ADMIN", "FINANCE")
                        .requestMatchers(HttpMethod.POST, "/invoices/*/cancel").hasAnyRole("ADMIN", "FINANCE")
                        .requestMatchers(HttpMethod.POST, "/invoices/*/refund").hasAnyRole("ADMIN", "FINANCE")

                        .requestMatchers(HttpMethod.GET, "/ledger/**").hasAnyRole("ADMIN", "FINANCE", "VIEWER")
                        .requestMatchers(HttpMethod.GET, "/reports/**").hasAnyRole("ADMIN", "FINANCE", "VIEWER")

                        .requestMatchers(HttpMethod.GET, "/audit-events/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/statements/**").hasAnyRole("ADMIN", "FINANCE")
                        .requestMatchers(HttpMethod.GET, "/statements/**").hasAnyRole("ADMIN", "FINANCE", "VIEWER")

                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        var admin = User.builder().username("admin").password(encoder.encode("admin")).roles("ADMIN").build();
        var finance = User.builder().username("finance").password(encoder.encode("finance")).roles("FINANCE").build();
        var viewer = User.builder().username("viewer").password(encoder.encode("viewer")).roles("VIEWER").build();
        return new InMemoryUserDetailsManager(admin, finance, viewer);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
