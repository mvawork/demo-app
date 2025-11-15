package ru.menshevva.demoapp.dto.roleprivilege;

import lombok.*;

import java.math.BigInteger;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RolePrivilegeData {
    private BigInteger roleId;
    private BigInteger privilegeId;
}
