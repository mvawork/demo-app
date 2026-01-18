package ru.menshevva.demoapp.service.script;

import groovy.lang.Script;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class DataProcessorScript extends Script {
    public void info(String format, Object... args) {
        log.info(format, args);
    }
}
