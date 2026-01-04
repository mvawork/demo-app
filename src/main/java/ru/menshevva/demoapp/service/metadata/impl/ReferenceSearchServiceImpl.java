package ru.menshevva.demoapp.service.metadata.impl;

import com.vaadin.flow.data.provider.Query;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.menshevva.demoapp.dto.metadata.ReferenceData;
import ru.menshevva.demoapp.dto.metadata.ReferenceFieldData;
import ru.menshevva.demoapp.entities.main.metadata.ReferenceEntity;
import ru.menshevva.demoapp.entities.main.metadata.ReferenceEntity_;
import ru.menshevva.demoapp.entities.main.metadata.ReferenceFieldEntity_;
import ru.menshevva.demoapp.service.metadata.ReferenceFilter;
import ru.menshevva.demoapp.service.metadata.ReferenceSearchService;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class ReferenceSearchServiceImpl implements ReferenceSearchService {


    @PersistenceContext(name = "main")
    private EntityManager em;


    @Override
    public Stream<ReferenceData> fetch(Query<ReferenceData, ReferenceFilter> query) {
        var cb = em.getCriteriaBuilder();
        var cq = cb.createTupleQuery();
        var root = cq.from(ReferenceEntity.class);
        var joinField = root.join(ReferenceEntity_.referenceFieldEntities);
        cq.select(
                cb.tuple(root.get(ReferenceEntity_.referenceId).alias(ReferenceEntity_.REFERENCE_ID),
                        root.get(ReferenceEntity_.schemaName).alias(ReferenceEntity_.SCHEMA_NAME),
                        root.get(ReferenceEntity_.tableName).alias(ReferenceEntity_.TABLE_NAME),
                        root.get(ReferenceEntity_.tableSql).alias(ReferenceEntity_.TABLE_SQL),
                        joinField.get(ReferenceFieldEntity_.fieldName).alias(ReferenceFieldEntity_.FIELD_NAME),
                        joinField.get(ReferenceFieldEntity_.fieldTitle).alias(ReferenceFieldEntity_.FIELD_TITLE),
                        joinField.get(ReferenceFieldEntity_.fieldOrder).alias(ReferenceFieldEntity_.FIELD_ORDER),
                        joinField.get(ReferenceFieldEntity_.fieldType).alias(ReferenceFieldEntity_.FIELD_TYPE),
                        joinField.get(ReferenceFieldEntity_.fieldLength).alias(ReferenceFieldEntity_.FIELD_LENGTH)
                )
        );
        var results = em.createQuery(cq)
                .getResultList()
                .stream()
                .collect(Collectors.groupingBy(t ->
                        t.get(ReferenceEntity_.REFERENCE_ID, ReferenceEntity_.referenceId.getJavaType())
                ));

        return results.entrySet()
                .stream()
                .map(entry -> {
                    var t = entry.getValue().getFirst();
                    var referenceData = ReferenceData.builder()
                            .referenceId(t.get(ReferenceEntity_.REFERENCE_ID, ReferenceEntity_.referenceId.getJavaType()))
                            .schemaName(t.get(ReferenceEntity_.SCHEMA_NAME, ReferenceEntity_.schemaName.getJavaType()))
                            .tableName(t.get(ReferenceEntity_.TABLE_NAME, ReferenceEntity_.tableName.getJavaType()))
                            .tableSQL(t.get(ReferenceEntity_.TABLE_SQL, ReferenceEntity_.tableSql.getJavaType()))
                            .build();
                    var fieldList = entry.getValue().stream()
                            .map(tuple -> ReferenceFieldData.builder()
                                    .fieldName(tuple.get(ReferenceFieldEntity_.FIELD_NAME, ReferenceFieldEntity_.fieldName.getJavaType()))
                                    .fieldTitle(tuple.get(ReferenceFieldEntity_.FIELD_TITLE, ReferenceFieldEntity_.fieldTitle.getJavaType()))
                                    .fieldLength(tuple.get(ReferenceFieldEntity_.FIELD_LENGTH, ReferenceFieldEntity_.fieldLength.getJavaType()))
                                    .fieldOrder(tuple.get(ReferenceFieldEntity_.FIELD_ORDER, ReferenceFieldEntity_.fieldOrder.getJavaType()))
                                    .build()
                            )
                            .collect(Collectors.toList());
                    referenceData.setMetaDataFieldsList(fieldList);
                    return referenceData;
                })
                .toList()
                .stream();
    }
}
