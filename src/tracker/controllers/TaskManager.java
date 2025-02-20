package tracker.controllers;

import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;

import java.util.List;

public interface TaskManager {
    Task addTask(Task task);

    void updateTask(Task task);

    Task getTaskById(int taskId);

    Epic getEpicById(int epicId);

    Subtask getSubtaskById(int subtaskId);

    List<Task> getTasks();

    List<Epic> getEpics();

    List<Subtask> getSubtasks();

    void removeTaskById(int taskId);

    void removeEpicById(int epicId);

    void removeSubtaskById(int subtaskId);

    void removeAllTasks();

    void removeAllEpics();

    void removeAllSubtasks();

    List<Subtask> getSubtasksForEpic(int epicId);

    List<Task> getHistory();

    List<Task> getPrioritizedTasks(); // Новый метод
}