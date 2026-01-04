package ru.menshevva.demoapp.dto.metadata;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ReferenceData {
    private Long referenceId;
    private String schemaName;
    private String tableName;
    private String tableSQL;
    private List<ReferenceFieldData> metaDataFieldsList;

}
