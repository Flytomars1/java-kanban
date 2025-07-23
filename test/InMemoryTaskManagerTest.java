import manager.InMemoryTaskManager;
import model.Epic;
import model.TaskStatus;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    protected InMemoryTaskManager createManager() {
        return new InMemoryTaskManager();
    }

    @Test
    void epicStatusShouldBeInProgressWhenAnySubtaskIsInProgress() {
        Epic savedEpic = manager.createEpic(epic);
        subtask.setEpicId(savedEpic.getId());
        subtask2.setEpicId(savedEpic.getId());

        manager.createSubtask(subtask);
        manager.createSubtask(subtask2);

        // Меняем одну подзадачу на IN_PROGRESS
        subtask.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateSubtask(subtask);

        Epic updatedEpic = manager.getEpicById(savedEpic.getId());
        assertEquals(TaskStatus.IN_PROGRESS, updatedEpic.getStatus(),
                "Если хотя бы одна подзадача IN_PROGRESS, статус эпика должен быть IN_PROGRESS");
    }

    @Test
    void epicCalculateStartTimeDurationAndEndTime() {
        manager.createSubtask(subtask);  // [11:00 - 11:45]
        manager.createSubtask(subtask2); // [12:00 - 13:00]

        Epic storedEpic = manager.getEpicById(epic.getId());

        assertNotNull(storedEpic.getStartTime(), "startTime эпика не должен быть null");
        assertEquals(LocalDateTime.of(2025, 4, 5, 11, 0), storedEpic.getStartTime(),
                "startTime эпика должен быть самым ранним");

        assertEquals(Duration.ofMinutes(105), storedEpic.getDuration(),
                "duration эпика должен быть суммой длительностей подзадач");

        assertEquals(LocalDateTime.of(2025, 4, 5, 13, 0), storedEpic.getEndTime(),
                "endTime эпика должен быть временем окончания самой поздней подзадачи");
    }

    @Test
    void epicStatusShouldBeNewAfterAllSubtasksAreDeleted() {
        Epic savedEpic = manager.createEpic(epic);
        subtask.setEpicId(savedEpic.getId());
        manager.createSubtask(subtask);

        assertEquals(TaskStatus.NEW, manager.getEpicById(savedEpic.getId()).getStatus());

        subtask.setStatus(TaskStatus.DONE);
        manager.updateSubtask(subtask);

        assertEquals(TaskStatus.DONE, manager.getEpicById(savedEpic.getId()).getStatus());

        manager.deleteAllSubtasks();

        Epic updatedEpic = manager.getEpicById(savedEpic.getId());
        assertNotNull(updatedEpic, "Эпик не должен быть удалён");
        assertTrue(updatedEpic.getSubtaskIds().isEmpty(), "Список подзадач должен быть пуст");
        assertEquals(TaskStatus.NEW, updatedEpic.getStatus(),
                "После удаления всех подзадач статус эпика должен стать NEW");
    }

}