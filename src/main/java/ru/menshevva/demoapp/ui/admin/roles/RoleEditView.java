package ru.menshevva.demoapp.ui.admin.roles;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import ru.menshevva.demoapp.dto.role.RoleData;


@Tag("role-edit-view")
@JsModule("./src/admin/roles/role-edit-view.ts")
public class RoleEditView extends LitTemplate {

    @Id
    private TextField roleName;

    @Id
    private TextField roleDescription;

    private final Binder<RoleData> binder = new Binder<>();

    public RoleEditView() {
        binder.forField(roleName)
                .asRequired("Имя роли обязательно")
                .bind(RoleData::getRoleName, RoleData::setRoleName);
        binder.forField(roleDescription)
                .asRequired("Описание роли обязательно")
                .bind(RoleData::getRoleDescription, RoleData::setRoleDescription);
    }

    public void setRoleData(RoleData roleData) {
        binder.readBean(roleData);
    }

    public void getRoleData(RoleData roleData) throws ValidationException {
        binder.writeBean(roleData);
    }

}
