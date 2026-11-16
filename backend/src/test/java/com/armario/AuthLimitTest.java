package com.armario;

import com.armario.domain.AuthToken;
import com.armario.repository.AuthTokenRepository;
import com.armario.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "armario.max-users=3")
class AuthLimitTest {

    @LocalServerPort
    private int port;

    private final HttpClient http = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private UserRepository users;

    @Autowired
    private AuthTokenRepository tokens;

    private record Resp(int status, String body) {}

    private Resp post(String path, String json) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8));
        HttpResponse<String> res = http.send(builder.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        return new Resp(res.statusCode(), res.body());
    }

    @BeforeEach
    void limpiarUsuarios() {
        tokens.deleteAll();
        users.deleteAll();
    }

    @Test
    void permiteHastaTresUsuariosYRechazaElCuarto() throws Exception {
        for (int i = 0; i < 3; i++) {
            Resp res = post("/api/auth/register",
                "{\"nombre\":\"Limitado" + i + "\",\"email\":\"limit" + i + "@armario.test\",\"password\":\"test1234\"}");
            assertEquals(201, res.status(), res.body());
            assertNotNull(mapper.readTree(res.body()).get("token"));
        }

        Resp cuarto = post("/api/auth/register",
            "{\"nombre\":\"Limitado3\",\"email\":\"limit3@armario.test\",\"password\":\"test1234\"}");
        assertEquals(403, cuarto.status());
        assertTrue(cuarto.body().contains("limite de usuarios"));
    }
}
