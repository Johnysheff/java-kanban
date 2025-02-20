package tracker.controllers;

import tracker.history.HistoryManager;
import tracker.history.InMemoryHistoryManager;
import tracker.network.server.HttpTaskServer;

import java.io.File;
import java.io.IOException;

public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getFileBackedTaskManager(File file) {
        return new FileBackedTaskManager(file);
    }

    public static HttpTaskServer getHttpTaskServer(TaskManager taskManager) throws IOException {
        return new HttpTaskServer(taskManager);
    }
}