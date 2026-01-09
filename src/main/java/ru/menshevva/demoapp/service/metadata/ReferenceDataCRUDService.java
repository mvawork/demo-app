package ru.menshevva.demoapp.service.metadata;

import ru.menshevva.demoapp.dto.metadata.ReferenceData;

import java.util.Map;

public interface ReferenceDataCRUDService {
    void create(ReferenceData referenceData, Map<String, ?> value);

    //@Transactional
    void update(ReferenceData referenceData, Map<String, ?> value);
}
