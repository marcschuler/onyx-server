package de.marcschuler.onyxserver.controller.v0;

import de.marcschuler.onyxserver.OnyxTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@OnyxTest
public class SwaggerTest {
    @LocalServerPort
    private int port;

    private RestClient restClient;

    @BeforeEach
    void setUp() {
        restClient = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    //just a simple test that swagger generates without an error - more testing should not be nessesary at this point
    @Test
    void testSwaggerGeneration(){
        var node = restClient.get().uri("/openapi")
                .retrieve()
                .body(JsonNode.class);

        assertNotNull(node);
        assertFalse(node.path("info").isNull());
        assertFalse(node.path("path").isNull());
        assertFalse(node.path("components").isNull());
    }

}
