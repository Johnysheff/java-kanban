package tracker.controllers;

import tracker.model.Epic;
import tracker.model.Status;
import tracker.model.Subtask;
import tracker.model.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private int nextId = 1;

    // Получение всех задач
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    // Получение всех подзадач
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    // Получение всех эпиков
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    // Получение подзадач эпика по ID
    public ArrayList<Subtask> getEpicSubTasks(int epicId) {
        if (epics.containsKey(epicId)) {
            return epics.get(epicId).getSubTasks();
        } else {
            return new ArrayList<>(); // Возврат пустого списка в случае ошибки
        }
    }

    // Удаление всех задач
    public void deleteTasks() {
        tasks.clear();
    }

    // Удаление всех подзадач
    public void deleteSubtasks() {
        for (Epic epic : epics.values()) {
            epic.clearSubtaskIds();
            updateEpicStatus(epic);
        }
        subtasks.clear();
    }

    // Удаление всех эпиков и связанных подзадач
    public void deleteEpics() {
        epics.clear();
        subtasks.clear(); // Удаляем все подзадачи при удалении эпиков
    }

    // Добавление задачи
    public void addTask(Task task) {
        task.setTaskId(nextId++);
        task.setStatus(Status.NEW);  // Установка начального статуса

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

    // Удаление задачи по ID
    public void removeTaskById(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.remove(id);
            // Удаление всех подзадач эпика
            for (Subtask subtask : epic.getSubTasks()) {
                subtasks.remove(subtask.getTaskId());
            }
        } else if (subtasks.containsKey(id)) {
            Subtask subtask = subtasks.remove(id);
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubTask(subtask);
                updateEpicStatus(epic);
            }
        } else if (tasks.containsKey(id)) {
            tasks.remove(id);
        } else {
            System.out.println("Неверный ID");
        }
    }

    // Обновление задачи
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

    // Обновление статуса эпика
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