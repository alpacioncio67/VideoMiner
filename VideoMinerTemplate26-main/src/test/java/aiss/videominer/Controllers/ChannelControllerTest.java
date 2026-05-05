package aiss.videominer.Controllers;

import aiss.videominer.model.Channel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ChannelControllerTest extends ApiKeyTestSupport {

    @LocalServerPort
    int port;

    @Autowired
    RestTemplate restTemplate;

    String uri(String path) {
        return "http://localhost:" + port + "/videominer/channels" + path;
    }

    Channel createChannel(String name) {
        String id = UUID.randomUUID().toString();
        Channel channel = new Channel(
                id,
                name,
                "Description " + name,
                "2026-05-04T10:00:00Z");

        ResponseEntity<Channel> response = restTemplate.exchange(
                uri(""),
                HttpMethod.POST,
                authorizedEntity(channel),
                Channel.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        return response.getBody();
    }

    @Test
    @DisplayName(" Prueba getAllChannels")
    void getAllChannels() {
        createChannel("Canal demo");

        ResponseEntity<Channel[]> response = restTemplate.exchange(
                uri(""),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                Channel[].class);
        Channel[] channelsArray = response.getBody();

        List<Channel> channels = new ArrayList<>();

        if (channelsArray != null) {
            channels = Arrays.asList(channelsArray);
        }

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(channels.isEmpty(), "The list of channels is empty");
        System.out.println(channels);
    }

    @Test
    @DisplayName(" Prueba getChannel")
    void getChannel() {
        Channel channel = createChannel("Canal demo");

        ResponseEntity<Channel> response = restTemplate.exchange(
                uri("/" + channel.getId()),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                Channel.class);
        Channel result = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(result, "The channel is null");
        System.out.println(result);
    }

    @Test
    @DisplayName(" Prueba postChannel")
    void testPostChannel() {
        Channel result = createChannel("Canal nuevo");

        assertNotNull(result, "The channel is null");
        System.out.println(result);
    }

    @Test
    @DisplayName(" Prueba updateChannel")
    void testUpdateChannel() {
        Channel channel = createChannel("Canal demo");
        Channel updatedChannel = new Channel(
                UUID.randomUUID().toString(),
                "Canal actualizado",
                "Description actualizada",
                "2026-05-04T12:00:00Z");

        ResponseEntity<Void> updateResponse = restTemplate.exchange(
                uri("/" + channel.getId()),
                HttpMethod.PUT,
                authorizedEntity(updatedChannel),
                Void.class);

        ResponseEntity<Channel> getResponse = restTemplate.exchange(
                uri("/" + channel.getId()),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                Channel.class);
        Channel result = getResponse.getBody();

        assertEquals(HttpStatus.NO_CONTENT, updateResponse.getStatusCode());
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertNotNull(result, "The channel is null");
        assertEquals("Canal actualizado", result.getName());
        System.out.println(result);
    }

    @Test
    @DisplayName(" Prueba deleteChannel")
    void testDeleteChannel() {
        Channel channel = createChannel("Canal demo");

        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                uri("/" + channel.getId()),
                HttpMethod.DELETE,
                authorizedEntity(),
                Void.class);

        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());
    }
}
