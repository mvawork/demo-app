package ru.menshevva.demoapp.dto.clients;

import lombok.Builder;

@Builder
public record ClientListData(Long clientId, String clientName) {
}
