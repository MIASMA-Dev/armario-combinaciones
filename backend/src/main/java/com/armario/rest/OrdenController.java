package com.armario.rest;

import com.armario.domain.Profile;
import com.armario.domain.User;
import com.armario.repository.PrendaRepository;
import com.armario.repository.ProfileRepository;
import com.armario.repository.UserRepository;
import com.armario.service.OrdenService;
import com.armario.service.OrdenService.OrdenDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ordenes")
public class OrdenController {

    private final PrendaRepository prendas;
    private final ProfileRepository profiles;
    private final UserRepository users;
    private final OrdenService service;

    public OrdenController(PrendaRepository prendas, ProfileRepository profiles,
                           UserRepository users, OrdenService service) {
        this.prendas = prendas;
        this.profiles = profiles;
        this.users = users;
        this.service = service;
    }

    @GetMapping({"", "/generar"})
    public ResponseEntity<?> generar(Authentication auth, @RequestParam Long profileId) {
        User user = currentUser(auth);
        if (!hasAccess(user, profileId)) return ResponseEntity.status(403).build();
        List<OrdenDTO> ordenes = service.generar(profileId, prendas.findByProfileId(profileId));
        return ResponseEntity.ok(ordenes);
    }

    private boolean hasAccess(User user, Long profileId) {
        Profile profile = profiles.findById(profileId).orElse(null);
        if (profile == null) return false;
        if (profile.getOwner().getId().equals(user.getId())) return true;
        return profile.getSharedWith().stream().anyMatch(u -> u.getId().equals(user.getId()));
    }

    private User currentUser(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return users.findById(userId).orElseThrow(() -> new IllegalStateException("usuario no encontrado"));
    }
}
