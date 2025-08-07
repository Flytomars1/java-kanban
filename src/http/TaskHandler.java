package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.NotFoundException;
import manager.TaskManager;
import model.Task;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class TaskHandler extends UserHandler {

    public TaskHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    protected void handleGet(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        if (path.equals("/tasks")) {
            sendJson(exchange, gson.toJson(taskManager.getTasks().values()));
        } else {
            int id = extractIdFromPath(path, "/tasks");
            if (id == -1) {
                sendResponse(exchange, 400, "Некорректный путь", "text/plain; charset=utf-8");
                return;
            }
            Task task = taskManager.getTaskById(id);
            if (task == null) {
                throw new NotFoundException("Задача с id=" + id + " не найдена");
            }
            sendJson(exchange, gson.toJson(task));
        }
    }

    @Override
    protected void handlePost(HttpExchange exchange) throws IOException {
        InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        Task task = gson.fromJson(reader, Task.class);

        if (task == null) {
            throw new IllegalArgumentException("Некорректный JSON");
        }

        if (task.getId() == 0) {
            if (taskManager.hasIntersection(task)) {
                sendHasOverlaps(exchange);
                return;
            }
            taskManager.createTask(task);
            sendText(exchange, "Задача создана", 201);
        } else {
            if (taskManager.getTaskById(task.getId()) == null) {
                sendNotFound(exchange, "Задача с id=" + task.getId() + " не найдена");
            }
            if (taskManager.hasIntersection(task)) {
                sendHasOverlaps(exchange);
                return;
            }
            taskManager.updateTask(task);
            sendText(exchange, "Задача обновлена", 200);
        }
    }

    @Override
    protected void handleDelete(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        int id = extractIdFromPath(path, "/tasks");

        if (id == -1) {
            sendResponse(exchange, 400, "Некорректный путь", "text/plain; charset=utf-8");
            return;
        }

        boolean removed = taskManager.deleteTaskById(id);

        if (!removed) {
            throw new NotFoundException("Задача с id=" + id + " не найдена");
        }
        sendText(exchange, "Задача с id=" + id + " удалена");
    }
}
