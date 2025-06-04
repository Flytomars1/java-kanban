import manager.HistoryManager;
import manager.InMemoryHistoryManager;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void historyIsEmptyInitially() {
        List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не должна быть null");
        assertTrue(history.isEmpty(), "История должна быть пуста после создания");
    }

    @Test
    void taskIsAddedToHistory() {
        Task task = new Task("Задача", "Описание");
        task.setId(1);

        historyManager.add(task);
        List<Task> history = historyManager.getHistory();

        assertNotNull(history, "История не должна быть null");
        assertFalse(history.isEmpty(), "История не должна быть пустой");
        assertEquals(task, history.get(0), "Добавленная задача должна быть в истории");
    }

    @Test
    void historyStoresOnlyLast10Tasks() {
        for (int i = 1; i <= 12; i++) {
            Task task = new Task("Задача " + i, "Описание");
            task.setId(i);
            historyManager.add(task);
        }

        List<Task> history = historyManager.getHistory();

        assertEquals(10, history.size(), "В истории должно быть только 10 последних задач");
        assertEquals(3, history.get(0).getId(), "Самая старая задача была удалена из истории");
        assertEquals(12, history.get(history.size() - 1).getId(), "Самая новая задача должна быть в конце");
    }
}