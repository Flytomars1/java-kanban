package test;

import main.Epic;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    private Epic epic1;
    private Epic epic2;

    @BeforeEach
    void setUp() {
        epic1 = new Epic("Эпик 1", "Описание");
        epic1.setId(1);

        epic2 = new Epic("Эпик 2", "Другое описание");
        epic2.setId(1);
    }

    @AfterEach
    void tearDown() {
        epic1 = null;
        epic2 = null;
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