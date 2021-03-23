package com.github.sirrop.historyj2;

import org.junit.jupiter.api.Test;

import java.io.Serializable;

import static org.junit.jupiter.api.Assertions.*;

public class BinaryHistoryTest {
    @Test
    public void initializeTest() {
        History<Serializable> history = new BinaryHistory();
        assertAll("default config",
                () -> assertFalse(history.canRedo()),
                () -> assertFalse(history.canUndo()),
                () -> assertEquals(history.getCapacity(), 100),
                () -> assertEquals(history.currentIndex(), -1),
                () -> assertNull(history.currentRecord())
        );

        History<Serializable> history1 = new BinaryHistory(200);
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
        BinaryHistory history = new BinaryHistory();
        history.add(0);
        assertAll(
                () -> assertFalse(history.canRedo()),
                () -> assertFalse(history.canUndo()),
                () -> assertEquals(history.getCapacity(), 100),
                () -> assertEquals(history.currentIndex(), 0),
                () -> assertEquals(history.currentRecord(), 0)
        );

        history.add(1);
        assertAll(
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
                () -> assertFalse(history.canRedo()),
                () -> assertTrue(history.canUndo()),
                () -> assertEquals(history.getCapacity(), 100),
                () -> assertEquals(history.currentIndex(), 99),
                () -> assertEquals(history.currentRecord(), 101)
        );
    }

    @Test
    public void undoAndRedoTest() {
        BinaryHistory history = new BinaryHistory();
        SerializableMock mock = SerializableMock.createRandom();
        history.add(mock);
        int index0 = mock.getValue();
        int index1 = 1;
        int index2 = 2;

        mock.setValue(index1);
        history.add(mock);

        mock.setValue(index2);
        history.add(mock);

        history.undo();
        assertEquals(index1, mock.getValue());
        history.undo();
        assertEquals(index0, mock.getValue());
    }
}
