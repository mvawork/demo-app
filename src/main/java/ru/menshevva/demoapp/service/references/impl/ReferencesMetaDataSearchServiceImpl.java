package ru.menshevva.demoapp.service.references.impl;

import com.vaadin.flow.data.provider.Query;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.menshevva.demoapp.dto.metadata.ReferenceData;
import ru.menshevva.demoapp.service.references.ReferenceMetaDataFilter;
import ru.menshevva.demoapp.service.references.ReferencesMetaDataSearchService;

import java.util.stream.Stream;

@Service
@Slf4j
public class ReferencesMetaDataSearchServiceImpl implements ReferencesMetaDataSearchService {

    private EntityManager entityManager;

    @Override
    public Stream<ReferenceData> fetch(Query<ReferenceData, ReferenceMetaDataFilter> query) {

        return Stream.empty();
    }
}
