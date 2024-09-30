package utilsTest;

import history.HistoryManager;
import manager.FileBackedTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.Test;
import utils.Managers;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void shouldGetInitialisedInstanceOfTaskManager() {
        TaskManager taskManager = Managers.getDefault();
        assertNotNull(taskManager, "Экземпляр InMemoryTaskManager не проинициализирован.");
    }

    @Test
    void shouldGetInitialisedInstanceOfHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager, "Экземпляр HistoryManager не проинициализирован.");
    }

    @Test
    void shouldGetInitialisedInstanceOfFileBackedTaskManager() throws IOException {
        File tempFile = File.createTempFile("dataTest",".csv");
        FileBackedTaskManager fileBackedTaskManager = Managers.getFileBackedTaskManager(tempFile.getPath());
        assertNotNull(fileBackedTaskManager, "Экземпляр FileBackedTaskManager не проинициализирован.");
    }

    @Test
    void shouldGetInitialisedInstanceOfFileBackedTaskManagerFromFile() throws IOException {
        File tempFile = File.createTempFile("dataTest",".csv");
        FileBackedTaskManager fileBackedTaskManager = Managers.downloadFromFile(tempFile.getPath());
        assertNotNull(fileBackedTaskManager, "Экземпляр FileBackedTaskManager из файла не восстановлен.");
    }
}