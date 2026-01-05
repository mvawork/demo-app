package ru.menshevva.demoapp.dto;

import lombok.Getter;

@Getter
public enum ChangeStatus {
    ADD("Добавлен"),
    MODIFIED("Изменен"),
    DELETED("Удален"),
    UNCHANGED("Без изменений");

    private final String description;

    ChangeStatus(String description) {
        this.description = description;
    }

}