package ru.menshevva.demoapp.service.metadata.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.menshevva.demoapp.dto.metadata.ReferenceData;
import ru.menshevva.demoapp.dto.metadata.ReferenceFieldData;
import ru.menshevva.demoapp.exception.EAppException;
import ru.menshevva.demoapp.service.metadata.ReferenceDataCRUDService;

import java.util.Map;
import java.util.Objects;
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
    public void create(ReferenceData referenceData, Map<String, ?> value) {
        if (referenceData == null || value == null) {
            return;
        }
    }

    @Override
    @Transactional
    public void update(ReferenceData referenceData, Map<String, ?> newValue) {
        if (referenceData == null || newValue == null) {
            return;
        }
        var sql = new StringBuilder();
        sql.append("UPDATE %s.%s SET\n".formatted(referenceData.getSchemaName(), referenceData.getTableName()));
        AtomicBoolean firstField = new AtomicBoolean(true);
        referenceData.getMetaDataFieldsList().forEach(field -> {
                    if (firstField.get()) {
                        sql.append("  %s = :%s".formatted(field.getFieldName(), field.getFieldName()));
                        firstField.set(false);
                    } else {
                        sql.append(",\n  %s = :%s".formatted(field.getFieldName(), field.getFieldName()));
                    }
                }
        );
        sql.append("\n");
        AtomicBoolean firstKey = new AtomicBoolean(true);
        referenceData.getMetaDataFieldsList()
                .stream()
                .filter(field -> Objects.nonNull(field.getFieldKey()) && field.getFieldKey())
                .forEach(field -> {
                    if (firstKey.get()) {
                        sql.append("WHERE %s = :%s\n".formatted(field.getFieldName(), field.getFieldName()));
                        firstKey.set(false);
                    } else {
                        sql.append("  AND %s = :%s\n".formatted(field.getFieldName(), field.getFieldName()));
                    }
                });
        if (firstKey.get()) {
            var errMsg = "В справочных данных не найдены ключевые поля для таблицы %s.%s".formatted(referenceData.getSchemaName(), referenceData.getTableName());
            log.error(errMsg);
            throw new EAppException(errMsg);
        }
        jdbcTemplate.update(sql.toString(), newValue);
    }

}
