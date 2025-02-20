package tracker.network.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import tracker.controllers.TaskManager;
import tracker.model.Epic;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EpicHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;


    public EpicHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            switch (method) {
                case "GET":
                    handleGetRequest(exchange, path);
                    break;
                case "POST":
                    handlePostRequest(exchange);
                    break;
                case "DELETE":
                    handleDeleteRequest(exchange, path);
                    break;
                default:
                    sendNotFound(exchange);
            }
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }

    private void handleGetRequest(HttpExchange exchange, String path) throws IOException {
        if (path.equals("/epics")) {
            List<Epic> epics = taskManager.getEpics();
            String response = gson.toJson(epics);
            sendText(exchange, response, 200);
        } else {
            sendNotFound(exchange);
        }
    }

    private void handlePostRequest(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        Epic epic = gson.fromJson(body, Epic.class);

        try {
            Epic newEpic = (Epic) taskManager.addTask(epic);
            sendText(exchange, gson.toJson(newEpic), 201);
        } catch (IllegalArgumentException e) {
            sendHasInteractions(exchange);
        }
    }

    private void handleDeleteRequest(HttpExchange exchange, String path) throws IOException {
        String[] parts = path.split("/");
        if (parts.length == 3) {
            int epicId = Integer.parseInt(parts[2]);
            taskManager.removeEpicById(epicId);
            sendText(exchange, "Epic deleted", 200);
        } else {
            sendNotFound(exchange);
        }
    }
}