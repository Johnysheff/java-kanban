package managerTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.controllers.TaskManager;
import tracker.model.Epic;
import tracker.model.Status;
import tracker.model.Subtask;
import tracker.model.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    protected abstract T createTaskManager();

    @BeforeEach
    void setUp() {
        taskManager = createTaskManager();
    }

    //Проверям добавление новой задачи и её корректное сохранение
    @Test
    void addNewTask() {
        Task task = new Task("Собрать мебель", "Купить и собрать шкаф");
        taskManager.addTask(task);

        final Task savedTask = taskManager.getTaskById(task.getTaskId());
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getTasks();
        assertNotNull(tasks, "Задачи не возвращены.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    //Проверям добавление нового эпика и его корректное сохранение
    @Test
    void addNewEpic() {
        Epic epic = new Epic("Ремонт", "Детальный план ремонта квартиры");
        taskManager.addTask(epic);

        final Epic savedEpic = taskManager.getEpicById(epic.getTaskId());
        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");

        final List<Epic> epics = taskManager.getEpics();
        assertNotNull(epics, "Эпики не возвращены.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.get(0), "Эпики не совпадают.");
    }

    //Проверяем добавление новой подзадачи и её корректное сохранение
    @Test
    void addNewSubtask() {
        Epic epic = new Epic("Ремонт", "Детальный план ремонта квартиры");
        taskManager.addTask(epic);
        Subtask subtask = new Subtask("Купить обои", "Купить обои для кухни", epic.getTaskId());

        taskManager.addTask(subtask);

        final Subtask savedSubtask = taskManager.getSubtaskById(subtask.getTaskId());
        assertNotNull(savedSubtask, "Подзадача не найдена.");
        assertEquals(subtask, savedSubtask, "Подзадачи не совпадают.");

        final List<Subtask> subtasks = taskManager.getSubtasks();
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask, subtasks.get(0), "Подзадачи не совпадают.");
    }

    //Проверяем равенство задач по id
    @Test
    void shouldCheckTaskEqualityById() {
        Task task1 = new Task("Задача 1", "Описание задачи 1");
        task1.setTaskId(1);
        Task task2 = new Task("Задача 2", "Описание задачи 2");
        task2.setTaskId(1);

        assertEquals(task1, task2, "Задачи должны быть равны, если их ID равны.");
    }

    //Проверяем чтобы эпик не содержал подзадачу с одинаковым id
    @Test
    void shouldNotSetEpicAsItsSubtask() {
        Epic epic = new Epic("Эпик", "Описание эпика");
        epic.setTaskId(3);
        Subtask subtask = new Subtask("Подзадача", "Описание подзадачи", epic.getTaskId());
        subtask.setTaskId(epic.getTaskId());
        taskManager.addTask(epic);
        taskManager.addTask(subtask);

        final List<Subtask> subtasksForEpic = taskManager.getSubtasksForEpic(epic.getTaskId());
        assertEquals(0, subtasksForEpic.size(), "Эпик не должен содержать подзадачу с тем же ID.");
    }

    //Проверям корректность работы менеджера истории задач
    @Test
    void historyManagerShouldReturnTaskHistory() {
        Task task = new Task("Задача для истории", "Описание задачи");
        task.setTaskId(4);
        taskManager.addTask(task);
        taskManager.getTaskById(task.getTaskId());

        final List<Task> history = taskManager.getHistory();
        assertNotNull(history, "История не должна быть пустой.");
        assertEquals(1, history.size(), "История должна содержать одну задачу.");
        assertEquals(task, history.get(0), "История не содержит правильную задачу.");
    }

    //Проверяем историю на содержание уникальной задачи
    @Test
    void ensureUniqueHistory() {
        Task task = new Task("Уникальная задача", "Описание уникальной задачи");
        task.setTaskId(5);

        taskManager.addTask(task);
        taskManager.getTaskById(task.getTaskId());

        // Добавляем задачу повторно
        taskManager.getTaskById(task.getTaskId());

        List<Task> history = taskManager.getHistory();
        assertEquals(1, history.size(), "История должна содержать только одну уникальную задачу.");
    }

    //Удаление задачи по id
    @Test
    void removeTaskById() {
        Task task = new Task("Написать отчет", "Подготовить отчет за месяц");
        taskManager.addTask(task);
        int taskId = task.getTaskId();

        taskManager.removeTaskById(taskId);
        assertNull(taskManager.getTaskById(taskId), "Задача должна быть удалена.");
    }

    //Удаление эпика по id
    @Test
    void removeEpicById() {
        Epic epic = new Epic("Ремонт", "Детальный план ремонта квартиры");
        taskManager.addTask(epic);

        Subtask subtask = new Subtask("Купить обои", "Купить обои для кухни", epic.getTaskId());
        taskManager.addTask(subtask);

        int epicId = epic.getTaskId();
        taskManager.removeEpicById(epicId);

        assertNull(taskManager.getEpicById(epicId), "Эпик должен быть удален.");
        assertNull(taskManager.getSubtaskById(subtask.getTaskId()),
                "Подзадача должна быть удалена с удалением эпика.");
    }

    //Проверяем,что задача в менеджере остается неизменной после добавления
    @Test
    void ensureTaskIsImmutableWhenAddedToManager() {
        Task task = new Task("Неизменная задача", "Описание неизменной задачи");
        task.setTaskId(6);
        taskManager.addTask(task);

        Task clonedTask = new Task(task.getTaskName(), task.getDescription());
        clonedTask.setTaskId(task.getTaskId());
        clonedTask.setStatus(Status.DONE);

        final Task savedTask = taskManager.getTaskById(task.getTaskId());
        assertEquals(Status.NEW, savedTask.getStatus(), "Статус задачи в менеджере не должен измениться.");
    }

    //Проверяем расчет статуса эпика
    @Test
    void testEpicStatusCalculation() {
        Epic epic = new Epic("Эпик", "Описание эпика");
        taskManager.addTask(epic);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание 1", epic.getTaskId());
        taskManager.addTask(subtask1);
        assertEquals(Status.NEW, epic.getStatus(), "Статус эпика должен быть NEW.");

        Subtask subtask2 = new Subtask("Подзадача 2", "Описание 2", epic.getTaskId());
        taskManager.addTask(subtask2);
        subtask2.setStatus(Status.DONE);
        taskManager.updateTask(subtask2);
        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Статус эпика должен быть IN_PROGRESS.");
        subtask1.setStatus(Status.DONE);
        taskManager.updateTask(subtask1);
        assertEquals(Status.DONE, epic.getStatus(), "Статус эпика должен быть DONE.");
    }

    //Проверяем как статус эпика меняется в зависимости от статусов подзадач
    @Test
    void testEpicStatusEdgeCases() {
        Epic epic = new Epic("Эпик", "Описание эпика");
        taskManager.addTask(epic);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание 1", epic.getTaskId());
        taskManager.addTask(subtask1);
        subtask1.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(subtask1);

        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Статус эпика должен быть IN_PROGRESS.");
    }
}