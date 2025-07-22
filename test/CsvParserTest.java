import model.*;
import manager.CsvParser;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CsvParserTest {
    private static final LocalDateTime TIME = LocalDateTime.of(2025, 4, 5, 10, 0);

    @Test
    void taskToStringIncludeAllFields() {
        Task task = new Task("Задача", "Описание", TIME, Duration.ofMinutes(30));
        task.setId(1);
        task.setStatus(TaskStatus.DONE);

        String result = CsvParser.taskToString(task);

        String expected = "1,TASK,Задача,DONE,Описание,2025-04-05 10:00,30,";
        assertEquals(expected, result);
    }

    @Test
    void epicToStringShouldIncludeAllFields() {
        Epic epic = new Epic("Эпик", "Описание");
        epic.setId(2);
        epic.setStatus(TaskStatus.IN_PROGRESS);
        epic.setStartTime(TIME);
        epic.setDuration(Duration.ofMinutes(60));

        String result = CsvParser.epicToString(epic);

        String expected = "2,EPIC,Эпик,IN_PROGRESS,Описание,2025-04-05 10:00,60,";
        assertEquals(expected, result);
    }

    @Test
    void subtaskToStringShouldIncludeAllFields() {
        Subtask subtask = new Subtask("Подзадача", "Описание", TIME, Duration.ofMinutes(30), 1);
        subtask.setId(3);
        subtask.setStatus(TaskStatus.DONE);

        String result = CsvParser.subtaskToString(subtask);

        String expected = "3,SUBTASK,Подзадача,DONE,Описание,2025-04-05 10:00,30,1";
        assertEquals(expected, result);
    }

    @Test
    void taskToStringWithNullTimeAndDurationShouldNotThrow() {
        Task task = new Task("Без времени", "Описание", null, null);
        task.setId(1);
        task.setStatus(TaskStatus.NEW);

        String result = CsvParser.taskToString(task);

        String expected = "1,TASK,Без времени,NEW,Описание,,,";
        assertEquals(expected, result);
    }



    @Test
    void parseTaskFromString() {
        String line = "1,TASK,Почистить зубы,NEW,Утром,2025-04-05 10:00,30";

        Task task = CsvParser.parseLine(line);

        assertNotNull(task);
        assertEquals(1, task.getId());
        assertEquals("Почистить зубы", task.getTitle());
        assertEquals(TaskStatus.NEW, task.getStatus());
        assertEquals("Утром", task.getDescription());
        assertEquals(LocalDateTime.of(2025, 4, 5, 10, 0), task.getStartTime());
        assertEquals(Duration.ofMinutes(30), task.getDuration());
    }

    @Test
    void parseEpicFromString() {
        String line = "2,EPIC,Купить продукты,DONE,В магазине,2025-04-05 10:00,30";

        Epic epic = (Epic) CsvParser.parseLine(line);

        assertNotNull(epic);
        assertEquals(2, epic.getId());
        assertEquals("Купить продукты", epic.getTitle());
        assertEquals(TaskStatus.DONE, epic.getStatus());
        assertEquals("В магазине", epic.getDescription());
        assertEquals(LocalDateTime.of(2025, 4, 5, 10, 0), epic.getStartTime());
        assertEquals(Duration.ofMinutes(30), epic.getDuration());
    }

    @Test
    void parseSubtaskFromString() {
        String line = "3,SUBTASK,Купить хлеб,IN_PROGRESS,Свежий батон,2025-04-05 10:00,30,2";

        Subtask subtask = (Subtask) CsvParser.parseLine(line);

        assertNotNull(subtask);
        assertEquals(3, subtask.getId());
        assertEquals("Купить хлеб", subtask.getTitle());
        assertEquals(TaskStatus.IN_PROGRESS, subtask.getStatus());
        assertEquals("Свежий батон", subtask.getDescription());
        assertEquals(2, subtask.getEpicId());
        assertEquals(LocalDateTime.of(2025, 4, 5, 10, 0), subtask.getStartTime());
        assertEquals(Duration.ofMinutes(30), subtask.getDuration());
    }

    @Test
    void parseWithInvalidTypeShouldThrowException() {
        String line = "1,UNKNOWN,Название,NEW,Описание";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> CsvParser.parseLine(line));

        assertTrue(exception.getMessage().contains("Неизвестный тип задачи"));
    }

    @Test
    void parseWithMalformedDateTimeShouldThrowException() {
        String line = "1,TASK,Название,NEW,Описание,invalid-date,30";

        Exception exception = assertThrows(Exception.class,
                () -> CsvParser.parseLine(line));

        assertTrue(exception instanceof RuntimeException || exception.getCause() instanceof java.time.format.DateTimeParseException);
    }
}