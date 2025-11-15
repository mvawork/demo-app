package ru.menshevva.demoapp.dto.roleprivilege;

import lombok.*;

import java.math.BigInteger;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RolePrivilegeListData {
    private BigInteger roleId;
    private String roleName;
    private String roleDescription;
    private BigInteger privilegeId;
    private String privilegeName;
    private String privilegeDescription;
}
