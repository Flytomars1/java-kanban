package test;

import main.Task;
import main.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    private Task task1;
    private Task task2;
    private Task task3;

    @BeforeEach
    void setUp() {
        // создаём задачи перед каждым тестом
        task1 = new Task("Задача", "Описание");
        task1.setId(1);
        task1.setStatus(TaskStatus.NEW);

        task2 = new Task("Вторая задача", "второе описание");
        task2.setId(1);
        task2.setStatus(TaskStatus.DONE);

        task3 = new Task("Задача", "описание");
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
}