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
        return "Subtask{" +
                "taskName='" + getTaskName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", taskId=" + getTaskId() +
                ", status=" + getStatus() +
                ", epicId=" + epicId +
                '}';
    }
}