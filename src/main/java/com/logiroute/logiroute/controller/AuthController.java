package com.logiroute.logiroute.controller;

import com.google.common.util.concurrent.RateLimiter;
import com.logiroute.logiroute.security.JwtUtil;
import com.logiroute.logiroute.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final RateLimiter rateLimiter;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> request) {
        if (!rateLimiter.tryAcquire()) {
            log.warn("Rate limit excedido para login");
            return ResponseEntity.status(429).body(Map.of("error", "Demasiadas peticiones. Intente más tarde."));
        }

        String email = request.get("email");
        String password = request.get("password");
        log.info("Intento de login para: {}", email);

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        final UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        final String token = jwtUtil.generateToken(userDetails);

        log.info("Login exitoso para: {}", email);
        return ResponseEntity.ok(Map.of("token", token));
    }
}
