package com.armario.service;

import com.armario.domain.EstadoPrenda;
import com.armario.domain.Prenda;
import com.armario.domain.TipoPrenda;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class OrdenService {

    public record OrdenDTO(
        String id,
        Long profileId,
        Long prendaId,
        String nombre,
        String categoria,
        String tipoAccion,
        String prioridad,
        String motivo,
        Instant fechaReferencia,
        long diasSinUso
    ) {}

    @Value("${armario.ordenes.dias-inactividad:180}")
    private long diasInactividad = 180;

    @Value("${armario.ordenes.min-disponibles:2}")
    private int minDisponibles = 2;

    public List<OrdenDTO> generar(Long profileId, List<Prenda> prendas) {
        Instant ahora = Instant.now();
        List<OrdenDTO> ordenes = new ArrayList<>();

        for (Prenda p : prendas) {
            Instant referencia = p.getUltimoUso() != null ? p.getUltimoUso() : p.getFechaCaptura();
            long dias = ChronoUnit.DAYS.between(referencia, ahora);
            if (dias >= diasInactividad) {
                ordenes.add(new OrdenDTO(
                    "inactividad-" + p.getId(),
                    profileId,
                    p.getId(),
                    p.getNombre(),
                    p.getTipoPrenda().name(),
                    "REVISAR",
                    prioridad(dias),
                    "Sin uso desde hace " + dias + " días",
                    referencia,
                    dias
                ));
            }
        }

        for (TipoPrenda tipo : List.of(TipoPrenda.SUPERIOR, TipoPrenda.INFERIOR, TipoPrenda.ZAPATOS)) {
            long disponibles = prendas.stream()
                .filter(p -> p.getTipoPrenda() == tipo && p.getEstado() == EstadoPrenda.DISPONIBLE)
                .count();
            if (disponibles < minDisponibles) {
                ordenes.add(new OrdenDTO(
                    "reponer-" + tipo.name(),
                    profileId,
                    null,
                    "Reponer " + etiqueta(tipo),
                    tipo.name(),
                    "REPONER",
                    disponibles == 0 ? "ALTA" : "MEDIA",
                    disponibles == 0
                        ? "No hay " + etiqueta(tipo) + " disponibles"
                        : "Solo " + disponibles + " prenda(s) de " + etiqueta(tipo) + " disponibles",
                    ahora,
                    0
                ));
            }
        }

        ordenes.sort(
            Comparator.comparingInt((OrdenDTO o) -> pesoPrioridad(o.prioridad())).reversed()
                .thenComparing(Comparator.comparingLong(OrdenDTO::diasSinUso).reversed())
        );
        return ordenes;
    }

    private String prioridad(long dias) {
        if (dias >= 365) return "ALTA";
        if (dias >= 270) return "MEDIA";
        return "BAJA";
    }

    private int pesoPrioridad(String prioridad) {
        return switch (prioridad) {
            case "ALTA" -> 3;
            case "MEDIA" -> 2;
            default -> 1;
        };
    }

    private String etiqueta(TipoPrenda tipo) {
        return switch (tipo) {
            case SUPERIOR -> "partes superiores";
            case INFERIOR -> "partes inferiores";
            case ZAPATOS -> "zapatos";
            default -> tipo.name().toLowerCase();
        };
    }
}
