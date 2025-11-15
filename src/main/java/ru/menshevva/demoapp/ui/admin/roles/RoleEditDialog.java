package ru.menshevva.demoapp.ui.admin.roles;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import ru.menshevva.demoapp.dto.role.RoleData;
import ru.menshevva.demoapp.service.roles.RoleCRUDService;
import ru.menshevva.demoapp.ui.components.EditActionCallback;
import ru.menshevva.demoapp.ui.components.EditActionComponent;

import java.math.BigInteger;

@SpringComponent
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class RoleEditDialog extends Dialog implements EditActionCallback {

    private final RoleCRUDService roleCRUDService;
    private final RoleEditView roleEditView;
    private final Tab roleTab;
    private final Tab privilegeTab;
    private final RolePrivilegesListView rolePrivilegesListView;
    private final Tabs tabs;

    public RoleEditDialog(RoleCRUDService roleCRUDService,
                          RolePrivilegesListView rolePrivilegesListView) {
        this.roleCRUDService = roleCRUDService;
        this.roleEditView = new RoleEditView();
        this.rolePrivilegesListView = rolePrivilegesListView;
        this.tabs = new Tabs();
        this.roleTab = new Tab("Роль");
        this.privilegeTab = new Tab("Привилегии");
        tabs.add(roleTab, privilegeTab);
        tabs.addSelectedChangeListener(e -> {
            roleEditView.setVisible(e.getSelectedTab() == roleTab);
            rolePrivilegesListView.setVisible(e.getSelectedTab() == privilegeTab);
        });
        tabs.setSelectedTab(null);
        setCloseOnEsc(false);
        setCloseOnOutsideClick(false);
        setDraggable(true);
        add(tabs, roleEditView, rolePrivilegesListView, new EditActionComponent(this));
    }

    private RoleData editValue;
    private EditActionCallback editActionCallback;

    public void editRole(BigInteger roleId, EditActionCallback editActionCallback) {
        this.editActionCallback = editActionCallback;
        if (roleId == null) {
            this.editValue = new RoleData();
            // Скрыть привилегии, если роль создается
            privilegeTab.setVisible(false);
        } else {
            this.editValue = roleCRUDService.read(roleId);
            // Показать привилегии, если роль существует
            privilegeTab.setVisible(true);
        }
        roleEditView.setRoleData(this.editValue);
        rolePrivilegesListView.setRoleId(roleId);

        tabs.setSelectedIndex(0);
        open();
    }

    @Override
    public void ok() {
        if (editValue != null) {
            try {
                roleEditView.getRoleData(editValue);
                if (editValue.getRoleId() == null) {
                    roleCRUDService.create(editValue);
                } else {
                    roleCRUDService.update(editValue);
                }
                close();
                if (editActionCallback != null) {
                    editActionCallback.ok();
                }
            } catch (ValidationException e) {
                // todo показать диалог с ошибкой валидации
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

    public void deleteRole(BigInteger userId, EditActionCallback editActionCallback) {
        roleCRUDService.delete(userId);
        if (editActionCallback != null) {
            editActionCallback.ok();
        }
    }

}
