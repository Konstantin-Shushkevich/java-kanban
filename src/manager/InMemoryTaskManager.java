package manager;

import history.HistoryManager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;
import utils.Managers;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class InMemoryTaskManager implements TaskManager {
    private int id = 0;
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, SubTask> subTasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final HistoryManager inMemoryHistoryManager = Managers.getDefaultHistory();

    private int addNewId() { // Присвоение id
        return ++id;
    }

    //
    // методы для Task
    //
    @Override
    public void deleteAllTasks() { // Удаление всех задач типа Task
        for (Integer id : tasks.keySet()) {
            inMemoryHistoryManager.removeFromHistory(id);
        }
        tasks.clear();
    }

    @Override
    public List<Task> getTaskList() { // Получение списка задач типа Task
        return new ArrayList<>(tasks.values());
    }

    @Override
    public Task getTaskById(int taskId) { // Получение задачи типа Task по идентификатору
        Task task = tasks.get(taskId);
        if (task != null) {
            inMemoryHistoryManager.addToHistory(task);
        }
        return task;
    }

    @Override
    public void addTask(Task task) { // Добавление задачи типа Task
        int taskId = addNewId();

        task.setId(taskId);
        tasks.put(taskId, task);
    }

    @Override
    public void updateTask(Task task) { // Обновление задачи типа Task
        int taskId = task.getId();

        if (tasks.containsKey(taskId)) {
            tasks.put(taskId, task);
        }
    }

    @Override
    public void deleteTaskById(int taskId) { // Удаление задачи типа Task по идентификатору
        tasks.remove(taskId);
        inMemoryHistoryManager.removeFromHistory(taskId);
    }

    //
    // методы для SubTask
    //
    @Override
    public void deleteAllSubTasks() { // Удаление всех задач типа SubTask
        for (Integer id : subTasks.keySet()) {
            inMemoryHistoryManager.removeFromHistory(id);
        }

        subTasks.clear();

        for (Epic epic : epics.values()) {
            epic.removeAllSubTaskIdInEpic();
            calculateEpicStatus(epic);
        }
    }

    @Override
    public List<SubTask> getSubTaskList() { // Получение списка задач типа SubTask
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public SubTask getSubTaskById(int subTaskId) { // Получение задачи типа SubTask по идентификатору
        SubTask subTask = subTasks.get(subTaskId);
        if (subTask != null) {
            inMemoryHistoryManager.addToHistory(subTask);
        }
        return subTask;
    }

    @Override
    public void addSubTask(SubTask subTask) { // Добавление задачи типа SubTask
        int currentEpicId = subTask.getEpicId();
        if (epics.containsKey(currentEpicId)) {
            int subTaskId = addNewId();
            subTask.setId(subTaskId);
            subTasks.put(subTaskId, subTask);

            Epic currentEpic = epics.get(currentEpicId);
            currentEpic.addSubTaskIdInEpic(subTaskId);
            calculateEpicStatus(currentEpic);
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) { // Обновление задачи типа SubTask
        int subTaskId = subTask.getId();
        subTasks.put(subTaskId, subTask);
        int currentEpicId = subTask.getEpicId();
        Epic currentEpic = getEpicById(currentEpicId);

        calculateEpicStatus(currentEpic);
    }

    @Override
    public void deleteSubTaskById(Integer subTaskId) { // Удаление задачи типа SubTask по идентификатору
        if (subTasks.containsKey(subTaskId)) {
            SubTask currentSubTask = subTasks.get(subTaskId);
            int epicForCurrentSubTaskId = currentSubTask.getEpicId();
            Epic epicForCurrentSubTask = epics.get(epicForCurrentSubTaskId);

            epicForCurrentSubTask.removeSubTaskIdInEpic(subTaskId);
            calculateEpicStatus(epicForCurrentSubTask);
            subTasks.remove(subTaskId);
            inMemoryHistoryManager.removeFromHistory(subTaskId);
        }
    }

    //
    // методы для tasks.Epic
    //
    @Override
    public void deleteAllEpics() { // Удаление всех задач типа Epic и связанных с ними SubTask
        for (Integer id : epics.keySet()) {
            inMemoryHistoryManager.removeFromHistory(id);
        }

        for (Integer id : subTasks.keySet()) {
            inMemoryHistoryManager.removeFromHistory(id);
        }

        epics.clear();
        subTasks.clear();
    }

    @Override
    public List<Epic> getEpicList() { // Получение списка задач типа Epic
        return new ArrayList<>(epics.values());
    }

    @Override
    public Epic getEpicById(int epicId) { // Получение по идентификатору задачи типа Epic
        Epic epic = epics.get(epicId);
        if (epic != null) {
            inMemoryHistoryManager.addToHistory(epic);
        }
        return epic;
    }

    @Override
    public void addEpic(Epic epic) { // Добавление задачи типа Epic
        int epicId = addNewId();

        epic.setId(epicId);
        epics.put(epicId, epic);
    }

    @Override
    public void updateEpic(Epic epic) { //Обновление задачи типа Epic
        int epicId = epic.getId();
        Epic oldVersion = epics.get(epicId);
        String newName = epic.getName();
        String newDescription = epic.getDescription();

        oldVersion.setName(newName);
        oldVersion.setDescription(newDescription);
    }

    // Получение списка всех подзадач определённого эпика
    @Override
    public List<SubTask> getSubTasksInEpic(Integer epicId) {
        Epic epic = epics.get(epicId);
        List<SubTask> result = new ArrayList<>();

        for (Integer currentSubTaskId : epic.getSubTasksIdInEpic()) {
            result.add(subTasks.get(currentSubTaskId));
        }
        return result;
    }

    @Override
    public void deleteEpicById(Integer epicId) { // Удаление задачи типа Epic по идентификатору
        if (epics.containsKey(epicId)) {
            Epic currentEpic = getEpicById(epicId);
            List<Integer> subTasksInEpicId = currentEpic.getSubTasksIdInEpic();

            for (Integer currentSubTaskId : subTasksInEpicId) {
                subTasks.remove(currentSubTaskId);
                inMemoryHistoryManager.removeFromHistory(currentSubTaskId);
            }
            epics.remove(epicId);
            inMemoryHistoryManager.removeFromHistory(epicId);
        }
    }

    private void calculateEpicStatus(Epic epic) { //Метод обновления статуса для Epic, содержащего субтаски
        int epicId = epic.getId();
        int countOfFinishedDeeds = 0;
        int countOfNewDeeds = 0;

        for (SubTask subTask : getSubTasksInEpic(epicId)) {
            if (subTask.getStatus() == (TaskStatus.DONE)) {
                countOfFinishedDeeds++;
            } else if (subTask.getStatus() == (TaskStatus.NEW)) {
                countOfNewDeeds++;
            }
        }
        int numberOfSubtasks = epic.getSubTasksIdInEpic().size();

        if (countOfNewDeeds == numberOfSubtasks) {
            epic.setStatus(TaskStatus.NEW);
        } else if (countOfFinishedDeeds == numberOfSubtasks) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    @Override
    public List<Task> getHistory() {
        return inMemoryHistoryManager.getHistory();
    }
}