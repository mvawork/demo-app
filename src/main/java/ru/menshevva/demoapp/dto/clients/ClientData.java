package ru.menshevva.demoapp.dto.clients;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@Builder
public class ClientData {

    private BigInteger clientId;
    private String clientName;

}
