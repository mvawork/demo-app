package ru.menshevva.demoapp.ui.components;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.BaseJsonNode;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class MenuItemProvider {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static BaseJsonNode getMenuItems(String path, Supplier<List<MenuItem>> menuItemSupplier) {
        var menuItems = menuItemSupplier.get();
        if (path == null || path.isEmpty()) {
            menuItems.forEach(menuItem -> menuItem.setActive(Objects.equals(path, menuItem.getUrl())));
        } else {
            var splitPath = path.split("/");
            menuItems.forEach(menuItem -> {
                var splitUrl = menuItem.getUrl().split("/");
                if (splitPath.length <= splitUrl.length) {
                    var f = true;
                    for (int i = 0; i < splitPath.length; i++) {
                        if (!splitPath[i].equals(splitUrl[i])) {
                            f = false;
                            break;
                        }
                    }
                    menuItem.setActive(f);
                } else {
                    menuItem.setActive(false);
                }
            });
        }
        return mapper.valueToTree(menuItems);
    }

}
