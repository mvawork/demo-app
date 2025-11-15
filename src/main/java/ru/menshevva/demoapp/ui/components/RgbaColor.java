package ru.menshevva.demoapp.ui.components;

import org.jetbrains.annotations.NotNull;

public record RgbaColor(int r, int g, int b, double a) {

    @NotNull
    @Override
    public String toString() {
        return "{ r: %s, g: %s, b: %s, a: %s }".formatted(r, g, b, a);
    }

}