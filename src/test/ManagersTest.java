package test;

import main.HistoryManager;
import main.Managers;
import main.TaskManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void taskManagerIsNotNull() {
        TaskManager manager = Managers.getDefault();
        assertNotNull(manager, "main.TaskManager не должен быть null");
    }

    @Test
    void historyManagerIsNotNull() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager, "main.HistoryManager не должен быть null");
    }
}