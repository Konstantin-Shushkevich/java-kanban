package manager;

import history.HistoryManager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;
import utils.Managers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private int id = 0;
    protected Map<Integer, Task> tasks = new HashMap<>();
    protected Map<Integer, SubTask> subTasks = new HashMap<>();
    protected Map<Integer, Epic> epics = new HashMap<>();
    private HistoryManager historyManager = Managers.getDefaultHistory();

    private final static Comparator<Task> comparator = (o1, o2) -> { // Компаратор для prioritizedTasks
        if (o1.getStartTime() != null && o2.getStartTime() != null) {
            if (o1.getStartTime().isAfter(o2.getStartTime())) {
                return 1;
            } else if (o2.getStartTime().isAfter(o1.getStartTime())) {
                return -1;
            } else {
                return 0;
            }
        } else {
            return o1.getId() - o2.getId();
        }
    };

    protected Set<Task> prioritizedTasks = new TreeSet<>(comparator);

    protected InMemoryTaskManager(Map<Integer, Task> tasks,
                                  Map<Integer, SubTask> subTasks,
                                  Map<Integer, Epic> epics) {
        this.tasks = tasks;
        this.subTasks = subTasks;
        this.epics = epics;
    } // Конструктор, используемый при восстановлении данных из файла

    public InMemoryTaskManager() {
    } // Базовый конструктор

    private int addNewId() { // Присвоение id
        return ++id;
    }

    protected void setId(int id) { // Восстановление очередного id при загрузке из файла
        this.id = id;
    }

    //
    // методы для Task
    //
    @Override
    public void deleteAllTasks() { // Удаление всех задач типа Task
        tasks.keySet().forEach(id -> {
            prioritizedTasks.remove(getTaskById(id));
            historyManager.remove(id);
        });
        tasks.clear();
    }

    @Override
    public List<Task> getTaskList() { // Получение списка задач типа Task
        return new ArrayList<>(tasks.values());
    }

    @Override
    public Task getTaskById(int taskId) { // Получение задачи типа Task по id
        Task task = tasks.get(taskId);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public void addTask(Task task) { // Добавление задачи типа Task
        if (!validateTimePeriods(task)) {
            return;
        }

        int taskId = addNewId();

        task.setId(taskId);
        tasks.put(taskId, task);
        prioritizedTasks.add(task);
    }

    @Override
    public void updateTask(Task task) { // Обновление задачи типа Task
        int taskId = task.getId();
        Task oldTask = tasks.get(taskId);

        if ((tasks.containsKey(taskId) &&
                Objects.equals(oldTask.getStartTime(), task.getStartTime()) &&
                Objects.equals(oldTask.getDuration(), task.getDuration())) ||
                validateTimePeriods(task)) {
            tasks.put(taskId, task);
            prioritizedTasks.remove(oldTask);
            prioritizedTasks.add(task);
        }
    }

    @Override
    public void deleteTaskById(int taskId) { // Удаление задачи типа Task по id
        Task taskForDelete = tasks.remove(taskId);
        prioritizedTasks.remove(taskForDelete);
        historyManager.remove(taskId);
    }

    //
    // методы для SubTask
    //
    @Override
    public void deleteAllSubTasks() { // Удаление всех задач типа SubTask
        subTasks.forEach((key, value) -> {
            historyManager.remove(key);
            prioritizedTasks.remove(value);
        });

        subTasks.clear();

        epics.values().forEach(epic -> {
            epic.removeAllSubTaskIdInEpic();
            calculateEpicStatus(epic);
            calculateEpicTimeProperties(epic);
        });
    }

    @Override
    public List<SubTask> getSubTaskList() { // Получение списка задач типа SubTask
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public SubTask getSubTaskById(int subTaskId) { // Получение задачи типа SubTask по id
        SubTask subTask = subTasks.get(subTaskId);

        if (subTask != null) {
            historyManager.add(subTask);
        }

        return subTask;
    }

    @Override
    public void addSubTask(SubTask subTask) { // Добавление задачи типа SubTask
        int currentEpicId = subTask.getEpicId();

        if (epics.containsKey(currentEpicId) && validateTimePeriods(subTask)) {
            int subTaskId = addNewId();
            subTask.setId(subTaskId);
            subTasks.put(subTaskId, subTask);
            prioritizedTasks.add(subTask);

            Epic currentEpic = epics.get(currentEpicId);
            currentEpic.addSubTaskIdInEpic(subTaskId);
            calculateEpicStatus(currentEpic);
            calculateEpicTimeProperties(currentEpic);
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) { // Обновление задачи типа SubTask
        int subTaskId = subTask.getId();
        SubTask oldSubTask = subTasks.get(subTaskId);

        if ((subTasks.containsKey(subTaskId) &&
                Objects.equals(oldSubTask.getStartTime(), subTask.getStartTime()) &&
                Objects.equals(oldSubTask.getDuration(), subTask.getDuration())) ||
                validateTimePeriods(subTask)) {

            subTasks.put(subTaskId, subTask);
            prioritizedTasks.remove(oldSubTask);
            prioritizedTasks.add(subTask);
            int currentEpicId = subTask.getEpicId();
            Epic currentEpic = getEpicById(currentEpicId);

            calculateEpicStatus(currentEpic);
            calculateEpicTimeProperties(currentEpic);
        }
    }

    @Override
    public void deleteSubTaskById(Integer subTaskId) { // Удаление задачи типа SubTask по id
        if (subTasks.containsKey(subTaskId)) {
            SubTask currentSubTask = subTasks.get(subTaskId);
            int epicForCurrentSubTaskId = currentSubTask.getEpicId();
            Epic epicForCurrentSubTask = epics.get(epicForCurrentSubTaskId);

            epicForCurrentSubTask.removeSubTaskIdInEpic(subTaskId);
            calculateEpicStatus(epicForCurrentSubTask);
            calculateEpicTimeProperties(epicForCurrentSubTask);
            prioritizedTasks.remove(currentSubTask);
            subTasks.remove(subTaskId);
            historyManager.remove(subTaskId);
        }
    }

    //
    // методы для Epic
    //
    @Override
    public void deleteAllEpics() { // Удаление всех задач типа Epic и связанных с ними SubTask
        for (Integer id : epics.keySet()) {
            historyManager.remove(id);
        }

        for (Map.Entry<Integer, SubTask> entry : subTasks.entrySet()) {
            historyManager.remove(entry.getKey());
            prioritizedTasks.remove(entry.getValue());
        }

        epics.clear();
        subTasks.clear();
    }

    @Override
    public List<Epic> getEpicList() { // Получение списка задач типа Epic
        return new ArrayList<>(epics.values());
    }

    @Override
    public Epic getEpicById(int epicId) { // Получение задачи типа Epic по id
        Epic epic = epics.get(epicId);
        if (epic != null) {
            historyManager.add(epic);
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
    public void deleteEpicById(Integer epicId) { // Удаление задачи типа Epic по id
        if (epics.containsKey(epicId)) {
            Epic currentEpic = getEpicById(epicId);
            List<Integer> subTasksInEpicId = currentEpic.getSubTasksIdInEpic();

            for (Integer currentSubTaskId : subTasksInEpicId) {
                SubTask currentSubTask = subTasks.remove(currentSubTaskId);
                prioritizedTasks.remove(currentSubTask);
                historyManager.remove(currentSubTaskId);
            }

            epics.remove(epicId);
            historyManager.remove(epicId);
        }
    }

    protected void calculateEpicStatus(Epic epic) { // Метод обновления статуса для Epic, содержащего субтаски
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

    protected void calculateEpicTimeProperties(Epic epic) { // Метод, рассчитывающий временные характеристики Epic
        List<SubTask> subTasksInCurrentEpic = new ArrayList<>(epic.getSubTasksIdInEpic().stream()
                .map(stId -> subTasks.get(stId))
                .toList());

        subTasksInCurrentEpic.sort(comparator);

        if (!subTasksInCurrentEpic.isEmpty()) {
            epic.setStartTime(subTasksInCurrentEpic.getFirst().getStartTime());
            epic.setEndTime(subTasksInCurrentEpic.getLast().getEndTime());

            LocalDateTime epicStartTime = epic.getStartTime();
            LocalDateTime epicEndTime = epic.getEndTime();
            epic.setDuration(Duration.between(epicStartTime, epicEndTime));
        }
    }

    @Override
    public List<Task> getHistory() { // Возвращает историю
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() { // Возвращает список задач по приоритетности
        return List.copyOf(prioritizedTasks);
    }

    // Метод валидации временных интервалов добавляемых Task и SubTask
    private boolean validateTimePeriods(Task anyTypeOfTask) {
        return prioritizedTasks.stream()
                .noneMatch(task -> checkIfExistsTaskForThisPeriod(anyTypeOfTask, task));
    }

    /* Вспомогательный метод для validateTimePeriods, проверяющий пересечение временных интервалов. Метод валидации
    разделен на 2 для лучшей читаемости кода.*/
    private boolean checkIfExistsTaskForThisPeriod(Task newTask, Task currentTask) {
        LocalDateTime start = newTask.getStartTime();
        LocalDateTime end = newTask.getEndTime();

        return !((end.isBefore(currentTask.getStartTime()) || end.equals(currentTask.getStartTime()))
                || (start.isAfter(currentTask.getEndTime()) || start.equals(currentTask.getEndTime())));
    }
}
