package ru.menshevva.demoapp.service.metadata.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.menshevva.demoapp.dto.metadata.ReferenceData;
import ru.menshevva.demoapp.dto.metadata.ReferenceFieldData;
import ru.menshevva.demoapp.exception.EAppException;
import ru.menshevva.demoapp.service.metadata.ReferenceDataCRUDService;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Slf4j
public class ReferenceDataCRUDServiceImpl implements ReferenceDataCRUDService {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public ReferenceDataCRUDServiceImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    @Transactional
    public void save(ReferenceData referenceData, Map<String, ?> newValue, Map<String, ?> oldValue) {
        if (referenceData == null || newValue == null) {
            return;
        }
        var sql = new StringBuilder();
        if (oldValue != null) {
            sql.append("UPDATE %s.%s SET\n".formatted(referenceData.getSchemaName(), referenceData.getTableName()));
            AtomicBoolean needUpdateField = new AtomicBoolean(true);
            referenceData.getMetaDataFieldsList()
                    .stream()
                    .filter(field -> !Objects.equals(oldValue.get(field.getFieldName()), newValue.get(field.getFieldName())))
                    .forEach(field -> {
                                if (needUpdateField.get()) {
                                    sql.append("  %s = :NEW_%s".formatted(field.getFieldName(), field.getFieldName()));
                                    needUpdateField.set(false);
                                } else {
                                    sql.append(",\n  %s = :NEW_%s".formatted(field.getFieldName(), field.getFieldName()));
                                }
                            }
                    );
            if (needUpdateField.get()) {
                return;
            }
            sql.append("\n");
            AtomicBoolean firstKey = new AtomicBoolean(true);
            referenceData.getMetaDataFieldsList()
                    .stream()
                    .filter(field -> Objects.nonNull(field.getFieldKey()) && field.getFieldKey())
                    .forEach(field -> {
                        if (firstKey.get()) {
                            sql.append("WHERE %s = :OLD_%s\n".formatted(field.getFieldName(), field.getFieldName()));
                            firstKey.set(false);
                        } else {
                            sql.append("  AND %s = :OLD_%s\n".formatted(field.getFieldName(), field.getFieldName()));
                        }
                    });
            if (firstKey.get()) {
                var errMsg = "В справочных данных не найдены ключевые поля для таблицы %s.%s".formatted(referenceData.getSchemaName(), referenceData.getTableName());
                log.error(errMsg);
                throw new EAppException(errMsg);
            }
            Map<String, Object> values = new HashMap<>();
            newValue.forEach((s, o) -> values.put("NEW_%s".formatted(s), o));
            oldValue.forEach((s, o) -> values.put("OLD_%s".formatted(s), o));
            jdbcTemplate.update(sql.toString(), values);
        } else {
            sql.append("INSERT INTO %s.%s\n(".formatted(referenceData.getSchemaName(), referenceData.getTableName()));
            List<String> fields = referenceData.getMetaDataFieldsList().stream()
                    .sorted(Comparator.comparingInt(ReferenceFieldData::getFieldOrder))
                    .map(ReferenceFieldData::getFieldName)
                    .toList();

            AtomicBoolean first = new AtomicBoolean(true);
            fields.forEach(field -> {
                        if (first.get()) {
                            sql.append("%s".formatted(field));
                            first.set(false);
                        } else {
                            sql.append(", %s".formatted(field));
                        }
                    }
            );
            sql.append(")\n");
            sql.append("VALUES (");
            first.set(true);
            fields.forEach(field -> {
                if (first.get()) {
                    sql.append(":%s".formatted(field));
                    first.set(false);
                } else {
                    sql.append(", :%s".formatted(field));
                }
            });
            sql.append(")\n");
            Map<String, Object> values = new HashMap<>();
            fields.forEach(field -> {
                var o = newValue.get(field);
                if (o == null) {
                    values.put(field, null);
                } else {
                    values.put(field, o);
                }
            });
            jdbcTemplate.update(sql.toString(), values);
        }
    }

    @Override
    @Transactional
    public void delete(ReferenceData referenceData, Map<String, ?> oldValue) {
        if (referenceData == null || oldValue == null) {
            return;
        }
        var sql = new StringBuilder();
        sql.append("DELETE FROM %s.%s\n".formatted(referenceData.getSchemaName(), referenceData.getTableName()));
        AtomicBoolean firstKey = new AtomicBoolean(true);
        referenceData.getMetaDataFieldsList()
                .stream()
                .filter(field -> Objects.nonNull(field.getFieldKey()) && field.getFieldKey())
                .forEach(field -> {
                    if (firstKey.get()) {
                        sql.append("WHERE %s = :OLD_%s\n".formatted(field.getFieldName(), field.getFieldName()));
                        firstKey.set(false);
                    } else {
                        sql.append("  AND %s = :OLD_%s\n".formatted(field.getFieldName(), field.getFieldName()));
                    }
                });
        if (firstKey.get()) {
            var errMsg = "В справочных данных не найдены ключевые поля для таблицы %s.%s".formatted(referenceData.getSchemaName(), referenceData.getTableName());
            log.error(errMsg);
            throw new EAppException(errMsg);
        }
        Map<String, Object> values = new HashMap<>();
        oldValue.forEach((s, o) -> values.put("OLD_%s".formatted(s), o));
        jdbcTemplate.update(sql.toString(), values);
    }
}
