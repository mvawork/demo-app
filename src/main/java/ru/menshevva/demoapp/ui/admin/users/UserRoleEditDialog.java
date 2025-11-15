package ru.menshevva.demoapp.ui.admin.users;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import ru.menshevva.demoapp.dto.userrole.UserRoleData;
import ru.menshevva.demoapp.service.users.UserRoleCRUDService;
import ru.menshevva.demoapp.ui.components.EditActionCallback;
import ru.menshevva.demoapp.ui.components.EditActionComponent;

import java.math.BigInteger;

@SpringComponent
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class UserRoleEditDialog extends Dialog implements EditActionCallback {

    private final UserRoleEditView editView;
    private final UserRoleCRUDService crudService;

    public UserRoleEditDialog(UserRoleCRUDService userCRUDService,
                              UserRoleEditView userRoleEditView) {
        this.crudService = userCRUDService;
        this.editView = userRoleEditView;
        setCloseOnEsc(false);
        setCloseOnOutsideClick(false);
        setDraggable(true);
        add(this.editView, new EditActionComponent(this));
    }

    private UserRoleData editValue;
    private EditActionCallback editActionCallback;

    public void addUserRole(BigInteger userId, EditActionCallback editActionCallback) {
        this.editActionCallback = editActionCallback;
        this.editValue = UserRoleData.builder()
                .userId(userId)
                .build();
        editView.setValue(this.editValue);
        open();
    }

    @Override
    public void ok() {
        if (editValue != null) {
            try {
                try {
                    editView.getValue(editValue);
                } catch (ValidationException e) {
                    return;
                }
                crudService.create(editValue);
                close();
                if (editActionCallback != null) {
                    editActionCallback.ok();
                }
            } catch (RuntimeException e) {
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

    public void delete(UserRoleData value, EditActionCallback editActionCallback) {
        crudService.delete(value);
        if (editActionCallback != null) {
            editActionCallback.ok();
        }
    }
}
