package httpTest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.adapters.DurationAdapter;
import tracker.adapters.LocalDateTimeAdapter;
import tracker.controllers.InMemoryTaskManager;
import tracker.controllers.TaskManager;
import tracker.model.Task;
import tracker.network.server.HttpTaskServer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpTaskServerTest {
    private HttpTaskServer server;
    private TaskManager taskManager;
    private Gson gson;

    private static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter().nullSafe());
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter().nullSafe());
        return gsonBuilder.create();
    }

    @BeforeEach
    void setUp() throws IOException {
        taskManager = new InMemoryTaskManager();
        server = new HttpTaskServer(taskManager);
        gson = getGson();
        server.start();
    }

    @AfterEach
    void tearDown() {
        server.stop();
    }

    //Проверяем добавление новой задачи через HTTP-запрос
    @Test
    void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("Test Task", "Description");
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Задача должна быть успешно добавлена.");
    }
}