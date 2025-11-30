package ru.menshevva.demoapp.ui;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.PermitAll;
import ru.menshevva.demoapp.security.SecurityUtils;
import ru.menshevva.demoapp.security.vaadin.LogoutUtils;

@Tag("main-layout")
@JsModule("./src/main-layout.ts")
@PermitAll
public class MainLayout extends LitTemplate implements RouterLayout {

    @Id
    private Paragraph logout;

    public MainLayout(AuthenticationContext authContext, LogoutUtils logoutUtils) {
        logout.addClickListener(event -> {
                    //        logoutUtils.logout()
            authContext.logout();
                }
        );
        var userInfo = SecurityUtils.getUserInfo();
        if (userInfo != null) {
            getElement().setProperty("userFio", userInfo.getUserName());
        } else {
            getElement().setProperty("userFio", "???");
        }
    }
}
