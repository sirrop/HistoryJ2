package com.github.sirrop.historyj2;

import com.github.sirrop.historyj2.annotation.History;

import java.io.Serializable;

public class UpdateObjectMock implements Serializable {
    private final SerializableMock mock = SerializableMock.createRandom();

    public int getValue() {
        return mock.getValue();
    }

    public void setValue(int value) {
        mock.setValue(value);
    }

    @History.Update
    private void updateObject(UpdateObjectMock object) {
        mock.setValue(object.mock.getValue());
    }
}
