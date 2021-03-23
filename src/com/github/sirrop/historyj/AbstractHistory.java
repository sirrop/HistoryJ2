package com.github.sirrop.historyj;

import java.util.Optional;

public abstract class AbstractHistory<R> implements History<R> {
    @Override
    public boolean canUndo() {
        return currentIndex() > 0;
    }

    @Override
    public boolean canRedo() {
        return currentIndex() != -1 && currentIndex() < size() - 1;
    }

    @Override
    public Optional<R> undoIfCan() {
        if (canUndo()) {
            return Optional.of(undo());
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<R> redoIfCan() {
        if (canRedo()) {
            return Optional.of(redo());
        } else {
            return Optional.empty();
        }
    }
}
