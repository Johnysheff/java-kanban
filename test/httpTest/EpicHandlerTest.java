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
import tracker.model.Epic;
import tracker.network.server.HttpTaskServer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class EpicHandlerTest {
    private HttpTaskServer server;
    private TaskManager taskManager;
    private Gson gson;
    private HttpClient client;

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
        client = HttpClient.newHttpClient();
    }

    @AfterEach
    void tearDown() {
        server.stop();
    }

    //Проверяем добавление нового эпика через HTTP-запрос
    @Test
    void testAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Test Epic", "Description");
        String epicJson = gson.toJson(epic);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Эпик должен быть успешно добавлен.");
        Epic test = gson.fromJson(response.body(), Epic.class);
        Epic savedEpic = taskManager.getEpicById(test.getTaskId());
        assertNotNull(savedEpic, "Эпик должен быть сохранен в менеджере.");
        assertEquals("Test Epic", savedEpic.getTaskName(), "Название эпика должно совпадать.");
    }
}