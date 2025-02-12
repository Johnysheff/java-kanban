package tracker.model;

import java.time.LocalDateTime;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(String taskName, String taskDesc, int epicId, LocalDateTime startTime, LocalDateTime endTime) {
        super(taskName, taskDesc, startTime, endTime);
        this.epicId = epicId;
    }

    public Subtask(String taskName, String taskDesc, int epicId) {
        super(taskName, taskDesc);
        this.epicId = epicId;
    }


    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "tracker.models.Subtask{" +
                "taskName='" + getTaskName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", taskId=" + getTaskId() +
                ", status=" + getStatus() +
                ", epicId=" + epicId +
                ", duration=" + getDuration() +
                ", startTime=" + getStartTime() +
                ", endTime=" + getEndTime() +
                '}';
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    // Сохранение подзадачи в CSV-формате
    @Override
    public String toCsvString() {
        return String.join(",",
                String.valueOf(getTaskId()),
                getType().name(),
                getTaskName(),
                getStatus().name(),
                getDescription(),
                String.valueOf(epicId),
                getDuration() != null ? String.valueOf(getDuration().toMinutes()) : "",
                getStartTime() != null ? getStartTime().toString() : "",
                getEndTime() != null ? getEndTime().toString() : ""
        ) + ",";
    }
}
