package ru.menshevva.demoapp.ui.admin.roles;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import ru.menshevva.demoapp.dto.roleprivilege.RolePrivilegeData;
import ru.menshevva.demoapp.service.roles.RolePrivilegeCRUDService;
import ru.menshevva.demoapp.ui.components.EditActionCallback;
import ru.menshevva.demoapp.ui.components.EditActionComponent;

@SpringComponent
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class RolePrivilegesEditDialog extends Dialog implements EditActionCallback {

    private final RolePrivilegeCRUDService crudService;
    private final RolePrivilegesEditView editView;


    public RolePrivilegesEditDialog(RolePrivilegeCRUDService crudService, RolePrivilegesEditView editView) {
        this.crudService = crudService;
        this.editView = editView;
        setCloseOnEsc(false);
        setCloseOnOutsideClick(false);
        setDraggable(true);
        add(editView, new EditActionComponent(this));
    }

    private EditActionCallback editActionCallback;
    private RolePrivilegeData value;

    public void edit(RolePrivilegeData value, EditActionCallback editActionCallback) {
        this.value = value;
        this.editActionCallback = editActionCallback;
        editView.setValue(value);
        open();
    }

    public void delete(RolePrivilegeData value, EditActionCallback editActionCallback) {
        crudService.delete(value);
        if (editActionCallback != null) {
            editActionCallback.ok();
        }
    }

    @Override
    public void ok() {
        try {
            editView.getValue(value);
            crudService.create(value);
            close();
            if (editActionCallback != null) {
                editActionCallback.ok();
            }
        } catch (ValidationException e) {
            // todo показать диалог с ошибкой валидации
        }
    }

    @Override
    public void cancel() {
        close();
        if (editActionCallback != null) {
            editActionCallback.cancel();
        }
    }

}
