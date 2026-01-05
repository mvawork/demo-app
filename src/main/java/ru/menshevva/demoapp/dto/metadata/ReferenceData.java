package ru.menshevva.demoapp.dto.metadata;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReferenceData {
    private Long referenceId;
    private String schemaName;
    private String tableName;
    private String tableSQL;
    private List<ReferenceFieldData> metaDataFieldsList;

}
