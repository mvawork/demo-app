package ru.menshevva.demoapp.service.roles;

import lombok.*;

import java.math.BigInteger;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PrivilegeSearchFilter {

    public static final String FILTER_PRIVILEGE_ID = "privilege_id";
    public static final String FILTER_PRIVILEGE_NAME = "privilege_name";
    public static final String FILTER_PRIVILEGE_DESCRIPTION = "privilege_description";
    public static final String FILTER_PRIVILEGE_LABEL = "privilege_label";

    private BigInteger privilegeId;
    private String privilegeName;
    private String privilegeDescription;

    private String privilegeLabel;
}

