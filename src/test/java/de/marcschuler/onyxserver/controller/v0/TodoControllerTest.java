package de.marcschuler.onyxserver.controller.v0;

import de.marcschuler.onyxserver.OnyxTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@OnyxTest
class TodoControllerTest {

    @LocalServerPort
    private int port;

    private RestClient restClient;

    @BeforeEach
    void setUp() {
        restClient = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @Test
    void testAuth() {
        var response = restClient.get()
                .uri("/v0/server/auth")
                .retrieve()
                .toBodilessEntity();
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testUserReference() {
        var response = restClient.get()
                .uri("/v0/server/userReference")
                .retrieve()
                .toBodilessEntity();
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testUserOnline() {
        var response = restClient.get()
                .uri("/v0/server/useronline")
                .retrieve()
                .toBodilessEntity();
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testIceServer() {
        var response = restClient.get()
                .uri("/v0/server/iceServer")
                .retrieve()
                .toBodilessEntity();
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testSignedContent() {
        var response = restClient.get()
                .uri("/v0/server/signedContent")
                .retrieve()
                .toBodilessEntity();
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testUserSimpleDTO() {
        var response = restClient.get()
                .uri("/v0/server/usersimpledto")
                .retrieve()
                .toBodilessEntity();
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testChannelExtendedDTO() {
        var response = restClient.get()
                .uri("/v0/server/channelExtendedDTO")
                .retrieve()
                .toBodilessEntity();
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testSectionExtendedDTO() {
        var response = restClient.get()
                .uri("/v0/server/sectionextendeddto")
                .retrieve()
                .toBodilessEntity();
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
