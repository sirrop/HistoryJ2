package com.github.sirrop.historyj2;

import com.github.sirrop.historyj2.annotation.History;

import java.io.Serializable;

public class UpdateObjectMock2 implements Serializable {
    private final SerializableMock mock = SerializableMock.createRandom();

    public void setValue(int value) {
        mock.setValue(value);
    }

    public int getValue() {
        return mock.getValue();
    }

    @History.Update
    private void updateObject(UpdateObjectMock2 object) {
        setValue(object.getValue());
        System.out.println("updated");
    }
}
