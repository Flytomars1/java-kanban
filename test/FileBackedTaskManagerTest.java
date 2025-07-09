import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import manager.FileBackedTaskManager;

import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {
    File file;
    FileBackedTaskManager manager;

    @BeforeEach
    void setUp() throws IOException {
        file = File.createTempFile("test", ".csv");
        manager = new FileBackedTaskManager(file);
    }

    @AfterEach
    void tearDown() {
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    void testSaveAndLoad() {
        Task task = new Task("Задача 1", "Описание задачи");
        manager.createTask(task);
        Epic epic = new Epic("Эпик 1", "Описание эпика");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Подзадача 1", "Описание подзадачи", epic.getId());
        manager.createSubtask(subtask);

        manager.save();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        assertEquals(1, loadedManager.getTasks().size());
        assertEquals(task.getTitle(), loadedManager.getTaskById(task.getId()).getTitle());

        assertEquals(1, loadedManager.getEpics().size());
        assertEquals(epic.getTitle(), loadedManager.getEpicById(epic.getId()).getTitle());

        assertEquals(1, loadedManager.getSubtasks().size());
        assertEquals(subtask.getTitle(), loadedManager.getSubtaskById(subtask.getId()).getTitle());

        Epic loadedEpic = loadedManager.getEpicById(epic.getId());
        assertTrue(loadedEpic.getSubtaskIds().contains(subtask.getId()));
    }

    @Test
    void testUpdateAndSave() {
        Task task = new Task("Старое название", "Старое описание");
        manager.createTask(task);

        task.setTitle("Новое названне");
        task.setDescription("Новое описание");
        task.setStatus(TaskStatus.DONE);
        manager.updateTask(task);

        manager.save();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);
        Task updated = loadedManager.getTaskById(task.getId());

        assertEquals("Новое названне", updated.getTitle());
        assertEquals("Новое описание", updated.getDescription());
        assertEquals(TaskStatus.DONE, updated.getStatus());
    }

    @Test
    void testLoadEmptyFile() {
        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(file);

        assertTrue(manager.getTasks().isEmpty());
        assertTrue(manager.getEpics().isEmpty());
        assertTrue(manager.getSubtasks().isEmpty());
    }

    @Test
    void testSaveEmptyFile() {
        manager.save();

        assertFalse(manager.getTasks().containsKey(1));
        assertFalse(manager.getEpics().containsKey(1));
        assertFalse(manager.getSubtasks().containsKey(1));
    }


}
