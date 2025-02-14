package tracker.controllers;

import tracker.history.HistoryManager;
import tracker.history.InMemoryHistoryManager;
import tracker.model.Epic;
import tracker.model.Status;
import tracker.model.Subtask;
import tracker.model.Task;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HistoryManager historyManager = new InMemoryHistoryManager();
    private int nextId = 1;
    private final TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime,
            Comparator.nullsLast(Comparator.naturalOrder())));


    @Override
    public void addTask(Task task) {
        if (task.getStartTime() != null && hasIntersections(task)) {
            throw new IllegalArgumentException("Задача пересекается по времени с другой задачей.");
        }
        task.setTaskId(nextId++);
        task.setStatus(Status.NEW);
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }

        switch (task.getType()) {
            case EPIC:
                epics.put(task.getTaskId(), (Epic) task);
                break;
            case SUBTASK:
                subtasks.put(task.getTaskId(), (Subtask) task);
                Epic epic = epics.get(((Subtask) task).getEpicId());
                if (epic != null) {
                    epic.addSubTask((Subtask) task);
                    updateEpicStatus(epic);
                }
                break;
            default:
                tasks.put(task.getTaskId(), task);
                break;
        }
    }

    @Override
    public void updateTask(Task task) {
        if (task.getStartTime() != null) {
            prioritizedTasks.remove(task);
            prioritizedTasks.add(task);
        }

        switch (task.getType()) {
            case EPIC:
                epics.put(task.getTaskId(), (Epic) task);
                break;
            case SUBTASK:
                subtasks.put(task.getTaskId(), (Subtask) task);
                Epic epic = epics.get(((Subtask) task).getEpicId());
                if (epic != null) {
                    updateEpicStatus(epic);
                }
                break;
            default:
                tasks.put(task.getTaskId(), task);
                break;
        }
    }

    @Override
    public Task getTaskById(int taskId) {
        Task task = tasks.get(taskId);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Epic getEpicById(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int subtaskId) {
        Subtask subtask = subtasks.get(subtaskId);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void removeTaskById(int taskId) {
        Task task = tasks.remove(taskId);
        if (task != null) {
            historyManager.remove(taskId);
            prioritizedTasks.remove(task);
        }
    }

    @Override
    public void removeEpicById(int epicId) {
        Epic epic = epics.remove(epicId);
        if (epic != null) {
            for (Subtask subtask : epic.getSubTasks()) {
                subtasks.remove(subtask.getTaskId());
                historyManager.remove(subtask.getTaskId());
                prioritizedTasks.remove(subtask);
            }
            historyManager.remove(epicId);
            prioritizedTasks.remove(epic);
        }
    }

    @Override
    public void removeSubtaskById(int subtaskId) {
        Subtask subtask = subtasks.remove(subtaskId);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubTask(subtask);
                updateEpicStatus(epic);
            }
            historyManager.remove(subtaskId);
            prioritizedTasks.remove(subtask);
        }
    }

    @Override
    public void removeAllTasks() {
        for (Task task : tasks.values()) {
            historyManager.remove(task.getTaskId());
            prioritizedTasks.remove(task);
        }
        tasks.clear();
    }

    @Override
    public void removeAllEpics() {
        for (Epic epic : epics.values()) {
            historyManager.remove(epic.getTaskId());
            prioritizedTasks.remove(epic);
            for (Subtask subtask : epic.getSubTasks()) {
                historyManager.remove(subtask.getTaskId());
                prioritizedTasks.remove(subtask);
            }
        }
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void removeAllSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getTaskId());
            prioritizedTasks.remove(subtask);
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubtaskIds();
        }
    }

    @Override
    public List<Subtask> getSubtasksForEpic(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return new ArrayList<>();
        }
        return epic.getSubTasks().stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    // Возвращаем список задач, отсортированных по приоритету
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    //Проверяем пересечение задач по времени
    private boolean hasIntersections(Task task) {
        if (task.getStartTime() == null || task.getEndTime() == null) {
            return false;
        }
        return prioritizedTasks.stream()
                .anyMatch(existingTask -> isTasksIntersect(task, existingTask));
    }

    //Проверяем пересечение двух задач
    private boolean isTasksIntersect(Task task1, Task task2) {
        if (task1.getStartTime() == null || task2.getStartTime() == null) {
            return false;
        }
        LocalDateTime start1 = task1.getStartTime();
        LocalDateTime end1 = task1.getEndTime();
        LocalDateTime start2 = task2.getStartTime();
        LocalDateTime end2 = task2.getEndTime();

        return !end1.isBefore(start2) && !end2.isBefore(start1);
    }

    //Проверка времени задачи
    private void validateTaskTime(Task newTask) {
        if (hasIntersections(newTask)) {
            throw new IllegalArgumentException("Задача пересекается с существующими задачами.");
        }
    }

    private void updateEpicStatus(Epic epic) {
        if (epic.getSubTasks().isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        int newCount = 0;
        int doneCount = 0;

        for (Subtask subtask : epic.getSubTasks()) {
            if (subtask.getStatus() == Status.NEW) {
                newCount++;
            } else if (subtask.getStatus() == Status.DONE) {
                doneCount++;
            }
        }

        if (newCount > 0 && doneCount < 1) {
            epic.setStatus(Status.NEW);
        } else if (doneCount == epic.getSubTasks().size()) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }
}