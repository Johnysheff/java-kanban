package managerTest;

import org.junit.jupiter.api.Test;
import tracker.controllers.FileBackedTaskManager;
import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedTaskManagerTest {

    //Проверка сохранения и чтения из файла
    @Test
    void testSaveAndLoadWithTasks() throws IOException {
        File file = File.createTempFile("tasks", ".csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        // Создаем задачи
        Task task = new Task("Task 1", "Description 1");
        Epic epic = new Epic("Epic 1", "Description 1");
        Subtask subtask = new Subtask("Subtask 1", "Description 1", epic.getTaskId());

        // Добавляем задачи в менеджер
        manager.addTask(task);
        manager.addTask(epic);
        manager.addTask(subtask);


        manager.save();

        // Загружаем из файла
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        // Проверяем, что задачи загружены корректно
        assertEquals(1, loadedManager.getTasks().size(), "Должна быть одна задача.");
        assertEquals(1, loadedManager.getEpics().size(), "Должен быть один эпик.");
        assertEquals(1, loadedManager.getSubtasks().size(), "Должна быть одна подзадача.");

        Task loadedTask = loadedManager.getTasks().get(0);
        Epic loadedEpic = loadedManager.getEpics().get(0);
        Subtask loadedSubtask = loadedManager.getSubtasks().get(0);

        assertEquals(task.getTaskName(), loadedTask.getTaskName(), "Название задачи должно совпадать.");
        assertEquals(epic.getTaskName(), loadedEpic.getTaskName(), "Название эпика должно совпадать.");
        assertEquals(subtask.getTaskName(), loadedSubtask.getTaskName(), "Название подзадачи должно совпадать.");
    }
}