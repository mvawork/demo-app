package ru.menshevva.demoapp.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import ru.menshevva.demoapp.security.dto.UserData;

public class SecurityUtils {

    public static String getUserFio() {
        Authentication auth = getAuthentication();
        if (auth instanceof OAuth2AuthenticationToken token) {
            if (token.getPrincipal() instanceof AppOidcUser user) {
                return user.getAppUserInfo().getUserName();
            }
        }
        return null;
    }

    private static Authentication getAuthentication() {
        var securityContext = SecurityContextHolder.getContext();
        if (securityContext != null && securityContext.getAuthentication() != null) {
            return securityContext.getAuthentication();
        }
        return null;
    }

    public static UserData getUserInfo() {
        var authentication = getAuthentication();
        if (authentication instanceof OAuth2AuthenticationToken token &&
        token.getPrincipal() instanceof AppOidcUser user) {
            return user.getAppUserInfo();
        }
        return null;
    }

    public static boolean checkPermission(String role) {
        var authentication = getAuthentication();
        if (authentication instanceof OAuth2AuthenticationToken token &&
                token.getPrincipal() instanceof AppOidcUser user) {
            var roles = user.getAuthorities();
            for (GrantedAuthority authority : roles) {
                if (authority.getAuthority().equals(role)) {
                    return true;
                }
            }
        }
        return false;
    }
}
