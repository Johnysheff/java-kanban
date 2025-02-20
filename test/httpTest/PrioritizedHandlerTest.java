package httpTest;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.controllers.InMemoryTaskManager;
import tracker.controllers.TaskManager;
import tracker.model.Task;
import tracker.network.server.HttpTaskServer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PrioritizedHandlerTest {
    private HttpTaskServer server;
    private TaskManager taskManager;
    private Gson gson;
    private HttpClient client;

    @BeforeEach
    void setUp() throws IOException {
        taskManager = new InMemoryTaskManager();
        server = new HttpTaskServer(taskManager);
        gson = new Gson();
        server.start();
        client = HttpClient.newHttpClient();
    }

    @AfterEach
    void tearDown() {
        server.stop();
    }

    //Проверяем получение приоритетных задач через HTTP-запрос
    @Test
    void testGetPrioritizedTasks() throws IOException, InterruptedException {
        Task task = new Task("Test Task", "Description");
        taskManager.addTask(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/prioritized"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Запрос должен быть успешным.");

        String responseBody = response.body();
        assertNotNull(responseBody, "Ответ не должен быть пустым.");
    }
}