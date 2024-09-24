package utilsTest;

import history.HistoryManager;
import manager.FileBackedTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.Test;
import utils.Managers;

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
    void shouldGetInitialisedInstanceOfFileBackedTaskManager() {
        FileBackedTaskManager fileBackedTaskManager = Managers.getFileBackedTaskManager();
        assertNotNull(fileBackedTaskManager, "Экземпляр FileBackedTaskManager не проинициализирован.");
    }

    @Test
    void shouldGetInitialisedInstanceOfFileBackedTaskManagerFromFile() {
        FileBackedTaskManager fileBackedTaskManager = Managers.getFileBackedTaskManager();
        assertNotNull(fileBackedTaskManager, "Экземпляр FileBackedTaskManager из файла не восстановлен.");
    }
}