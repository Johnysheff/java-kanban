package tracker.test.taskTest;

import tracker.model.Status;
import tracker.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    private Task task;

    @BeforeEach
    void setUp() {
        task = new Task("Тестовая задача", "Описание задачи");
        task.setTaskId(1); // Присваиваем ID для задачи
    }

    @Test
    void shouldBeEqualIfIdsAreEqual() {
        Task task2 = new Task("Другая задача", "Описание другой задачи");
        task2.setTaskId(1); // Присваиваем одинаковый ID
        assertEquals(task, task2, "Задачи должны быть равны, если у них одинаковый ID.");
    }

    @Test
    void shouldReturnTaskNameAndDescription() {
        assertEquals("Тестовая задача", task.getTaskName(), "Имя задачи не совпадает.");
        assertEquals("Описание задачи", task.getDescription(), "Описание задачи не совпадает.");
    }

    @Test
    void shouldSetAndGetStatus() {
        task.setStatus(Status.DONE);
        assertEquals(Status.DONE, task.getStatus(), "Статус задачи не установлен корректно.");
    }
}