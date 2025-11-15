package ru.menshevva.demoapp.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import ru.menshevva.demoapp.security.dto.UserData;

import java.util.Collection;

@Getter
public class AppOidcUser extends DefaultOidcUser {

    private final UserData appUserInfo;

    public AppOidcUser(Collection<? extends GrantedAuthority> authorities, OidcIdToken idToken, OidcUserInfo userInfo, UserData appUserInfo) {
        super(authorities, idToken, userInfo);
        this.appUserInfo = appUserInfo;
    }
}
