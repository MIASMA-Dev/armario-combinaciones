package com.armario.service;

import com.armario.domain.EstadoPrenda;
import com.armario.domain.Prenda;
import com.armario.domain.TipoPrenda;
import com.armario.service.OrdenService.OrdenDTO;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrdenServiceTest {

    private final OrdenService service = new OrdenService();

    private Prenda prenda(TipoPrenda tipo, EstadoPrenda estado, long diasSinUso) {
        Prenda p = new Prenda(1L, tipo.name(), tipo);
        p.setEstado(estado);
        p.setFechaCaptura(Instant.now().minus(diasSinUso, ChronoUnit.DAYS));
        p.setUltimoUso(Instant.now().minus(diasSinUso, ChronoUnit.DAYS));
        return p;
    }

    private List<Prenda> armarioCompleto() {
        List<Prenda> prendas = new ArrayList<>();
        prendas.add(prenda(TipoPrenda.SUPERIOR, EstadoPrenda.DISPONIBLE, 30));
        prendas.add(prenda(TipoPrenda.SUPERIOR, EstadoPrenda.DISPONIBLE, 30));
        prendas.add(prenda(TipoPrenda.INFERIOR, EstadoPrenda.DISPONIBLE, 30));
        prendas.add(prenda(TipoPrenda.INFERIOR, EstadoPrenda.DISPONIBLE, 30));
        prendas.add(prenda(TipoPrenda.ZAPATOS, EstadoPrenda.DISPONIBLE, 30));
        prendas.add(prenda(TipoPrenda.ZAPATOS, EstadoPrenda.DISPONIBLE, 30));
        return prendas;
    }

    @Test
    void sugiereRevisarPrendasSinUsoMasDe180Dias() {
        List<Prenda> prendas = armarioCompleto();
        prendas.add(prenda(TipoPrenda.ABRIGOS, EstadoPrenda.DISPONIBLE, 200));

        List<OrdenDTO> ordenes = service.generar(1L, prendas);

        assertEquals(1, ordenes.size());
        assertEquals("REVISAR", ordenes.get(0).tipoAccion());
        assertEquals(200, ordenes.get(0).diasSinUso());
        assertTrue(ordenes.get(0).motivo().contains("200"));
    }

    @Test
    void noSugierePrendasUsadasRecientemente() {
        List<Prenda> prendas = armarioCompleto();
        prendas.add(prenda(TipoPrenda.ABRIGOS, EstadoPrenda.DISPONIBLE, 90));

        assertTrue(service.generar(1L, prendas).isEmpty());
    }

    @Test
    void priorizaPorAntiguedad() {
        List<Prenda> prendas = armarioCompleto();
        prendas.add(prenda(TipoPrenda.ABRIGOS, EstadoPrenda.DISPONIBLE, 400));
        prendas.add(prenda(TipoPrenda.ABRIGOS, EstadoPrenda.DISPONIBLE, 300));
        prendas.add(prenda(TipoPrenda.ABRIGOS, EstadoPrenda.DISPONIBLE, 200));

        List<OrdenDTO> ordenes = service.generar(1L, prendas);

        assertEquals(3, ordenes.size());
        assertEquals("ALTA", ordenes.get(0).prioridad());
        assertEquals("MEDIA", ordenes.get(1).prioridad());
        assertEquals("BAJA", ordenes.get(2).prioridad());
    }

    @Test
    void sugiereReponerCategoriasConMenosDeDosDisponibles() {
        List<Prenda> prendas = List.of(prenda(TipoPrenda.SUPERIOR, EstadoPrenda.DISPONIBLE, 10));

        List<OrdenDTO> ordenes = service.generar(1L, prendas);

        assertEquals(3, ordenes.stream().filter(o -> "REPONER".equals(o.tipoAccion())).count());
        assertTrue(ordenes.stream().anyMatch(o -> o.motivo().contains("partes inferiores")));
        assertTrue(ordenes.stream().anyMatch(o -> "ALTA".equals(o.prioridad())));
    }

    @Test
    void prendasNoDisponiblesNoCuentanComoStock() {
        List<Prenda> prendas = armarioCompleto();
        prendas.get(0).setEstado(EstadoPrenda.LAVANDERIA);

        List<OrdenDTO> ordenes = service.generar(1L, prendas);

        assertTrue(ordenes.stream().anyMatch(o -> "REPONER".equals(o.tipoAccion())
            && "SUPERIOR".equals(o.categoria())));
    }

    @Test
    void usaFechaDeCapturaCuandoNuncaSeHaUsado() {
        Prenda p = new Prenda(1L, "Antigua", TipoPrenda.ABRIGOS);
        p.setFechaCaptura(Instant.now().minus(365, ChronoUnit.DAYS));

        List<Prenda> prendas = armarioCompleto();
        prendas.add(p);

        List<OrdenDTO> ordenes = service.generar(1L, prendas);

        assertEquals(1, ordenes.size());
        assertEquals("ALTA", ordenes.get(0).prioridad());
    }
}
