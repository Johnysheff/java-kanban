package tracker.model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Task {
    private String taskName;
    private String description;
    private Status status;
    private int taskId;
    private Duration duration;
    protected LocalDateTime startTime;
    protected LocalDateTime endTime;


    public Task(String taskName, String description, LocalDateTime startTime, LocalDateTime endTime) {
        this.taskName = taskName;
        this.description = description;
        this.status = Status.NEW;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Task(String taskName, String description) {
        this.taskName = taskName;
        this.description = description;
        this.status = Status.NEW;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
        calculateDuration();
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
        calculateDuration();
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    private void calculateDuration() {
        if (startTime != null && endTime != null) {
            this.duration = Duration.between(startTime, endTime);
        }
    }

    public String getTaskName() {
        return taskName;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public int getTaskId() {
        return taskId;
    }


    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public TaskType getType() {
        return TaskType.TASK;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;
        Task task = (Task) o;
        return taskId == task.taskId;
    }

    @Override
    public int hashCode() {
        return taskId;
    }

    @Override
    public String toString() {
        return "tracker.models.Task{" +
                "taskName='" + taskName + '\'' +
                ", description='" + description + '\'' +
                ", taskId=" + taskId +
                ", status=" + status +
                ", duration=" + duration +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }

    // Сохранение задачи в CSV-формате
    public String toCsvString() {
        return String.join(",",
                String.valueOf(taskId),
                TaskType.TASK.name(),
                taskName,
                status.name(),
                description,
                duration != null ? String.valueOf(duration.toMinutes()) : "",
                startTime != null ? startTime.toString() : "",
                endTime != null ? endTime.toString() : ""
        ) + ",";
    }
}