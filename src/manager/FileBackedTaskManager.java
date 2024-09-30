package manager;

import exceptions.ManagerSaveException;
import tasks.*;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private String path;

    public FileBackedTaskManager(String path) {
        this.path = path;
    }

    private FileBackedTaskManager(String path,
                                  Map<Integer, Task> tasks,
                                  Map<Integer, SubTask> subTasks,
                                  Map<Integer, Epic> epics) {
        super(tasks, subTasks, epics);
        this.path = path;
    }

    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    public void deleteTaskById(int taskId) {
        super.deleteTaskById(taskId);
        save();
    }

    public void deleteAllSubTasks() {
        super.deleteAllSubTasks();
        save();
    }

    public void addSubTask(SubTask subTask) {
        super.addSubTask(subTask);
        save();
    }

    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    public void deleteSubTaskById(Integer subTaskId) {
        super.deleteSubTaskById(subTaskId);
        save();
    }

    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    public void deleteEpicById(Integer epicId) {
        super.deleteEpicById(epicId);
        save();
    }

    private void save() {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(path))) {
            List<Task> tasks = getTaskList();
            List<SubTask> subTasks = getSubTaskList();
            List<Epic> epics = getEpicList();

            bufferedWriter.write("id,type,name,status,description,epic\n");

            for (Task task : tasks) {
                bufferedWriter.write(task.toStringForFile() + "\n");
            }

            for (Epic epic : epics) {
                bufferedWriter.write(epic.toStringForFile() + "\n");
            }

            for (SubTask subTask : subTasks) {
                bufferedWriter.write(subTask.toStringForFile() + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при записи в файл!");
        }
    }

    public static FileBackedTaskManager loadFromFile(String path) {
        Map<Integer, Task> loadedTasks = new HashMap<>();
        Map<Integer, SubTask> loadedSubTasks = new HashMap<>();
        Map<Integer, Epic> loadedEpics = new HashMap<>();
        int maxId = 0;

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(path))) {
            String currentLine = bufferedReader.readLine();
            while (bufferedReader.ready()) {
                currentLine = bufferedReader.readLine();
                String[] dataInLine = currentLine.split(",");

                if (Integer.parseInt(dataInLine[0]) > maxId) {
                    maxId = Integer.parseInt(dataInLine[0]);
                }

                if (TaskTypes.valueOf(dataInLine[1]) == TaskTypes.TASK) {
                    Task task = new Task(dataInLine[2], dataInLine[4], TaskStatus.valueOf(dataInLine[3]),
                            Integer.parseInt(dataInLine[0]));
                    loadedTasks.put(task.getId(), task);
                } else if (TaskTypes.valueOf(dataInLine[1]) == TaskTypes.EPIC) {
                    Epic epic = new Epic(dataInLine[2], dataInLine[4], TaskStatus.valueOf(dataInLine[3]),
                            Integer.parseInt(dataInLine[0]));
                    loadedEpics.put(epic.getId(), epic);
                } else {
                    SubTask subTask = new SubTask(dataInLine[2], dataInLine[4], TaskStatus.valueOf(dataInLine[3]),
                            Integer.parseInt(dataInLine[0]), Integer.parseInt(dataInLine[5]));
                    loadedSubTasks.put(subTask.getId(), subTask);

                    int epicId = Integer.parseInt(dataInLine[5]); // Обновление внутреннего списка субтасок в эпике
                    Epic currentEpic = loadedEpics.get(epicId);
                    currentEpic.addSubTaskIdInEpic(Integer.parseInt(dataInLine[5]));
                    loadedEpics.put(epicId, currentEpic);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при чтении файла!");
        }
        FileBackedTaskManager taskManager = new FileBackedTaskManager(path, loadedTasks, loadedSubTasks, loadedEpics);
        taskManager.setId(maxId);
        return taskManager;
    }
}
