import com.google.gson.Gson;
import http.HttpTaskServer;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

public class BaseEndpointTest {
    protected static final int PORT = 8080;
    protected static HttpTaskServer server;
    protected static TaskManager taskManager;
    protected static Gson gson;

    @BeforeAll
    static void setUpAll() {
        gson = Managers.getGson();
    }

    @BeforeEach
    void setUp() throws Exception {
        taskManager = Managers.getDefault();
        server = new HttpTaskServer(taskManager, gson);
        server.start();
    }

    @AfterEach
    void tearDown() {
        server.stop();
        //даем остановиться, не получается по другому
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
