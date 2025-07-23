import model.Epic;
import model.Subtask;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static java.time.Duration.ofMinutes;
import static java.time.LocalDateTime.of;
import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    private Epic epic1;
    private Epic epic2;
    private Subtask subtask1;
    private Subtask subtask2;

    @BeforeEach
    void setUp() {
        // Создаём эпики
        epic1 = new Epic("Эпик 1", "Описание");
        epic1.setId(1);

        epic2 = new Epic("Эпик 2", "Другое описание");
        epic2.setId(1);

        // Создаём подзадачи
        subtask1 = new Subtask("Подзадача 1", "Описание 1", of(2025, 4, 5, 10, 0), ofMinutes(30), 1);
        subtask1.setId(101);

        subtask2 = new Subtask("Подзадача 2", "Описание 2", of(2025, 4, 5, 11, 30), ofMinutes(60), 1);
        subtask2.setId(102);
    }

    @AfterEach
    void tearDown() {
        epic1 = null;
        epic2 = null;
        subtask1 = null;
        subtask2 = null;
    }

    @Test
    void epicCalculatesStartTimeAsEarliestSubtask() {
        epic1.addSubtaskId(101);
        epic1.addSubtaskId(102);

        List<Subtask> allSubtasks = List.of(subtask1, subtask2);
        LocalDateTime result = epic1.calculateStartTime(allSubtasks);
        LocalDateTime expected = of(2025, 4, 5, 10, 0);

        assertEquals(expected, result, "startTime эпика должен быть самым ранним");
    }

    @Test
    void epicCalculatesDurationAsSumOfSubtasks() {
        epic1.addSubtaskId(101);
        epic1.addSubtaskId(102);

        List<Subtask> allSubtasks = List.of(subtask1, subtask2);
        Duration result = epic1.calculateDuration(allSubtasks);
        Duration expected = Duration.ofMinutes(90);

        assertEquals(expected, result, "duration эпика должен быть суммой подзадач");
    }

    @Test
    void epicCalculatesEndTimeAsLatestSubtaskEndTime() {
        epic1.addSubtaskId(101);
        epic1.addSubtaskId(102);

        List<Subtask> allSubtasks = List.of(subtask1, subtask2);
        LocalDateTime result = epic1.calculateEndTime(allSubtasks);
        LocalDateTime expected = of(2025, 4, 5, 12, 30); // 11:30 + 60 мин

        assertEquals(expected, result, "endTime эпика должен быть самым поздним");
    }

    @Test
    void epicsWithSameIdShouldBeEqual() {
        assertEquals(epic1, epic2, "Эпики с одинаковым id должны быть равны");
    }

    @Test
    void epicCannotAddItselfAsSubtask() {
        epic1.addSubtaskId(1);
        assertFalse(epic1.getSubtaskIds().contains(1),
                "Эпик не должен позволять добавлять себя как подзадачу");
    }
}