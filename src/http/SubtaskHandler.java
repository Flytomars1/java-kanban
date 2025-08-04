package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.NotFoundException;
import manager.TaskManager;
import model.Subtask;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class SubtaskHandler extends UserHandler implements HttpHandler {
    public SubtaskHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    protected void handleGet(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        if (path.equals("/subtasks")) {
            sendJson(exchange, gson.toJson(taskManager.getSubtasks().values()));
        } else {
            int id = extractIdFromPath(path, "/subtasks");
            if (id == -1) {
                sendResponse(exchange, 400, "Некорректный путь", "text/plain; charset=utf-8");
                return;
            }
            Subtask subtask = taskManager.getSubtaskById(id);
            if (subtask == null) {
                throw new NotFoundException("Подзадача с id=" + id + " не найдена");
            }
            sendJson(exchange, gson.toJson(subtask));
        }
    }

    @Override
    protected void handlePost(HttpExchange exchange) throws IOException {
        InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        Subtask subtask = gson.fromJson(reader, Subtask.class);

        if (subtask == null) {
            throw new IllegalArgumentException("Некорректный JSON");
        }

        if (taskManager.getEpicById(subtask.getEpicId()) == null) {
            throw new NotFoundException("Эпик с id=" + subtask.getEpicId() + " не найден");
        }

        if (subtask.getId() == 0) {
            if (taskManager.hasIntersection(subtask)) {
                throw new IllegalArgumentException("Есть пересечения по времени");
            }
            taskManager.createSubtask(subtask);
            sendText(exchange, "Подзадача создана", 201);
        } else {
            taskManager.getSubtaskById(subtask.getId());
            if (taskManager.hasIntersection(subtask)) {
                throw new IllegalArgumentException("Есть пересечения по времени");
            }
            taskManager.updateSubtask(subtask);
            sendText(exchange, "Подзадача обновлена");
        }
    }

    @Override
    protected void handleDelete(HttpExchange exchange) throws IOException {
        int id = extractIdFromPath(exchange.getRequestURI().getPath(), "/subtasks"); // или "/subtasks"
        if (id == -1) {
            sendResponse(exchange, 400, "Некорректный путь", "text/plain; charset=utf-8");
            return;
        }
        boolean removed = taskManager.deleteSubtaskById(id); // или deleteSubtaskById
        if (!removed) {
            throw new NotFoundException("Подзадача с id=" + id + " не найдена");
        }
        sendText(exchange, "Подзадача с id=" + id + " удалена");
    }
}
