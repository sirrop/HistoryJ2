package com.github.sirrop.historyj2;

import com.github.sirrop.historyj2.annotation.History;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 直列化を使用した履歴オブジェクトです。直列化可能なオブジェクトしか登録できない代わりに、
 * {@link BinaryHistory#undo()}, {@link BinaryHistory#redo()}を使用すると登録する際に使用した
 * オブジェクトの状態を戻り値のオブジェクトの状態に変更します。
 *
 * <p>
 *     デフォルトではフィールドに直接代入を行います。直接代入したくない場合は、
 *     {@link History.Update}を付与した、自身と同じ型を引数にとるメソッドを定義することで
 *     更新処理をそのメソッドに委譲することが出来ます。
 * </p>
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
        } catch (IOException | ClassNotFoundException | IllegalAccessException | InvocationTargetException e) {
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
        } catch (IOException | ClassNotFoundException | IllegalAccessException | InvocationTargetException e) {
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

    private Serializable restore(Record record) throws IOException, ClassNotFoundException, IllegalAccessException, InvocationTargetException {
        var in = new ByteArrayInputStream(record.data);
        var stream = new ObjectInputStream(in);
        Serializable target = record.ref;
        if (target == null) {
            throw new IllegalStateException("オブジェクトはすでに破棄されています。");
        }
        Object restored = stream.readObject();
        var success = updateObjectIfPresent(target, restored);
        if (success) {
            return target;
        }
        Field[] fields = target.getClass().getDeclaredFields();
        for (Field field: fields) {
            field.setAccessible(true);
            field.set(target, field.get(restored));
        }
        return target;
    }

    private boolean updateObjectIfPresent(Serializable target, Object restored) {
        var result = false;
        try {
            Method[] methods = target.getClass().getDeclaredMethods();
            for (Method method: methods) {
                if (method.isAnnotationPresent(History.Update.class)) {
                    method.setAccessible(true);
                    method.invoke(target, restored);
                    result = true;
                }
            }
        } catch (IllegalAccessException | InvocationTargetException ignored) {
        }
        return result;
    }
}
