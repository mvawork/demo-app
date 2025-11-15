package ru.menshevva.demoapp.security.dto;

import lombok.*;

import java.math.BigInteger;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserData {

    private BigInteger userId;
    private String userLogin;
    private String userName;

}
