import model.Epic;
import model.Subtask;
import model.Task;
import manager.FileBackedTaskManager;

import model.TaskStatus;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private File tempFile;

    @Override
    protected FileBackedTaskManager createManager() {
        try {
            if (tempFile == null || !tempFile.exists()) {
                tempFile = File.createTempFile("test", ".csv");
            }
            return new FileBackedTaskManager(tempFile);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать временный файл", e);
        }
    }

    @BeforeEach
    void setUp() {
        super.setUp(); // вызываем родительский setUp()
    }

    @AfterEach
    void tearDown() {
        if (tempFile != null && tempFile.exists()) {
            assertTrue(tempFile.delete(), "Не удалось удалить временный файл");
        }
    }

    @Test
    void shouldPreserveAllDataAfterSaveAndReload() {
        Task savedTask = manager.createTask(task);
        Subtask savedSubtask = manager.createSubtask(subtask);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        Task loadedTask = loadedManager.getTaskById(savedTask.getId());
        assertNotNull(loadedTask);
        assertEquals(savedTask.getTitle(), loadedTask.getTitle());

        Epic loadedEpic = loadedManager.getEpicById(epic.getId());
        assertNotNull(loadedEpic);
        assertEquals(epic.getTitle(), loadedEpic.getTitle());

        Subtask loadedSubtask = loadedManager.getSubtaskById(savedSubtask.getId());
        assertNotNull(loadedSubtask);
        assertEquals(savedSubtask.getTitle(), loadedSubtask.getTitle());
        assertEquals(epic.getId(), loadedSubtask.getEpicId());

        assertTrue(loadedEpic.getSubtaskIds().contains(savedSubtask.getId()));

        assertEquals(TaskStatus.NEW, loadedEpic.getStatus());

        List<Task> history = loadedManager.getHistory();
        assertFalse(history.isEmpty());
        assertEquals(savedTask.getId(), history.getFirst().getId());
    }

    @Test
    void shouldLoadEmptyFileWithoutException() {
        File emptyFile = assertDoesNotThrow(() -> File.createTempFile("empty", ".csv"));

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(emptyFile);

        assertTrue(loadedManager.getTasks().isEmpty());
        assertTrue(loadedManager.getEpics().isEmpty());
        assertTrue(loadedManager.getSubtasks().isEmpty());
        assertTrue(loadedManager.getHistory().isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenLoadFromNonExistentFile() {
        File file = new File("non-existent-file.csv");
        assertFalse(file.exists());

        Exception exception = assertThrows(RuntimeException.class, () ->
                FileBackedTaskManager.loadFromFile(file));

        assertTrue(exception.getMessage().toLowerCase().contains("чтения") ||
                exception.getMessage().toLowerCase().contains("существует"));
    }

    @Test
    void shouldPreservePrioritizedTasksAfterReload() {
        Task savedTask = manager.createTask(task);
        Subtask savedSubtask = manager.createSubtask(subtask);
        Subtask savedSubtask2 = manager.createSubtask(subtask2);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        List<Task> prioritized = loadedManager.getPrioritizedTasks();
        assertEquals(3, prioritized.size(), "Должно быть 3 задачи с временем");

        assertEquals(savedTask.getId(), prioritized.get(0).getId());   // 10:00
        assertEquals(savedSubtask.getId(), prioritized.get(1).getId()); // 11:00
        assertEquals(savedSubtask2.getId(), prioritized.get(2).getId()); // 12:00
    }

    @Test
    void shouldPreservePrioritizedTasksAfterReload1() {
        Task task1 = new Task("Ранняя задача", "Описание",
                LocalDateTime.of(2025, 4, 5, 9, 0), Duration.ofMinutes(30));
        Task task2 = new Task("Поздняя задача", "Описание",
                LocalDateTime.of(2025, 4, 5, 11, 0), Duration.ofMinutes(30));

        manager.createTask(task1);
        manager.createTask(task2);

        manager.save();
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        List<Task> prioritized = loadedManager.getPrioritizedTasks();
        assertEquals(2, prioritized.size());
        assertEquals("Ранняя задача", prioritized.get(0).getTitle());
        assertEquals("Поздняя задача", prioritized.get(1).getTitle());
    }
}
