package ru.menshevva.demoapp.service.metadata.impl;

import com.vaadin.flow.data.provider.Query;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.JoinType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import ru.menshevva.demoapp.dto.ChangeStatus;
import ru.menshevva.demoapp.dto.metadata.ReferenceData;
import ru.menshevva.demoapp.dto.metadata.ReferenceFieldData;
import ru.menshevva.demoapp.dto.metadata.ReferenceFieldType;
import ru.menshevva.demoapp.entities.main.metadata.ReferenceEntity;
import ru.menshevva.demoapp.entities.main.metadata.ReferenceEntity_;
import ru.menshevva.demoapp.entities.main.metadata.ReferenceFieldEntity_;
import ru.menshevva.demoapp.service.metadata.ReferenceFilter;
import ru.menshevva.demoapp.service.metadata.ReferenceSearchService;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class ReferenceSearchServiceImpl implements ReferenceSearchService, InitializingBean {


    @PersistenceContext(name = "main")
    private EntityManager em;

    private List<ReferenceData> referenceDataList;

    private final ReadWriteLock lock = new ReentrantReadWriteLock();


    @Override
    public Stream<ReferenceData> fetch(Query<ReferenceData, ReferenceFilter> query) {
        lock.readLock().lock();
        try {
            return referenceDataList.stream()
                    .filter(v -> applyFilter(v, query.getFilter()))
                    .skip(query.getOffset())
                    .limit(query.getLimit());
        } finally {
            lock.readLock().unlock();
        }
    }

    private boolean applyFilter(ReferenceData value, Optional<ReferenceFilter> filter) {
        return filter.map(v -> {
            var result = true;
            if (v.getReferenceName() != null && !v.getReferenceName().trim().isEmpty()) {
                result = value.getReferenceName().contains(v.getReferenceName().trim());
            }
            if (result && v.getSchemaName() != null && !v.getSchemaName().trim().isEmpty()) {
                result = value.getSchemaName().contains(v.getSchemaName().trim());
            }
            if (result && v.getTableName() != null && !v.getTableName().trim().isEmpty()) {
                result = value.getTableName().contains(v.getTableName().trim());
            }
            return result;
        }).orElse(Boolean.TRUE);
    }

    @Override
    public int count(Query<ReferenceData, ReferenceFilter> query) {
        lock.readLock().lock();
        try {
            return Math.toIntExact(referenceDataList.stream()
                    .filter(v -> applyFilter(v, query.getFilter()))
                    .count());
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void afterPropertiesSet() {
        refresh();
    }

    @Override
    public void refresh() {
        lock.writeLock().lock();
        try {
            loadData();
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void loadData() {
        var cb = em.getCriteriaBuilder();
        var cq = cb.createTupleQuery();
        var root = cq.from(ReferenceEntity.class);
        var joinField = root.join(ReferenceEntity_.referenceFieldEntities, JoinType.LEFT);
        cq.select(
                cb.tuple(root.get(ReferenceEntity_.referenceId).alias(ReferenceEntity_.REFERENCE_ID),
                        root.get(ReferenceEntity_.referenceName).alias(ReferenceEntity_.REFERENCE_NAME),
                        root.get(ReferenceEntity_.schemaName).alias(ReferenceEntity_.SCHEMA_NAME),
                        root.get(ReferenceEntity_.tableName).alias(ReferenceEntity_.TABLE_NAME),
                        root.get(ReferenceEntity_.tableSql).alias(ReferenceEntity_.TABLE_SQL),
                        joinField.get(ReferenceFieldEntity_.fieldId).alias(ReferenceFieldEntity_.FIELD_ID),
                        joinField.get(ReferenceFieldEntity_.fieldName).alias(ReferenceFieldEntity_.FIELD_NAME),
                        joinField.get(ReferenceFieldEntity_.fieldTitle).alias(ReferenceFieldEntity_.FIELD_TITLE),
                        joinField.get(ReferenceFieldEntity_.fieldKey).alias(ReferenceFieldEntity_.FIELD_KEY),
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

        this.referenceDataList = results.entrySet()
                .stream()
                .map(entry -> {
                    var t = entry.getValue().getFirst();
                    var referenceData = ReferenceData.builder()
                            .referenceId(t.get(ReferenceEntity_.REFERENCE_ID, ReferenceEntity_.referenceId.getJavaType()))
                            .referenceName(t.get(ReferenceEntity_.REFERENCE_NAME, ReferenceEntity_.referenceName.getJavaType()))
                            .schemaName(t.get(ReferenceEntity_.SCHEMA_NAME, ReferenceEntity_.schemaName.getJavaType()))
                            .tableName(t.get(ReferenceEntity_.TABLE_NAME, ReferenceEntity_.tableName.getJavaType()))
                            .tableSQL(t.get(ReferenceEntity_.TABLE_SQL, ReferenceEntity_.tableSql.getJavaType()))
                            .changeStatus(ChangeStatus.UNCHANGED)
                            .build();
                    var fieldList = entry.getValue().stream()
                            .filter(v -> v.get(ReferenceFieldEntity_.FIELD_ID, ReferenceFieldEntity_.referenceId.getJavaType()) != null)
                            .map(v -> ReferenceFieldData.builder()
                                    .fieldId(v.get(ReferenceFieldEntity_.FIELD_ID, ReferenceFieldEntity_.referenceId.getJavaType()))
                                    .fieldName(v.get(ReferenceFieldEntity_.FIELD_NAME, ReferenceFieldEntity_.fieldName.getJavaType()))
                                    .fieldTitle(v.get(ReferenceFieldEntity_.FIELD_TITLE, ReferenceFieldEntity_.fieldTitle.getJavaType()))
                                    .fieldLength(v.get(ReferenceFieldEntity_.FIELD_LENGTH, ReferenceFieldEntity_.fieldLength.getJavaType()))
                                    .fieldOrder(v.get(ReferenceFieldEntity_.FIELD_ORDER, ReferenceFieldEntity_.fieldOrder.getJavaType()))
                                    .fieldType(ReferenceFieldType.getForName(v.get(ReferenceFieldEntity_.FIELD_TYPE, ReferenceFieldEntity_.fieldType.getJavaType())))
                                    .fieldKey(v.get(ReferenceFieldEntity_.FIELD_KEY, ReferenceFieldEntity_.fieldKey.getJavaType()))
                                    .changeStatus(ChangeStatus.UNCHANGED)
                                    .build()
                            )
                            .collect(Collectors.toList());
                    referenceData.setMetaDataFieldsList(fieldList);
                    return referenceData;
                })
                .toList();

    }
}
