package ru.menshevva.demoapp.dto.metadata;

import lombok.*;
import ru.menshevva.demoapp.dto.ChangeStatus;

import java.io.Serializable;
import java.util.ArrayList;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReferenceData implements Serializable {
    private Long referenceId;
    private String referenceName;
    private String schemaName;
    private String tableName;
    private String tableSQL;
    private String jvmScript;
    private ArrayList<ReferenceFieldData> metaDataFieldsList;

    private ChangeStatus changeStatus;

}
