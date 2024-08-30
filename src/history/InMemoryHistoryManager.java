package history;

import tasks.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> history = new LinkedList<>();

    @Override
    public void addToHistory(Task Task) {
        if (history.size() > 10) {
            history.removeFirst();
            history.add(Task);
        } else {
            history.add(Task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }
}
