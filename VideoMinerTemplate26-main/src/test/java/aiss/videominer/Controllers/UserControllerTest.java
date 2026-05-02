package aiss.videominer.Controllers;

import aiss.videominer.model.User;
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
class UserControllerTest {

    @LocalServerPort
    int port;

    @Autowired
    RestTemplate restTemplate;

    String uri(String path) {
        return "http://localhost:" + port + "/api/users" + path;
    }

    User createUser(String name) {
        User user = new User(
                name,
                "https://example.com/" + name,
                "https://example.com/" + name + ".jpg");

        ResponseEntity<User> response = restTemplate.exchange(
                uri(""),
                HttpMethod.POST,
                new HttpEntity<>(user),
                User.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        return response.getBody();
    }

    @Test
    @DisplayName(" Prueba getAllUsers")
    void getAllUsers() {
        createUser("Ana");

        ResponseEntity<User[]> response = restTemplate.exchange(
                uri(""),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                User[].class);
        User[] usersArray = response.getBody();
        List<User> users = Arrays.asList(usersArray);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(users.isEmpty(), "The list of users is empty");
        System.out.println(users);
    }

    @Test
    @DisplayName(" Prueba getUser")
    void getUser() {
        User user = createUser("Ana");

        ResponseEntity<User> response = restTemplate.exchange(
                uri("/" + user.getId()),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                User.class);
        User result = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(result, "The user is null");
        System.out.println(result);
    }

    @Test
    @DisplayName(" Prueba postUser")
    void testPostUser() {
        User result = createUser("Luis");

        assertNotNull(result, "The user is null");
        System.out.println(result);
    }

    @Test
    @DisplayName(" Prueba updateUser")
    void testUpdateUser() {
        User user = createUser("Ana");
        User updatedUser = new User(
                "Ana actualizada",
                "https://example.com/ana-updated",
                "https://example.com/ana-updated.jpg");

        ResponseEntity<Void> updateResponse = restTemplate.exchange(
                uri("/" + user.getId()),
                HttpMethod.PUT,
                new HttpEntity<>(updatedUser),
                Void.class);

        ResponseEntity<User> getResponse = restTemplate.exchange(
                uri("/" + user.getId()),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                User.class);
        User result = getResponse.getBody();

        assertEquals(HttpStatus.NO_CONTENT, updateResponse.getStatusCode());
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertNotNull(result, "The user is null");
        assertEquals("Ana actualizada", result.getName());
        System.out.println(result);
    }

    @Test
    @DisplayName(" Prueba deleteUser")
    void testDeleteUser() {
        User user = createUser("Ana");

        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                uri("/" + user.getId()),
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                Void.class);

        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());
        assertThrows(HttpClientErrorException.NotFound.class, () -> restTemplate.exchange(
                uri("/" + user.getId()),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                User.class));
    }
}
