package managerTest;

import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;
import utils.Managers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private TaskManager inMemoryTaskManager;

    @BeforeEach
    void instanceOfInMemoryTaskManagerForTests() {
        inMemoryTaskManager = Managers.getDefault();
    }

    // Тесты для Task
    @Test
    void shouldReturnTrueIfTaskIsAdded() {
        Task taskTest1 = new Task("taskTestName1", "taskTestDescription1");

        inMemoryTaskManager.addTask(taskTest1);
        List<Task> tasksTestList = inMemoryTaskManager.getTaskList();

        assertTrue(tasksTestList.contains(taskTest1), "Задача типа Task не была добавлена.");
    }

    @Test
    void shouldReturnTrueAfterDeleteAllTasks() {
        Task taskTest1 = new Task("taskTestName1", "taskTestDescription1");
        Task taskTest2 = new Task("taskTestName2", "taskTestDescription2");

        inMemoryTaskManager.addTask(taskTest1);
        inMemoryTaskManager.addTask(taskTest2);
        inMemoryTaskManager.deleteAllTasks();

        List<Task> tasksTestList = inMemoryTaskManager.getTaskList();

        assertTrue(tasksTestList.isEmpty(), "Задачи типа Task не были удалены.");
    }

    @Test
    void shouldReturnTaskById() {
        Task taskTest1 = new Task("taskTestName1", "taskTestDescription1");

        inMemoryTaskManager.addTask(taskTest1);

        int id = taskTest1.getId();

        Task recievedByIdTask = inMemoryTaskManager.getTaskById(id);

        assertEquals(recievedByIdTask, taskTest1, "Возвращаемый объект не соответствует ожидаемому.");
    }

    @Test
    void shouldDeleteTaskById() {
        Task taskTest1 = new Task("taskTestName1", "taskTestDescription1");

        inMemoryTaskManager.addTask(taskTest1);

        int id = taskTest1.getId();

        inMemoryTaskManager.deleteTaskById(id);
        assertNull(inMemoryTaskManager.getTaskById(id), "Задача Task с id = " + id + " не была удалена");
    }

    @Test
    void shouldCompareOfEqualityOfTaskInstances() {
        Task taskTest1 = new Task("taskTestName1", "taskTestDescription1");

        inMemoryTaskManager.addTask(taskTest1);

        int id = taskTest1.getId();
        TaskStatus taskStatus = taskTest1.getStatus();

        Task taskTest2 = new Task("taskTestName1", "taskTestDescription1", taskStatus, id);

        assertEquals(taskTest1, taskTest2, "Экземпляры класса Task не равны при равных id.");
    }

    @Test
    void shouldCheckTheCorrectnessOfTaskUpdate() {
        Task taskTest1 = new Task("taskTestName1", "taskTestDescription1");

        inMemoryTaskManager.addTask(taskTest1);

        int numberOfTasks = inMemoryTaskManager.getTaskList().size();
        int id = taskTest1.getId();
        Task taskTest2 = new Task("taskTestName1", "taskTestDescription2", TaskStatus.DONE, id);

        inMemoryTaskManager.updateTask(taskTest2);

        int numberOfUpdatedTasks = inMemoryTaskManager.getTaskList().size();
        Task updatedTask = inMemoryTaskManager.getTaskById(id);

        assertTrue(numberOfUpdatedTasks == numberOfTasks && !taskTest1.equals(updatedTask) &&
                        updatedTask.getStatus() == TaskStatus.DONE,
                "Обновление задачи типа Task произведено некорректно.");
    }

    // Тесты для SubTask
    @Test
    void shouldReturnTrueIfSubTaskIsAddedByCorrectEpic() {
        Epic epic = new Epic("epicTestName1", "epicTestDescription1");

        inMemoryTaskManager.addEpic(epic);

        int epicId = epic.getId();
        SubTask subTaskTest1 = new SubTask("testName1", "testDescription1", epicId);

        inMemoryTaskManager.addSubTask(subTaskTest1);

        List<SubTask> subTasksTestList = inMemoryTaskManager.getSubTaskList();

        assertTrue(subTasksTestList.contains(subTaskTest1), "Задача типа SubTask не была добавлена.");
    }

    @Test
    void shouldCheckThatSubTaskIsNotAnEpicForSubTask() {
        Epic epic = new Epic("epicTestName1", "epicTestDescription1");

        inMemoryTaskManager.addEpic(epic);

        int epicId = epic.getId();
        SubTask subTaskTest1 = new SubTask("subTaskTestName1", "subTaskTestDescription1", epicId);

        inMemoryTaskManager.addSubTask(subTaskTest1);

        int subTaskAsEpicForTestId = subTaskTest1.getId();
        SubTask subTaskTest2 = new SubTask("subTaskTestName2", "subTaskTestDescription2",
                subTaskAsEpicForTestId);

        inMemoryTaskManager.addSubTask(subTaskTest2);

        List<SubTask> subTasksTestList = inMemoryTaskManager.getSubTaskList();

        assertFalse(subTasksTestList.contains(subTaskTest2),
                "Задача типа SubTask является эпиком для добавленной задачи типа SubTask.");
    }

    @Test
    void shouldReturnTrueAfterDeleteAllSubTasks() {
        Epic epic = new Epic("epicTestName1", "epicTestDescription1");

        inMemoryTaskManager.addEpic(epic);

        int epicId = epic.getId();
        SubTask subTaskTest1 = new SubTask("subTaskTestName1", "subTaskTestDescription1", epicId);
        SubTask subTaskTest2 = new SubTask("subTaskTestName2", "subTaskTestDescription2", epicId);

        inMemoryTaskManager.addSubTask(subTaskTest1);
        inMemoryTaskManager.addSubTask(subTaskTest2);
        inMemoryTaskManager.deleteAllSubTasks();

        List<SubTask> subTasksTestList = inMemoryTaskManager.getSubTaskList();

        assertTrue(subTasksTestList.isEmpty(), "Задачи типа SubTask не были удалены.");
    }

    @Test
    void shouldReturnSubTaskById() {
        Epic epic = new Epic("epicTestName1", "epicTestDescription1");

        inMemoryTaskManager.addEpic(epic);

        int epicId = epic.getId();
        SubTask subTaskTest1 = new SubTask("subTaskTestName1", "subTaskTestDescription1", epicId);

        inMemoryTaskManager.addSubTask(subTaskTest1);

        int id = subTaskTest1.getId();
        SubTask recievedByIdSubTask = inMemoryTaskManager.getSubTaskById(id);

        assertEquals(recievedByIdSubTask, subTaskTest1, "Возвращаемый объект не соответствует ожидаемому.");
    }

    @Test
    void shouldDeleteSubTaskById() {
        Epic epic = new Epic("epicTestName1", "epicTestDescription1");

        inMemoryTaskManager.addEpic(epic);

        int epicId = epic.getId();
        SubTask subTaskTest1 = new SubTask("subTaskTestName1", "subTaskTestDescription1", epicId);

        inMemoryTaskManager.addSubTask(subTaskTest1);

        int id = subTaskTest1.getId();

        inMemoryTaskManager.deleteSubTaskById(id);
        assertNull(inMemoryTaskManager.getSubTaskById(id), "Задача SubTask с id = " + id + " не была удалена");
    }

    @Test
    void shouldCompareOfEqualityOfSubTaskInstances() {
        Epic epic = new Epic("epicTestName1", "epicTestDescription1");

        inMemoryTaskManager.addEpic(epic);

        int epicId = epic.getId();
        SubTask subTaskTest1 = new SubTask("subTaskTestName1", "subTaskTestDescription1", epicId);

        inMemoryTaskManager.addSubTask(subTaskTest1);

        int id = subTaskTest1.getId();
        TaskStatus taskStatus = subTaskTest1.getStatus();
        SubTask subTaskTest2 = new SubTask("subTaskTestName1", "subTaskTestDescription1", taskStatus,
                id, epicId);

        assertEquals(subTaskTest1, subTaskTest2, "Экземпляры класса SubTask не равны при равных id.");
    }

    @Test
    void shouldCheckTheCorrectnessOfSubTaskUpdate() {
        Epic epic = new Epic("epicTestName1", "epicTestDescription1");

        inMemoryTaskManager.addEpic(epic);

        int epicId = epic.getId();

        SubTask subTaskTest1 = new SubTask("subTaskTestName1", "subTaskTestDescription1", epicId);

        inMemoryTaskManager.addSubTask(subTaskTest1);

        int numberOfSubTasks = inMemoryTaskManager.getSubTasksInEpic(epicId).size();
        int id = subTaskTest1.getId();
        SubTask subTaskTest2 = new SubTask("subTaskTestName2", "subTaskTestDescription2",
                TaskStatus.DONE, id, epicId);

        inMemoryTaskManager.updateSubTask(subTaskTest2);

        int numberOfUpdatedSubTasks = inMemoryTaskManager.getSubTasksInEpic(epicId).size();
        SubTask updatedSubTask = inMemoryTaskManager.getSubTaskById(id);

        assertTrue(numberOfUpdatedSubTasks == numberOfSubTasks && !subTaskTest1.equals(updatedSubTask) &&
                        updatedSubTask.getStatus() == TaskStatus.DONE,
                "Обновление задачи типа SubTask произведено некорректно.");
    }

    // Тесты для Epic
    @Test
    void shouldReturnTrueWhenEpicAdded() {
        Epic epic = new Epic("epicTestName1", "epicTestDescription1");

        inMemoryTaskManager.addEpic(epic);

        List<Epic> epicTestList = inMemoryTaskManager.getEpicList();

        assertTrue(epicTestList.contains(epic), "Задача типа Epic не была добавлена.");
    }

    @Test
    void shouldReturnTrueAfterDeleteAllEpics() {
        Epic epic1 = new Epic("epicTestName1", "epicTestDescription1");
        Epic epic2 = new Epic("epicTestName2", "epicTestDescription2");

        inMemoryTaskManager.addEpic(epic1);
        inMemoryTaskManager.addEpic(epic2);

        int firstEpicId = epic1.getId();
        SubTask subTaskTest1 = new SubTask("subTaskTestName1", "subTaskTestDescription1", firstEpicId);

        inMemoryTaskManager.addSubTask(subTaskTest1);
        inMemoryTaskManager.deleteAllEpics();

        List<Epic> epicsTestList = inMemoryTaskManager.getEpicList();
        List<SubTask> subTasksTestList = inMemoryTaskManager.getSubTaskList();

        assertTrue(epicsTestList.isEmpty() && subTasksTestList.isEmpty(),
                "Задачи типа Task не были удалены.");
    }

    @Test
    void shouldReturnEpicById() {
        Epic epic1 = new Epic("epicTestName1", "epicTestDescription1");

        inMemoryTaskManager.addEpic(epic1);

        int id = epic1.getId();
        Task recievedByIdEpic = inMemoryTaskManager.getEpicById(id);

        assertEquals(recievedByIdEpic, epic1, "Возвращаемый объект не соответствует ожидаемому.");
    }

    @Test
    void shouldDeleteEpicById() {
        Epic epic1 = new Epic("epicTestName1", "epicTestDescription1");

        inMemoryTaskManager.addEpic(epic1);

        int epicId = epic1.getId();
        SubTask subTaskTest1 = new SubTask("subTaskTestName1", "subTaskTestDescription1", epicId);

        inMemoryTaskManager.addSubTask(subTaskTest1);
        inMemoryTaskManager.deleteEpicById(epicId);
        assertTrue(inMemoryTaskManager.getEpicList().isEmpty() &&
                        inMemoryTaskManager.getSubTaskList().isEmpty(),
                "Задача Epic с id = " + epicId + " не была удалена");
    }

    @Test
    void shouldCompareOfEqualityOfEpicInstances() {
        Epic epic1 = new Epic("epicTestName1", "epicTestDescription1");

        inMemoryTaskManager.addEpic(epic1);

        int id = epic1.getId();

        assertEquals(epic1, inMemoryTaskManager.getEpicById(id),
                "Экземпляры класса Epic не равны при равных id.");
    }

    @Test
    void shouldCheckTheCorrectnessOfEpicStatusUpdate() {
        Epic epic = new Epic("epicTestName1", "epicTestDescription1");

        inMemoryTaskManager.addEpic(epic);

        int epicId = epic.getId();
        TaskStatus defaultStatus = epic.getStatus();
        SubTask subTaskTest1 = new SubTask("subTaskTestName1", "subTaskTestDescription1", epicId);

        inMemoryTaskManager.addSubTask(subTaskTest1);

        int id = subTaskTest1.getId();
        SubTask subTaskTestInProgress = new SubTask("subTaskTestName1", "subTaskTestDescription1",
                TaskStatus.IN_PROGRESS, id, epicId);
        SubTask subTaskTestDone = new SubTask("subTaskTestName1", "subTaskTestDescription1",
                TaskStatus.DONE, id, epicId);

        TaskStatus newStatus = epic.getStatus();
        inMemoryTaskManager.updateSubTask(subTaskTestInProgress);
        TaskStatus inProgressStatus = epic.getStatus();
        inMemoryTaskManager.updateSubTask(subTaskTestDone);
        TaskStatus doneStatus = epic.getStatus();

        assertTrue(defaultStatus == TaskStatus.NEW && newStatus == TaskStatus.NEW &&
                        inProgressStatus == TaskStatus.IN_PROGRESS && doneStatus == TaskStatus.DONE,
                "Обновление задачи типа типа Epic и связанного SubTask произведено некорректно.");
    }

    // Проверка работы InMemoryHistoryManager через inMemoryTaskManager
    @Test
    void shouldCheckTheCorrectnessOfInMemoryHistoryManager() {
        Task taskTest1 = new Task("taskTestName1", "taskTestDescription1");
        Task taskTest2 = new Task("taskTestName2", "taskTestDescription2");

        inMemoryTaskManager.addTask(taskTest1);
        inMemoryTaskManager.addTask(taskTest2);

        Task updatedTaskTest1 = inMemoryTaskManager.getTaskById(1);
        Task updatedTaskTest2 = inMemoryTaskManager.getTaskById(2);
        List<Task> history = inMemoryTaskManager.getHistory();

        assertEquals(history.get(0), updatedTaskTest1);
        assertEquals(history.get(1), updatedTaskTest2);

        Task taskTest1Updated = new Task("taskTestName1Updated", "taskTestDescription1Updated",
                TaskStatus.DONE, 1);
        inMemoryTaskManager.updateTask(taskTest1Updated);
        inMemoryTaskManager.getTaskById(1);

        assertEquals(history.get(0), updatedTaskTest1);
        assertEquals(history.get(1), updatedTaskTest2);
        assertEquals(history.get(2), taskTest1Updated);
    }
}