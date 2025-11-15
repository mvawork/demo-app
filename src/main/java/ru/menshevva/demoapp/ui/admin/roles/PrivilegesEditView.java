package ru.menshevva.demoapp.ui.admin.roles;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import ru.menshevva.demoapp.dto.privilege.PrivilegeData;

@Tag("privileges-edit-view")
@JsModule("./src/admin/roles/privileges-edit-view.ts")
public class PrivilegesEditView extends LitTemplate {

    @Id
    private TextField privilegesName;

    @Id
    private TextField privilegesDescription;

    private final Binder<PrivilegeData> binder = new Binder<>();

    public PrivilegesEditView() {
        binder.forField(privilegesName)
                .asRequired("Имя привилегии обязательно")
                .bind(PrivilegeData::getPrivilegeName, PrivilegeData::setPrivilegeName);
        binder.forField(privilegesDescription)
                .asRequired("Описание привилегии обязательно")
                .bind(PrivilegeData::getPrivilegeDescription, PrivilegeData::setPrivilegeDescription);
    }

    public void setValue(PrivilegeData privilegeData) {
        binder.readBean(privilegeData);
    }

    public void getRoleData(PrivilegeData privilegeData) throws ValidationException {
        binder.writeBean(privilegeData);
    }

}
