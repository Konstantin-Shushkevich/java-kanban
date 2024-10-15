package manager;

import exceptions.ManagerSaveException;
import tasks.*;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private String path;
    private static final String HEADLINE_FOR_FILE =
            "id,type,name,description,status,duration,startTime,endTimeEpic,epic";

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

            bufferedWriter.write(HEADLINE_FOR_FILE + "\n");

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

                convertFromArrayToTaskAndAddToTaskManager(dataInLine, taskManager);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при чтении файла!");
        }
        taskManager.setId(maxId);
        return taskManager;
    }

    private static void convertFromArrayToTaskAndAddToTaskManager(String[] dataInLine,
                                                                  FileBackedTaskManager taskManager) {
        int id = Integer.parseInt(dataInLine[0]);
        TaskTypes type = TaskTypes.valueOf(dataInLine[1]);
        String name = dataInLine[2];
        String description = dataInLine[3];
        TaskStatus status = TaskStatus.valueOf(dataInLine[4]);
        Duration duration = null;
        LocalDateTime startTime = null;
        LocalDateTime endTimeEpic = null;

        if (dataInLine.length >= 6) {
            duration = parseDuration(dataInLine[5]);
            startTime = parseDate(dataInLine[6]);
        }

        if (dataInLine.length == 8) {
            endTimeEpic = parseDate(dataInLine[7]);
        }

        if (type == TaskTypes.TASK) {
            Task task = new Task(name, description, status, id, duration, startTime);

            if (duration != null && startTime != null) {
                taskManager.prioritizedTasks.add(task);
            }

            taskManager.tasks.put(task.getId(), task);
        } else if (type == TaskTypes.EPIC) {
            Epic epic;

            if (dataInLine.length < 8) {
                epic = new Epic(name, description, status, id);
            } else {
                epic = new Epic(name, description, status, id, duration, startTime, endTimeEpic);
            }

            taskManager.epics.put(epic.getId(), epic);
        } else {
            int epicId = Integer.parseInt(dataInLine[8]);

            SubTask subTask = new SubTask(name, description, status, id, epicId, duration, startTime);

            if (duration != null && startTime != null) {
                taskManager.prioritizedTasks.add(subTask);
            }

            taskManager.subTasks.put(subTask.getId(), subTask);

            Epic currentEpic = taskManager.epics.get(epicId); // Обновление внутреннего списка субтасок в эпике
            currentEpic.addSubTaskIdInEpic(id);
        }
    }

    private static LocalDateTime parseDate(String date) {
        if (date.equals("null")) {
            return null;
        }
        return LocalDateTime.parse(date);
    }

    private static Duration parseDuration(String duration) {
        if (duration.equals("null")) {
            return null;
        }
        return Duration.ofMinutes(Integer.parseInt(duration));
    }
}
