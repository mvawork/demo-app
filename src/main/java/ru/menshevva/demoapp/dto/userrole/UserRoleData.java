package ru.menshevva.demoapp.dto.userrole;

import lombok.*;

import java.math.BigInteger;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRoleData {
    private BigInteger userId;
    private BigInteger roleId;

}
