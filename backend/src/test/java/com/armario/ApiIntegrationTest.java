package com.armario;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "armario.max-users=50")
class ApiIntegrationTest {

    @LocalServerPort
    private int port;

    private final HttpClient http = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    private record Resp(int status, String body) {}

    private Resp post(String path, String json, String token) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8));
        if (token != null) builder.header("Authorization", "Bearer " + token);
        HttpResponse<String> res = http.send(builder.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        return new Resp(res.statusCode(), res.body());
    }

    private Resp get(String path, String token) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path)).GET();
        if (token != null) builder.header("Authorization", "Bearer " + token);
        HttpResponse<String> res = http.send(builder.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        return new Resp(res.statusCode(), res.body());
    }

    private Resp put(String path, String json, String token) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
            .header("Content-Type", "application/json")
            .PUT(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8));
        if (token != null) builder.header("Authorization", "Bearer " + token);
        HttpResponse<String> res = http.send(builder.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        return new Resp(res.statusCode(), res.body());
    }

    private Resp delete(String path, String token) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path)).DELETE();
        if (token != null) builder.header("Authorization", "Bearer " + token);
        HttpResponse<String> res = http.send(builder.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        return new Resp(res.statusCode(), res.body());
    }

    private JsonNode json(String body) {
        return mapper.readTree(body);
    }

    private String emailUnico() {
        return "test-" + UUID.randomUUID() + "@armario.test";
    }

    private String registrar(String email) throws Exception {
        Resp res = post("/api/auth/register",
            "{\"nombre\":\"Test\",\"email\":\"" + email + "\",\"password\":\"test1234\"}", null);
        assertEquals(201, res.status(), res.body());
        return json(res.body()).get("token").asText();
    }

    @Test
    void registroLoginPerfilPrendasYCombinaciones() throws Exception {
        String email = emailUnico();
        String token = registrar(email);

        Resp login = post("/api/auth/login",
            "{\"email\":\"" + email + "\",\"password\":\"test1234\"}", null);
        assertEquals(200, login.status());
        String tokenLogin = json(login.body()).get("token").asText();
        assertNotNull(tokenLogin);

        Resp perfil = post("/api/profiles",
            "{\"nombre\":\"Integracion\",\"tipo\":\"MUJER\"}", tokenLogin);
        assertEquals(201, perfil.status(), perfil.body());
        long profileId = json(perfil.body()).get("id").asLong();

        Resp superior = post("/api/prendas",
            "{\"profileId\":" + profileId + ",\"nombre\":\"Camisa\",\"color\":\"#f2efe6\",\"tipoPrenda\":\"SUPERIOR\"}",
            tokenLogin);
        assertEquals(201, superior.status(), superior.body());

        Resp inferior = post("/api/prendas",
            "{\"profileId\":" + profileId + ",\"nombre\":\"Vaqueros\",\"color\":\"#3b5bdb\",\"tipoPrenda\":\"INFERIOR\"}",
            tokenLogin);
        assertEquals(201, inferior.status(), inferior.body());

        Resp prendas = get("/api/prendas?profileId=" + profileId, tokenLogin);
        assertEquals(200, prendas.status());
        assertEquals(2, json(prendas.body()).size());

        Resp combos = get("/api/combinaciones?profileId=" + profileId, tokenLogin);
        assertEquals(200, combos.status(), combos.body());
        JsonNode lista = json(combos.body());
        assertTrue(lista.size() >= 1);
        assertTrue(lista.get(0).get("prendas").size() >= 2);
    }

    @Test
    void capturaGuardaDescripcionDePrendaSinGuardarImagen() throws Exception {
        String token = registrar(emailUnico());
        long profileId = json(post("/api/profiles",
            "{\"nombre\":\"Vision\",\"tipo\":\"MUJER\"}", token).body()).get("id").asLong();

        Resp captura = post("/api/prendas",
            "{\"profileId\":" + profileId + ",\"nombre\":\"Blusa\",\"color\":\"#e8a0bf\",\"tipoPrenda\":\"SUPERIOR\",\"tejido\":\"algodon\"}",
            token);
        assertEquals(201, captura.status(), captura.body());

        JsonNode prenda = json(captura.body());
        assertEquals("#e8a0bf", prenda.get("color").asText());
        assertEquals("SUPERIOR", prenda.get("tipoPrenda").asText());
        assertEquals("algodon", prenda.get("tejido").asText());
        JsonNode imagenUrl = prenda.get("imagenUrl");
        assertTrue(imagenUrl == null || imagenUrl.isNull());
    }

    @Test
    void loginIncorrectoDevuelve401() throws Exception {
        Resp res = post("/api/auth/login", "{\"email\":\"ana@armario.test\",\"password\":\"incorrecta\"}", null);
        assertEquals(401, res.status());
    }

    @Test
    void sinTokenDevuelve401() throws Exception {
        assertEquals(401, get("/api/profiles", null).status());
    }

    @Test
    void logoutInvalidaElToken() throws Exception {
        String token = registrar(emailUnico());
        assertEquals(200, get("/api/profiles", token).status());

        Resp logout = post("/api/auth/logout", "{}", token);
        assertEquals(204, logout.status());

        assertEquals(401, get("/api/profiles", token).status());
    }

    @Test
    void meDevuelveElUsuarioDeLaSesionY401SinToken() throws Exception {
        String email = emailUnico();
        String token = registrar(email);

        Resp me = get("/api/auth/me", token);
        assertEquals(200, me.status(), me.body());
        assertEquals(email, json(me.body()).get("email").asText());

        assertEquals(401, get("/api/auth/me", null).status());
    }

    @Test
    void actualizarEstadoYEliminarPrenda() throws Exception {
        String token = registrar(emailUnico());
        long profileId = json(post("/api/profiles",
            "{\"nombre\":\"QA\",\"tipo\":\"HOMBRE\"}", token).body()).get("id").asLong();
        long prendaId = json(post("/api/prendas",
            "{\"profileId\":" + profileId + ",\"nombre\":\"Sudadera\",\"color\":\"#2b2b2b\",\"tipoPrenda\":\"SUPERIOR\"}",
            token).body()).get("id").asLong();

        Resp update = put("/api/prendas/" + prendaId, "{\"estado\":\"LAVANDERIA\"}", token);
        assertEquals(200, update.status(), update.body());
        assertEquals("LAVANDERIA", json(update.body()).get("estado").asText());
        assertEquals("Sudadera", json(update.body()).get("nombre").asText());

        Resp lista = get("/api/prendas?profileId=" + profileId, token);
        assertEquals("LAVANDERIA", json(lista.body()).get(0).get("estado").asText());

        Resp borrado = delete("/api/prendas/" + prendaId, token);
        assertEquals(204, borrado.status());
        assertEquals(0, json(get("/api/prendas?profileId=" + profileId, token).body()).size());
    }

    @Test
    void noSePuedeModificarNiBorrarPrendaDeOtroUsuario() throws Exception {
        String tokenAna = json(post("/api/auth/login",
            "{\"email\":\"ana@armario.test\",\"password\":\"test1234\"}", null).body()).get("token").asText();
        long perfilAna = -1;
        for (JsonNode p : json(get("/api/profiles", tokenAna).body())) {
            if ("MUJER".equals(p.get("tipo").asText())) {
                perfilAna = p.get("id").asLong();
                break;
            }
        }
        assertTrue(perfilAna > 0);
        JsonNode prendasAna = json(get("/api/prendas?profileId=" + perfilAna, tokenAna).body());
        assertTrue(prendasAna.size() > 0);
        long prendaAna = prendasAna.get(0).get("id").asLong();

        String tokenCarlos = json(post("/api/auth/login",
            "{\"email\":\"carlos@armario.test\",\"password\":\"test1234\"}", null).body()).get("token").asText();

        assertEquals(403, put("/api/prendas/" + prendaAna, "{\"estado\":\"EN_USO\"}", tokenCarlos).status());
        assertEquals(403, delete("/api/prendas/" + prendaAna, tokenCarlos).status());
    }

    @Test
    void ordenesSugierenRevisarPrendasInactivasYDenieganPerfilAjeno() throws Exception {
        String tokenAna = json(post("/api/auth/login",
            "{\"email\":\"ana@armario.test\",\"password\":\"test1234\"}", null).body()).get("token").asText();
        long perfilAna = -1;
        for (JsonNode p : json(get("/api/profiles", tokenAna).body())) {
            if ("MUJER".equals(p.get("tipo").asText())) {
                perfilAna = p.get("id").asLong();
                break;
            }
        }
        assertTrue(perfilAna > 0);

        Resp ordenes = get("/api/ordenes/generar?profileId=" + perfilAna, tokenAna);
        assertEquals(200, ordenes.status(), ordenes.body());
        boolean revisarTrenca = false;
        for (JsonNode o : json(ordenes.body())) {
            if ("REVISAR".equals(o.get("tipoAccion").asText()) && "Trenca".equals(o.get("nombre").asText())) {
                revisarTrenca = true;
            }
        }
        assertTrue(revisarTrenca, ordenes.body());

        String tokenCarlos = json(post("/api/auth/login",
            "{\"email\":\"carlos@armario.test\",\"password\":\"test1234\"}", null).body()).get("token").asText();
        assertEquals(403, get("/api/ordenes/generar?profileId=" + perfilAna, tokenCarlos).status());
        assertEquals(403, get("/api/ordenes?profileId=" + perfilAna, tokenCarlos).status());
    }

    @Test
    void combinacionesIgnoranPrendasNoDisponibles() throws Exception {
        String token = registrar(emailUnico());
        long profileId = json(post("/api/profiles",
            "{\"nombre\":\"Armario\",\"tipo\":\"MUJER\"}", token).body()).get("id").asLong();

        long camisa = json(post("/api/prendas",
            "{\"profileId\":" + profileId + ",\"nombre\":\"Camisa\",\"color\":\"#f2efe6\",\"tipoPrenda\":\"SUPERIOR\"}",
            token).body()).get("id").asLong();
        post("/api/prendas",
            "{\"profileId\":" + profileId + ",\"nombre\":\"Blusa\",\"color\":\"#2b2b2b\",\"tipoPrenda\":\"SUPERIOR\"}",
            token);
        post("/api/prendas",
            "{\"profileId\":" + profileId + ",\"nombre\":\"Vaqueros\",\"color\":\"#3b5bdb\",\"tipoPrenda\":\"INFERIOR\"}",
            token);

        assertEquals(200, put("/api/prendas/" + camisa, "{\"estado\":\"LAVANDERIA\"}", token).status());

        Resp combos = get("/api/combinaciones?profileId=" + profileId, token);
        assertEquals(200, combos.status());
        for (JsonNode combo : json(combos.body())) {
            for (JsonNode p : combo.get("prendas")) {
                assertNotEquals("Camisa", p.get("nombre").asText());
            }
        }
        assertTrue(json(combos.body()).size() >= 1);
    }

    @Test
    void noSePuedeAccederACombinacionesDeOtroUsuario() throws Exception {
        String tokenAna = json(post("/api/auth/login",
            "{\"email\":\"ana@armario.test\",\"password\":\"test1234\"}", null).body()).get("token").asText();
        JsonNode perfilesAna = json(get("/api/profiles", tokenAna).body());
        long perfilMujer = -1;
        for (JsonNode p : perfilesAna) {
            if ("MUJER".equals(p.get("tipo").asText())) {
                perfilMujer = p.get("id").asLong();
                break;
            }
        }
        assertTrue(perfilMujer > 0);

        String tokenCarlos = json(post("/api/auth/login",
            "{\"email\":\"carlos@armario.test\",\"password\":\"test1234\"}", null).body()).get("token").asText();
        assertEquals(403, get("/api/combinaciones?profileId=" + perfilMujer, tokenCarlos).status());
    }

    @Test
    void perfilCompartidoVisibleParaAmbosUsuarios() throws Exception {
        String tokenAna = json(post("/api/auth/login",
            "{\"email\":\"ana@armario.test\",\"password\":\"test1234\"}", null).body()).get("token").asText();
        String tokenCarlos = json(post("/api/auth/login",
            "{\"email\":\"carlos@armario.test\",\"password\":\"test1234\"}", null).body()).get("token").asText();

        assertTrue(contienePerfil(json(get("/api/profiles", tokenAna).body()), "Peque"));
        assertTrue(contienePerfil(json(get("/api/profiles", tokenCarlos).body()), "Peque"));
    }

    private boolean contienePerfil(JsonNode perfiles, String nombre) {
        for (JsonNode p : perfiles) {
            if (nombre.equals(p.get("nombre").asText())) return true;
        }
        return false;
    }
}
