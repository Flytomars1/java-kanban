import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import manager.CsvParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CsvParserTest {

    @Test
    void parseTaskFromString() {
        String line = "1,TASK,Почистить зубы,NEW,Утром";

        Task task = CsvParser.parseLine(line);

        assertNotNull(task);
        assertEquals(1, task.getId());
        assertEquals("Почистить зубы", task.getTitle());
        assertEquals(TaskStatus.NEW, task.getStatus());
        assertEquals("Утром", task.getDescription());
    }

    @Test
    void parseEpicFromString() {
        String line = "2,EPIC,Купить продукты,DONE,В магазине";

        Epic epic = (Epic) CsvParser.parseLine(line);

        assertNotNull(epic);
        assertEquals(2, epic.getId());
        assertEquals("Купить продукты", epic.getTitle());
        assertEquals(TaskStatus.DONE, epic.getStatus());
        assertEquals("В магазине", epic.getDescription());
    }

    @Test
    void parseSubtaskFromString() {
        String line = "3,SUBTASK,Купить хлеб,IN_PROGRESS,Свежий батон,2";

        Subtask subtask = (Subtask) CsvParser.parseLine(line);

        assertNotNull(subtask);
        assertEquals(3, subtask.getId());
        assertEquals("Купить хлеб", subtask.getTitle());
        assertEquals(TaskStatus.IN_PROGRESS, subtask.getStatus());
        assertEquals("Свежий батон", subtask.getDescription());
        assertEquals(2, subtask.getEpicId());
    }
}