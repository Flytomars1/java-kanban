import model.Epic;
import model.Subtask;
import model.Task;
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

public class HistoryEndpointTest extends BaseEndpointTest {
    private HttpClient client;

    @BeforeEach
    void setUpClient() {
        client = HttpClient.newHttpClient();
    }

    @Test
    void testReturnEmptyHistory() throws IOException, InterruptedException {

        URI url = URI.create("http://localhost:" + PORT + "/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body());
    }

    @Test
    void testReturnTasksInOrderOfViewing() throws IOException, InterruptedException {
        // given: создаём задачи
        Task task1 = new Task("test task", "test task", LocalDateTime.now(), Duration.ofMinutes(30));
        taskManager.createTask(task1);

        Epic epic1 = new Epic("test task", "test task");
        taskManager.createEpic(epic1);

        Subtask subtask1 = new Subtask("test task", "test task",
                LocalDateTime.now().plusHours(1), Duration.ofMinutes(60), 2);
        taskManager.createSubtask(subtask1);

        taskManager.getTaskById(1);
        taskManager.getEpicById(2);
        taskManager.getSubtaskById(3);
        taskManager.getTaskById(1);

        URI url = URI.create("http://localhost:" + PORT + "/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Subtask> history = Arrays.asList(gson.fromJson(response.body(), Subtask[].class));

        assertNotNull(history);
        assertEquals(3, history.size());

        assertEquals(2, history.get(0).getId());
        assertEquals(3, history.get(1).getId());
        assertEquals(1, history.get(2).getId());
    }

    @Test
    void shouldReturn405WhenPostToHistory() throws IOException, InterruptedException {

        URI url = URI.create("http://localhost:" + PORT + "/history");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{}"))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(405, response.statusCode());
        assertTrue(response.body().contains("не поддерживается"));
    }

    @Test
    void shouldReturn405WhenDeleteToHistory() throws IOException, InterruptedException {

        URI url = URI.create("http://localhost:" + PORT + "/history");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(405, response.statusCode());
        assertTrue(response.body().contains("не поддерживается"));
    }


}
