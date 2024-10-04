package managerTest;

import manager.FileBackedTaskManager;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import utils.Managers;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {

    @Test
    void instanceOfInMemoryTaskManagerForTests() throws IOException {
        File tempFile = File.createTempFile("dataTest",".csv");
        FileBackedTaskManager fileBackedTaskManagerTest = Managers.getFileBackedTaskManager(tempFile.getPath());
        Task taskTest = new Task("taskTestName1", "taskTestDescription1", Duration.ofMinutes(30),
                LocalDateTime.of(2024, Month.JANUARY, 1, 0, 0));
        Epic epicTest = new Epic("epicTestName1", "epicTestDescription1");

        fileBackedTaskManagerTest.addTask(taskTest);
        fileBackedTaskManagerTest.addEpic(epicTest);

        SubTask subTaskTest = new SubTask("taskTestName1", "taskTestDescription1", epicTest.getId(),
                Duration.ofMinutes(30), LocalDateTime.of(2025, Month.JANUARY, 1, 0, 0));
        fileBackedTaskManagerTest.addSubTask(subTaskTest);

        FileBackedTaskManager fileBackedTaskManagerLoaded = Managers.downloadFromFile(tempFile.getPath());

        assertEquals(fileBackedTaskManagerLoaded.getTaskList(), fileBackedTaskManagerTest.getTaskList(),
                "Задачи типа Task загружаются из файла некорректно.");
        assertEquals(fileBackedTaskManagerLoaded.getEpicList(), fileBackedTaskManagerTest.getEpicList(),
                "Задачи типа Epic загружаются из файла некорректно.");
        assertEquals(fileBackedTaskManagerLoaded.getSubTaskList(), fileBackedTaskManagerTest.getSubTaskList(),
                "Задачи типа SubTask загружаются из файла некорректно.");
        assertEquals(fileBackedTaskManagerLoaded.getPrioritizedTasks(), fileBackedTaskManagerTest.getPrioritizedTasks(),
                "Список задач, отсортированных по приорететности загружается из файла некорректно.");

        Task taskTestId = new Task("taskTestName2", "taskTestDescription2", Duration.ofMinutes(30),
                LocalDateTime.of(2024, Month.MAY, 1, 0, 0));
        fileBackedTaskManagerLoaded.addTask(taskTestId);
        int idOfFirstAddedTaskAfterLoad = taskTestId.getId();

        assertEquals(4, idOfFirstAddedTaskAfterLoad,
                "Присваивание id после загрузки данных из файла работает некорректно");
    }
}
