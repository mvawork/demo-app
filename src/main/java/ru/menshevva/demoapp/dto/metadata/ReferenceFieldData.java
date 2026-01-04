package ru.menshevva.demoapp.dto.metadata;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ReferenceFieldData {

    private String fieldName;
    private String fieldTitle;
    private Integer fieldLength;
    private Integer fieldOrder;

}
