package ru.menshevva.demoapp.ui.components;

public interface  EditValueActionCallback<T> {

    void ok(T value);
    default void cancel() {};

}
