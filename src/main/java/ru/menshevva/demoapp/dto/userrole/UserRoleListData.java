package ru.menshevva.demoapp.dto.userrole;

import lombok.*;

import java.math.BigInteger;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRoleListData {

    private BigInteger userId;
    private BigInteger roleId;
    private String roleName;
    private String roleDescription;
}
