package com.github.sirrop.historyj2;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class LinkedHistoryTest {
    @Test
    public void initializeTest() {
        History<Integer> history = new LinkedHistory<>();
        assertAll("default config",
                () -> assertFalse(history.canRedo()),
                () -> assertFalse(history.canUndo()),
                () -> assertEquals(history.getCapacity(), 100),
                () -> assertEquals(history.currentIndex(), -1),
                () -> assertNull(history.currentRecord())
        );

        History<Integer> history1 = new LinkedHistory<>(200);
        assertAll("specified config",
                () -> assertFalse(history1.canRedo()),
                () -> assertFalse(history1.canUndo()),
                () -> assertEquals(history1.getCapacity(), 200),
                () -> assertEquals(history1.currentIndex(), -1),
                () -> assertNull(history1.currentRecord())
        );
    }

    @Test
    public void addTest() {
        LinkedHistory<Integer> history = new LinkedHistory<>();
        history.add(0);
        assertAll(
                () -> assertEquals(history.get(0), 0),
                () -> assertFalse(history.canRedo()),
                () -> assertFalse(history.canUndo()),
                () -> assertEquals(history.getCapacity(), 100),
                () -> assertEquals(history.currentIndex(), 0),
                () -> assertEquals(history.currentRecord(), 0)
        );

        history.add(1);
        assertAll(
                () -> assertEquals(history.get(0), 0),
                () -> assertEquals(history.get(1), 1),
                () -> assertFalse(history.canRedo()),
                () -> assertTrue(history.canUndo()),
                () -> assertEquals(history.getCapacity(), 100),
                () -> assertEquals(history.currentIndex(), 1),
                () -> assertEquals(history.currentRecord(), 1)
        );

        for (int i = 0; i < 100; i++) {
            history.add(i + 2);
        }

        assertAll(
                () -> assertEquals(history.get(0), 2),
                () -> assertFalse(history.canRedo()),
                () -> assertTrue(history.canUndo()),
                () -> assertEquals(history.getCapacity(), 100),
                () -> assertEquals(history.currentIndex(), 99),
                () -> assertEquals(history.currentRecord(), 101)
        );
    }

    @Test
    public void undoAndRedoTest() {
        LinkedHistory<Integer> history = new LinkedHistory<>();
        for (int i = 0; i < 100; i++) {
            history.add(i);
        }
        for (int i = 0; i < 5; i++) {
            history.undo();
        }
        assertAll(
                () -> assertEquals(94, history.currentIndex()),
                () -> assertEquals(94, history.currentRecord()),
                () -> assertTrue(history.canUndo()),
                () -> assertTrue(history.canRedo())
        );
        history.redo();
        assertEquals(95, history.currentIndex());
        assertEquals(95, history.currentRecord());
    }
}
