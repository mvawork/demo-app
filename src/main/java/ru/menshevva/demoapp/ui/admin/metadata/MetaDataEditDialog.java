package ru.menshevva.demoapp.ui.admin.metadata;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import ru.menshevva.demoapp.dto.metadata.ReferenceData;
import ru.menshevva.demoapp.service.metadata.MetaDataCRUDservice;
import ru.menshevva.demoapp.ui.components.EditActionCallback;
import ru.menshevva.demoapp.ui.components.EditActionComponent;

@SpringComponent
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class MetaDataEditDialog extends Dialog implements EditActionCallback {


    private final MetaDataEditView editView;
    private final MetaDataCRUDservice service;
    private ReferenceData value;
    private EditActionCallback editActionCallback;

    public MetaDataEditDialog(MetaDataCRUDservice service) {
        this.service = service;
        var content = new VerticalLayout();
        this.editView = new MetaDataEditView();
        content.setWidth(800, Unit.PIXELS);
        content.setHeight(600, Unit.PIXELS);
        content.add(editView, new EditActionComponent(this));
        content.setFlexGrow(1, editView);
        setCloseOnEsc(false);
        setCloseOnOutsideClick(false);
        setDraggable(true);
        add(content);

    }

    public void editValue(ReferenceData referenceData, EditActionCallback editActionCallback) {
        this.editActionCallback = editActionCallback;
        if (referenceData == null) {
            this.value = new ReferenceData();
        } else {
            this.value = referenceData;
        }
        editView.setValue(this.value);
        open();
    }


    @Override
    public void ok() {
        if (value != null) {
            try {
                editView.getValue(value);
                service.save(value);
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
}
