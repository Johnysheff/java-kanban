import tracker.controllers.TaskManager;
import tracker.model.Epic;
import tracker.model.Status;
import tracker.model.Subtask;
import tracker.model.Task;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = new TaskManager();

        // Создание задач
        Task task1 = new Task("Первая основная задача", "Описание первой основной задачи");
        Task task2 = new Task("Вторая основная задача", "Описание второй основной задачи");

        // Создание эпиков
        Epic epic1 = new Epic("Первая эпическая задача", "Описание первой эпической задачи");
        Epic epic2 = new Epic("Вторая эпическая задача", "Описание второй эпической задачи");

        // Создание подзадач
        Subtask subTask1 = new Subtask("Первая подзадача первой эпической задачи",
                "Описание первой подзадачи первой эпической задачи",
                epic1.getTaskId());
        Subtask subTask2 = new Subtask("Вторая подзадача первой эпической задачи",
                "Описание второй подзадачи первой эпической задачи",
                epic1.getTaskId());
        Subtask subTask3 = new Subtask("Первая подзадача второй эпической задачи",
                "Описание первой подзадачи второй эпической задачи",
                epic2.getTaskId());

        // Добавление задач и эпиков в Менеджер
        manager.addTask(task1);
        manager.addTask(task2);
        manager.addTask(epic1);
        manager.addTask(epic2);
        manager.addTask(subTask1);
        manager.addTask(subTask2);
        manager.addTask(subTask3);

        // Печатаем все основные задачи
        System.out.println("Все основные задачи:");
        for (Task task : manager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("--------------------------"); //разделитель текста в консоли

        // Печатаем все эпики
        System.out.println("Все эпики:");
        for (Epic epic : manager.getEpics()) {
            System.out.println(epic);
        }
        System.out.println("--------------------------"); //разделитель текста в консоли

        // Печатаем все подзадачи
        System.out.println("Все подзадачи:");
        for (Subtask subtask : manager.getSubtasks()) {
            System.out.println(subtask);
        }
        System.out.println("--------------------------"); //разделитель текста в консоли

        // Изменение статусов и печать
        subTask1.setStatus(Status.DONE);
        subTask2.setStatus(Status.IN_PROGRESS);
        manager.updateTask(subTask1);
        manager.updateTask(subTask2);
        manager.updateTask(epic1); // Обновляем статус эпика
        System.out.println("Статусы после обновления:");
        System.out.println(task1);
        System.out.println(task2);
        System.out.println(subTask1);
        System.out.println(subTask2);
        System.out.println(epic1);
        System.out.println("--------------------------");

        // Удаление подзадачи и эпика, печатаем оставшиеся задачи
        manager.removeTaskById(subTask1.getTaskId());
        manager.removeTaskById(epic2.getTaskId());
        System.out.println("Все задачи после удаления подзадачи и эпика:");
        for (Task task : manager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("--------------------------");

        // Проверка подзадач эпика
        System.out.println("Подзадачи эпика 1:");
        System.out.println(manager.getEpicSubTasks(epic1.getTaskId()));
        System.out.println("--------------------------");
    }
}