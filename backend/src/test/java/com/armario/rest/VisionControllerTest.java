package com.armario.rest;

import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VisionControllerTest {

    private BufferedImage imagen(Color color, int ancho, int alto) {
        BufferedImage img = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setColor(color);
        g.fillRect(0, 0, ancho, alto);
        g.dispose();
        return img;
    }

    @Test
    void devuelveElColorRealNoElInicioDelBucket() {
        List<String> colores = VisionController.coloresDominantes(imagen(new Color(59, 91, 219), 64, 64), 3);

        assertEquals(1, colores.size());
        assertEquals("#3b5bdb", colores.get(0));
    }

    @Test
    void ordenaPorFrecuenciaYRespetaElMaximo() {
        BufferedImage img = imagen(new Color(242, 239, 230), 80, 80);
        Graphics2D g = img.createGraphics();
        g.setColor(new Color(59, 91, 219));
        g.fillRect(0, 0, 40, 40);
        g.dispose();

        List<String> colores = VisionController.coloresDominantes(img, 1);

        assertEquals(1, colores.size());
        assertEquals("#f2efe6", colores.get(0));
    }

    @Test
    void imagenNegraDevuelveNegro() {
        assertEquals(List.of("#000000"), VisionController.coloresDominantes(imagen(Color.BLACK, 16, 16), 3));
    }
}
