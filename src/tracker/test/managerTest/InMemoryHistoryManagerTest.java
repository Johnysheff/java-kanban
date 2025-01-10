package tracker.test.managerTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.controllers.InMemoryTaskManager;
import tracker.history.HistoryManager;
import tracker.history.InMemoryHistoryManager;
import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    HistoryManager historyManager;
    InMemoryTaskManager taskManager;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
        taskManager = new InMemoryTaskManager();
    }

    //Тестирование добавления задач в историю
    @Test
    void addTaskToHistory() {
        Task task1 = new Task("Первая задача", "Описание первой задачи");
        task1.setTaskId(1);
        Task task2 = new Task("Вторая задача", "Описание второй задачи");
        task2.setTaskId(2);

        historyManager.add(task1);
        historyManager.add(task2);

        List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не должна быть пустой.");
        assertEquals(2, history.size(), "История должна содержать 2 задачи.");
    }

    // Проверка правильного порядка задач в истории после добавления
    @Test
    void ensureLastAddedIsPresent() {
        for (int i = 1; i <= 5; i++) {
            Task task = new Task("Задача " + i, "Описание задачи " + i);
            task.setTaskId(i);
            historyManager.add(task);
        }

        List<Task> history = historyManager.getHistory();
        assertEquals(5, history.size(), "История должна содержать 5 задач.");
        assertEquals("Задача 1", history.get(0).getTaskName(),
                "Самая старая задача должна быть Задача 1.");
        assertEquals("Задача 5", history.get(4).getTaskName(),
                "Самая новая задача должна быть Задача 5.");
    }

    // Проверка, что задача правильно удаляется из истории
    @Test
    void removeTaskFromHistory() {
        Task task = new Task("Задача для удаления", "Описание задачи");
        task.setTaskId(3);
        historyManager.add(task);
        historyManager.remove(3);

        List<Task> history = historyManager.getHistory();
        assertEquals(0, history.size(), "История должна быть пустой после удаления задачи.");
    }

    // Проверка наличия задачи в истории, после её добавления
    @Test
    void shouldContainTaskAfterAdding() {
        Task task = new Task("Уникальная задача", "Описание уникальной задачи");
        task.setTaskId(4);
        historyManager.add(task);

        List<Task> history = historyManager.getHistory();
        assertTrue(history.contains(task), "История должна содержать добавленную задачу.");
    }

    // Проверка целостности истории, после удаления задачи
    @Test
    void testRemovingFromHistoryUpdatesInternalStructure() {
        for (int i = 1; i <= 5; i++) {
            Task task = new Task("Задача " + i, "Описание задачи " + i);
            task.setTaskId(i);
            historyManager.add(task);
        }

        historyManager.remove(1);

        List<Task> history = historyManager.getHistory();
        assertEquals(4, history.size(), "История должна содержать 4 задачи после удаления.");
        assertEquals("Задача 2", history.get(0).getTaskName(),
                "Самой старой задачей должна быть Задача 2.");
    }

    // Проверка добавления задач в историю при получении задач из менеджера
    @Test
    void testGetTasksAddsToHistory() {
        Task task1 = new Task("Задача 1", "Описание задачи 1");
        task1.setTaskId(2);
        taskManager.addTask(task1);
        List<Task> tasks = taskManager.getTasks();
        List<Task> history = taskManager.getHistory();
        assertTrue(history.contains(task1), "Запрос задачи должен быть добавлен в историю.");
    }

    // Проверка добавления эпиков в историю при получении эпиков из менеджера
    @Test
    void testGetEpicsAddsToHistory() {
        Epic epic = new Epic("Эпик", "Описание эпика");
        epic.setTaskId(4);
        taskManager.addTask(epic);
        List<Epic> epics = taskManager.getEpics();
        List<Task> history = taskManager.getHistory();
        assertTrue(history.contains(epic), "Эпик должен быть добавлен в историю.");
    }

    // Проверка добавления подзадач в историю при получении подзадач из менеджера
    @Test
    void testGetSubtasksAddsToHistory() {
        Epic epic = new Epic("Эпик", "Описание эпика");
        epic.setTaskId(5);
        taskManager.addTask(epic);

        Subtask subtask = new Subtask("Подзадача", "Описание подзадачи", 5);
        subtask.setTaskId(6);
        taskManager.addTask(subtask);
        List<Subtask> subtasks = taskManager.getSubtasks();
        List<Task> history = taskManager.getHistory();
        assertTrue(history.contains(subtask), "Подзадача должна быть добавлена в историю.");
    }
}