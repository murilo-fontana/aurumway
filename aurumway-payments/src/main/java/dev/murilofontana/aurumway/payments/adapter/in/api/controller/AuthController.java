package dev.murilofontana.aurumway.payments.adapter.in.api.controller;

import dev.murilofontana.aurumway.payments.config.JwtService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final java.util.Set<String> ALLOWED_TENANTS = java.util.Set.of(
            "acme-corp", "globex-inc", "default"
    );

    private final AuthenticationManager authManager;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authManager, JwtService jwtService) {
        this.authManager = authManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest request) {
        if (!ALLOWED_TENANTS.contains(request.tenantId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "Invalid tenant: " + request.tenantId()));
        }

        var auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        var roles = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(a -> a.startsWith("ROLE_") ? a.substring(5) : a)
                .toList();

        var token = jwtService.generateToken(auth.getName(), roles, request.tenantId());
        return ResponseEntity.ok(new LoginResponse(token, auth.getName(), roles, request.tenantId()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ProblemDetail handleBadCredentials(BadCredentialsException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "Invalid username or password");
    }

    public record LoginRequest(@NotBlank String username, @NotBlank String password, @NotBlank String tenantId) {}

    public record LoginResponse(String token, String username, java.util.List<String> roles, String tenantId) {}
}
