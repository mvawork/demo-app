package ru.menshevva.demoapp.security.vaadin;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.web.util.UriComponentsBuilder;

@SpringComponent
public class LogoutUtils {

    private final String logoutUrl;

    public LogoutUtils(ServerProperties serverProperties) {
        this.logoutUrl = UriComponentsBuilder
                .fromPath(serverProperties.getServlet().getContextPath())
                .path("/logout")
                .build()
                .toUriString();
    }

    public void logout() {
        UI.getCurrent().getPage().setLocation(logoutUrl);
    }
}
