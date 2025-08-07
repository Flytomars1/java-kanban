package http;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class BaseHttpHandler {

    protected void sendResponse(HttpExchange h, int statusCode, String response, String contentType) throws IOException {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().set("Content-Type", contentType);
        h.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = h.getResponseBody()) {
            os.write(bytes);
        }
    }

    protected void sendJson(HttpExchange h, String json) throws IOException {
        sendResponse(h, 200, json, "application/json; charset=utf-8");
    }

    protected void sendJson(HttpExchange h, String json, int statusCode) throws IOException {
        sendResponse(h, statusCode, json, "application/json; charset=utf-8");
    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        sendResponse(h, 200, text, "text/plain; charset=utf-8");
    }

    protected void sendText(HttpExchange h, String text, int statusCode) throws IOException {
        sendResponse(h, statusCode, text, "text/plain; charset=utf-8");
    }

    protected void sendNotFound(HttpExchange h, String message) throws IOException {
        sendResponse(h, 404, message, "text/plain; charset=utf-8");
    }

    protected void sendHasOverlaps(HttpExchange h) throws IOException {
        sendResponse(h, 406, "Задача пересекается с существующей", "text/plain; charset=utf-8");
    }
}
