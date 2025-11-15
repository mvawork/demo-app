package ru.menshevva.demoapp.ui.components;

public interface EditActionCallback {
    void ok();
    default void cancel() {};
}
