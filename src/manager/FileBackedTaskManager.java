package manager;

import exceptions.ManagerSaveException;
import tasks.*;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private String path;

    public FileBackedTaskManager(String path) {
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

            bufferedWriter.write("id,type,name,description,status,duration,startTime,epic\n");

            for (Task task : tasks) {
                bufferedWriter.write(task.toStringForFile() + "\n");
            }

            for (Epic epic : epics) {
                if (epic.getSubTasksIdInEpic().isEmpty()) {
                    bufferedWriter.write(epic.toStringForFileFromEmptyEpic() + "\n");
                } else {
                    bufferedWriter.write(epic.toStringForFile() + "\n");
                }
            }

            for (SubTask subTask : subTasks) {
                bufferedWriter.write(subTask.toStringForFile() + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при записи в файл!");
        }
    }

    public static FileBackedTaskManager loadFromFile(String path) {
        FileBackedTaskManager taskManager = new FileBackedTaskManager(path);
        int maxId = 0;

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(path))) {
            String currentLine = bufferedReader.readLine();
            while (bufferedReader.ready()) {
                currentLine = bufferedReader.readLine();
                String[] dataInLine = currentLine.split(",");

                if (Integer.parseInt(dataInLine[0]) > maxId) {
                    maxId = Integer.parseInt(dataInLine[0]);
                }

                int id = Integer.parseInt(dataInLine[0]);
                TaskTypes type = TaskTypes.valueOf(dataInLine[1]);
                String name = dataInLine[2];
                String description = dataInLine[3];
                String status = "";
                String duration = "";
                String startTime = "";
                if (dataInLine.length >= 5) {
                    status = dataInLine[4];
                    duration = dataInLine[5];
                    startTime = dataInLine[6];
                }

                if (type == TaskTypes.TASK) {
                    Task task = new Task(name, description, TaskStatus.valueOf(status), id,
                            Duration.ofMinutes(Integer.parseInt(duration)), LocalDateTime.parse(startTime));
                    taskManager.tasks.put(task.getId(), task);
                } else if (type == TaskTypes.EPIC) {
                    Epic epic = new Epic(name, description, id);
                    taskManager.epics.put(epic.getId(), epic);
                } else {
                    String epicIdForSubTask = dataInLine[7];
                    SubTask subTask = new SubTask(name, description, TaskStatus.valueOf(status), id,
                            Integer.parseInt(epicIdForSubTask), Duration.ofMinutes(Integer.parseInt(duration)),
                            LocalDateTime.parse(startTime));
                    taskManager.subTasks.put(subTask.getId(), subTask);

                    int epicId = Integer.parseInt(epicIdForSubTask); // Обновление внутреннего списка субтасок в эпике
                    Epic currentEpic = taskManager.epics.get(epicId);
                    currentEpic.addSubTaskIdInEpic(id);
                    taskManager.epics.put(currentEpic.getId(), currentEpic);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при чтении файла!");
        }
        taskManager.getEpicList()
                .forEach(taskManager::calculateEpicTimeProperties);
        taskManager.getEpicList()
                .forEach(taskManager::calculateEpicStatus);
        taskManager.loadPrioritizedTasksFromFile();
        taskManager.setId(maxId);
        return taskManager;
    }

    // Служебный метод для восстановления коллекции задач, отсортированной по приорететности
    protected void loadPrioritizedTasksFromFile() {
        prioritizedTasks.addAll(tasks.values());
        prioritizedTasks.addAll(subTasks.values());
    }
}
