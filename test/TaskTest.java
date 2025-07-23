import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    private Task task1;
    private Task task2;
    private Task task3;

    @BeforeEach
    void setUp() {
        // создаём задачи перед каждым тестом
        task1 = new Task("Задача", "Описание", LocalDateTime.of(2025, 4, 5, 10, 0), Duration.ofMinutes(30));
        task1.setId(1);
        task1.setStatus(TaskStatus.NEW);

        task2 = new Task("Вторая задача", "второе описание", LocalDateTime.of(2025, 4, 5, 10, 0), Duration.ofMinutes(30));
        task2.setId(1);
        task2.setStatus(TaskStatus.DONE);

        task3 = new Task("Задача", "описание", LocalDateTime.of(2025, 4, 5, 10, 0), Duration.ofMinutes(30));
        task3.setId(3);
        task3.setStatus(TaskStatus.NEW);
    }

    @AfterEach
    void tearDown() {
        task1 = null;
        task2 = null;
        task3 = null;
    }

    @Test
    void tasksWithSameIdShouldBeEqual() {
        assertEquals(task1, task2, "Задачи с одинаковым id должны быть равны");
    }

    @Test
    void tasksWithDifferentIdShouldNotBeEqual() {
        assertNotEquals(task1, task3, "Задачи с разными id не должны быть равны");
    }

    @Test
    void taskHasCorrectStartTime() {
        assertEquals(LocalDateTime.of(2025, 4, 5, 10, 0), task1.getStartTime(), "startTime должен совпадать");
    }

    @Test
    void taskHasCorrectDuration() {
        assertEquals(Duration.ofMinutes(30), task1.getDuration(), "duration должен совпадать");
    }

    @Test
    void taskEndTimeIsCalculatedCorrectly() {
        assertEquals(LocalDateTime.of(2025, 4, 5, 10, 30), task1.getEndTime(), "endTime = startTime + duration");
    }


}