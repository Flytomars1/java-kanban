import model.Epic;
import model.Subtask;
import model.TaskStatus;
import org.junit.jupiter.api.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class SubtaskEndpointTest extends BaseEndpointTest {
    private HttpClient client;

    @BeforeEach
    void setUpClient() {
        client = HttpClient.newHttpClient();
    }

    @Test
    void testReturnEmptyWhenNoSubtasks() throws IOException, InterruptedException {

        URI url = URI.create("http://localhost:" + PORT + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body());
    }

    @Test
    void testCreateSubtask() throws IOException, InterruptedException {

        Epic epic = new Epic("test epic", "test epic");
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask("test subtask", "test subtask",
                LocalDateTime.now(), Duration.ofMinutes(30), 1);
        String json = gson.toJson(subtask);

        URI url = URI.create("http://localhost:" + PORT + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals("Подзадача создана", response.body());

        Subtask savedSubtask = taskManager.getSubtaskById(2);
        assertNotNull(savedSubtask);
        assertEquals("test subtask", savedSubtask.getTitle());
        assertEquals(1, savedSubtask.getEpicId());

        Epic updatedEpic = taskManager.getEpicById(1);
        assertEquals(TaskStatus.NEW, updatedEpic.getStatus());
        assertTrue(updatedEpic.getSubtaskIds().contains(2));
    }

    @Test
    void testReturnSubtaskById() throws IOException, InterruptedException {

        Epic epic = new Epic("test epic", "test epic");
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask("test subtask", "test subtask",
                LocalDateTime.now(), Duration.ofMinutes(30), 1);
        taskManager.createSubtask(subtask);

        URI url = URI.create("http://localhost:" + PORT + "/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Subtask responseSubtask = gson.fromJson(response.body(), Subtask.class);
        assertNotNull(responseSubtask);
        assertEquals(2, responseSubtask.getId());
        assertEquals("test subtask", responseSubtask.getTitle());
    }

    @Test
    void testReturnNotFoundSubtaskNotExist() throws IOException, InterruptedException {

        URI url = URI.create("http://localhost:" + PORT + "/subtasks/999");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
        assertTrue(response.body().contains("не найдена"));
    }

    @Test
    void shouldUpdateSubtask() throws IOException, InterruptedException {

        Epic epic = new Epic("test epic", "test epic");
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask("test subtask", "test subtask",
                LocalDateTime.now(), Duration.ofMinutes(30), 1);
        taskManager.createSubtask(subtask);

        subtask.setTitle("test subtask1");
        String json = gson.toJson(subtask);

        URI url = URI.create("http://localhost:" + PORT + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("Подзадача обновлена", response.body());

        Subtask updatedSubtask = taskManager.getSubtaskById(2);
        assertNotNull(updatedSubtask);
        assertEquals("test subtask1", updatedSubtask.getTitle());
    }

    @Test
    void testReturn404WhenUpdatingSubtaskNonExistentEpic() throws IOException, InterruptedException {

        Subtask subtask = new Subtask("test", "test",
                LocalDateTime.now(), Duration.ofMinutes(30), 999);
        String json = gson.toJson(subtask);

        URI url = URI.create("http://localhost:" + PORT + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
        assertTrue(response.body().contains("Эпик с id=999 не найден"));
    }

    @Test
    void testDeleteSubtaskById() throws IOException, InterruptedException {
        // given: создадим эпик и подзадачу
        Epic epic = new Epic("test epic", "test epic");
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask("test subtask", "test subtask",
                LocalDateTime.now(), Duration.ofMinutes(30), 1);
        taskManager.createSubtask(subtask);

        URI url = URI.create("http://localhost:" + PORT + "/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("удалена"));

        assertNull(taskManager.getSubtaskById(1));

        Epic updatedEpic = taskManager.getEpicById(1);
        assertFalse(updatedEpic.getSubtaskIds().contains(2));
        assertEquals(TaskStatus.NEW, updatedEpic.getStatus());
    }

    @Test
    void testReturn400WhenDeleteWithIncorrectPath() throws IOException, InterruptedException {

        URI url = URI.create("http://localhost:" + PORT + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
        assertTrue(response.body().contains("Некорректный путь"));
    }
}
