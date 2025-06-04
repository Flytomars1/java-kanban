import manager.HistoryManager;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void taskManagerIsNotNull() {
        TaskManager manager = Managers.getDefault();
        assertNotNull(manager, "manager.TaskManager не должен быть null");
    }

    @Test
    void historyManagerIsNotNull() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager, "manager.HistoryManager не должен быть null");
    }
}