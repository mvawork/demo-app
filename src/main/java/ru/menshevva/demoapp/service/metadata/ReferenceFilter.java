package ru.menshevva.demoapp.service.metadata;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReferenceFilter {

    public static final String FILTER_SCHEMA_NAME = "SCHEMA_NAME";
    public static final String FILTER_TABLE_NAME = "TABlE_NAME";
    public static final String FILTER_REFERENCE_NAME = "REFERENCE_NAME";

    private String referenceName;
    private String schemaName;
    private String tableName;

}
