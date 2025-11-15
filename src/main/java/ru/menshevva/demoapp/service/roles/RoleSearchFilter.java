package ru.menshevva.demoapp.service.roles;


import lombok.*;

import java.math.BigInteger;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleSearchFilter {

    public static final String FILTER_ROLE_ID = "role_id";
    public static final String FILTER_ROLE_NAME = "role_name";
    public static final String FILTER_ROLE_DESCRIPTION = "role_description";
    public static final String FILTER_ROLE_LABEL = "role_label";

    private BigInteger roleId;
    private String roleName;
    private String roleDescription;

    private String roleLabel;

}
