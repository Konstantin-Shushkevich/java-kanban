package utilsTest;

import history.HistoryManager;
import manager.TaskManager;
import org.junit.jupiter.api.Test;
import utils.Managers;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void shouldGetInitialisedInstanceOfTaskManager() {
        TaskManager taskManager = Managers.getDefault();
        assertEquals(taskManager instanceof TaskManager,
                "Экземпляр TaskManager не проинициализирован.");
    }

    @Test
    void shouldGetInitialisedInstanceOfHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertEquals(historyManager instanceof HistoryManager,
                "Экземпляр HistoryManager не проинициализирован.");
    }
}