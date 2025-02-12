package tracker.controllers;

import tracker.exceptions.ManagerSaveException;
import tracker.model.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void removeTaskById(int taskId) {
        super.removeTaskById(taskId);
        save();
    }

    @Override
    public void removeEpicById(int epicId) {
        super.removeEpicById(epicId);
        save();
    }

    @Override
    public void removeSubtaskById(int subtaskId) {
        super.removeSubtaskById(subtaskId);
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    public void save() {
        try {
            List<String> lines = new ArrayList<>();
            lines.add("id,type,name,status,description,epic,duration,startTime,endTime");

            for (Task task : getTasks()) {
                lines.add(task.toCsvString());
            }
            for (Epic epic : getEpics()) {
                lines.add(epic.toCsvString());
            }
            for (Subtask subtask : getSubtasks()) {
                lines.add(subtask.toCsvString());
            }

            // Удаление запятой в конце последней строки
            if (lines.size() > 1) {
                String lastLine = lines.get(lines.size() - 1);
                lastLine = lastLine.endsWith(",") ? lastLine.substring(0, lastLine.length() - 1) : lastLine;
                lines.set(lines.size() - 1, lastLine);
            }

            Files.write(file.toPath(), lines);
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения файла", e);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try {
            String content = Files.readString(file.toPath());
            String[] lines = content.split("\n");

            for (int i = 1; i < lines.length; i++) {
                String line = lines[i].trim();
                if (line.isEmpty()) {
                    continue;
                }
                Task task = fromString(line);
                manager.addTask(task);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки файла", e);
        }
        return manager;
    }

    private static Task fromString(String value) {
        String[] parts = value.split(",");
        if (parts.length < 5) {
            throw new IllegalArgumentException("Некорректный формат строки: " + value);
        }

        int id = Integer.parseInt(parts[0].trim());
        TaskType type = TaskType.valueOf(parts[1].trim());
        String name = parts[2].trim();
        Status status = Status.valueOf(parts[3].trim());
        String description = parts[4].trim();
        Duration duration = parts.length > 5 && !parts[5].trim().isEmpty() ?
                Duration.ofMinutes(Long.parseLong(parts[5].trim())) : null;
        LocalDateTime startTime = parts.length > 6 && !parts[6].trim().isEmpty() ?
                LocalDateTime.parse(parts[6].trim()) : null;
        LocalDateTime endTime = parts.length > 7 && !parts[7].trim().isEmpty() ?
                LocalDateTime.parse(parts[7].trim()) : null;

        switch (type) {
            case TASK:
                Task task = new Task(name, description);
                task.setTaskId(id);
                task.setStatus(status);
                task.setDuration(duration);
                task.setStartTime(startTime);
                task.setEndTime(endTime);
                return task;
            case EPIC:
                Epic epic = new Epic(name, description);
                epic.setTaskId(id);
                epic.setStatus(status);
                return epic;
            case SUBTASK:
                if (parts.length < 8) {
                    throw new IllegalArgumentException("Некорректный формат строки для подзадачи: " + value);
                }
                int epicId = Integer.parseInt(parts[5].trim());
                Subtask subtask = new Subtask(name, description, epicId);
                subtask.setTaskId(id);
                subtask.setStatus(status);
                subtask.setDuration(duration);
                subtask.setStartTime(startTime);
                subtask.setEndTime(endTime);
                return subtask;
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
    }
}