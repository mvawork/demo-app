package ru.menshevva.demoapp.security.vaadin;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.util.UriComponentsBuilder;

@SpringComponent
public class LogoutUtils {

    private final String logoutUrl;
    private static final String LOGOUT_SUCCESS_URL = "/";

    public LogoutUtils(ServerProperties serverProperties) {
        this.logoutUrl = UriComponentsBuilder
                .fromPath(serverProperties.getServlet().getContextPath())
                .path("/logout")
                .build()
                .toUriString();
    }

    public void logout() {
        UI.getCurrent().getPage().setLocation(logoutUrl);
//        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
//        logoutHandler.logout(
//                VaadinServletRequest.getCurrent().getHttpServletRequest(), null,
//                null);

        //UI.getCurrent().getPage().setLocation(logoutUrl);
    }
}
