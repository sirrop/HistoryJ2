package com.github.sirrop.historyj;

import java.util.Optional;

public interface History<R> {
    /**
     * Registers the specified element.
     * <p>
     *     Basically, History does not permit null but it depends on
     *     the implementation.
     * </p>
     * @param record element to be registered
     * @exception NullPointerException if the argument is null
     */
    void add(R record);

    /**
     * Removes all of the elements from this history.
     * The history will be empty after this method returns.
     */
    void clear();

    /**
     * Returns the currently referenced element.
     * If no element exists in this history, returns null.
     * @return the currently referenced element
     */
    R currentRecord();

    /**
     * Returns the index of the currently referenced element.
     * If no element exists in this history, returns -1.
     * @return index
     */
    int currentIndex();

    /**
     * Returns the capacity of this history
     * @return capacity
     */
    int getCapacity();

    /**
     * Changes the capacity of this history
     * @param capacity the new capacity for this history
     * @exception IllegalArgumentException if the new capacity is negative or zero
     */
    void setCapacity(int capacity);

    /**
     * Returns the number of elements in this history.
     * This result is not larger than the result of {@link History#getCapacity()}.
     * @return the number of elements in this history.
     */
    int size();

    /**
     * Returns true if this history can undo otherwise false
     * @return true or false
     */
    boolean canUndo();

    /**
     * Returns true if this history can redo otherwise false
     * @return true or false
     */
    boolean canRedo();

    /**
     * Returns the previous element of the current record in this history and sets
     * the current record to that.
     * @return the previous element of the current record
     * @exception IllegalStateException if cannot undo
     */
    R undo();

    /**
     * Returns the next element of the current record in this history
     * and set the current record to that.
     * @return the next element of the current record
     * @exception IllegalStateException if cannot redo
     */
    R redo();

    /**
     * Returns an {@link Optional} describing the previous element
     * of the current record, or an empty Optional if cannot undo.
     * @return an Optional describing the previous element of the current record, or an empty Optional if cannot undo.
     */
    Optional<R> undoIfCan();

    /**
     * Returns an {@link Optional} describing the next element of the current record, or an empty Optional if cannot undo.
     * @return an Optional describing the previous element of the current record, or an empty Optional if cannot undo.
     */
    Optional<R> redoIfCan();
}
