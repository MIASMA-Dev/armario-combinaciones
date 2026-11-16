package com.armario.service;

import com.armario.domain.Prenda;
import com.armario.domain.TipoPrenda;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CombinacionService {

    public record CombinacionDTO(String id, Long profileId, List<Prenda> prendas, String esquemaColor) {}

    public List<CombinacionDTO> generar(Long profileId, List<Prenda> prendas) {
        List<Prenda> superiores = porTipo(prendas, TipoPrenda.SUPERIOR);
        List<Prenda> inferiores = porTipo(prendas, TipoPrenda.INFERIOR);
        List<Prenda> zapatos = porTipo(prendas, TipoPrenda.ZAPATOS);
        List<Prenda> abrigos = porTipo(prendas, TipoPrenda.ABRIGOS, TipoPrenda.CHAQUETAS);
        List<Prenda> complementos = porTipo(prendas, TipoPrenda.COMPLEMENTOS);
        List<Prenda> joyas = porTipo(prendas, TipoPrenda.JOYAS);
        List<Prenda> sombreros = porTipo(prendas, TipoPrenda.SOMBREROS);

        List<CombinacionDTO> resultado = new ArrayList<>();

        for (Prenda sup : superiores) {
            for (Prenda inf : inferiores) {
                if (!compatible(sup.getColor(), inf.getColor())) continue;

                List<Prenda> combo = new ArrayList<>();
                combo.add(sup);
                combo.add(inf);
                anadirCompatible(combo, zapatos, sup.getColor());
                anadirCompatible(combo, abrigos, sup.getColor());
                anadirCompatible(combo, complementos, sup.getColor());
                anadirCompatible(combo, joyas, sup.getColor());
                anadirCompatible(combo, sombreros, sup.getColor());

                resultado.add(new CombinacionDTO(
                    UUID.randomUUID().toString(),
                    profileId,
                    combo,
                    esquema(sup.getColor(), inf.getColor())
                ));
                if (resultado.size() >= 24) return resultado;
            }
        }
        return resultado;
    }

    private void anadirCompatible(List<Prenda> combo, List<Prenda> candidatos, String colorBase) {
        candidatos.stream()
            .filter(p -> compatible(colorBase, p.getColor()))
            .findFirst()
            .ifPresent(combo::add);
    }

    private List<Prenda> porTipo(List<Prenda> prendas, TipoPrenda... tipos) {
        List<TipoPrenda> set = List.of(tipos);
        return prendas.stream().filter(p -> set.contains(p.getTipoPrenda())).toList();
    }

    private boolean compatible(String c1, String c2) {
        if (c1 == null || c1.isBlank() || c2 == null || c2.isBlank()) return true;
        try {
            Color a = Color.decode(c1);
            Color b = Color.decode(c2);
            float[] h1 = Color.RGBtoHSB(a.getRed(), a.getGreen(), a.getBlue(), null);
            float[] h2 = Color.RGBtoHSB(b.getRed(), b.getGreen(), b.getBlue(), null);

            if (esNeutro(h1) || esNeutro(h2)) return true;

            float diff = Math.abs(h1[0] - h2[0]) * 360f;
            if (diff > 180f) diff = 360f - diff;
            return diff <= 40f || (diff >= 150f && diff <= 210f);
        } catch (NumberFormatException e) {
            return true;
        }
    }

    private boolean esNeutro(float[] hsb) {
        return hsb[1] < 0.18f || hsb[2] < 0.15f;
    }

    private String esquema(String c1, String c2) {
        if (c1 == null || c2 == null) return "neutro";
        try {
            Color a = Color.decode(c1);
            Color b = Color.decode(c2);
            float[] h1 = Color.RGBtoHSB(a.getRed(), a.getGreen(), a.getBlue(), null);
            float[] h2 = Color.RGBtoHSB(b.getRed(), b.getGreen(), b.getBlue(), null);
            if (esNeutro(h1) || esNeutro(h2)) return "neutro";
            float diff = Math.abs(h1[0] - h2[0]) * 360f;
            if (diff > 180f) diff = 360f - diff;
            if (diff <= 20f) return "monocromatico";
            if (diff <= 40f) return "analogo";
            return "complementario";
        } catch (NumberFormatException e) {
            return "neutro";
        }
    }
}