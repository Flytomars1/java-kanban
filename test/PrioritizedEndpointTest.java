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

public class PrioritizedEndpointTest extends BaseEndpointTest {
    private HttpClient client;

    @BeforeEach
    void setUpClient() {
        client = HttpClient.newHttpClient();
    }

    @Test
    void testTasksSortedByStartTime() throws IOException, InterruptedException {

        Task task1 = new Task("Ранняя задача", "Описание", LocalDateTime.now(), Duration.ofMinutes(30));
        taskManager.createTask(task1);

        Task task2 = new Task("Поздняя задача", "Описание", LocalDateTime.now().plusHours(2), Duration.ofMinutes(60));
        taskManager.createTask(task2);

        Task task3 = new Task("Средняя задача", "Описание", LocalDateTime.now().plusHours(1), Duration.ofMinutes(45));
        taskManager.createTask(task3);

        URI url = URI.create("http://localhost:" + PORT + "/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Subtask> prioritized = Arrays.asList(gson.fromJson(response.body(), Subtask[].class));

        assertNotNull(prioritized);
        assertEquals(3, prioritized.size());
        assertEquals("Ранняя задача", prioritized.get(0).getTitle());
        assertEquals("Средняя задача", prioritized.get(1).getTitle());
        assertEquals("Поздняя задача", prioritized.get(2).getTitle());
    }

    @Test
    void testReturn405WhenPostToPrioritized() throws IOException, InterruptedException {

        URI url = URI.create("http://localhost:" + PORT + "/prioritized");
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
    void testReturn405WhenDeleteToPrioritized() throws IOException, InterruptedException {

        URI url = URI.create("http://localhost:" + PORT + "/prioritized");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(405, response.statusCode());
        assertTrue(response.body().contains("не поддерживается"));
    }
}
