package ru.menshevva.demoapp.service.users;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@Builder
public class UserSearchFilter {

    public static final String FILTER_USER_ID = "FILTER_USER_ID";
    public static final String FILTER_USER_LOGIN = "FILTER_USER_LOGIN";
    public static final String FILTER_USER_NAME = "FILTER_USER_NAME";

    private BigInteger userId;
    private String userLogin;
    private String userName;
}
