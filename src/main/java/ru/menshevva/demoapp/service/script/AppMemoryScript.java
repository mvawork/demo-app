package ru.menshevva.demoapp.service.script;

import java.time.LocalDateTime;

public record AppMemoryScript(LocalDateTime lastChange, String content) {
}
