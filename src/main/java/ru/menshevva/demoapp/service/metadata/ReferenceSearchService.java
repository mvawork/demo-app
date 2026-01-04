package ru.menshevva.demoapp.service.metadata;

import com.vaadin.flow.data.provider.Query;
import ru.menshevva.demoapp.dto.metadata.ReferenceData;

import java.util.stream.Stream;

public interface ReferenceSearchService {

    Stream<ReferenceData> fetch(Query<ReferenceData, ReferenceFilter> query);
}
