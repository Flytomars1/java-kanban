package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends UserHandler {
    public HistoryHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    protected void handleGet(HttpExchange exchange) throws IOException {
        List<?> history = taskManager.getHistory();
        if (history.isEmpty()) {
            sendJson(exchange, "[]");
        } else {
            sendJson(exchange, gson.toJson(history));
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
