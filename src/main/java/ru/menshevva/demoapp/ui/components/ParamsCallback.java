package ru.menshevva.demoapp.ui.components;

import java.util.Map;

@FunctionalInterface
public interface ParamsCallback {

    Map<String, ?> getParams();
}
