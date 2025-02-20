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

import static org.junit.jupiter.api.Assertions.*;

class TaskHandlerTest {
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

    //Проверяем добавление задачи с временем и продолжительностью через HTTP-запрос
    @Test
    void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("Test Task", "Description");
        task.setStartTime(LocalDateTime.now());
        task.setDuration(Duration.ofMinutes(30));

        String taskJson = gson.toJson(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Задача должна быть успешно добавлена.");

        Task newTask = gson.fromJson(response.body(), Task.class);
        Task savedTask = taskManager.getTaskById(newTask.getTaskId());
        assertNotNull(savedTask, "Задача должна быть сохранена в менеджере.");
        assertEquals("Test Task", savedTask.getTaskName(), "Название задачи должно совпадать.");
    }

    //Проверяем получение списка задач через HTTP-запрос
    @Test
    void testGetTasks() throws IOException, InterruptedException {
        Task task = new Task("Test Task", "Description");
        taskManager.addTask(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Запрос должен быть успешным.");

        String responseBody = response.body();
        assertNotNull(responseBody, "Ответ не должен быть пустым.");
    }

    //Проверяем удаление задачи через HTTP-запрос
    @Test
    void testDeleteTask() throws IOException, InterruptedException {
        Task task = new Task("Test Task", "Description");
        taskManager.addTask(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/" + task.getTaskId()))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Задача должна быть успешно удалена.");

        assertNull(taskManager.getTaskById(task.getTaskId()), "Задача должна быть удалена из менеджера.");
    }
}