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
        subTasks.add(subtask);
    }

    public void removeSubTask(Subtask subtask) {
        subTasks.remove(subtask);
    }

    public void clearSubTasks() {
        subTasks.clear();
    }

    @Override
    public String toString() {
        return "Epic{" +
                "taskName='" + getTaskName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", taskId=" + getTaskId() +
                ", status=" + getStatus() +
                ", subTasks=" + subTasks +
                '}';
    }
}