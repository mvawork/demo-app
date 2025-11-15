package ru.menshevva.demoapp.dto.role;

import lombok.*;

import java.math.BigInteger;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleData {
    private BigInteger roleId;
    private String roleName;
    private String roleDescription;
}
