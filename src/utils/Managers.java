package utils;

import history.HistoryManager;
import manager.FileBackedTaskManager;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import history.InMemoryHistoryManager;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static FileBackedTaskManager getFileBackedTaskManager(String path) {
        return new FileBackedTaskManager(path);
    }

    public static FileBackedTaskManager downloadFromFile(String path) {
        return FileBackedTaskManager.loadFromFile(path);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}