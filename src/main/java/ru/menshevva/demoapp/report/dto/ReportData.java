package ru.menshevva.demoapp.report.dto;

import lombok.Builder;

@Builder
public record ReportData(Long id, String group, String name) {
}
