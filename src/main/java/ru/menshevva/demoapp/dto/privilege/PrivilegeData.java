package ru.menshevva.demoapp.dto.privilege;

import lombok.*;

import java.math.BigInteger;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PrivilegeData {

    private BigInteger privilegeId;
    private String privilegeName;
    private String privilegeDescription;

}