package tracker.test.taskTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.model.Status;
import tracker.model.Subtask;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SubtaskTest {
    private Subtask subtask;

    @BeforeEach
    void setUp() {
        subtask = new Subtask("Купить краску", "Купить черную краску", 1);
        subtask.setTaskId(1); // Присваиваем ID для подзадачи
    }

    @Test
    void shouldBeEqualIfIdsAreEqual() {
        Subtask subtask2 = new Subtask("Купить мелки", "Купить мелки всех цветов", 1);
        subtask2.setTaskId(1); // Присваиваем одинаковый ID
        assertEquals(subtask, subtask2, "Подзадачи должны быть равны, если у них одинаковый ID.");
    }

    @Test
    void shouldReturnCorrectEpicId() {
        assertEquals(1, subtask.getEpicId(), "ID эпика не совпадает.");
    }

    @Test
    void shouldSetAndGetStatus() {
        subtask.setStatus(Status.IN_PROGRESS);
        assertEquals(Status.IN_PROGRESS, subtask.getStatus(), "Статус подзадачи не установлен корректно.");
    }
}