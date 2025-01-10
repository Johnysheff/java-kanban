package tracker.test.taskTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.model.Epic;
import tracker.model.Subtask;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest {
    private Epic epic;

    @BeforeEach
    void setUp() {
        epic = new Epic("Сделать дипломную работу", "Нужно успеть за месяц");
        epic.setTaskId(1);
    }

    @Test
    void shouldBeEqualIfIdsAreEqual() {
        Epic epic2 = new Epic("Сделать тз", "Сегодня крайний день");
        epic2.setTaskId(1); // Присваиваем одинаковый ID
        assertEquals(epic, epic2, "Эпики должны быть равны, если у них одинаковый ID.");
    }

    @Test
    void shouldAddAndRetrieveSubtasks() {
        Subtask subtask = new Subtask("Приобрести материалы", "Купить бумагу и ручки", epic.getTaskId());
        subtask.setTaskId(2); // Присваиваем ID для подзадачи

        epic.addSubTask(subtask);
        assertEquals(1, epic.getSubTasks().size(), "Эпик должен содержать одну подзадачу.");
        assertEquals(subtask, epic.getSubTasks().get(0), "Подзадача не добавлена к эпическому заданию.");
    }

    @Test
    void shouldNotAddItselfAsSubtask() {
        // Проверяем, что добавление самого Epic в подзадачи не допускается,
        // Мы можем попытаться создать подзадачу с неправильным ID
        Subtask invalidSubtask = new Subtask("Подзадача", "Описание", epic.getTaskId());
        invalidSubtask.setTaskId(epic.getTaskId()); // Подзадача с тем же ID, что и эпик
        epic.addSubTask(invalidSubtask);
        assertEquals(0, epic.getSubTasks().size(), "Эпик не должен добавлять себя как подзадачу.");
    }
}
