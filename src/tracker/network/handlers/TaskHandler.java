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
    private final TaskManager taskManager;
    private final Gson gson;

    public TaskHandler(TaskManager taskManager, Gson gson) {
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
        if (path.equals("/tasks")) {
            List<Task> tasks = taskManager.getTasks();
            String response = gson.toJson(tasks);
            sendText(exchange, response, 200);
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