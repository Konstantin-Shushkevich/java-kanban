package managerTest;

import manager.FileBackedTaskManager;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Task;
import utils.Managers;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {

    @Test
    void instanceOfInMemoryTaskManagerForTests() throws IOException {
        File tempFile = File.createTempFile("dataTest",".csv");
        FileBackedTaskManager fileBackedTaskManagerTest = Managers.getFileBackedTaskManager(tempFile.getPath());
        Task taskTest = new Task("taskTestName1", "taskTestDescription1");
        Epic epicTest = new Epic("epicTestName1", "epicTestDescription1");

        fileBackedTaskManagerTest.addTask(taskTest);
        fileBackedTaskManagerTest.addEpic(epicTest);

        FileBackedTaskManager fileBackedTaskManagerLoaded = Managers.downloadFromFile(tempFile.getPath());

        assertEquals(fileBackedTaskManagerLoaded.getTaskList(), fileBackedTaskManagerTest.getTaskList(),
                "Данные из файла загружены не корректно.");
        assertEquals(fileBackedTaskManagerLoaded.getEpicList(), fileBackedTaskManagerTest.getEpicList(),
                "Данные из файла загружены не корректно.");
    }
}
