package managerTest;

import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;
import utils.Managers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
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
        Task taskTest1 = new Task("taskTestName1", "taskTestDescription1", Duration.ofMinutes(30),
                LocalDateTime.of(2024, Month.JANUARY, 1, 0, 0));

        inMemoryTaskManager.addTask(taskTest1);
        List<Task> tasksTestList = inMemoryTaskManager.getTaskList();

        assertTrue(tasksTestList.contains(taskTest1), "Задача типа Task не была добавлена.");
    }

    @Test
    void shouldReturnTrueAfterDeleteAllTasks() {
        Task taskTest1 = new Task("taskTestName1", "taskTestDescription1", Duration.ofMinutes(30),
                LocalDateTime.of(2024, Month.JANUARY, 1, 0, 0));
        Task taskTest2 = new Task("taskTestName2", "taskTestDescription2", Duration.ofMinutes(30),
                LocalDateTime.of(2024, Month.JANUARY, 2, 0, 0));

        inMemoryTaskManager.addTask(taskTest1);
        inMemoryTaskManager.addTask(taskTest2);
        inMemoryTaskManager.deleteAllTasks();

        List<Task> tasksTestList = inMemoryTaskManager.getTaskList();

        assertTrue(tasksTestList.isEmpty(), "Задачи типа Task не были удалены.");
    }

    @Test
    void shouldReturnTaskById() {
        Task taskTest1 = new Task("taskTestName1", "taskTestDescription1", Duration.ofMinutes(30),
                LocalDateTime.of(2024, Month.JANUARY, 1, 0, 0));

        inMemoryTaskManager.addTask(taskTest1);

        int id = taskTest1.getId();

        Task recievedByIdTask = inMemoryTaskManager.getTaskById(id);

        assertEquals(recievedByIdTask, taskTest1, "Возвращаемый объект не соответствует ожидаемому.");
    }

    @Test
    void shouldDeleteTaskById() {
        Task taskTest1 = new Task("taskTestName1", "taskTestDescription1", Duration.ofMinutes(30),
                LocalDateTime.of(2024, Month.JANUARY, 1, 0, 0));

        inMemoryTaskManager.addTask(taskTest1);

        int id = taskTest1.getId();

        inMemoryTaskManager.deleteTaskById(id);
        assertNull(inMemoryTaskManager.getTaskById(id), "Задача Task с id = " + id + " не была удалена");
    }

    @Test
    void shouldCompareOfEqualityOfTaskInstances() {
        Task taskTest1 = new Task("taskTestName1", "taskTestDescription1", Duration.ofMinutes(30),
                LocalDateTime.of(2024, Month.JANUARY, 1, 0, 0));

        inMemoryTaskManager.addTask(taskTest1);

        int id = taskTest1.getId();
        TaskStatus taskStatus = taskTest1.getStatus();

        Task taskTest2 = new Task("taskTestName1", "taskTestDescription1", taskStatus, id,
                Duration.ofMinutes(30),
                LocalDateTime.of(2024, Month.JANUARY, 1, 0, 0));

        assertEquals(taskTest1, taskTest2, "Экземпляры класса Task не равны при равных id.");
    }

    @Test
    void shouldCheckTheCorrectnessOfTaskUpdate() {
        Task taskTest1 = new Task("taskTestName1", "taskTestDescription1", Duration.ofMinutes(30),
                LocalDateTime.of(2024, Month.JANUARY, 1, 0, 0));

        inMemoryTaskManager.addTask(taskTest1);

        int numberOfTasks = inMemoryTaskManager.getTaskList().size();
        int id = inMemoryTaskManager.getTaskList().getFirst().getId();
        Task taskTest2 = new Task("taskTestName1", "taskTestDescription2", TaskStatus.DONE, id,
                Duration.ofMinutes(30),
                LocalDateTime.of(2024, Month.JANUARY, 1, 0, 0));

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
        SubTask subTaskTest1 = new SubTask("testName1", "testDescription1", epicId,
                Duration.ofMinutes(30), LocalDateTime.of(2024, Month.JANUARY, 1, 0, 0));

        inMemoryTaskManager.addSubTask(subTaskTest1);

        List<SubTask> subTasksTestList = inMemoryTaskManager.getSubTaskList();

        assertTrue(subTasksTestList.contains(subTaskTest1), "Задача типа SubTask не была добавлена.");
    }

    @Test
    void shouldCheckThatSubTaskIsNotAnEpicForSubTask() {
        Epic epic = new Epic("epicTestName1", "epicTestDescription1");

        inMemoryTaskManager.addEpic(epic);

        int epicId = epic.getId();
        SubTask subTaskTest1 = new SubTask("subTaskTestName1", "subTaskTestDescription1", epicId,
                Duration.ofMinutes(30), LocalDateTime.of(2024, Month.JANUARY, 1, 0, 0));

        inMemoryTaskManager.addSubTask(subTaskTest1);

        int subTaskAsEpicForTestId = subTaskTest1.getId();
        SubTask subTaskTest2 = new SubTask("subTaskTestName2", "subTaskTestDescription2",
                subTaskAsEpicForTestId, Duration.ofMinutes(30),
                LocalDateTime.of(2025, Month.JANUARY, 1, 0, 0));

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
        SubTask subTaskTest1 = new SubTask("subTaskTestName1", "subTaskTestDescription1", epicId,
                Duration.ofMinutes(30), LocalDateTime.of(2024, Month.JANUARY, 1, 0, 0));
        SubTask subTaskTest2 = new SubTask("subTaskTestName2", "subTaskTestDescription2", epicId,
                Duration.ofMinutes(30), LocalDateTime.of(2025, Month.JANUARY, 1, 0, 0));

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
        SubTask subTaskTest1 = new SubTask("subTaskTestName1", "subTaskTestDescription1", epicId,
                Duration.ofMinutes(30), LocalDateTime.of(2024, Month.JANUARY, 1, 0, 0));

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
        SubTask subTaskTest1 = new SubTask("subTaskTestName1", "subTaskTestDescription1", epicId,
                Duration.ofMinutes(30), LocalDateTime.of(2024, Month.JANUARY, 1, 0, 0));

        inMemoryTaskManager.addSubTask(subTaskTest1);

        int id = inMemoryTaskManager.getSubTaskList().getFirst().getId();

        inMemoryTaskManager.deleteSubTaskById(id);
        assertNull(inMemoryTaskManager.getSubTaskById(id), "Задача SubTask с id = " + id + " не была удалена");
    }

    @Test
    void shouldCompareOfEqualityOfSubTaskInstances() {
        Epic epic = new Epic("epicTestName1", "epicTestDescription1");

        inMemoryTaskManager.addEpic(epic);

        int epicId = epic.getId();
        SubTask subTaskTest1 = new SubTask("subTaskTestName1", "subTaskTestDescription1", epicId,
                Duration.ofMinutes(30), LocalDateTime.of(2024, Month.JANUARY, 1, 0, 0));

        inMemoryTaskManager.addSubTask(subTaskTest1);

        int id = subTaskTest1.getId();
        TaskStatus taskStatus = subTaskTest1.getStatus();
        SubTask subTaskTest2 = new SubTask("subTaskTestName1", "subTaskTestDescription1", taskStatus,
                id, epicId, Duration.ofMinutes(30),
                LocalDateTime.of(2024, Month.JANUARY, 1, 0, 0));

        assertEquals(subTaskTest1, subTaskTest2, "Экземпляры класса SubTask не равны при равных id.");
    }

    @Test
    void shouldCheckTheCorrectnessOfSubTaskUpdate() {
        Epic epic = new Epic("epicTestName1", "epicTestDescription1");

        inMemoryTaskManager.addEpic(epic);

        int epicId = epic.getId();

        SubTask subTaskTest1 = new SubTask("subTaskTestName1", "subTaskTestDescription1", epicId,
                Duration.ofMinutes(30), LocalDateTime.of(2024, Month.JANUARY, 1, 0, 0));

        inMemoryTaskManager.addSubTask(subTaskTest1);

        int numberOfSubTasks = inMemoryTaskManager.getSubTasksInEpic(epicId).size();
        int id = subTaskTest1.getId();
        SubTask subTaskTest2 = new SubTask("subTaskTestName2", "subTaskTestDescription2",
                TaskStatus.DONE, id, epicId, Duration.ofMinutes(45),
                LocalDateTime.of(2024, Month.JANUARY, 2, 0, 0));

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
        SubTask subTaskTest1 = new SubTask("subTaskTestName1", "subTaskTestDescription1", firstEpicId,
                Duration.ofMinutes(30), LocalDateTime.of(2024, Month.JANUARY, 1, 0, 0));

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
        Task receivedByIdEpic = inMemoryTaskManager.getEpicById(id);

        assertEquals(receivedByIdEpic, epic1, "Возвращаемый объект не соответствует ожидаемому.");
    }

    @Test
    void shouldDeleteEpicById() {
        Epic epic1 = new Epic("epicTestName1", "epicTestDescription1");

        inMemoryTaskManager.addEpic(epic1);

        int epicId = epic1.getId();
        SubTask subTaskTest1 = new SubTask("subTaskTestName1", "subTaskTestDescription1", epicId,
                Duration.ofMinutes(30), LocalDateTime.of(2024, Month.JANUARY, 1, 0, 0));

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

        SubTask subTaskTest1 = new SubTask("subTaskTestName1", "subTaskTestDescription1", epic.getId(),
                Duration.ofMinutes(30), LocalDateTime.of(2024, Month.JANUARY, 1, 0, 0));
        SubTask subTaskTest2 = new SubTask("subTaskTestName2", "subTaskTestDescription2", epic.getId(),
                Duration.ofMinutes(30), LocalDateTime.of(2024, Month.JANUARY, 1, 2, 0));

        inMemoryTaskManager.addSubTask(subTaskTest1);
        inMemoryTaskManager.addSubTask(subTaskTest2);
        assertSame(epic.getStatus(), TaskStatus.NEW, "Epic с 2 SubTask со статусом NEW - не NEW");

        int idOfSubTaskTest1 = subTaskTest1.getId();
        SubTask subTaskTest1InProgress = new SubTask("subTaskTestName1", "subTaskTestDescription1",
                TaskStatus.IN_PROGRESS, idOfSubTaskTest1, epic.getId(), Duration.ofMinutes(30),
                LocalDateTime.of(2024, Month.JANUARY, 1, 0, 0));
        inMemoryTaskManager.updateSubTask(subTaskTest1InProgress);
        assertSame(epic.getStatus(), TaskStatus.IN_PROGRESS,
                "Epic с 1 SubTask (NEW) и 1 SubTask (IN_PROGRESS) - не IN_PROGRESS");

        int idOfSubTaskTest2 = subTaskTest2.getId();
        SubTask subTaskTest2InProgress = new SubTask("subTaskTestName2", "subTaskTestDescription2",
                TaskStatus.IN_PROGRESS, idOfSubTaskTest2, epic.getId(), Duration.ofMinutes(30),
                LocalDateTime.of(2024, Month.JANUARY, 1, 2, 0));
        inMemoryTaskManager.updateSubTask(subTaskTest2InProgress);
        assertSame(epic.getStatus(), TaskStatus.IN_PROGRESS,
                "Epic с 2 SubTask со статусом IN_PROGRESS - не IN_PROGRESS");

        SubTask subTaskTest1Done = new SubTask("subTaskTestName1", "subTaskTestDescription1",
                TaskStatus.DONE, idOfSubTaskTest1, epic.getId(), Duration.ofMinutes(30),
                LocalDateTime.of(2024, Month.JANUARY, 1, 0, 0));
        inMemoryTaskManager.updateSubTask(subTaskTest1Done);
        assertSame(epic.getStatus(), TaskStatus.IN_PROGRESS,
                "Epic с 1 SubTask (DONE) и 1 SubTask (IN_PROGRESS) - не IN_PROGRESS");

        SubTask subTaskTest2Done = new SubTask("subTaskTestName2", "subTaskTestDescription2",
                TaskStatus.DONE, idOfSubTaskTest2, epic.getId(), Duration.ofMinutes(30),
                LocalDateTime.of(2024, Month.JANUARY, 1, 2, 0));
        inMemoryTaskManager.updateSubTask(subTaskTest2Done);
        assertSame(epic.getStatus(), TaskStatus.DONE,
                "Epic с 2 SubTask со статусом DONE - не DONE");

        SubTask subTaskTest1NewAgain = new SubTask("subTaskTestName1", "subTaskTestDescription1",
                TaskStatus.NEW, idOfSubTaskTest1, epic.getId(), Duration.ofMinutes(30),
                LocalDateTime.of(2024, Month.JANUARY, 1, 0, 0));
        inMemoryTaskManager.updateSubTask(subTaskTest1NewAgain);
        assertSame(epic.getStatus(), TaskStatus.IN_PROGRESS,
                "Epic с 1 SubTask (DONE) и 1 SubTask (NEW) - не IN_PROGRESS");
    }

    // Проверка работы InMemoryHistoryManager через inMemoryTaskManager
    @Test
    void shouldCheckTheCorrectnessOfInMemoryHistoryManager() {
        Task taskTest1 = new Task("taskTestName1", "taskTestDescription1", Duration.ofMinutes(30),
                LocalDateTime.of(2024, Month.JANUARY, 1, 0, 0));
        Task taskTest2 = new Task("taskTestName2", "taskTestDescription2", Duration.ofMinutes(40),
                LocalDateTime.of(2024, Month.MAY, 1, 0, 0));
        Epic epicWithSubTasksTest = new Epic("epicTestName1", "epicTestDescription1");
        Epic epicNoSubTasksTest = new Epic("epicTestName2", "epicTestDescription2");

        inMemoryTaskManager.addTask(taskTest1);
        inMemoryTaskManager.addTask(taskTest2);
        inMemoryTaskManager.addEpic(epicWithSubTasksTest);
        inMemoryTaskManager.addEpic(epicNoSubTasksTest);

        SubTask subTaskTest1 = new SubTask("subTaskTestName1", "subTaskTestDescription1",
                epicWithSubTasksTest.getId(), Duration.ofMinutes(30),
                LocalDateTime.of(2024, Month.JANUARY, 2, 0, 0));
        SubTask subTaskTest2 = new SubTask("subTaskTestName2", "subTaskTestDescription2",
                epicWithSubTasksTest.getId(), Duration.ofMinutes(30),
                LocalDateTime.of(2024, Month.JANUARY, 1, 12, 0));

        inMemoryTaskManager.addSubTask(subTaskTest1);
        inMemoryTaskManager.addSubTask(subTaskTest2);

        Task addedTaskTest1 = inMemoryTaskManager.getTaskById(1);
        Task addedTaskTest2 = inMemoryTaskManager.getTaskById(2);
        Task addedEpicWithSubTasksTest = inMemoryTaskManager.getEpicById(3);
        inMemoryTaskManager.getEpicById(3);
        Task addedEpicNoSubTasksTest = inMemoryTaskManager.getEpicById(4);
        Task addedSubTaskTest1 = inMemoryTaskManager.getSubTaskById(5);
        Task addedSubTaskTest2 = inMemoryTaskManager.getSubTaskById(6);
        inMemoryTaskManager.getSubTaskById(5);
        inMemoryTaskManager.getTaskById(2);

        List<Task> history = inMemoryTaskManager.getHistory();

        assertEquals(history.get(0), addedTaskTest1);
        assertEquals(history.get(1), addedEpicWithSubTasksTest);
        assertEquals(history.get(2), addedEpicNoSubTasksTest);
        assertEquals(history.get(3), addedSubTaskTest2);
        assertEquals(history.get(4), addedSubTaskTest1);
        assertEquals(history.get(5), addedTaskTest2);

        Task taskTest1Updated = new Task("taskTestName1Updated", "taskTestDescription1Updated",
                TaskStatus.DONE, 1, Duration.ofMinutes(45),
                LocalDateTime.of(2027, Month.JANUARY, 1, 12, 0));

        inMemoryTaskManager.updateTask(taskTest1Updated);
        inMemoryTaskManager.getTaskById(1);
        inMemoryTaskManager.deleteEpicById(4);
        inMemoryTaskManager.deleteSubTaskById(6);

        List<Task> historyFirstUpd = inMemoryTaskManager.getHistory();
        assertEquals(historyFirstUpd.get(0), addedEpicWithSubTasksTest);
        assertEquals(historyFirstUpd.get(1), addedSubTaskTest1);
        assertEquals(historyFirstUpd.get(2), addedTaskTest2);
        assertEquals(historyFirstUpd.get(3), taskTest1Updated);
        assertEquals(4, historyFirstUpd.size());

        inMemoryTaskManager.deleteAllEpics();

        List<Task> historySecondUpd = inMemoryTaskManager.getHistory();
        assertEquals(historySecondUpd.get(0), addedTaskTest2);
        assertEquals(historySecondUpd.get(1), taskTest1Updated);
        assertEquals(2, historySecondUpd.size());

        inMemoryTaskManager.deleteAllTasks();

        List<Task> historyThirdUpd = inMemoryTaskManager.getHistory();
        assertTrue(historyThirdUpd.isEmpty());
    }

    @Test
    void shouldCheckTheCorrectnessOfTimeValidation() {
        Task taskTest1 = new Task("taskTestName1", "taskTestDescription1", Duration.ofMinutes(30),
                LocalDateTime.of(2024, Month.JANUARY, 1, 0, 0));
        Task taskTest2 = new Task("taskTestName2", "taskTestDescription2", Duration.ofMinutes(30),
                LocalDateTime.of(2024, Month.JANUARY, 1, 0, 0));
        inMemoryTaskManager.addTask(taskTest1);
        inMemoryTaskManager.addTask(taskTest2);
        assertTrue(inMemoryTaskManager.getTaskList().size() == 1 &&
                        inMemoryTaskManager.getTaskList().getFirst() == taskTest1,
                "Были добавлены две задачи с одинаковым временем начала и длительностью!");

        Task taskTest3 = new Task("taskTestName3", "taskTestDescription3", Duration.ofMinutes(30),
                LocalDateTime.of(2024, Month.JANUARY, 1, 0, 25));
        inMemoryTaskManager.addTask(taskTest3);
        assertTrue(inMemoryTaskManager.getTaskList().size() == 1 &&
                        inMemoryTaskManager.getTaskList().getFirst() == taskTest1,
                "Начало добавленной задачи пересекается с временем выполнения добавленной раннее задачи!");

        Task taskTest4 = new Task("taskTestName4", "taskTestDescription4", Duration.ofMinutes(30),
                LocalDateTime.of(2023, Month.DECEMBER, 31, 23, 55));
        inMemoryTaskManager.addTask(taskTest4);
        assertTrue(inMemoryTaskManager.getTaskList().size() == 1 &&
                        inMemoryTaskManager.getTaskList().getFirst() == taskTest1,
                "Окончание добавленной задачи пересекается с временем выполнения добавленной раннее задачи!");
    }

    @Test
    void shouldCheckTheCorrectnessOfGettingPrioritizedTasks() {
        Task taskTest1 = new Task("taskTestName1", "taskTestDescription1", Duration.ofMinutes(30),
                LocalDateTime.of(2024, Month.JANUARY, 1, 0, 0));
        Task taskTest2 = new Task("taskTestName2", "taskTestDescription2", Duration.ofMinutes(40),
                LocalDateTime.of(2024, Month.FEBRUARY, 1, 0, 0));
        Epic epicTest = new Epic("epicTestName1", "epicTestDescription1");

        inMemoryTaskManager.addTask(taskTest1);
        inMemoryTaskManager.addTask(taskTest2);
        inMemoryTaskManager.addEpic(epicTest);

        SubTask subTaskTest1 = new SubTask("subTaskTestName1", "subTaskTestDescription1",
                epicTest.getId(), Duration.ofMinutes(30),
                LocalDateTime.of(2024, Month.MARCH, 2, 0, 0));
        SubTask subTaskTest2 = new SubTask("subTaskTestName2", "subTaskTestDescription2",
                epicTest.getId(), Duration.ofMinutes(30),
                LocalDateTime.of(2024, Month.APRIL, 1, 12, 0));
        inMemoryTaskManager.addSubTask(subTaskTest1);
        inMemoryTaskManager.addSubTask(subTaskTest2);

        List<Task> prioritizedTasksTestAsList = inMemoryTaskManager.getPrioritizedTasks();
        assertTrue(prioritizedTasksTestAsList.size() == 4 &&
                        prioritizedTasksTestAsList.get(0) == taskTest1 &&
                        prioritizedTasksTestAsList.get(1) == taskTest2 &&
                        prioritizedTasksTestAsList.get(2) == subTaskTest1 &&
                        prioritizedTasksTestAsList.get(3) == subTaskTest2,
                "Нарушен порядок задач в ранжированной коллекции!");
    }

    @Test
    void shouldCheckTheCorrectnessOfTimeFeaturesOfEpic() {
        Epic epicWithSubTasksTest = new Epic("epicTestName1", "epicTestDescription1");

        inMemoryTaskManager.addEpic(epicWithSubTasksTest);

        SubTask subTaskTest1 = new SubTask("subTaskTestName1", "subTaskTestDescription1",
                epicWithSubTasksTest.getId(), Duration.ofMinutes(30),
                LocalDateTime.of(2024, Month.JANUARY, 2, 0, 0));
        SubTask subTaskTest2 = new SubTask("subTaskTestName2", "subTaskTestDescription2",
                epicWithSubTasksTest.getId(), Duration.ofMinutes(30),
                LocalDateTime.of(2024, Month.MARCH, 1, 12, 0));
        inMemoryTaskManager.addSubTask(subTaskTest1);
        inMemoryTaskManager.addSubTask(subTaskTest2);

        assertTrue(epicWithSubTasksTest.getStartTime().equals(subTaskTest1.getStartTime()) &&
                        epicWithSubTasksTest.getEndTime().equals(subTaskTest2.getEndTime()) &&
                        epicWithSubTasksTest.getDuration().equals(Duration.between(subTaskTest1.getStartTime(),
                                subTaskTest2.getEndTime())),
                "Временные показатели для эпика вычисляются некорректно!");
    }
}
