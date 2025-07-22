import manager.InMemoryTaskManager;
import manager.TaskManager;
import model.Task;
import model.Epic;
import model.Subtask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerHistoryTest {
    private TaskManager taskManager;
    private Task task;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager();

        task = new Task("Задача", "Описание", LocalDateTime.of(2025, 4, 5, 10, 0), Duration.ofMinutes(30));
        Epic epic = new Epic("Эпик", "Описание");
        Subtask subtask = new Subtask("Подзадача", "Описание", LocalDateTime.of(2025, 4, 5, 10, 0), Duration.ofMinutes(30), 1);
    }

    @Test
    void taskIsRemovedFromHistoryAfterDeletion() {
        Task createdTask = taskManager.createTask(task);
        int taskId = createdTask.getId();

        // Добавляем задачу в историю
        taskManager.getTaskById(taskId);

        // Проверяем, что она есть в истории
        List<Task> history = taskManager.getHistory();
        assertEquals(1, history.size(), "Задача должна быть в истории");

        // Удаляем задачу
        taskManager.deleteTaskById(taskId);

        // Проверяем, что её больше нет в истории
        history = taskManager.getHistory();
        assertTrue(history.isEmpty(), "История должна быть пустой после удаления задачи");
    }

    @Test
    void subtaskIsRemovedFromHistoryAfterDeletion() {
        Epic epic = new Epic("Эпик", "Описание");
        epic = taskManager.createEpic(epic);

        Subtask subtask = new Subtask("Подзадача", "Описание", LocalDateTime.of(2025, 4, 5, 10, 0), Duration.ofMinutes(30), epic.getId());
        subtask = taskManager.createSubtask(subtask);
        int subtaskId = subtask.getId();

        // Добавляем подзадачу в историю
        taskManager.getSubtaskById(subtaskId);

        // Проверяем, что она есть в истории
        List<Task> history = taskManager.getHistory();
        assertEquals(1, history.size(), "Подзадача должна быть в истории");

        // Удаляем подзадачу
        taskManager.deleteSubtaskById(subtaskId);

        // Проверяем, что её больше нет в истории
        history = taskManager.getHistory();
        assertTrue(history.isEmpty(), "История должна быть пустой после удаления подзадачи");
    }

    @Test
    void epicAndItsSubtasksAreRemovedFromHistoryAfterDeletion() {
        Epic epic = new Epic("Эпик", "Описание");
        taskManager.createEpic(epic);
        int epicId = epic.getId();

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание 1", LocalDateTime.of(2025, 4, 5, 10, 0), Duration.ofMinutes(30), epicId);
        taskManager.createSubtask(subtask1);
        int s1Id = subtask1.getId();

        Subtask subtask2 = new Subtask("Подзадача 2", "Описание 2", LocalDateTime.of(2025, 4, 5, 20, 0), Duration.ofMinutes(30), epicId);
        taskManager.createSubtask(subtask2);
        int s2Id = subtask2.getId();

        // Добавляем в историю
        taskManager.getEpicById(epicId);
        taskManager.getSubtaskById(s1Id);
        taskManager.getSubtaskById(s2Id);

        List<Task> history = taskManager.getHistory();
        assertEquals(3, history.size(), "В истории должно быть 3 элемента");

        taskManager.deleteEpicById(epicId);

        history = taskManager.getHistory();
        assertTrue(history.isEmpty(), "История должна быть пустой после удаления эпика и подзадач");
    }

    @Test
    void allTasksAreRemovedFromHistoryAfterDeleteAll() {
        Epic epic = new Epic("Эпик", "Описание");
        taskManager.createEpic(epic);
        int epicId = epic.getId();

        Task task = new Task("Задача", "Описание", LocalDateTime.of(2025, 4, 5, 10, 0), Duration.ofMinutes(30));
        taskManager.createTask(task);
        int taskId = task.getId();

        Subtask subtask = new Subtask("Подзадача", "Описание", LocalDateTime.of(2025, 4, 5, 20, 0), Duration.ofMinutes(30),  epicId);
        taskManager.createSubtask(subtask);
        int subtaskId = subtask.getId();

        taskManager.getEpicById(epicId);
        taskManager.getTaskById(taskId);
        taskManager.getSubtaskById(subtaskId);

        List<Task> history = taskManager.getHistory();
        assertEquals(3, history.size(), "В истории должно быть 3 элемента");

        taskManager.deleteAll();

        history = taskManager.getHistory();
        assertEquals(0, history.size(), "После deleteAll() ничего не остается");
    }
}
