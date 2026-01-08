package ru.menshevva.demoapp.service.metadata;

import com.vaadin.flow.data.provider.Query;
import ru.menshevva.demoapp.dto.metadata.ReferenceData;


import java.util.Map;
import java.util.stream.Stream;

public interface ReferenceDataSearchService {
    Stream<Map<String, ?>> search(ReferenceData referenceData, Query<Map<String, ?>, Map<String, ?>> query);
    int count (ReferenceData referenceData, Query<Map<String, ?>, Map<String, ?>> query);
}
