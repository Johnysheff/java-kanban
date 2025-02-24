package tracker.network.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import tracker.controllers.TaskManager;
import tracker.model.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SubtaskHandler extends BaseHttpHandler {

    public SubtaskHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
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
                case "PUT":
                    handlePutRequest(exchange);
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
        if (path.equals("/subtasks")) {
            List<Subtask> subtasks = taskManager.getSubtasks();
            String response = gson.toJson(subtasks);
            sendText(exchange, response, 200);
        } else if (path.startsWith("/subtasks/")) {
            String[] parts = path.split("/");
            if (parts.length == 3) {
                int subtaskId = Integer.parseInt(parts[2]);
                Subtask subtask = taskManager.getSubtaskById(subtaskId);
                if (subtask != null) {
                    String response = gson.toJson(subtask);
                    sendText(exchange, response, 200);
                } else {
                    sendNotFound(exchange);
                }
            } else {
                sendNotFound(exchange);
            }
        } else {
            sendNotFound(exchange);
        }
    }

    private void handlePostRequest(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        Subtask subtask = gson.fromJson(body, Subtask.class);

        try {
            taskManager.addTask(subtask);
            sendText(exchange, "Subtask added", 201);
        } catch (IllegalArgumentException e) {
            sendHasInteractions(exchange);
        }
    }

    private void handlePutRequest(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        Subtask updatedSubtask = gson.fromJson(body, Subtask.class);

        try {
            Subtask existingSubtask = taskManager.getSubtaskById(updatedSubtask.getTaskId());
            if (existingSubtask != null) {
                taskManager.updateTask(updatedSubtask);
                sendText(exchange, gson.toJson(updatedSubtask), 200);
            } else {
                sendNotFound(exchange);
            }
        } catch (IllegalArgumentException e) {
            sendHasInteractions(exchange);
        }
    }

    private void handleDeleteRequest(HttpExchange exchange, String path) throws IOException {
        String[] parts = path.split("/");
        if (parts.length == 3) {
            int subtaskId = Integer.parseInt(parts[2]);
            taskManager.removeSubtaskById(subtaskId);
            sendText(exchange, "Subtask deleted", 200);
        } else {
            sendNotFound(exchange);
        }
    }
}