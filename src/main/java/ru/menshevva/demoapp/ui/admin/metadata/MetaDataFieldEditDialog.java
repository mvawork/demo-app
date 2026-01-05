package ru.menshevva.demoapp.ui.admin.metadata;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import ru.menshevva.demoapp.dto.metadata.ReferenceFieldData;
import ru.menshevva.demoapp.ui.components.EditActionCallback;
import ru.menshevva.demoapp.ui.components.EditActionComponent;

public class MetaDataFieldEditDialog extends Dialog implements EditActionCallback {

    private final Binder<ReferenceFieldData> binder = new Binder<>();
    private EditActionCallback editActionCallback;
    private ReferenceFieldData editValue;

    public MetaDataFieldEditDialog() {
        var content = new VerticalLayout();
        var editView = new VerticalLayout();
        var fieldName = new TextField("Имя поля");
        binder.forField(fieldName)
                .bind(ReferenceFieldData::getFieldName, ReferenceFieldData::setFieldName);
        var fieldTitle = new TextField("Наименование поля");
        binder.forField(fieldTitle)
                .bind(ReferenceFieldData::getFieldTitle, ReferenceFieldData::setFieldTitle);
        var fieldLength = new TextField("Ширина поля");
        binder.forField(fieldLength)
                .bind(b -> b.getFieldLength() == null ? "" : b.getFieldLength().toString(),
                        (b, s) -> {
                            if (s == null || s.isEmpty()) {
                                b.setFieldLength(null);
                            } else {
                                b.setFieldLength(Integer.parseInt(s));
                            }
                        }
                );
        editView.add(fieldName, fieldTitle, fieldLength);
        content.setWidth(400, Unit.PIXELS);
        content.setHeight(400, Unit.PIXELS);
        content.add(editView, new EditActionComponent(this));
        content.setFlexGrow(1, editView);
        setCloseOnEsc(false);
        setCloseOnOutsideClick(false);
        setDraggable(true);
        add(content);

    }

    public void setValue(ReferenceFieldData value, EditActionCallback editActionCallback) {
        this.editActionCallback = editActionCallback;
        this.editValue = value == null ? new ReferenceFieldData() : value;
        binder.readBean(editValue);
        open();
    }

    @Override
    public void ok() {
        try {
            binder.writeBean(editValue);
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
