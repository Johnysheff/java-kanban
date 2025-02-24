package tracker.network.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import tracker.controllers.TaskManager;
import tracker.model.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TaskHandler extends BaseHttpHandler {

    public TaskHandler(TaskManager taskManager, Gson gson) {
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
        if (path.equals("/tasks")) {
            List<Task> tasks = taskManager.getTasks();
            String response = gson.toJson(tasks);
            sendText(exchange, response, 200);
        } else if (path.startsWith("/tasks/")) {
            String[] parts = path.split("/");
            if (parts.length == 3) {
                int taskId = Integer.parseInt(parts[2]);
                Task task = taskManager.getTaskById(taskId);
                if (task != null) {
                    String response = gson.toJson(task);
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
        Task task = gson.fromJson(body, Task.class);

        try {
            Task newTask = taskManager.addTask(task);
            sendText(exchange, gson.toJson(newTask), 201);
        } catch (IllegalArgumentException e) {
            sendHasInteractions(exchange);
        }
    }

    private void handlePutRequest(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        Task updatedTask = gson.fromJson(body, Task.class);

        try {
            Task existingTask = taskManager.getTaskById(updatedTask.getTaskId());
            if (existingTask != null) {
                taskManager.updateTask(updatedTask);
                sendText(exchange, gson.toJson(updatedTask), 200);
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
            int taskId = Integer.parseInt(parts[2]);
            taskManager.removeTaskById(taskId);
            sendText(exchange, "Task deleted", 200);
        } else {
            sendNotFound(exchange);
        }
    }
}