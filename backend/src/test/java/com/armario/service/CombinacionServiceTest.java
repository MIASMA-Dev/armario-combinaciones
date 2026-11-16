package com.armario.service;

import com.armario.domain.Prenda;
import com.armario.domain.TipoPrenda;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CombinacionServiceTest {

    private final CombinacionService service = new CombinacionService();

    private Prenda prenda(String nombre, String color, TipoPrenda tipo) {
        Prenda p = new Prenda(1L, nombre, tipo);
        p.setColor(color);
        return p;
    }

    @Test
    void generaCombinacionConSuperiorEInferior() {
        List<Prenda> prendas = List.of(
            prenda("Camisa", "#f2efe6", TipoPrenda.SUPERIOR),
            prenda("Vaqueros", "#3b5bdb", TipoPrenda.INFERIOR)
        );

        var combos = service.generar(1L, prendas);

        assertEquals(1, combos.size());
        assertEquals(2, combos.get(0).prendas().size());
        assertEquals(1L, combos.get(0).profileId());
        assertNotNull(combos.get(0).id());
    }

    @Test
    void anadeCalzadoCompatible() {
        List<Prenda> prendas = List.of(
            prenda("Camisa", "#f2efe6", TipoPrenda.SUPERIOR),
            prenda("Vaqueros", "#3b5bdb", TipoPrenda.INFERIOR),
            prenda("Zapatillas", "#ffffff", TipoPrenda.ZAPATOS)
        );

        var combos = service.generar(1L, prendas);

        assertTrue(combos.get(0).prendas().stream()
            .anyMatch(p -> p.getTipoPrenda() == TipoPrenda.ZAPATOS));
    }

    @Test
    void sinSuperiorOInferiorNoGeneraNada() {
        List<Prenda> prendas = List.of(
            prenda("Camisa", "#f2efe6", TipoPrenda.SUPERIOR)
        );

        assertTrue(service.generar(1L, prendas).isEmpty());
    }

    @Test
    void esquemaNeutroCuandoUnColorEsNeutro() {
        List<Prenda> prendas = List.of(
            prenda("Camisa", "#f2efe6", TipoPrenda.SUPERIOR),
            prenda("Vaqueros", "#3b5bdb", TipoPrenda.INFERIOR)
        );

        assertEquals("neutro", service.generar(1L, prendas).get(0).esquemaColor());
    }

    @Test
    void esquemaComplementarioConColoresOpuestos() {
        List<Prenda> prendas = List.of(
            prenda("Camisa", "#ff0000", TipoPrenda.SUPERIOR),
            prenda("Pantalon", "#00ffff", TipoPrenda.INFERIOR)
        );

        var combos = service.generar(1L, prendas);

        assertFalse(combos.isEmpty());
        assertEquals("complementario", combos.get(0).esquemaColor());
    }

    @Test
    void noSuperaElLimiteDeCombinaciones() {
        List<Prenda> prendas = new java.util.ArrayList<>();
        for (int i = 0; i < 10; i++) {
            prendas.add(prenda("Sup" + i, "#f2efe6", TipoPrenda.SUPERIOR));
            prendas.add(prenda("Inf" + i, "#2b2b2b", TipoPrenda.INFERIOR));
        }

        assertTrue(service.generar(1L, prendas).size() <= 24);
    }
}
