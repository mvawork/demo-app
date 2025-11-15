package ru.menshevva.demoapp.service.roles;

import ru.menshevva.demoapp.dto.role.RoleData;

import java.math.BigInteger;

public interface RoleCRUDService {

    RoleData read(BigInteger roleId);
    void create(RoleData roleData);
    void update(RoleData roleData);
    void delete(BigInteger roleId);
}
