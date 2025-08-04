package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.NotFoundException;
import manager.TaskManager;

import java.io.IOException;

public abstract class UserHandler extends BaseHttpHandler implements HttpHandler {
    protected final TaskManager taskManager;
    protected final Gson gson;

    public UserHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public final void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        try {
            switch (method) {
                case "GET":
                    handleGet(exchange);
                    break;
                case "POST":
                    handlePost(exchange);
                    break;
                case "DELETE":
                    handleDelete(exchange);
                    break;
                default:
                    sendResponse(exchange, 405, "Метод не поддерживается", "text/plain; charset=utf-8");
            }
        } catch (NumberFormatException e) {
            sendResponse(exchange, 400, "Некорректный id", "text/plain; charset=utf-8");
        } catch (IllegalArgumentException e) {
            sendHasOverlaps(exchange);
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        } catch (Exception e) {
            sendResponse(exchange, 400, "Ошибка в запросе: " + e.getMessage(), "text/plain; charset=utf-8");
        } finally {
            exchange.close();
        }
    }

    protected int extractIdFromPath(String path, String taskType) {
        if (path == null || !path.startsWith(taskType + "/")) {
            return -1;
        }

        String[] parts = path.split("/");
        if (parts.length != 3) {
            return -1;
        }

        try {
            return Integer.parseInt(parts[2]);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    protected abstract void handleGet(HttpExchange exchange) throws IOException;

    protected abstract void handlePost(HttpExchange exchange) throws IOException;

    protected abstract void handleDelete(HttpExchange exchange) throws IOException;
}