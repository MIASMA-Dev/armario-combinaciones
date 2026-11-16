package com.armario.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.*;

@RestController
@RequestMapping("/api/vision")
public class VisionController {

    @PostMapping("/analyze")
    public ResponseEntity<?> analyze(@RequestParam("file") MultipartFile file) {
        try (InputStream in = file.getInputStream()) {
            BufferedImage img = ImageIO.read(in);
            if (img == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "imagen no valida"));
            }
            List<String> colores = coloresDominantes(img, 3);
            return ResponseEntity.ok(Map.of(
                "colores", colores,
                "tipoPrenda", "SUPERIOR",
                "tejido", "algodon",
                "confianza", 0.5
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "no se pudo analizar la imagen"));
        }
    }

    static List<String> coloresDominantes(BufferedImage img, int max) {
        // Cada bucket acumula contador y sumas de canales para devolver el color medio real.
        Map<Integer, long[]> buckets = new HashMap<>();
        int stepX = Math.max(1, img.getWidth() / 80);
        int stepY = Math.max(1, img.getHeight() / 80);

        for (int y = 0; y < img.getHeight(); y += stepY) {
            for (int x = 0; x < img.getWidth(); x += stepX) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                int key = ((r / 32) << 10) | ((g / 32) << 5) | (b / 32);
                long[] acc = buckets.computeIfAbsent(key, k -> new long[4]);
                acc[0]++;
                acc[1] += r;
                acc[2] += g;
                acc[3] += b;
            }
        }

        return buckets.entrySet().stream()
            .sorted((a, b) -> Long.compare(b.getValue()[0], a.getValue()[0]))
            .limit(max)
            .map(e -> {
                long[] acc = e.getValue();
                long n = acc[0];
                return String.format("#%02x%02x%02x", acc[1] / n, acc[2] / n, acc[3] / n);
            })
            .toList();
    }
}