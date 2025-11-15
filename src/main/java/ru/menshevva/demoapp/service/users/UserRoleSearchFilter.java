package ru.menshevva.demoapp.service.users;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@Builder
public class UserRoleSearchFilter {
    public static final String FILTER_USER_ID = "FILTER_USER_ID";
    public static final String FILTER_ROLE_ID = "FILTER_ROLE_ID";
    public static final String FILTER_ROLE_NAME = "FILTER_ROLE_NAME";
    public static final String FILTER_ROLE_DESCRIPTION = "FILTER_ROLE_DESCRIPTION";

    private BigInteger userId;
    private BigInteger roleId;
    private String roleName;
    private String roleDescription;

}
