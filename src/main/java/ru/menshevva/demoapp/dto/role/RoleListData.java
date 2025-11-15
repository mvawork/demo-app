package ru.menshevva.demoapp.dto.role;

import lombok.*;

import java.math.BigInteger;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleListData {

    private BigInteger roleId;
    private String roleName;
    private String roleDescription;

}
