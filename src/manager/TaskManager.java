package manager;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.util.List;

public interface TaskManager {

    void deleteAllTasks();

    List<Task> getTaskList();

    Task getTaskById(int taskId);

    void addTask(Task task);

    void updateTask(Task task);

    void deleteTaskById(int taskId);

    void deleteAllSubTasks();

    List<SubTask> getSubTaskList();

    SubTask getSubTaskById(int subTaskId);

    void addSubTask(SubTask subTask);

    void updateSubTask(SubTask subTask);

    void deleteSubTaskById(Integer subTaskId);

    void deleteAllEpics();

    List<Epic> getEpicList();

    Epic getEpicById(int epicId);

    void addEpic(Epic epic);

    void updateEpic(Epic epic);

    List<SubTask> getSubTasksInEpic(Integer epicId);

    void deleteEpicById(Integer epicId);

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();
}
