package ru.menshevva.demoapp.service.metadata;

import ru.menshevva.demoapp.dto.metadata.ReferenceData;

public interface MetaDataCRUDservice {

    void save(ReferenceData value);
    void delete(Long id);
}
