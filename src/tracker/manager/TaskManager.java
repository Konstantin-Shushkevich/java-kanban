package tracker.manager;

import tracker.tasks.Epic;
import tracker.tasks.SubTask;
import tracker.tasks.Task;

import java.util.List;

public interface TaskManager {

    void deleteAllTasks();

    List<Task> getTaskList();

    Task getTaskById(int taskId);

    boolean addTask(Task task);

    boolean updateTask(Task task);

    boolean deleteTaskById(int taskId);

    void deleteAllSubTasks();

    List<SubTask> getSubTaskList();

    SubTask getSubTaskById(int subTaskId);

    boolean addSubTask(SubTask subTask);

    boolean updateSubTask(SubTask subTask);

    boolean deleteSubTaskById(Integer subTaskId);

    void deleteAllEpics();

    List<Epic> getEpicList();

    Epic getEpicById(int epicId);

    boolean addEpic(Epic epic);

    void updateEpic(Epic epic);

    List<SubTask> getSubTasksInEpic(Integer epicId);

    boolean deleteEpicById(Integer epicId);

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();
}
