package ru.menshevva.demoapp.dto.metadata;

import lombok.*;
import ru.menshevva.demoapp.dto.ChangeStatus;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReferenceData {
    private Long referenceId;
    private String referenceName;
    private String schemaName;
    private String tableName;
    private String tableSQL;
    private List<ReferenceFieldData> metaDataFieldsList;

    private ChangeStatus changeStatus;

}
