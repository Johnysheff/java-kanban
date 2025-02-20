package tracker.network.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import tracker.controllers.TaskManager;
import tracker.model.Task;

import java.io.IOException;
import java.util.List;

public class PrioritizedHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public PrioritizedHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            if (method.equals("GET") && path.equals("/prioritized")) {
                List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
                String response = gson.toJson(prioritizedTasks);
                sendText(exchange, response, 200);
            } else {
                sendNotFound(exchange);
            }
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }
}