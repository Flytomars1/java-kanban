package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import model.Task;

import java.io.IOException;
import java.util.List;

public class PrioritizedHandler extends UserHandler implements HttpHandler {
    public PrioritizedHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    protected void handleGet(HttpExchange exchange) throws IOException {
        List<Task> prioritized = taskManager.getPrioritizedTasks();
        if (prioritized.isEmpty()) {
            sendJson(exchange, "[]");
        } else {
            sendJson(exchange, gson.toJson(prioritized));
        }
    }

    @Override
    protected void handlePost(HttpExchange exchange) throws IOException {
        sendResponse(exchange, 405, "Метод не поддерживается", "text/plain; charset=utf-8");
    }

    @Override
    protected void handleDelete(HttpExchange exchange) throws IOException {
        sendResponse(exchange, 405, "Метод не поддерживается", "text/plain; charset=utf-8");
    }
}
