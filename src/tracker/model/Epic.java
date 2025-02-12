package tracker.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Subtask> subTasks = new ArrayList<>();

    public Epic(String taskName, String taskDesc) {
        super(taskName, taskDesc);
    }

    public ArrayList<Subtask> getSubTasks() {
        return subTasks;
    }

    public void addSubTask(Subtask subtask) {
        if (subtask != null && !subtask.equals(this)) {
            subTasks.add(subtask);
            if (subtask.getStartTime() != null && subtask.getEndTime() != null) {
                updateEpicDetails();
            }
        }
    }

    public void removeSubTask(Subtask subtask) {
        subTasks.remove(subtask);
    }

    public void clearSubtaskIds() {
        subTasks.clear();
    }

    // Обновляем время начала, окончания и продолжительность эпика
    private void updateEpicDetails() {
        LocalDateTime earliestStartTime = subTasks.stream()
                .map(Subtask::getStartTime)
                .filter(start -> start != null)
                .min(LocalDateTime::compareTo).orElse(null);

        LocalDateTime latestEndTime = subTasks.stream()
                .map(Subtask::getEndTime)
                .filter(end -> end != null)
                .max(LocalDateTime::compareTo).orElse(null);

        Duration totalDuration = Duration.between(earliestStartTime, latestEndTime);

        setStartTime(earliestStartTime);
        setEndTime(latestEndTime);
        setDuration(totalDuration);
    }

    @Override
    public String toString() {
        return "tracker.models.Epic{" +
                "taskName='" + getTaskName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", taskId=" + getTaskId() +
                ", status=" + getStatus() +
                ", subTasks=" + subTasks +
                ", duration=" + getDuration() +
                ", startTime=" + getStartTime() +
                ", endTime=" + getEndTime() +
                '}';
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    // Сохраняем эпик в CSV-формате
    @Override
    public String toCsvString() {
        return String.join(",",
                String.valueOf(getTaskId()),
                getType().name(),
                getTaskName(),
                getStatus().name(),
                getDescription(),
                getDuration() != null ? String.valueOf(getDuration().toMinutes()) : "",
                getStartTime() != null ? getStartTime().toString() : "",
                getEndTime() != null ? getEndTime().toString() : ""
        ) + ",";
    }
}