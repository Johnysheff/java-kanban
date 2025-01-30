package tracker.model;

public class Subtask extends Task {
    private final int epicId;

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
                '}';
    }

    // Сохранение подзадачи в CSV-формате
    @Override
    public String toCsvString() {
        return String.join(",",
                String.valueOf(getTaskId()),
                TaskType.SUBTASK.name(),
                getTaskName(),
                getStatus().name(),
                getDescription(),
                String.valueOf(epicId)
        ) + ",";
    }
}
