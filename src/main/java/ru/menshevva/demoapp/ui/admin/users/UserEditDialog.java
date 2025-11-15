package ru.menshevva.demoapp.ui.admin.users;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import ru.menshevva.demoapp.security.dto.UserData;
import ru.menshevva.demoapp.service.users.UserCRUDService;
import ru.menshevva.demoapp.ui.components.EditActionCallback;
import ru.menshevva.demoapp.ui.components.EditActionComponent;

import java.math.BigInteger;

@SpringComponent
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class UserEditDialog extends Dialog implements EditActionCallback {

    private final UserEditView userEditView;
    private final UserCRUDService userCRUDService;

    private final Tab userTab;
    private final Tab roleTab;

    private final Tabs tabs;
    private final UserRoleListView userRoleListView;


    public UserEditDialog(UserCRUDService userCRUDService,
                          UserRoleListView userRoleListView) {
        this.userCRUDService = userCRUDService;
        this.userEditView = new UserEditView();
        this.userRoleListView = userRoleListView;
        this.tabs = new Tabs();
        this.userTab = new Tab("Пользователь");
        this.roleTab = new Tab("Роли");
        tabs.add(userTab, roleTab);
        tabs.addSelectedChangeListener(e -> {
            userEditView.setVisible(e.getSelectedTab() == userTab);
            userRoleListView.setVisible(e.getSelectedTab() == roleTab);
        });
        tabs.setSelectedTab(null);
        setCloseOnEsc(false);
        setCloseOnOutsideClick(false);
        setDraggable(true);
        add(tabs, this.userEditView, this.userRoleListView, new EditActionComponent(this));
    }

    private UserData editValue;
    private EditActionCallback editActionCallback;

    public void editUser(BigInteger id, EditActionCallback editActionCallback) {
        this.editActionCallback = editActionCallback;
        if (id == null) {
            this.editValue = new UserData();
            roleTab.setVisible(false);
        } else {
            this.editValue = userCRUDService.read(id);
            roleTab.setVisible(true);
        }
        userEditView.setValue(this.editValue);
        userRoleListView.setUserId(id);
        tabs.setSelectedIndex(0);
        open();
    }

    @Override
    public void ok() {
        if (editValue != null) {
            try {
                userEditView.getValue(editValue);
                if (editValue.getUserId() == null) {
                    userCRUDService.create(editValue);
                } else {
                    userCRUDService.update(editValue);
                }
                close();
                if (editActionCallback != null) {
                    editActionCallback.ok();
                }
            } catch (Exception e) {
                Notification notification = new Notification();
                notification.setPosition(Notification.Position.TOP_END);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                notification.setDuration(5000);
                Div text = new Div(new Text(e.getMessage().replace("java.lang.Exception: ", "")));
                (notification).add(text);
                notification.open();
            }
        }
    }

    @Override
    public void cancel() {
        close();
        if (editActionCallback != null) {
            editActionCallback.cancel();
        }
    }

    public void deleteUser(BigInteger userId, EditActionCallback editActionCallback) {
        userCRUDService.delete(userId);
        if (editActionCallback != null) {
            editActionCallback.ok();
        }
    }
}
