package ru.menshevva.demoapp.service.references;

import com.vaadin.flow.data.provider.Query;
import ru.menshevva.demoapp.dto.metadata.ReferenceData;

import java.util.stream.Stream;

public interface ReferencesMetaDataSearchService {

    Stream<ReferenceData> fetch(Query<ReferenceData, ReferenceMetaDataFilter> query);
}
