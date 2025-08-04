import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class TaskEndpointTest extends BaseEndpointTest {
    private HttpClient client;

    @BeforeEach
    void setUpClient() {
        client = HttpClient.newHttpClient();
    }

    @Test
    void testReturnEmptyWhenNoTasks() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:" + PORT + "/tasks");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body());
    }

    @Test
    void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2", LocalDateTime.now(), Duration.ofMinutes(5));
        String json = gson.toJson(task);

        URI url = URI.create("http://localhost:" + PORT + "/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode()); // или 200, если ты не менял код
        assertEquals("Задача создана", response.body());

        Task savedTask = taskManager.getTaskById(1);
        assertNotNull(savedTask);
        assertEquals("Test 2", savedTask.getTitle());
    }

    @Test
    void testReturnTaskById() throws IOException, InterruptedException {
        Task task = new Task("Test", "Test",
                LocalDateTime.now(), Duration.ofMinutes(5));
        taskManager.createTask(task);

        URI url = URI.create("http://localhost:" + PORT + "/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Task responseTask = gson.fromJson(response.body(), Task.class);
        assertNotNull(responseTask);
        assertEquals(1, responseTask.getId());
        assertEquals("Test", responseTask.getTitle());
    }

    @Test
    void testReturnNotFoundTaskNotExist() throws IOException, InterruptedException {

        URI url = URI.create("http://localhost:" + PORT + "/tasks/999");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
        assertTrue(response.body().contains("не найдена"));
    }

    @Test
    void testUpdateTask() throws IOException, InterruptedException {
        // given
        Task task = new Task("Test", "Test",
                LocalDateTime.now(), Duration.ofMinutes(5));
        taskManager.createTask(task);

        task.setTitle("Updated");
        String json = gson.toJson(task);

        URI url = URI.create("http://localhost:" + PORT + "/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("Задача обновлена", response.body());

        Task updatedTask = taskManager.getTaskById(1);
        assertNotNull(updatedTask);
        assertEquals("Updated", updatedTask.getTitle());
    }

    @Test
    void testDeleteTaskById() throws IOException, InterruptedException {

        Task task = new Task("Test", "Test",
                LocalDateTime.now(), Duration.ofMinutes(5));
        taskManager.createTask(task);

        URI url = URI.create("http://localhost:" + PORT + "/tasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("удалена"));

        assertNull(taskManager.getTaskById(1));
    }

    @Test
    void testReturn400WhenDeleteWithIncorrectPath() throws IOException, InterruptedException {

        URI url = URI.create("http://localhost:" + PORT + "/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
        assertTrue(response.body().contains("Некорректный путь"));
    }
}
