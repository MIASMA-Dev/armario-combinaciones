package com.armario.config;

import com.armario.domain.Prenda;
import com.armario.domain.Profile;
import com.armario.domain.TipoPrenda;
import com.armario.domain.TipoProfile;
import com.armario.domain.User;
import com.armario.repository.PrendaRepository;
import com.armario.repository.ProfileRepository;
import com.armario.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedTestUsers(UserRepository users, ProfileRepository profiles,
                                    PrendaRepository prendas, PasswordEncoder encoder) {
        return args -> {
            User ana = users.findByEmail("ana@armario.test").orElseGet(() -> {
                User u = new User("Ana", "ana@armario.test", encoder.encode("test1234"));
                return users.save(u);
            });
            User carlos = users.findByEmail("carlos@armario.test").orElseGet(() -> {
                User u = new User("Carlos", "carlos@armario.test", encoder.encode("test1234"));
                return users.save(u);
            });
            User abuela = users.findByEmail("abuela@armario.test").orElseGet(() -> {
                User u = new User("Abuela", "abuela@armario.test", encoder.encode("test1234"));
                return users.save(u);
            });

            if (profiles.findByOwner(ana).isEmpty()) {
                Profile mujer = new Profile("Ana", TipoProfile.MUJER, ana);
                Profile peque = new Profile("Peque", TipoProfile.NINO, ana);
                peque.getSharedWith().add(carlos);
                profiles.save(mujer);
                profiles.save(peque);
            }
            if (profiles.findByOwner(carlos).isEmpty()) {
                Profile hombre = new Profile("Carlos", TipoProfile.HOMBRE, carlos);
                Profile bebe = new Profile("Bebe", TipoProfile.BEBE, carlos);
                bebe.getSharedWith().add(ana);
                profiles.save(hombre);
                profiles.save(bebe);
            }
            if (profiles.findByOwner(abuela).isEmpty()) {
                profiles.save(new Profile("Abuela", TipoProfile.MUJER, abuela));
            }

            Profile mujerAna = profiles.findByOwner(ana).stream()
                .filter(p -> p.getTipo() == TipoProfile.MUJER)
                .findFirst()
                .orElse(null);
            if (mujerAna != null && prendas.findByProfileId(mujerAna.getId()).isEmpty()) {
                sembrarPrenda(prendas, mujerAna.getId(), "Camisa lino", "#f2efe6", "Todo el año", "M", TipoPrenda.SUPERIOR);
                sembrarPrenda(prendas, mujerAna.getId(), "Blusa seda", "#e8a0bf", "Primavera", "S", TipoPrenda.SUPERIOR);
                sembrarPrenda(prendas, mujerAna.getId(), "Vaqueros", "#3b5bdb", "Todo el año", "38", TipoPrenda.INFERIOR);
                sembrarPrenda(prendas, mujerAna.getId(), "Falda midi", "#2b2b2b", "Otoño", "M", TipoPrenda.INFERIOR);
                sembrarPrenda(prendas, mujerAna.getId(), "Zapatillas", "#f2efe6", "Todo el año", "39", TipoPrenda.ZAPATOS);
                sembrarPrenda(prendas, mujerAna.getId(), "Botines", "#6b4f3a", "Invierno", "39", TipoPrenda.ZAPATOS);
                Prenda trenca = sembrarPrenda(prendas, mujerAna.getId(), "Trenca", "#c9b18f", "Invierno", "M", TipoPrenda.ABRIGOS);
                trenca.setFechaCaptura(Instant.now().minus(200, ChronoUnit.DAYS));
                trenca.setUltimoUso(Instant.now().minus(200, ChronoUnit.DAYS));
                prendas.save(trenca);
                sembrarPrenda(prendas, mujerAna.getId(), "Pañuelo", "#e8a0bf", "Primavera", "Única", TipoPrenda.COMPLEMENTOS);
            }
        };
    }

    private Prenda sembrarPrenda(PrendaRepository prendas, Long profileId, String nombre, String color,
                                 String temporada, String talla, TipoPrenda tipo) {
        Prenda p = new Prenda(profileId, nombre, tipo);
        p.setColor(color);
        p.setTemporada(temporada);
        p.setTalla(talla);
        return prendas.save(p);
    }
}