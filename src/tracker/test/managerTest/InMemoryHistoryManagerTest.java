package tracker.test.managerTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.history.HistoryManager;
import tracker.history.InMemoryHistoryManager;
import tracker.model.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

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

    @Test
    void limitHistorySize() {
        for (int i = 1; i <= 12; i++) {
            Task task = new Task("Задача " + i, "Описание задачи " + i);
            task.setTaskId(i);
            historyManager.add(task);
        }

        List<Task> history = historyManager.getHistory();
        assertEquals(10, history.size(), "История должна содержать только последние 10 задач.");
    }

    @Test
    void ensureUniqueHistory() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task = new Task("Уникальная задача", "Описание уникальной задачи");
        task.setTaskId(1);

        historyManager.add(task);
        historyManager.add(task); // добавляем задачу повторно

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "История должна содержать только одну задачу.");
    }

    @Test
    void ensureLastAddedIsPresentWithOverLimit() {
        for (int i = 1; i <= 16; i++) {
            Task task = new Task("Задача №" + i, "Описание задачи №" + i);
            task.setTaskId(i);
            historyManager.add(task);
        }

        assertEquals(10, historyManager.getHistory().size(), "История хранимых задач не заполнена до лимита");

        Task overlimitTask = new Task("задача с переполнением", "задача");
        overlimitTask.setTaskId(11);
        historyManager.add(overlimitTask);

        Task lastTaskInHistory = historyManager.getHistory().getLast();
        assertNotNull(lastTaskInHistory, "Последняя задача из истории - null");
        assertEquals(overlimitTask, lastTaskInHistory,
                "Последняя добавленная залдача отличается от последней в истории");
    }
}