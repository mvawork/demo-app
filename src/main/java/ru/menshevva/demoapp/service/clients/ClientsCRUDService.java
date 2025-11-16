package ru.menshevva.demoapp.service.clients;

import ru.menshevva.demoapp.dto.clients.ClientData;

import java.math.BigInteger;

public interface ClientsCRUDService {
    ClientData read(BigInteger privilegeId);
    void create(ClientData privilegeData);
    void update(ClientData privilegeData);
    void delete(BigInteger privilegeId);
}
