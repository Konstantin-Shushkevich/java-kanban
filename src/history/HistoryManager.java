package history;

import tasks.Task;

import java.util.List;

public interface HistoryManager {
    void addToHistory(Task Task);

    List<Task> getHistory();
}
