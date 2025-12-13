package ru.menshevva.demoapp.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import ru.menshevva.demoapp.security.dto.UserData;

import java.util.Collection;

@Getter
public class AppUser extends User  {

    private final UserData appUserInfo;

    public AppUser(String username, String password, Collection<? extends GrantedAuthority> authorities, UserData appUserInfo) {
        super(username, password, authorities);
        this.appUserInfo = appUserInfo;
    }
}
