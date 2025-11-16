package ru.menshevva.demoapp.service.clients.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import ru.menshevva.demoapp.dto.clients.ClientData;
import ru.menshevva.demoapp.service.clients.ClientsCRUDService;

import java.math.BigInteger;

@Service
public class ClientsCRUDServiceImpl implements ClientsCRUDService {

    @PersistenceContext(unitName = "second")
    private EntityManager entityManager;

    @Override
    public ClientData read(BigInteger privilegeId) {
        return null;
    }

    @Override
    public void create(ClientData privilegeData) {

    }

    @Override
    public void update(ClientData privilegeData) {

    }

    @Override
    public void delete(BigInteger privilegeId) {

    }
}
