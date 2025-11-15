package ru.menshevva.demoapp.ui.components;

@FunctionalInterface
public interface ViewCallback<T> {

    void ok(T value);
    default void cancel() {}
}
