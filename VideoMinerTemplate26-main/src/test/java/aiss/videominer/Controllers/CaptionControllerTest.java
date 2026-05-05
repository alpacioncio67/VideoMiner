package aiss.videominer.Controllers;

import aiss.videominer.model.Caption;
import aiss.videominer.model.Channel;
import aiss.videominer.model.Video;
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
class CaptionControllerTest extends ApiKeyTestSupport {

    @LocalServerPort
    int port;

    @Autowired
    RestTemplate restTemplate;

    String uri(String path) {
        return "http://localhost:" + port + "/videominer/captions" + path;
    }

    String channelUri(String path) {
        return "http://localhost:" + port + "/videominer/channels" + path;
    }

    String videoUri(String path) {
        return "http://localhost:" + port + "/videominer/videos" + path;
    }

    Channel createChannel() {
        String channelId = UUID.randomUUID().toString();
        Channel channel = new Channel(
                channelId,
                "Canal " + channelId,
                "Description",
                "2026-05-04T10:00:00Z");

        ResponseEntity<Channel> response = restTemplate.exchange(
                channelUri(""),
                HttpMethod.POST,
                authorizedEntity(channel),
                Channel.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        return response.getBody();
    }

    Video createVideo(String channelId, String name) {
        Video video = new Video(
                UUID.randomUUID().toString(),
                name,
                "Description",
                "2026-05-04T11:00:00Z");

        ResponseEntity<Video> response = restTemplate.exchange(
                videoUri("/channels/" + channelId + "/videos"),
                HttpMethod.POST,
                authorizedEntity(video),
                Video.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        return response.getBody();
    }

    Caption createCaption(String videoId, String name) {
        Caption caption = new Caption(
                UUID.randomUUID().toString(),
                name,
                "es");

        ResponseEntity<Caption> response = restTemplate.exchange(
                uri("/videos/" + videoId + "/captions"),
                HttpMethod.POST,
                authorizedEntity(caption),
                Caption.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        return response.getBody();
    }

    @Test
    @DisplayName(" Prueba getAllCaptions")
    void getAllCaptions() {
        Channel channel = createChannel();
        Video video = createVideo(channel.getId(), "Video demo");
        createCaption(video.getId(), "Subtitulo demo");

        ResponseEntity<Caption[]> response = restTemplate.exchange(
                uri(""),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                Caption[].class);
        Caption[] captionsArray = response.getBody();

        List<Caption> captions = new ArrayList<>();

        if (captionsArray != null) {
            captions = Arrays.asList(captionsArray);
        }

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(captions.isEmpty(), "The list of captions is empty");
        System.out.println(captions);
    }

    @Test
    @DisplayName(" Prueba getCaption")
    void getCaption() {
        Channel channel = createChannel();
        Video video = createVideo(channel.getId(), "Video demo");
        Caption caption = createCaption(video.getId(), "Subtitulo demo");

        ResponseEntity<Caption> response = restTemplate.exchange(
                uri("/" + caption.getId()),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                Caption.class);
        Caption result = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(result, "The caption is null");
        System.out.println(result);
    }

    @Test
    @DisplayName(" Prueba postCaption")
    void testPostCaption() {
        Channel channel = createChannel();
        Video video = createVideo(channel.getId(), "Video demo");
        Caption result = createCaption(video.getId(), "Subtitulo nuevo");

        assertNotNull(result, "The caption is null");
        System.out.println(result);
    }

    @Test
    @DisplayName(" Prueba updateCaption")
    void testUpdateCaption() {
        Channel channel = createChannel();
        Video video = createVideo(channel.getId(), "Video demo");
        Caption caption = createCaption(video.getId(), "Subtitulo demo");
        Caption updatedCaption = new Caption(
                UUID.randomUUID().toString(),
                "Subtitulo actualizado",
                "en");

        ResponseEntity<Void> updateResponse = restTemplate.exchange(
                uri("/" + caption.getId()),
                HttpMethod.PUT,
                authorizedEntity(updatedCaption),
                Void.class);

        ResponseEntity<Caption> getResponse = restTemplate.exchange(
                uri("/" + caption.getId()),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                Caption.class);
        Caption result = getResponse.getBody();

        assertEquals(HttpStatus.NO_CONTENT, updateResponse.getStatusCode());
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertNotNull(result, "The caption is null");
        assertEquals("Subtitulo actualizado", result.getName());
        System.out.println(result);
    }

    @Test
    @DisplayName(" Prueba deleteCaption")
    void testDeleteCaption() {
        Channel channel = createChannel();
        Video video = createVideo(channel.getId(), "Video demo");
        Caption caption = createCaption(video.getId(), "Subtitulo demo");

        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                uri("/" + caption.getId()),
                HttpMethod.DELETE,
                authorizedEntity(),
                Void.class);

        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());
    }
}
