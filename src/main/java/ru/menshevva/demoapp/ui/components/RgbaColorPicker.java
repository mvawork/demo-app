package ru.menshevva.demoapp.ui.components;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.react.ReactAdapterComponent;
import com.vaadin.flow.function.SerializableConsumer;

@NpmPackage(value = "react-colorful", version = "5.6.1")
@JsModule("./src/components/rgba-color-picker.tsx")
@Tag("rgba-color-picker")
public class RgbaColorPicker extends ReactAdapterComponent {


    public RgbaColor getColor() {
        return getState("color", RgbaColor.class);
    }

    public void setColor(RgbaColor color) {
        setState("color", color);
    }

    public RgbaColorPicker() {
        setColor(new RgbaColor(255, 0, 0, 0.5));
    }

    public void addColorChangeListener(SerializableConsumer<RgbaColor> listener) {
        addStateChangeListener("color", RgbaColor.class, listener);
    }


}