package tracker.controllers;

import tracker.history.HistoryManager;
import tracker.history.InMemoryHistoryManager;
import tracker.model.Epic;
import tracker.model.Status;
import tracker.model.Subtask;
import tracker.model.Task;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HistoryManager historyManager = new InMemoryHistoryManager();
    private int nextId = 1;

    @Override
    public void addTask(Task task) {
        task.setTaskId(nextId++);
        task.setStatus(Status.NEW);

        if (task instanceof Epic) {
            epics.put(task.getTaskId(), (Epic) task);
        } else if (task instanceof Subtask) {
            subtasks.put(task.getTaskId(), (Subtask) task);
            Epic epic = epics.get(((Subtask) task).getEpicId());
            if (epic != null) {
                epic.addSubTask((Subtask) task);
                updateEpicStatus(epic);
            }
        } else {
            tasks.put(task.getTaskId(), task);
        }
    }

    @Override
    public void updateTask(Task task) {
        if (task instanceof Epic) {
            epics.put(task.getTaskId(), (Epic) task);
        } else if (task instanceof Subtask) {
            subtasks.put(task.getTaskId(), (Subtask) task);
            Epic epic = epics.get(((Subtask) task).getEpicId());
            if (epic != null) {
                updateEpicStatus(epic);
            }
        } else {
            tasks.put(task.getTaskId(), task);
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
        for (Task task : tasks.values()) {
            historyManager.add(task);
        }
        return new ArrayList<>(tasks.values());

    }

    @Override
    public List<Epic> getEpics() {
        for (Epic epic : epics.values()) {
            historyManager.add(epic);
        }
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            historyManager.add(subtask);
        }
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void removeTaskById(int taskId) {
        Task task = tasks.remove(taskId);
        if (task != null) {
            historyManager.remove(taskId);
        }
    }

    @Override
    public void removeEpicById(int epicId) {
        Epic epic = epics.remove(epicId);
        if (epic != null) {
            for (Subtask subtask : epic.getSubTasks()) {
                subtasks.remove(subtask.getTaskId());
                historyManager.remove(subtask.getTaskId());
            }
            historyManager.remove(epicId);
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
        }
    }

    @Override
    public void removeAllTasks() {
        for (Task task : tasks.values()) {
            historyManager.remove(task.getTaskId());
        }
        tasks.clear();
    }

    @Override
    public void removeAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void removeAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubtaskIds();
        }
    }

    @Override
    public List<Subtask> getSubtasksForEpic(int epicId) {
        Epic epic = epics.get(epicId);
        return epic != null ? epic.getSubTasks() : new ArrayList<>();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
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

        if (newCount > 0) {
            epic.setStatus(Status.NEW);
        } else if (doneCount == epic.getSubTasks().size()) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }
}