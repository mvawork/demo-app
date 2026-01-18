package ru.menshevva.demoapp.dto.metadata;

import lombok.*;
import ru.menshevva.demoapp.dto.ChangeStatus;

import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReferenceFieldData implements Serializable {

    private Long fieldId;
    private String fieldName;
    private String fieldTitle;
    private Integer fieldLength;
    private Integer fieldOrder;
    private ReferenceFieldType fieldType;
    private Boolean fieldKey;
    private ChangeStatus changeStatus;

}
