package ru.menshevva.demoapp.dto;

import lombok.Builder;

import java.math.BigInteger;


@Builder
public record UserListData (BigInteger userId, String userLogin, String userName) {

}
