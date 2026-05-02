package aiss.videominer.Controllers;

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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class VideoControllerTest {

    @LocalServerPort
    int port;

    @Autowired
    RestTemplate restTemplate;

    String uri(String path) {
        return "http://localhost:" + port + "/api/videos" + path;
    }

    Video createVideo(String name) {
        Video video = new Video(
                name,
                "Description",
                "2026-05-02T10:00:00Z");

        ResponseEntity<Video> response = restTemplate.exchange(
                uri(""),
                HttpMethod.POST,
                new HttpEntity<>(video),
                Video.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        return response.getBody();
    }

    @Test
    @DisplayName(" Prueba getAllVideos")
    void getAllVideos() {
        createVideo("Demo");

        ResponseEntity<Video[]> response = restTemplate.exchange(
                uri(""),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                Video[].class);
        Video[] videosArray = response.getBody();
        List<Video> videos = Arrays.asList(videosArray);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(videos.isEmpty(), "The list of videos is empty");
        System.out.println(videos);
    }

    @Test
    @DisplayName(" Prueba getVideo")
    void getVideo() {
        Video video = createVideo("Demo");

        ResponseEntity<Video> response = restTemplate.exchange(
                uri("/" + video.getId()),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                Video.class);
        Video result = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(result, "The video is null");
        System.out.println(result);
    }

    @Test
    @DisplayName(" Prueba postVideo")
    void testPostVideo() {
        Video result = createVideo("Nuevo video");

        assertNotNull(result, "The video is null");
        System.out.println(result);
    }

    @Test
    @DisplayName(" Prueba updateVideo")
    void testUpdateVideo() {
        Video video = createVideo("Demo");
        Video updatedVideo = new Video(
                "Demo actualizado",
                "Description actualizada",
                "2026-05-04T10:00:00Z");

        ResponseEntity<Void> updateResponse = restTemplate.exchange(
                uri("/" + video.getId()),
                HttpMethod.PUT,
                new HttpEntity<>(updatedVideo),
                Void.class);

        ResponseEntity<Video> getResponse = restTemplate.exchange(
                uri("/" + video.getId()),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                Video.class);
        Video result = getResponse.getBody();

        assertEquals(HttpStatus.NO_CONTENT, updateResponse.getStatusCode());
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertNotNull(result, "The video is null");
        assertEquals("Demo actualizado", result.getName());
        System.out.println(result);
    }

    @Test
    @DisplayName(" Prueba deleteVideo")
    void testDeleteVideo() {
        Video video = createVideo("Demo");

        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                uri("/" + video.getId()),
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                Void.class);

        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());
        assertThrows(HttpClientErrorException.NotFound.class, () -> restTemplate.exchange(
                uri("/" + video.getId()),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                Video.class));
    }
}
