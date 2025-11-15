package ru.menshevva.demoapp.service.users;

import ru.menshevva.demoapp.security.dto.UserData;

import java.math.BigInteger;

public interface UserCRUDService {

    UserData read(BigInteger id);

    void create(UserData value);

    void update(UserData editValue);

    void delete(BigInteger id);
}
