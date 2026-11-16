package com.armario.rest;

import com.armario.domain.Profile;
import com.armario.domain.TipoProfile;
import com.armario.domain.User;
import com.armario.repository.ProfileRepository;
import com.armario.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/profiles")
public class ProfileController {

    private final ProfileRepository profiles;
    private final UserRepository users;

    public ProfileController(ProfileRepository profiles, UserRepository users) {
        this.profiles = profiles;
        this.users = users;
    }

    @GetMapping
    public List<ProfileDTO> list(Authentication auth) {
        User user = currentUser(auth);
        Map<Long, Profile> result = new LinkedHashMap<>();
        profiles.findByOwner(user).forEach(p -> result.put(p.getId(), p));
        profiles.findBySharedWithContaining(user).forEach(p -> result.put(p.getId(), p));
        return result.values().stream().map(this::toDto).toList();
    }

    @PostMapping
    public ResponseEntity<?> create(Authentication auth, @RequestBody CreateProfileRequest req) {
        User user = currentUser(auth);
        Profile profile = new Profile(req.nombre(), TipoProfile.valueOf(req.tipo()), user);
        profiles.save(profile);
        return ResponseEntity.status(201).body(toDto(profile));
    }

    @PostMapping("/{id}/share")
    public ResponseEntity<?> share(Authentication auth, @PathVariable Long id, @RequestBody ShareRequest req) {
        User user = currentUser(auth);
        Profile profile = profiles.findById(id).orElse(null);
        if (profile == null) return ResponseEntity.notFound().build();
        if (!profile.getOwner().getId().equals(user.getId())) {
            return ResponseEntity.status(403).body(Map.of("error", "solo el propietario puede compartir"));
        }
        User target = users.findByEmail(req.email()).orElse(null);
        if (target == null) return ResponseEntity.status(404).body(Map.of("error", "usuario no encontrado"));
        if (!profile.getSharedWith().contains(target)) {
            profile.getSharedWith().add(target);
            profiles.save(profile);
        }
        return ResponseEntity.ok(toDto(profile));
    }

    private User currentUser(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return users.findById(userId).orElseThrow(() -> new IllegalStateException("usuario no encontrado"));
    }

    private ProfileDTO toDto(Profile p) {
        return new ProfileDTO(
            p.getId(),
            p.getNombre(),
            p.getTipo().name(),
            p.getOwner().getId(),
            p.getSharedWith().stream().map(User::getId).toList()
        );
    }

    public record ProfileDTO(Long id, String nombre, String tipo, Long ownerId, List<Long> sharedWith) {}
    public record CreateProfileRequest(String nombre, String tipo) {}
    public record ShareRequest(String email) {}
}