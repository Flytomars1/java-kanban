import manager.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T manager;
    protected Task task;
    protected Epic epic;
    protected Subtask subtask;
    protected Subtask subtask2;

    @BeforeEach
    void setUp() {
        manager = createManager();
        // Создаём тестовые объекты
        task = new Task("Задача", "Описание",
                LocalDateTime.of(2025, 4, 5, 10, 0), Duration.ofMinutes(30));
        epic = new Epic("Эпик", "Описание");
        manager.createEpic(epic);

        subtask = new Subtask("Подзадача 1", "Описание 1",
                LocalDateTime.of(2025, 4, 5, 11, 0), Duration.ofMinutes(45), epic.getId());
        subtask2 = new Subtask("Подзадача 2", "Описание 2",
                LocalDateTime.of(2025, 4, 5, 12, 0), Duration.ofMinutes(60), epic.getId());
    }

    protected abstract T createManager();

    @Test
    void shouldCreateAndRetrieveTaskById() {
        Task created = manager.createTask(task);
        Task retrieved = manager.getTaskById(created.getId());

        assertNotNull(retrieved, "Задача не должна быть null");
        assertEquals(task.getTitle(), retrieved.getTitle());
        assertEquals(task.getDescription(), retrieved.getDescription());
        assertEquals(task.getStartTime(), retrieved.getStartTime());
        assertEquals(task.getDuration(), retrieved.getDuration());
        assertEquals(task.getEndTime(), retrieved.getEndTime());
    }

    @Test
    void shouldCreateAndRetrieveEpicById() {
        Epic created = manager.createEpic(epic);
        Epic retrieved = manager.getEpicById(created.getId());

        assertNotNull(retrieved);
        assertEquals(epic.getTitle(), retrieved.getTitle());
        assertEquals(epic.getDescription(), retrieved.getDescription());
    }

    @Test
    void shouldCreateAndRetrieveSubtaskById() {
        Epic savedEpic = manager.createEpic(epic);
        subtask.setEpicId(savedEpic.getId());
        Subtask created = manager.createSubtask(subtask);
        Subtask retrieved = manager.getSubtaskById(created.getId());

        assertNotNull(retrieved);
        assertEquals(subtask.getTitle(), retrieved.getTitle());
        assertEquals(subtask.getDescription(), retrieved.getDescription());
        assertEquals(subtask.getEpicId(), retrieved.getEpicId());
        assertEquals(subtask.getStartTime(), retrieved.getStartTime());
        assertEquals(subtask.getDuration(), retrieved.getDuration());
    }

    @Test
    void subtaskShouldBeLinkedToEpic() {
        Epic savedEpic = manager.createEpic(epic);
        subtask.setEpicId(savedEpic.getId());
        manager.createSubtask(subtask);

        List<Integer> subtaskIds = savedEpic.getSubtaskIds();
        assertTrue(subtaskIds.contains(subtask.getId()),
                "ID подзадачи должно быть в списке эпика");
    }

    @Test
    void epicStatusShouldBeDoneWhenAllSubtasksAreDone() {
        Epic savedEpic = manager.createEpic(epic);
        subtask.setEpicId(savedEpic.getId());
        subtask2.setEpicId(savedEpic.getId());

        manager.createSubtask(subtask);
        manager.createSubtask(subtask2);

        subtask.setStatus(TaskStatus.DONE);
        subtask2.setStatus(TaskStatus.DONE);
        manager.updateSubtask(subtask);
        manager.updateSubtask(subtask2);

        Epic updatedEpic = manager.getEpicById(savedEpic.getId());
        assertEquals(TaskStatus.DONE, updatedEpic.getStatus(),
                "Если все подзадачи DONE, статус эпика должен быть DONE");
    }

    @Test
    void epicStatusShouldBeInProgressWhenSubtasksHaveMixedStatuses() {
        Epic savedEpic = manager.createEpic(epic);
        subtask.setEpicId(savedEpic.getId());
        subtask2.setEpicId(savedEpic.getId());

        manager.createSubtask(subtask);
        manager.createSubtask(subtask2);

        subtask.setStatus(TaskStatus.DONE);
        manager.updateSubtask(subtask);

        Epic updatedEpic = manager.getEpicById(savedEpic.getId());
        assertEquals(TaskStatus.IN_PROGRESS, updatedEpic.getStatus(),
                "Если есть DONE и NEW — статус эпика должен быть IN_PROGRESS");
    }

    @Test
    void shouldNotAllowOverlappingTasks() {
        Task task1 = new Task("Задача 1", "Описание",
                LocalDateTime.of(2025, 4, 5, 10, 0), Duration.ofMinutes(30));
        manager.createTask(task1);

        Task task2 = new Task("Задача 2", "Описание",
                LocalDateTime.of(2025, 4, 5, 10, 15), Duration.ofMinutes(30)); // пересекается

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> manager.createTask(task2));

        assertTrue(exception.getMessage().toLowerCase().contains("пересечения") ||
                exception.getMessage().toLowerCase().contains("time"));
    }

    @Test
    void shouldUpdateTaskWithoutOverlap() {
        Task task1 = new Task("Задача 1", "Описание",
                LocalDateTime.of(2025, 4, 5, 10, 0), Duration.ofMinutes(30));
        manager.createTask(task1);

        task1.setTitle("Обновлённая задача");
        task1.setStartTime(LocalDateTime.of(2025, 4, 5, 9, 0));
        manager.updateTask(task1);

        Task updated = manager.getTaskById(task1.getId());
        assertEquals("Обновлённая задача", updated.getTitle());
        assertEquals(LocalDateTime.of(2025, 4, 5, 9, 0), updated.getStartTime());
    }

    @Test
    void shouldDeleteTaskById() {
        Task created = manager.createTask(task);
        int id = created.getId();

        manager.deleteTaskById(id);
        assertNull(manager.getTaskById(id), "Задача должна быть удалена");
        assertFalse(manager.getHistory().stream().anyMatch(t -> t.getId() == id),
                "Задача не должна быть в истории");
    }

}
