package ru.menshevva.demoapp.ui.clients;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.RouterLayout;
import jakarta.annotation.security.RolesAllowed;
import ru.menshevva.demoapp.security.ApplicationRoles;
import ru.menshevva.demoapp.ui.MainMenuLayout;

@RolesAllowed({ApplicationRoles.ROLE_CLIENTS})
@Tag("client-layout")
@JsModule("./src/client/client-layout.ts")
@ParentLayout(MainMenuLayout.class)
public class ClientLayout extends LitTemplate implements RouterLayout, AfterNavigationObserver {

    @Override
    public void afterNavigation(AfterNavigationEvent event) {

    }
}
