package ru.menshevva.demoapp.ui;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.router.*;
import jakarta.annotation.security.PermitAll;
import lombok.extern.slf4j.Slf4j;
import ru.menshevva.demoapp.security.ApplicationRoles;
import ru.menshevva.demoapp.security.SecurityUtils;
import ru.menshevva.demoapp.ui.admin.users.UserListView;
import ru.menshevva.demoapp.ui.clients.ClientListView;
import ru.menshevva.demoapp.ui.components.MenuItem;
import ru.menshevva.demoapp.ui.components.MenuItemProvider;

import java.util.ArrayList;
import java.util.List;

@Layout
@PermitAll
@Tag("menu-layout")
@JsModule("./src/main-menu-layout.ts")
@ParentLayout(MainLayout.class)
@Slf4j
public class MainMenuLayout extends LitTemplate implements RouterLayout, AfterNavigationObserver {


    @Override
    public void afterNavigation(AfterNavigationEvent afterNavigationEvent) {
        var paths = afterNavigationEvent.getLocation()
                .getSegments();
        var path = paths.isEmpty() ? "" : paths.getFirst();
        getElement().setPropertyJson("menuItems",
                MenuItemProvider.getMenuItems(path, this::getMenuItems));
    }

    private List<MenuItem> getMenuItems() {
        var result = new ArrayList<MenuItem>();
        result.add(MenuItem.builder().id("main").title("")
                .url(RouteConfiguration.forApplicationScope().getUrl(WelcomeView.class)).build());

        if (SecurityUtils.checkPermission(ApplicationRoles.ROLE_CLIENTS)) {
            result.add(MenuItem.builder().id("Клиенты").title("Клиенты")
                    .url(RouteConfiguration.forApplicationScope().getUrl(ClientListView.class)).build());
        }

        if (SecurityUtils.checkPermission(ApplicationRoles.ROLE_ADMIN)) {
            result.add(MenuItem.builder().id("admin").title("Админ")
                    .url(RouteConfiguration.forApplicationScope().getUrl(UserListView.class)).build());
        }



        return result;
    }

}
