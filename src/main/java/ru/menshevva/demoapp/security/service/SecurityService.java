package ru.menshevva.demoapp.security.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import ru.menshevva.demoapp.security.AppOidcUser;

public interface SecurityService {


    AppOidcUser getOidcUser(OidcUser oidcUser);

    User findByUsername(String username);
}