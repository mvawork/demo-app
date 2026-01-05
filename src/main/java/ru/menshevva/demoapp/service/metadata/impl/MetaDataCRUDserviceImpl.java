package ru.menshevva.demoapp.service.metadata.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.menshevva.demoapp.dto.metadata.ReferenceData;
import ru.menshevva.demoapp.entities.main.metadata.ReferenceEntity;
import ru.menshevva.demoapp.entities.main.metadata.ReferenceFieldEntity;
import ru.menshevva.demoapp.service.metadata.MetaDataCRUDservice;

@Service
@Slf4j
public class MetaDataCRUDserviceImpl implements MetaDataCRUDservice {

    @PersistenceContext(name = "main")
    private EntityManager entityManager;

    @Override
    @Transactional
    public void save(ReferenceData value) {
        if (value == null) {
            throw new IllegalArgumentException("value is null");
        }
        if (value.getReferenceId() == null) {
            add(value);
        } else {
            update(value);
        }

    }

    private void update(ReferenceData value) {

    }

    private void add(ReferenceData value) {
        var e = new ReferenceEntity();
        e.setSchemaName(value.getSchemaName());
        e.setTableName(value.getTableName());
        e.setTableSql(value.getTableSQL());
        entityManager.persist(e);
        value.setReferenceId(e.getReferenceId());
        if (value.getMetaDataFieldsList() != null) {
            value.getMetaDataFieldsList().forEach(metaDataField -> {
                var f = new ReferenceFieldEntity();
                f.setReferenceId(value.getReferenceId());
                f.setFieldName(metaDataField.getFieldName());
                f.setFieldTitle(metaDataField.getFieldTitle());
                f.setFieldLength(metaDataField.getFieldLength());
                f.setFieldOrder(metaDataField.getFieldOrder());
                f.setFieldType(metaDataField.getFieldType());
                entityManager.persist(f);
                metaDataField.setFieldId(f.getFieldId());
            });
        }
    }

    @Override
    public void delete(Long id) {

    }
}
