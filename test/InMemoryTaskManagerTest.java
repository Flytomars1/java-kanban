import manager.InMemoryTaskManager;
import manager.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private Task task;
    private Epic epic;
    private Subtask subtask;
    private Subtask subtask1;
    private TaskManager manager;
    int idCounter;

    @BeforeEach
    void setUp() {
        idCounter = 1;

        manager = new InMemoryTaskManager();

        epic = new Epic("Эпик 1", "Описание");
        manager.createEpic(epic);
        int epicId = epic.getId();

        task = new Task("Задача", "Описание");
        task.setId(2);

        subtask = new Subtask("Подзадача 1", "Описание 1", 1);
        subtask.setId(3);

        subtask1 = new Subtask("Подзадача 2", "Описание 2", 1);
        subtask1.setId(4);
    }

    @AfterEach
    void tearDown() {
        idCounter = 1;
        subtask = null;
        subtask1 = null;
        task = null;
        epic = null;
        manager = null;
    }

    @Test
    void taskCanBeCreatedAndRetrievedById() {

        Task createdTask = manager.createTask(task);
        Task retrievedTask = manager.getTaskById(createdTask.getId());

        assertNotNull(retrievedTask, "Задача не должна быть null");
        assertEquals(task.getTitle(), retrievedTask.getTitle(), "Названия задач должны совпадать");
        assertEquals(task.getDescription(), retrievedTask.getDescription(), "Описания задач должны совпадать");
        assertEquals(createdTask.getId(), retrievedTask.getId(), "ID задач должны совпадать");
    }

    @Test
    void epicCanBeCreatedAndRetrievedById() {
        Epic createdEpic = manager.createEpic(epic);
        Epic retrievedEpic = manager.getEpicById(createdEpic.getId());

        assertNotNull(retrievedEpic, "Эпик не должен быть null");
        assertEquals(epic.getTitle(), retrievedEpic.getTitle(), "Названия эпиков должны совпадать");
        assertEquals(epic.getDescription(), retrievedEpic.getDescription(), "Описания эпиков должны совпадать");
        assertEquals(epic.getId(), retrievedEpic.getId(), "ID эпиков должны совпадать");
    }

    @Test
    void subtaskCanBeCreatedAndRetrievedById() {
        manager.createSubtask(subtask);

        Subtask retrievedSubtask = manager.getSubtaskById(subtask.getId());

        assertNotNull(retrievedSubtask, "Подзадача должна существовать после создания");
        assertEquals(subtask.getId(), retrievedSubtask.getId(), "ID должен совпадать");
        assertEquals(subtask.getTitle(), retrievedSubtask.getTitle(), "Название должно совпадать");
        assertEquals(subtask.getDescription(), retrievedSubtask.getDescription(), "Описание должно совпадать");
        assertEquals(subtask.getEpicId(), retrievedSubtask.getEpicId(), "epicId должен совпадать");
    }

    @Test
    void subtaskAddsToEpicAndUpdatesStatus() {
        manager.createSubtask(subtask);

        Epic storedEpic = manager.getEpicById(epic.getId());

        assertNotNull(storedEpic, "Эпик должен существовать");
        assertTrue(storedEpic.getSubtaskIds().contains(subtask.getId()),
                "Эпик должен содержать ID этой подзадачи");

        // создать свою копию подзадачи, чтобы обратится к правильному id
        Subtask storedSubtask = manager.getSubtaskById(subtask.getId());
        storedSubtask.setStatus(TaskStatus.DONE);
        manager.updateSubtask(storedSubtask);

        storedEpic = manager.getEpicById(epic.getId());

        assertEquals(TaskStatus.DONE, storedEpic.getStatus(),
                "Статус эпика должен стать DONE, если подзадача DONE");
    }

    @Test
    void epicStatusIsNewIfNoSubtasks() {
        Epic createdEpic = manager.createEpic(epic);
        Epic retrievedEpic = manager.getEpicById(createdEpic.getId());

        assertEquals(TaskStatus.NEW, retrievedEpic.getStatus(),
                "Эпик без подзадач должен иметь статус NEW");
    }

    @Test
    void taskUpdateChangesOnlyTargetedFields() {
        manager.createTask(task);
        Task updatedTask = new Task("Обновлённая задача", "Новое описание");
        updatedTask.setId(task.getId());
        updatedTask.setStatus(TaskStatus.DONE);

        manager.updateTask(updatedTask);

        Task storedTask = manager.getTaskById(task.getId());

        assertNotNull(storedTask, "Задача не найдена");
        assertEquals("Обновлённая задача", storedTask.getTitle(), "Заголовок должен быть изменён");
        assertEquals("Новое описание", storedTask.getDescription(), "Описание должно быть изменено");
        assertEquals(TaskStatus.DONE, storedTask.getStatus(), "Статус должен быть изменён");

        assertEquals(task.getId(), storedTask.getId(), "ID задачи не должен меняться при обновлении");
    }

    @Test
    void taskIsNotRetrievableAfterDeletion() {
        Task createdTask = manager.createTask(task);
        int taskId = createdTask.getId();

        manager.deleteTaskById(taskId);

        Task retrievedTask = manager.getTaskById(taskId);

        assertNull(retrievedTask, "После удаления задача не должна быть доступна");
    }

}