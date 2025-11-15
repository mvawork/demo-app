package ru.menshevva.demoapp.ui.admin.roles;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import ru.menshevva.demoapp.dto.privilege.PrivilegeData;
import ru.menshevva.demoapp.service.roles.PrivilegeCRUDService;
import ru.menshevva.demoapp.ui.components.EditActionCallback;
import ru.menshevva.demoapp.ui.components.EditActionComponent;

import java.math.BigInteger;

@SpringComponent
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class PrivilegesEditDialog extends Dialog  implements EditActionCallback {

    private final PrivilegeCRUDService CRUDService;
    private final PrivilegesEditView editView;

    public PrivilegesEditDialog(PrivilegeCRUDService CRUDService) {
        this.CRUDService = CRUDService;
        this.editView = new PrivilegesEditView();
        setCloseOnEsc(false);
        setCloseOnOutsideClick(false);
        setDraggable(true);
        add(editView, new EditActionComponent(this));
    }

    private PrivilegeData value;
    private EditActionCallback editActionCallback;

    public void editValue(BigInteger privilegeId, EditActionCallback editActionCallback) {
        this.editActionCallback = editActionCallback;
        if (privilegeId == null) {
            this.value = new PrivilegeData();
        } else {
            this.value = CRUDService.read(privilegeId);
        }
        editView.setValue(this.value);
        open();
    }

    @Override
    public void ok() {
        if (value != null) {
            try {
                editView.getRoleData(value);
                if (value.getPrivilegeId() == null) {
                    CRUDService.create(value);
                } else {
                    CRUDService.update(value);
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

    public void deleteValue(BigInteger privilegeId, EditActionCallback editActionCallback) {
        CRUDService.delete(privilegeId);
        if (editActionCallback != null) {
            editActionCallback.ok();
        }


    }
}
