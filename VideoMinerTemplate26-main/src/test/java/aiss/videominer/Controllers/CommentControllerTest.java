package aiss.videominer.Controllers;

import aiss.videominer.model.Channel;
import aiss.videominer.model.Comment;
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
class CommentControllerTest extends ApiKeyTestSupport {

    @LocalServerPort
    int port;

    @Autowired
    RestTemplate restTemplate;

    String uri(String path) {
        return "http://localhost:" + port + "/videominer/comments" + path;
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

    Comment createComment(String videoId, String text) {
        Comment comment = new Comment(
                UUID.randomUUID().toString(),
                text,
                "2026-05-04T12:00:00Z");

        ResponseEntity<Comment> response = restTemplate.exchange(
                uri("/videos/" + videoId + "/comments"),
                HttpMethod.POST,
                authorizedEntity(comment),
                Comment.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        return response.getBody();
    }

    @Test
    @DisplayName(" Prueba getAllComments")
    void getAllComments() {
        Channel channel = createChannel();
        Video video = createVideo(channel.getId(), "Video demo");
        createComment(video.getId(), "Comentario demo");

        ResponseEntity<Comment[]> response = restTemplate.exchange(
                uri(""),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                Comment[].class);
        Comment[] commentsArray = response.getBody();

        List<Comment> comments = new ArrayList<>();

        if (commentsArray != null) {
            comments = Arrays.asList(commentsArray);
        }

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(comments.isEmpty(), "The list of comments is empty");
        System.out.println(comments);
    }

    @Test
    @DisplayName(" Prueba getComment")
    void getComment() {
        Channel channel = createChannel();
        Video video = createVideo(channel.getId(), "Video demo");
        Comment comment = createComment(video.getId(), "Comentario demo");

        ResponseEntity<Comment> response = restTemplate.exchange(
                uri("/" + comment.getId()),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                Comment.class);
        Comment result = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(result, "The comment is null");
        System.out.println(result);
    }

    @Test
    @DisplayName(" Prueba postComment")
    void testPostComment() {
        Channel channel = createChannel();
        Video video = createVideo(channel.getId(), "Video demo");
        Comment result = createComment(video.getId(), "Comentario nuevo");

        assertNotNull(result, "The comment is null");
        System.out.println(result);
    }

    @Test
    @DisplayName(" Prueba updateComment")
    void testUpdateComment() {
        Channel channel = createChannel();
        Video video = createVideo(channel.getId(), "Video demo");
        Comment comment = createComment(video.getId(), "Comentario demo");
        Comment updatedComment = new Comment(
                UUID.randomUUID().toString(),
                "Comentario actualizado",
                "2026-05-04T13:00:00Z");

        ResponseEntity<Void> updateResponse = restTemplate.exchange(
                uri("/" + comment.getId()),
                HttpMethod.PUT,
                authorizedEntity(updatedComment),
                Void.class);

        ResponseEntity<Comment> getResponse = restTemplate.exchange(
                uri("/" + comment.getId()),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                Comment.class);
        Comment result = getResponse.getBody();

        assertEquals(HttpStatus.NO_CONTENT, updateResponse.getStatusCode());
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertNotNull(result, "The comment is null");
        assertEquals("Comentario actualizado", result.getText());
        System.out.println(result);
    }

    @Test
    @DisplayName(" Prueba deleteComment")
    void testDeleteComment() {
        Channel channel = createChannel();
        Video video = createVideo(channel.getId(), "Video demo");
        Comment comment = createComment(video.getId(), "Comentario demo");

        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                uri("/" + comment.getId()),
                HttpMethod.DELETE,
                authorizedEntity(),
                Void.class);

        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());
    }
}
