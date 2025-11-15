package ru.menshevva.demoapp.service.roles;

import ru.menshevva.demoapp.dto.privilege.PrivilegeData;
import java.math.BigInteger;

public interface PrivilegeCRUDService {

    PrivilegeData read(BigInteger privilegeId);
    void create(PrivilegeData privilegeData);
    void update(PrivilegeData privilegeData);
    void delete(BigInteger privilegeId);

}