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

import java.util.ArrayList;
import java.util.List;
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
                    .filter(v -> applyFilter(v, query.getFilter().orElse(null)))
                    .skip(query.getOffset())
                    .limit(query.getLimit());
        } finally {
            lock.readLock().unlock();
        }
    }

    private boolean applyFilter(ReferenceData value, ReferenceFilter filter) {
        if (filter == null) {
            return true;
        }
        var result = true;
        if (filter.getReferenceName() != null && !filter.getReferenceName().trim().isEmpty()) {
            result = value.getReferenceName().contains(filter.getReferenceName().trim());
        }
        if (result && filter.getSchemaName() != null && !filter.getSchemaName().trim().isEmpty()) {
            result = value.getSchemaName().contains(filter.getSchemaName().trim());
        }
        if (result && filter.getTableName() != null && !filter.getTableName().trim().isEmpty()) {
            result = value.getTableName().contains(filter.getTableName().trim());
        }
        return result;
    }

    @Override
    public int count(Query<ReferenceData, ReferenceFilter> query) {
        lock.readLock().lock();
        try {
            return Math.toIntExact(referenceDataList.stream()
                    .filter(v -> applyFilter(v, query.getFilter().orElse(null)))
                    .count());
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void afterPropertiesSet() {
        refresh(null);
    }

    @Override
    public void refresh(Long referenceId) {
        lock.writeLock().lock();
        try {
            loadData(referenceId);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void loadData(Long referenceId) {
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
                        root.get(ReferenceEntity_.jvmScript).alias(ReferenceEntity_.JVM_SCRIPT),
                        joinField.get(ReferenceFieldEntity_.fieldId).alias(ReferenceFieldEntity_.FIELD_ID),
                        joinField.get(ReferenceFieldEntity_.fieldName).alias(ReferenceFieldEntity_.FIELD_NAME),
                        joinField.get(ReferenceFieldEntity_.fieldTitle).alias(ReferenceFieldEntity_.FIELD_TITLE),
                        joinField.get(ReferenceFieldEntity_.fieldKey).alias(ReferenceFieldEntity_.FIELD_KEY),
                        joinField.get(ReferenceFieldEntity_.fieldOrder).alias(ReferenceFieldEntity_.FIELD_ORDER),
                        joinField.get(ReferenceFieldEntity_.fieldType).alias(ReferenceFieldEntity_.FIELD_TYPE),
                        joinField.get(ReferenceFieldEntity_.fieldLength).alias(ReferenceFieldEntity_.FIELD_LENGTH)
                )
        );
        if (referenceId != null) {
            cq.where(cb.equal(root.get(ReferenceEntity_.referenceId), referenceId));
        }
        var results = em.createQuery(cq)
                .getResultList()
                .stream()
                .collect(Collectors.groupingBy(t ->
                        t.get(ReferenceEntity_.REFERENCE_ID, ReferenceEntity_.referenceId.getJavaType())
                ));
        var data = new ArrayList<ReferenceData>();

        results.forEach((k, v) -> {
            var t = v.getFirst();
            var referenceData = ReferenceData.builder()
                    .referenceId(t.get(ReferenceEntity_.REFERENCE_ID, ReferenceEntity_.referenceId.getJavaType()))
                    .referenceName(t.get(ReferenceEntity_.REFERENCE_NAME, ReferenceEntity_.referenceName.getJavaType()))
                    .schemaName(t.get(ReferenceEntity_.SCHEMA_NAME, ReferenceEntity_.schemaName.getJavaType()))
                    .tableName(t.get(ReferenceEntity_.TABLE_NAME, ReferenceEntity_.tableName.getJavaType()))
                    .tableSQL(t.get(ReferenceEntity_.TABLE_SQL, ReferenceEntity_.tableSql.getJavaType()))
                    .jvmScript(t.get(ReferenceEntity_.JVM_SCRIPT, ReferenceEntity_.jvmScript.getJavaType()))
                    .changeStatus(ChangeStatus.UNCHANGED)
                    .build();
            var fieldList = v.stream()
                    .filter(f -> f.get(ReferenceFieldEntity_.FIELD_ID, ReferenceFieldEntity_.referenceId.getJavaType()) != null)
                    .map(f -> ReferenceFieldData.builder()
                            .fieldId(f.get(ReferenceFieldEntity_.FIELD_ID, ReferenceFieldEntity_.referenceId.getJavaType()))
                            .fieldName(f.get(ReferenceFieldEntity_.FIELD_NAME, ReferenceFieldEntity_.fieldName.getJavaType()))
                            .fieldTitle(f.get(ReferenceFieldEntity_.FIELD_TITLE, ReferenceFieldEntity_.fieldTitle.getJavaType()))
                            .fieldLength(f.get(ReferenceFieldEntity_.FIELD_LENGTH, ReferenceFieldEntity_.fieldLength.getJavaType()))
                            .fieldOrder(f.get(ReferenceFieldEntity_.FIELD_ORDER, ReferenceFieldEntity_.fieldOrder.getJavaType()))
                            .fieldType(ReferenceFieldType.getForName(f.get(ReferenceFieldEntity_.FIELD_TYPE, ReferenceFieldEntity_.fieldType.getJavaType())))
                            .fieldKey(f.get(ReferenceFieldEntity_.FIELD_KEY, ReferenceFieldEntity_.fieldKey.getJavaType()))
                            .changeStatus(ChangeStatus.UNCHANGED)
                            .build()
                    )
                    .collect(Collectors.toCollection(ArrayList::new));
            referenceData.setMetaDataFieldsList(fieldList);
            data.add(referenceData);
        });
        if (referenceId == null) {
            this.referenceDataList = data;
        } else {
            this.referenceDataList.removeIf(f -> f.getReferenceId().equals(referenceId));
            this.referenceDataList.addAll(data);
        }
    }
}
