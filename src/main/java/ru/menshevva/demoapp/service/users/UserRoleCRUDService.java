package ru.menshevva.demoapp.service.users;

import ru.menshevva.demoapp.dto.userrole.UserRoleData;

public interface UserRoleCRUDService {
    void create(UserRoleData userRole);
    void delete(UserRoleData userRole);
}
