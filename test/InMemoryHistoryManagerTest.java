import manager.HistoryManager;
import manager.InMemoryHistoryManager;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
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
        Task task = new Task("Задача", "Описание", LocalDateTime.of(2025, 4, 5, 10, 0), Duration.ofMinutes(30));
        task.setId(1);

        historyManager.add(task);
        List<Task> history = historyManager.getHistory();

        assertNotNull(history, "История не должна быть null");
        assertFalse(history.isEmpty(), "История не должна быть пустой");
        assertEquals(task, history.get(0), "Добавленная задача должна быть в истории");
    }

    @Test
    void duplicatesRemovedAndGetLatestWhenCall() {
        Task task1 = new Task("Задача 1", "Описание 1", LocalDateTime.of(2025, 4, 5, 10, 0), Duration.ofMinutes(30));
        task1.setId(1);
        Task task2 = new Task("Задача 2", " Описание 2", LocalDateTime.of(2025, 4, 5, 10, 0), Duration.ofMinutes(30));
        task2.setId(2);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task1);

        List<Task> history = historyManager.getHistory();

        assertEquals(2, history.size(), "в истории 2 задачи");
        assertEquals(task2, history.get(0), "первая в истории - вторая задача, потому что первая автоматически удалилась");
        assertEquals(task1, history.get(1), "последняя в итосрии - первая вызванная повторно");
    }

    @Test
    void removingTaskFromHistory() {
        Task task1 = new Task("Задача 1", "Описание 1", LocalDateTime.of(2025, 4, 5, 10, 0), Duration.ofMinutes(30));
        task1.setId(1);

        Task task2 = new Task("Задача 2", "Описание 2", LocalDateTime.of(2025, 4, 5, 10, 0), Duration.ofMinutes(30));
        task2.setId(2);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.remove(2);

        List<Task> history = historyManager.getHistory();

        assertEquals(1, history.size(), "В истории должна быть только одна задача");
        assertEquals(task1, history.get(0), "В истории должна остаться только задача 1");
    }

    @Test
    void multipleCallsMaintainOrderOfLastViewed() {
        Task task1 = new Task("Задача 1", "Описание 1", LocalDateTime.of(2025, 4, 5, 10, 0), Duration.ofMinutes(30));
        task1.setId(1);

        Task task2 = new Task("Задача 2", "Описание 2", LocalDateTime.of(2025, 4, 5, 10, 0), Duration.ofMinutes(30));
        task2.setId(2);

        Task task3 = new Task("Задача 3", "Описание 3", LocalDateTime.of(2025, 4, 5, 10, 0), Duration.ofMinutes(30));
        task3.setId(3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(task2);
        historyManager.add(task1);

        List<Task> history = historyManager.getHistory();

        assertEquals(3, history.size(), "В истории должно быть 3 задачи");

        assertEquals(task3, history.get(0), "Первая задача должна быть task3");
        assertEquals(task2, history.get(1), "Вторая задача должна быть task2");
        assertEquals(task1, history.get(2), "Третья задача должна быть task1");
    }

    @Test
    void changesToOriginalTaskShouldNotAffectHistory() {
        Task task = new Task("Оригинальное имя", "Описание", LocalDateTime.of(2025, 4, 5, 10, 0), Duration.ofMinutes(30));
        task.setId(1);

        historyManager.add(task);
        task.setTitle("Изменённое имя");
        List<Task> history = historyManager.getHistory();

        assertEquals("Оригинальное имя", history.get(0).getTitle(), "Имя в истории не должно измениться после изменения оригинала");
    }
}