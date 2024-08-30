package utils;

import history.HistoryManager;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import history.InMemoryHistoryManager;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}