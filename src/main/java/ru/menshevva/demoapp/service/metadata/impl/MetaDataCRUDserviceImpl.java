package ru.menshevva.demoapp.service.metadata.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.menshevva.demoapp.dto.ChangeStatus;
import ru.menshevva.demoapp.dto.metadata.ReferenceData;
import ru.menshevva.demoapp.dto.metadata.ReferenceFieldData;
import ru.menshevva.demoapp.entities.main.metadata.ReferenceEntity;
import ru.menshevva.demoapp.entities.main.metadata.ReferenceEntity_;
import ru.menshevva.demoapp.entities.main.metadata.ReferenceFieldEntity;
import ru.menshevva.demoapp.entities.main.metadata.ReferenceFieldEntity_;
import ru.menshevva.demoapp.exception.EAppException;
import ru.menshevva.demoapp.service.metadata.MetaDataCRUDservice;

import java.util.Optional;

@Service
@Slf4j
public class MetaDataCRUDserviceImpl implements MetaDataCRUDservice {

    @PersistenceContext(name = "main")
    private EntityManager entityManager;

    @Override
    @Transactional
    public void save(ReferenceData value) {
        if (value == null) {
            var msg = "Передано пустое значение";
            log.error(msg);
            throw new EAppException(msg);
        }
        if (value.getChangeStatus() == ChangeStatus.ADD) {
            add(value);
        } else if (value.getChangeStatus() == ChangeStatus.MODIFIED) {
            update(value);
        }
    }

    private void add(ReferenceData value) {
        var e = new ReferenceEntity();
        e.setReferenceName(value.getReferenceName());
        e.setSchemaName(value.getSchemaName());
        e.setTableName(value.getTableName());
        e.setTableSql(value.getTableSQL());
        e.setJvmScript(value.getJvmScript());
        entityManager.persist(e);
        value.setReferenceId(e.getReferenceId());
        saveFields(value);
    }

    private void update(ReferenceData value) {
        var cb = entityManager.getCriteriaBuilder();
        var cu = cb.createCriteriaUpdate(ReferenceEntity.class);
        var root = cu.from(ReferenceEntity.class);
        cu.set(ReferenceEntity_.referenceName, value.getReferenceName());
        cu.set(ReferenceEntity_.schemaName, value.getSchemaName());
        cu.set(ReferenceEntity_.tableName, value.getTableName());
        cu.set(ReferenceEntity_.tableSql, value.getTableSQL());
        cu.set(ReferenceEntity_.jvmScript, value.getJvmScript());
        cu.where(cb.equal(root.get(ReferenceEntity_.referenceId), value.getReferenceId()));
        entityManager.createQuery(cu).executeUpdate();
        saveFields(value);
    }

    private void saveFields(ReferenceData value) {
        if (value.getChangeStatus() == ChangeStatus.ADD) {
            Optional<ReferenceFieldData> errStateFields = value.getMetaDataFieldsList()
                    .stream()
                    .filter(v -> v.getChangeStatus() != ChangeStatus.ADD)
                    .findAny();
            errStateFields.ifPresent(v -> {
                var errMsg = "Не верный статус поля %s".formatted(v.getFieldName());
                log.error(errMsg);
                throw new EAppException(errMsg);
            });
        }
        value.getMetaDataFieldsList()
                .stream()
                .filter(v -> v.getChangeStatus() == ChangeStatus.DELETED && v.getFieldId() != null)
                .forEach(this::deleteField);
        value.getMetaDataFieldsList()
                .stream()
                .filter(field -> field.getChangeStatus() == ChangeStatus.MODIFIED)
                .forEach(this::updateField);
        value.getMetaDataFieldsList()
                .stream()
                .filter(field -> field.getChangeStatus() == ChangeStatus.ADD)
                .forEach(v -> addField(value.getReferenceId(), v));

    }

    private void addField(Long referenceId, ReferenceFieldData v) {
        var e = new ReferenceFieldEntity();
        e.setReferenceId(referenceId);
        e.setFieldName(v.getFieldName());
        e.setFieldTitle(v.getFieldTitle());
        e.setFieldKey(v.getFieldKey());
        e.setFieldLength(v.getFieldLength());
        e.setFieldOrder(v.getFieldOrder());
        e.setFieldType(v.getFieldType() == null ? null : v.getFieldType().name());
        entityManager.persist(e);
        v.setFieldId(e.getFieldId());
    }

    private void updateField(ReferenceFieldData referenceFieldData) {
        var cb = entityManager.getCriteriaBuilder();
        var cu = cb.createCriteriaUpdate(ReferenceFieldEntity.class);
        var root = cu.from(ReferenceFieldEntity.class);
        cu.set(root.get(ReferenceFieldEntity_.fieldName), referenceFieldData.getFieldName());
        cu.set(root.get(ReferenceFieldEntity_.fieldTitle), referenceFieldData.getFieldTitle());
        cu.set(root.get(ReferenceFieldEntity_.fieldKey), referenceFieldData.getFieldKey());
        cu.set(root.get(ReferenceFieldEntity_.fieldLength), referenceFieldData.getFieldLength());
        cu.set(root.get(ReferenceFieldEntity_.fieldOrder), referenceFieldData.getFieldOrder());
        cu.set(root.get(ReferenceFieldEntity_.fieldType), referenceFieldData.getFieldType() == null ? null : referenceFieldData.getFieldType().name());
        cu.where(cb.equal(root.get(ReferenceFieldEntity_.fieldId), referenceFieldData.getFieldId()));
        entityManager.createQuery(cu).executeUpdate();
    }

    private void deleteField(ReferenceFieldData referenceFieldData) {
        var cb = entityManager.getCriteriaBuilder();
        var cd = cb.createCriteriaDelete(ReferenceFieldEntity.class);
        var root = cd.from(ReferenceFieldEntity.class);
        cd.where(cb.equal(root.get(ReferenceFieldEntity_.fieldId), referenceFieldData.getFieldId()));
        entityManager.createQuery(cd).executeUpdate();
    }



    @Override
    @Transactional
    public void delete(Long id) {
        deleteFields(id);
        var cb = entityManager.getCriteriaBuilder();
        var cd = cb.createCriteriaDelete(ReferenceEntity.class);
        var root = cd.from(ReferenceEntity.class);
        cd.where(cb.equal(root.get(ReferenceEntity_.referenceId), id));
        entityManager.createQuery(cd).executeUpdate();

    }

    private void deleteFields(Long id) {
        var cb = entityManager.getCriteriaBuilder();
        var cd = cb.createCriteriaDelete(ReferenceFieldEntity.class);
        var root = cd.from(ReferenceFieldEntity.class);
        cd.where(cb.equal(root.get(ReferenceFieldEntity_.referenceId), id));
        entityManager.createQuery(cd).executeUpdate();

    }
}
