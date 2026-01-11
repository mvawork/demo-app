package ru.menshevva.demoapp.service.metadata;

import ru.menshevva.demoapp.dto.metadata.ReferenceData;

import java.util.Map;

public interface AutoEditCrudService {
    void save(ReferenceData referenceData, Map<String, ?> value, Map<String, ?> oldValue);
    void delete(ReferenceData referenceData, Map<String, ?> oldValue);
}
