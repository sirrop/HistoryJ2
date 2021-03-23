package com.github.sirrop.historyj2;

import java.io.Serial;
import java.io.Serializable;

public class SerializableMock implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private int value;

    public void setValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static SerializableMock createRandom() {
        SerializableMock result = new SerializableMock();
        result.value = (int) (Math.random() * Integer.MAX_VALUE);
        return result;
    }
}
