package ru.menshevva.demoapp.ui.admin;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.router.*;
import jakarta.annotation.security.RolesAllowed;
import ru.menshevva.demoapp.security.ApplicationRoles;
import ru.menshevva.demoapp.ui.MainMenuLayout;
import ru.menshevva.demoapp.ui.admin.metadata.MetaDataListView;
import ru.menshevva.demoapp.ui.admin.roles.RoleListView;
import ru.menshevva.demoapp.ui.admin.roles.PrivilegesListView;
import ru.menshevva.demoapp.ui.admin.users.UserListView;
import ru.menshevva.demoapp.ui.components.MenuItem;
import ru.menshevva.demoapp.ui.components.MenuItemProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RolesAllowed({ApplicationRoles.ROLE_ADMIN})
@Tag("admin-layout")
@JsModule("./src/admin/admin-layout.ts")
@ParentLayout(MainMenuLayout.class)
public class AdminLayout extends LitTemplate implements RouterLayout, AfterNavigationObserver {
    @Override
    public void afterNavigation(AfterNavigationEvent afterNavigationEvent) {
        /* Получить сегменты url*/
        var paths = afterNavigationEvent.getLocation()
                .getSegments()
                .stream()
                .limit(2)
                .collect(Collectors.joining("/"));
        getElement().setPropertyJson("menuItems",
                MenuItemProvider.getMenuItems(paths, this::getMenuItems));
    }

    private List<MenuItem> getMenuItems() {
        var result = new ArrayList<MenuItem>();
        result.add(MenuItem.builder()
                .id("users")
                .title("Пользователи")
                .url(RouteConfiguration.forApplicationScope().getUrl(UserListView.class))
                .active(false)
                .build());
        result.add(MenuItem.builder()
                .id("roles")
                .title("Роли")
                .url(RouteConfiguration.forApplicationScope().getUrl(RoleListView.class))
                .active(false)
                .build());
        result.add(MenuItem.builder()
                .id("privilege")
                .title("Привилегии")
                .url(RouteConfiguration.forApplicationScope().getUrl(PrivilegesListView.class))
                .active(false)
                .build());
        result.add(MenuItem.builder()
                .id("metadata")
                .title("Метаданные")
                .url(RouteConfiguration.forApplicationScope().getUrl(MetaDataListView.class))
                .active(false)
                .build());
        return result;

    }
}
