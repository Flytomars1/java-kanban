import model.Epic;
import model.Subtask;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

    private Subtask subtask1;
    private Subtask subtask2;
    private Epic epic;

    @BeforeEach
    void setUp() {
        subtask1 = new Subtask("Подзадача 1", "Описание 1", LocalDateTime.of(2025, 4, 5, 10, 0), Duration.ofMinutes(30), 100);
        subtask1.setId(1);

        subtask2 = new Subtask("Подзадача 2", "Описание 2", LocalDateTime.of(2025, 4, 5, 10, 0), Duration.ofMinutes(30), 200);
        subtask2.setId(1);

        epic = new Epic("Эпик", "Описание");
        epic.setId(2);
    }

    @AfterEach
    void tearDown() {
        subtask1 = null;
        subtask2 = null;
        epic = null;
    }

    @Test
    void subtasksWithSameIdShouldBeEqual() {
        assertEquals(subtask1, subtask2,
                "Подзадачи с одинаковым id должны быть равны");
    }

    @Test
    void subtaskCannotBeAddedToEpicWithSameId() {
        Subtask selfReferencingSubtask = new Subtask("Само-подзадача", "Я сам себе эпик", LocalDateTime.of(2025, 4, 5, 10, 0), Duration.ofMinutes(30), 1);
        selfReferencingSubtask.setId(1);

        selfReferencingSubtask.setEpicId(1);
        epic.setId(1);

        epic.addSubtaskId(selfReferencingSubtask.getId());

        assertTrue(epic.getSubtaskIds().isEmpty(),
                "Подзадача не должна быть добавлена к эпику с таким же id");
    }

    @Test
    void subtaskHasCorrectStartTime() {
        assertEquals(LocalDateTime.of(2025, 4, 5, 10, 0), subtask1.getStartTime(), "startTime должен совпадать");
    }

    @Test
    void subtaskHasCorrectDuration() {
        assertEquals(Duration.ofMinutes(30), subtask1.getDuration(), "duration должен совпадать");
    }

    @Test
    void subtaskEndTimeIsCalculatedCorrectly() {
        assertEquals(LocalDateTime.of(2025, 4, 5, 10, 30), subtask1.getEndTime(), "endTime = startTime + duration");
    }
}