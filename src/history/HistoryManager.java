package history;

import tasks.Task;

import java.util.List;

public interface HistoryManager {
    void add(Task Task);
    void remove(int id);

    List<Task> getHistory();
}
