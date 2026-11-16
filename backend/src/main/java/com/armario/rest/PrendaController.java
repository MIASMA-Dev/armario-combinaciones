package com.armario.rest;

import com.armario.domain.*;
import com.armario.repository.PrendaRepository;
import com.armario.repository.ProfileRepository;
import com.armario.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/prendas")
public class PrendaController {

    private final PrendaRepository prendas;
    private final ProfileRepository profiles;
    private final UserRepository users;

    public PrendaController(PrendaRepository prendas, ProfileRepository profiles, UserRepository users) {
        this.prendas = prendas;
        this.profiles = profiles;
        this.users = users;
    }

    @GetMapping
    public ResponseEntity<?> list(Authentication auth, @RequestParam Long profileId) {
        User user = currentUser(auth);
        if (!hasAccess(user, profileId)) return ResponseEntity.status(403).build();
        return ResponseEntity.ok(prendas.findByProfileId(profileId));
    }

    @PostMapping
    public ResponseEntity<?> create(Authentication auth, @RequestBody PrendaRequest req) {
        User user = currentUser(auth);
        if (!hasAccess(user, req.profileId())) return ResponseEntity.status(403).build();

        Prenda prenda = new Prenda(req.profileId(), req.nombre(), TipoPrenda.valueOf(req.tipoPrenda()));
        prenda.setColor(req.color());
        prenda.setTemporada(req.temporada());
        prenda.setTalla(req.talla());
        prenda.setTejido(req.tejido());
        prenda.setImagenUrl(req.imagenUrl());
        if (req.estado() != null) prenda.setEstado(EstadoPrenda.valueOf(req.estado()));
        prendas.save(prenda);
        return ResponseEntity.status(201).body(prenda);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(Authentication auth, @PathVariable Long id, @RequestBody PrendaRequest req) {
        User user = currentUser(auth);
        Prenda prenda = prendas.findById(id).orElse(null);
        if (prenda == null) return ResponseEntity.notFound().build();
        if (!hasAccess(user, prenda.getProfileId())) return ResponseEntity.status(403).build();

        try {
            if (req.nombre() != null && !req.nombre().isBlank()) prenda.setNombre(req.nombre().trim());
            if (req.color() != null) prenda.setColor(req.color());
            if (req.temporada() != null) prenda.setTemporada(req.temporada());
            if (req.talla() != null) prenda.setTalla(req.talla());
            if (req.tipoPrenda() != null) prenda.setTipoPrenda(TipoPrenda.valueOf(req.tipoPrenda()));
            if (req.estado() != null) prenda.setEstado(EstadoPrenda.valueOf(req.estado()));
            if (req.imagenUrl() != null) prenda.setImagenUrl(req.imagenUrl());
            if (req.tejido() != null) prenda.setTejido(req.tejido());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "tipo o estado no valido"));
        }

        prendas.save(prenda);
        return ResponseEntity.ok(prenda);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(Authentication auth, @PathVariable Long id) {
        User user = currentUser(auth);
        Prenda prenda = prendas.findById(id).orElse(null);
        if (prenda == null) return ResponseEntity.notFound().build();
        if (!hasAccess(user, prenda.getProfileId())) return ResponseEntity.status(403).build();
        prendas.delete(prenda);
        return ResponseEntity.noContent().build();
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

    public record PrendaRequest(
        Long profileId,
        String nombre,
        String color,
        String temporada,
        String talla,
        String tipoPrenda,
        String estado,
        String imagenUrl,
        String tejido
    ) {}
}