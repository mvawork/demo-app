package ru.menshevva.demoapp.service.roles;

import lombok.*;

import java.math.BigInteger;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RolePrivilegeSearchFilter {

    public static final String FILTER_ROLE_ID = "role_id";
    public static final String FILTER_ROLE_NAME = "role_name";
    public static final String FILTER_ROLE_DESCRIPTION = "role_description";
    public static final String FILTER_PRIVILEGE_ID = "privilege_id";
    public static final String FILTER_PRIVILEGE_NAME = "privilege_name";
    public static final String FILTER_PRIVILEGE_DESCRIPTION = "privilege_description";


    private BigInteger roleId;
    private String roleName;
    private String roleDescription;

    private BigInteger privilegeId;
    private String privilegeName;
    private String privilegeDescription;


}
