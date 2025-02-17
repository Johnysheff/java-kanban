package managerTest;

import org.junit.jupiter.api.Test;
import tracker.controllers.InMemoryTaskManager;
import tracker.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    protected InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }

    //Проверяем корректность получения задач в порядке приоритета
    @Test
    void testGetPrioritizedTasks() {
        Task task1 = new Task("Task 1", "Description 1");
        task1.setStartTime(LocalDateTime.now());
        task1.setDuration(Duration.ofMinutes(30));

        Task task2 = new Task("Task 2", "Description 2");
        task2.setStartTime(task1.getStartTime().plusMinutes(60));
        task2.setDuration(Duration.ofMinutes(30));

        taskManager.addTask(task1);
        taskManager.addTask(task2);

        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        assertEquals(2, prioritizedTasks.size(), "Должно быть 2 задачи в списке приоритетов.");
        assertEquals(task1, prioritizedTasks.get(0), "Первая задача должна быть Task 1.");
        assertEquals(task2, prioritizedTasks.get(1), "Вторая задача должна быть Task 2.");
    }
}