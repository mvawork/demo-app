package ru.menshevva.demoapp.service.roles;

import ru.menshevva.demoapp.dto.roleprivilege.RolePrivilegeData;

import java.math.BigInteger;

public interface RolePrivilegeCRUDService {

    void create(RolePrivilegeData value);


    void delete(RolePrivilegeData value);


    void deleteForRole(BigInteger roleId);
}
