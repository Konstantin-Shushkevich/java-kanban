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
            "id,type,name,description,status,duration,startTime,epic,endTimeEpic";

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
        String status = dataInLine[4];
        String duration = "";
        String startTime = "";
        String endTimeEpic = "";

        if (dataInLine.length >= 6) {
            duration = dataInLine[5];
            startTime = dataInLine[6];
        }

        if (dataInLine.length == 9) {
            endTimeEpic = dataInLine[8];
        }

        if (type == TaskTypes.TASK) {
            Task task;
            if (checkIfDurationAndStartTimeAreNull(duration, startTime)) {
                task = new Task(name, description, TaskStatus.valueOf(status), id, null, null);
            } else if (checkIfStartTimeIsNotNullAndDurationIs(duration, startTime)) {
                task = new Task(name, description, TaskStatus.valueOf(status), id, null,
                        LocalDateTime.parse(startTime));
            } else if (checkIfDurationIsNotNullAndStartTimeIs(duration, startTime)) {
                task = new Task(name, description, TaskStatus.valueOf(status), id,
                        Duration.ofMinutes(Integer.parseInt(duration)), null);
            } else {
                task = new Task(name, description, TaskStatus.valueOf(status), id,
                        Duration.ofMinutes(Integer.parseInt(duration)), LocalDateTime.parse(startTime));
                taskManager.prioritizedTasks.add(task);
            }
            taskManager.tasks.put(task.getId(), task);
        } else if (type == TaskTypes.EPIC) {
            Epic epic;
            if (dataInLine.length < 9) {
                epic = new Epic(name, description, TaskStatus.valueOf(status), id);
            } else if (checkIfDurationAndStartTimeAreNull(duration, startTime)) {
                epic = new Epic(name, description, TaskStatus.valueOf(status), id, null, null,
                        null);
            } else if (checkIfStartTimeIsNotNullAndDurationIs(duration, startTime)) {
                epic = new Epic(name, description, TaskStatus.valueOf(status), id, null,
                        LocalDateTime.parse(startTime), null);
            } else {
                epic = new Epic(name, description, TaskStatus.valueOf(status), id,
                        Duration.ofMinutes(Integer.parseInt(duration)), LocalDateTime.parse(startTime),
                        LocalDateTime.parse(endTimeEpic));
            }
            taskManager.epics.put(epic.getId(), epic);
        } else {
            String epicIdForSubTask = dataInLine[7];
            SubTask subTask;
            if (checkIfDurationAndStartTimeAreNull(duration, startTime)) {
                subTask = new SubTask(name, description, TaskStatus.valueOf(status), id,
                        Integer.parseInt(epicIdForSubTask), null, null);
            } else if (checkIfStartTimeIsNotNullAndDurationIs(duration, startTime)) {
                subTask = new SubTask(name, description, TaskStatus.valueOf(status), id,
                        Integer.parseInt(epicIdForSubTask), null, LocalDateTime.parse(startTime));
            } else if (checkIfDurationIsNotNullAndStartTimeIs(duration, startTime)) {
                subTask = new SubTask(name, description, TaskStatus.valueOf(status), id,
                        Integer.parseInt(epicIdForSubTask), Duration.ofMinutes(Integer.parseInt(duration)),
                        null);
            } else {
                subTask = new SubTask(name, description, TaskStatus.valueOf(status), id,
                        Integer.parseInt(epicIdForSubTask), Duration.ofMinutes(Integer.parseInt(duration)),
                        LocalDateTime.parse(startTime));
                taskManager.prioritizedTasks.add(subTask);
            }
            taskManager.subTasks.put(subTask.getId(), subTask);

            int epicId = Integer.parseInt(epicIdForSubTask); // Обновление внутреннего списка субтасок в эпике
            Epic currentEpic = taskManager.epics.get(epicId);
            currentEpic.addSubTaskIdInEpic(id);
        }
    }

    // Методы валидации загружаемых из файла данных (защита от NPE)
    private static boolean checkIfDurationAndStartTimeAreNull(String duration, String startTime) {
        return Objects.equals(duration, "null") && Objects.equals(startTime, "null");
    }

    private static boolean checkIfStartTimeIsNotNullAndDurationIs(String duration, String startTime) {
        return Objects.equals(duration, "null") && !Objects.equals(startTime, "null");
    }

    private static boolean checkIfDurationIsNotNullAndStartTimeIs(String duration, String startTime) {
        return !Objects.equals(duration, "null") && Objects.equals(startTime, "null");
    }
}
