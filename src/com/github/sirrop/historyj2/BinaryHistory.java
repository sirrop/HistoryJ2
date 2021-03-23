package com.github.sirrop.historyj2;

import java.io.*;
import java.lang.reflect.Field;

/**
 * 直列化を使用した履歴オブジェクトです。直列化可能なオブジェクトしか登録できない代わりに、
 * {@link BinaryHistory#undo()}, {@link BinaryHistory#redo()}を使用すると登録する際に使用した
 * オブジェクトの状態を戻り値のオブジェクトの状態に変更します。
 */
public class BinaryHistory extends AbstractHistory<Serializable> {
    private static class Record {
        public Serializable ref;
        public byte[] data;
    }

    private final LinkedHistory<Record> delegate;

    public BinaryHistory(int initialCapacity) {
        delegate = new LinkedHistory<>(initialCapacity);
    }

    public BinaryHistory() {
        this(100);
    }

    /**
     * オブジェクトを直列化する際に例外が発生した場合、追加は失敗します。
     * @param record element to be registered
     * @exception IllegalArgumentException 直列化に失敗した場合、IOExceptionをラップして投げられます
     */
    @Override
    public void add(Serializable record) {
        Record element;
        try {
            element = createRecord(record);
        } catch (IOException e) {
           throw new IllegalArgumentException(e);
        }
        delegate.add(element);
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public Serializable currentRecord() {
        Record record = delegate.currentRecord();
        if (record == null) {
            return null;
        }
        return record.ref;
    }

    @Override
    public int currentIndex() {
        return delegate.currentIndex();
    }

    @Override
    public int getCapacity() {
        return delegate.getCapacity();
    }

    @Override
    public void setCapacity(int capacity) {
        delegate.setCapacity(capacity);
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public Serializable undo() {
        if (!canUndo()) {
            throw new IllegalStateException("Can't undo");
        }
        Record record = delegate.undo();
        try {
            return restore(record);
        } catch (IOException | ClassNotFoundException | IllegalAccessException e) {
            throw new IllegalStateException("Can't undo", e);
        }
    }

    @Override
    public Serializable redo() {
        if (!canRedo()) {
            throw new IllegalStateException("Can't redo");
        }
        Record record = delegate.redo();
        try {
            return restore(record);
        } catch (IOException | ClassNotFoundException | IllegalAccessException e) {
            throw new IllegalStateException("Can't redo", e);
        }
    }

    private static Record createRecord(Serializable serializable) throws IOException {
        Record result = new Record();
        result.ref = serializable;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream stream = new ObjectOutputStream(out);
        stream.writeObject(serializable);
        stream.close();
        result.data = out.toByteArray();
        out.close();
        return result;
    }

    private Serializable restore(Record record) throws IOException, ClassNotFoundException, IllegalAccessException {
        var in = new ByteArrayInputStream(record.data);
        var stream = new ObjectInputStream(in);
        Serializable target = record.ref;
        if (target == null) {
            throw new IllegalStateException("オブジェクトはすでに破棄されています。");
        }
        Object restored = stream.readObject();
        Field[] fields = target.getClass().getDeclaredFields();
        for (Field field: fields) {
            field.setAccessible(true);
            field.set(target, field.get(restored));
        }
        return target;
    }
}
