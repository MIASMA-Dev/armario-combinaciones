package com.armario.rest;

import com.armario.domain.AuthToken;
import com.armario.domain.User;
import com.armario.repository.AuthTokenRepository;
import com.armario.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.Map;
import java.util.UUID;

import tools.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final long LIMITE_USUARIOS_DEFAULT = 3L;

    private final UserRepository userRepository;
    private final AuthTokenRepository tokens;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Value("${armario.max-users:" + LIMITE_USUARIOS_DEFAULT + "}")
    private long maxUsuarios;

    public AuthController(UserRepository userRepository, AuthTokenRepository tokens,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tokens = tokens;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req, HttpSession session) {
        if (userRepository.findByEmail(req.email()).isPresent()) {
            return ResponseEntity.status(409).body(Map.of("error", "email ya registrado"));
        }
        if (userRepository.count() >= maxUsuarios) {
            return ResponseEntity.status(403).body(Map.of("error", "limite de usuarios alcanzado"));
        }
        User user = new User(req.nombre(), req.email(), passwordEncoder.encode(req.password()));
        userRepository.save(user);
        session.setAttribute("userId", user.getId());
        return ResponseEntity.status(201).body(respuesta(user));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req, HttpSession session) {
        return userRepository.findByEmail(req.email())
            .filter(u -> passwordEncoder.matches(req.password(), u.getPasswordHash()))
            .map(u -> {
                session.setAttribute("userId", u.getId());
                return ResponseEntity.ok(respuesta(u));
            })
            .orElse(ResponseEntity.status(401).body(Map.of("error", "credenciales invalidas")));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication auth) {
        if (auth == null || !(auth.getPrincipal() instanceof Long userId)) {
            return ResponseEntity.status(401).body(Map.of("error", "no autenticado"));
        }
        return userRepository.findById(userId)
            .<ResponseEntity<?>>map(u -> ResponseEntity.ok(Map.of(
                "id", u.getId(),
                "nombre", u.getNombre(),
                "email", u.getEmail()
            )))
            .orElse(ResponseEntity.status(401).body(Map.of("error", "no autenticado")));
    }

    @PostMapping("/logout")
    @Transactional
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String authorization,
                                    HttpSession session) {
        String token = extraerToken(authorization);
        if (token != null) {
            tokens.deleteByToken(token);
        }
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.noContent().build();
    }

    private Map<String, Object> respuesta(User user) {
        String token = UUID.randomUUID().toString();
        tokens.save(new AuthToken(token, user.getId()));
        return Map.of(
            "id", user.getId(),
            "nombre", user.getNombre(),
            "email", user.getEmail(),
            "token", token
        );
    }

    private String extraerToken(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) return null;
        return authorization.substring(7).trim();
    }

    public record RegisterRequest(String nombre, String email, String password) {}
    public record LoginRequest(String email, String password) {}
}
