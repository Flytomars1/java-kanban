package http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.google.gson.Gson;
import manager.NotFoundException;
import manager.TaskManager;
import model.Epic;
import model.Subtask;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class EpicHandler extends UserHandler implements HttpHandler {
    public EpicHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    protected void handleGet(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] parts = path.split("/");

        if (parts.length == 2) {
            sendJson(exchange, gson.toJson(taskManager.getEpics().values()));
            return;
        }

        if (parts.length == 3) {
            try {
                int id = Integer.parseInt(parts[2]);
                Epic epic = taskManager.getEpicById(id);
                if (epic == null) {
                    throw new NotFoundException("Эпик с id=" + id + " не найден");
                }
                sendJson(exchange, gson.toJson(epic));
                return;
            } catch (NumberFormatException e) {
                sendResponse(exchange, 400, "Некорректный id", "text/plain; charset=utf-8");
                return;
            }
        }

        if (parts.length == 4 && parts[3].equals("subtasks")) {
            try {
                int id = Integer.parseInt(parts[2]);
                Epic epic = taskManager.getEpicById(id);
                if (epic == null) {
                    throw new NotFoundException("Эпик с id=" + id + " не найден");
                }
                List<Subtask> subtasks = epic.getSubtaskIds().stream()
                        .map(taskManager::getSubtaskById)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                sendJson(exchange, gson.toJson(subtasks));
                return;
            } catch (NumberFormatException e) {
                sendResponse(exchange, 400, "Некорректный id", "text/plain; charset=utf-8");
                return;
            }
        }
    }

    @Override
    protected void handlePost(HttpExchange exchange) throws IOException {
        InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        Epic epic = gson.fromJson(reader, Epic.class);

        if (epic == null) {
            throw new IllegalArgumentException("Некорректный Json");
        }

        if (epic.getId() == 0) {
            taskManager.createEpic(epic);
            sendText(exchange, "Эпик создан", 201);
        } else {
            if (taskManager.getEpicById(epic.getId()) == null) {
                sendNotFound(exchange, "Epic с id=" + epic.getId() + " не найден");
            }
            taskManager.updateEpic(epic);
            sendText(exchange, "Эпик обновлён");
        }
    }

    @Override
    protected void handleDelete(HttpExchange exchange) throws IOException {
        int id = extractIdFromPath(exchange.getRequestURI().getPath(), "/epics");
        if (id == -1) {
            sendResponse(exchange, 400, "Некорректный путь", "text/plain; charset=utf-8");
            return;
        }
        boolean removed = taskManager.deleteEpicById(id);
        if (!removed) {
            throw new NotFoundException("Эпик с id=" + id + " не найден");
        }
        sendText(exchange, "Эпик с id=" + id + " удален. Все подзадачи эпика удалены");
    }
}
