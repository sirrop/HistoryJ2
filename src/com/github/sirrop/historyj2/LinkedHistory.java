package com.github.sirrop.historyj2;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class LinkedHistory<R> extends AbstractHistory<R> {
    private int capacity;
    private final List<R> undoList = new LinkedList<>();
    private final List<R> redoList = new LinkedList<>();

    public LinkedHistory(int initialCapacity) {
        capacity = initialCapacity;
    }

    public LinkedHistory() {
        this(100);
    }

    R get(int index) {
        if (index < undoList.size()) {
            return undoList.get(index);
        } else if (index < size()) {
            return redoList.get(size() - 1 - index);
        } else {
            throw new IndexOutOfBoundsException(index);
        }
    }

    @Override
    public void add(R record) {
        Objects.requireNonNull(record);
        undoList.add(record);
        redoList.clear();
        while (size() > capacity) {
            undoList.remove(0);
        }
    }

    @Override
    public void clear() {
        undoList.clear();
        redoList.clear();
    }

    @Override
    public R currentRecord() {
        if (currentIndex() == -1) {
            return null;
        }
        return undoList.get(currentIndex());
    }

    @Override
    public int currentIndex() {
        return undoList.size() - 1;
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    @Override
    public void setCapacity(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity is negative or zero.");
        }
        this.capacity = capacity;
    }

    @Override
    public int size() {
        return undoList.size() + redoList.size();
    }

    @Override
    public R undo() {
        if (!canUndo()) {
            throw new IllegalStateException("Can't undo");
        }
        var old = undoList.remove(currentIndex());
        redoList.add(old);
        return currentRecord();
    }

    @Override
    public R redo() {
        if (!canRedo()) {
            throw new IllegalStateException("Can't redo");
        }
        var result = redoList.remove(redoList.size() - 1);
        undoList.add(result);
        return result;
    }
}
