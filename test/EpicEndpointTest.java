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
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EpicEndpointTest extends BaseEndpointTest {
    private HttpClient client;

    @BeforeEach
    void setUpClient() {
        client = HttpClient.newHttpClient();
    }

    @Test
    void testReturnEmptyWhenNoEpics() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:" + PORT + "/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body());
    }

    @Test
    void testCreateEpic() throws IOException, InterruptedException {

        Epic epic = new Epic("test", "test");
        String json = gson.toJson(epic);

        URI url = URI.create("http://localhost:" + PORT + "/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals("Эпик создан", response.body());

        Epic savedEpic = taskManager.getEpicById(1);
        assertNotNull(savedEpic);
        assertEquals("test", savedEpic.getTitle());
        assertEquals(TaskStatus.NEW, savedEpic.getStatus());
    }

    @Test
    void testReturnEpicById() throws IOException, InterruptedException {

        Epic epic = new Epic("test", "test");
        taskManager.createEpic(epic);

        URI url = URI.create("http://localhost:" + PORT + "/epics/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Epic responseEpic = gson.fromJson(response.body(), Epic.class);
        assertNotNull(responseEpic);
        assertEquals(1, responseEpic.getId());
        assertEquals("test", responseEpic.getTitle());
    }

    @Test
    void testReturnNotFoundEpicNotExist() throws IOException, InterruptedException {

        URI url = URI.create("http://localhost:" + PORT + "/epics/999");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
        assertTrue(response.body().contains("не найден"));
    }

    @Test
    void testUpdateEpic() throws IOException, InterruptedException {

        Epic epic = new Epic("test", "test");
        taskManager.createEpic(epic);

        epic.setTitle("test1");
        String json = gson.toJson(epic);

        URI url = URI.create("http://localhost:" + PORT + "/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("Эпик обновлён", response.body());

        Epic updatedEpic = taskManager.getEpicById(1);
        assertNotNull(updatedEpic);
        assertEquals("test1", updatedEpic.getTitle());
    }

    @Test
    void testDeleteEpicById() throws IOException, InterruptedException {
        Epic epic = new Epic("test", "test");
        taskManager.createEpic(epic);

        URI url = URI.create("http://localhost:" + PORT + "/epics/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("удален"));

        assertNull(taskManager.getEpicById(1));
    }

    @Test
    void testReturn400WhenDeleteWithIncorrectPat() throws IOException, InterruptedException {

        URI url = URI.create("http://localhost:" + PORT + "/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
        assertTrue(response.body().contains("Некорректный путь"));
    }

    @Test
    void testReturnSubtasksOfEpic() throws IOException, InterruptedException {

        Epic epic = new Epic("test epic", "test epic");
        taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask("test subtask", "test subtask",
                LocalDateTime.now(), Duration.ofMinutes(5), 1);
        taskManager.createSubtask(subtask1);

        Subtask subtask2 = new Subtask("test subtask1", "test subtask1",
                LocalDateTime.now().plusHours(1), Duration.ofMinutes(5), 1);
        taskManager.createSubtask(subtask2);

        URI url = URI.create("http://localhost:" + PORT + "/epics/1/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        List<Subtask> subtasks = Arrays.asList(gson.fromJson(response.body(), Subtask[].class));
        assertNotNull(subtasks);
        assertEquals(2, subtasks.size());
        assertTrue(subtasks.stream().anyMatch(st -> st.getTitle().equals("test subtask")));
        assertTrue(subtasks.stream().anyMatch(st -> st.getTitle().equals("test subtask1")));
    }

    @Test
    void testReturnEmptyListWhenEpicWithNoSubtasks() throws IOException, InterruptedException {

        Epic epic = new Epic("test", "test");
        taskManager.createEpic(epic);

        URI url = URI.create("http://localhost:" + PORT + "/epics/1/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body());
    }
}
