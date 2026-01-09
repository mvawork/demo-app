package ru.menshevva.demoapp.service.metadata.impl;

import com.vaadin.flow.data.provider.Query;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import ru.menshevva.demoapp.dto.metadata.ReferenceData;
import ru.menshevva.demoapp.service.metadata.ReferenceDataSearchService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@Service
@Slf4j
public class ReferenceDataSearchServiceImpl implements ReferenceDataSearchService {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public ReferenceDataSearchServiceImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Stream<Map<String, ?>> search(ReferenceData referenceData, Query<Map<String, ?>, Map<String, ?>> query) {
        if (referenceData == null) {
            return Stream.empty();
        }
        var sql = "%s OFFSET :offset LIMIT :limit ".formatted(referenceData.getTableSQL());
        Map<String, Object> params = new HashMap<>();
        params.put("limit", query.getLimit());
        params.put("offset", query.getOffset());
        var result = jdbcTemplate.query(sql, params, (rs, rowNum) -> {
            Map<String, Object> m = new HashMap<>();
            referenceData.getMetaDataFieldsList().forEach(v -> {
                        try {
                            var value = switch (v.getFieldType()) {
                                case FIELD_TYPE_STRING -> rs.getString(v.getFieldName());
                                case FIELD_TYPE_INTEGER -> rs.getInt(v.getFieldName());
                                case FIELD_TYPE_DOUBLE -> rs.getDouble(v.getFieldName());
                                case FIELD_TYPE_LONG -> rs.getLong(v.getFieldName());
                                case FIELD_TYPE_BOOLEAN -> rs.getBoolean(v.getFieldName());
                                case FIELD_TYPE_DATE -> {
                                    var dateValue = rs.getDate(v.getFieldName());
                                    yield dateValue == null ? null : rs.getDate(v.getFieldName()).toLocalDate();
                                }
                                case FIELD_TYPE_TIMESTAMP -> {
                                    var dateTimeValue = rs.getTimestamp(v.getFieldName());
                                    yield dateTimeValue == null ? null : rs.getTimestamp(v.getFieldName()).toLocalDateTime();
                                }
                                case FIELD_TYPE_BIGDECIMAL -> rs.getBigDecimal(v.getFieldName());
                                case FIELD_TYPE_BYTE -> rs.getBytes(v.getFieldName());
                                case FIELD_TYPE_FLOAT -> rs.getFloat(v.getFieldName());
                                default -> {
                                    log.warn("Unknown field type: {}", v.getFieldType());
                                    yield null;
                                }
                            };
                            m.put(v.getFieldName(), value);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
            );
            return m;
        });
        return result.stream().map(v -> (Map<String, ?>) v);
    }

    @Override
    public int count(ReferenceData referenceData, Query<Map<String, ?>, Map<String, ?>> query) {
        if (referenceData == null) {
            return 0;
        }
        var sql = "Select count(*) from (%s)".formatted(referenceData.getTableSQL());
        try {
            var params = new HashMap<String, Object>();
            var count = jdbcTemplate.queryForObject(sql, params, Integer.class);
            return count != null ? count : 0;
        } catch (RuntimeException e) {
            log.warn("Ошибка выполнения запрос {}", sql);
            return 0;
        }
    }
}
