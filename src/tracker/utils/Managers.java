package tracker.utils;

import tracker.history.HistoryManager;
import tracker.manager.FileBackedTaskManager;
import tracker.manager.InMemoryTaskManager;
import tracker.manager.TaskManager;
import tracker.history.InMemoryHistoryManager;

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