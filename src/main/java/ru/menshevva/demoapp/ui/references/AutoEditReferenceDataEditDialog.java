package ru.menshevva.demoapp.ui.references;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import ru.menshevva.demoapp.dto.metadata.ReferenceData;
import ru.menshevva.demoapp.dto.metadata.ReferenceFieldData;
import ru.menshevva.demoapp.service.metadata.ReferenceDataCRUDService;
import ru.menshevva.demoapp.ui.components.EditActionCallback;
import ru.menshevva.demoapp.ui.components.EditActionComponent;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

@SpringComponent
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class AutoEditReferenceDataEditDialog extends Dialog implements EditActionCallback {

    private final VerticalLayout editView;
    private final ReferenceDataCRUDService crudService;
    private EditActionCallback editActionCallback;
    private Map<String, Object> oldValue;
    private Binder<Map<String, Object>> binder = new Binder<>();
    private ReferenceData referenceData;

    public AutoEditReferenceDataEditDialog(ReferenceDataCRUDService crudService) {
        this.crudService = crudService;
        var content = new VerticalLayout();
        var editAction = new EditActionComponent(this);
        this.editView = new VerticalLayout();
        content.add(editView, editAction);
        add(content);
        setCloseOnEsc(false);
        setCloseOnOutsideClick(false);
        setDraggable(true);
    }

    public void editValue(ReferenceData referenceData, Map<String, ?> value, EditActionCallback editActionCallback) {
        if (referenceData == null) {
            return;
        }
        this.editActionCallback = editActionCallback;

        if (!referenceData.equals(this.referenceData)) {
            this.referenceData = referenceData;
            editView.removeAll();
            referenceData.getMetaDataFieldsList()
                    .stream()
                    .sorted(Comparator.comparingInt(ReferenceFieldData::getFieldOrder))
                    .forEach(f -> {
                        var editField = new TextField(f.getFieldTitle());
                        binder.forField(editField)
                                .bind((b) -> {
                                    var o = b.get(f.getFieldName());
                                    if (o == null) {
                                        return "";
                                    }
                                    return switch (o) {
                                        case String stringValue -> stringValue;
                                        case Integer integerValue -> Integer.toString(integerValue);
                                        case Long longValue -> Long.toString(longValue);
                                        case Double doubleValue -> Double.toString(doubleValue);
                                        case Boolean booleanValue -> Boolean.toString(booleanValue);
                                        case LocalDate localDateValue -> localDateValue.toString();
                                        case LocalDateTime localDateTimeValue -> localDateTimeValue.toString();
                                        case BigDecimal bigDecimalValue -> bigDecimalValue.toString();
                                        case Byte byteValue -> byteValue.toString();
                                        case Float floatValue -> floatValue.toString();
                                        default -> "";
                                    };
                                }, (b, t) -> {
                                    Object o = null;
                                    if (t != null && !t.isEmpty()) {
                                        o = switch (f.getFieldType()) {
                                            case FIELD_TYPE_STRING -> t;
                                            case FIELD_TYPE_INTEGER -> Integer.parseInt(t);
                                            case FIELD_TYPE_DOUBLE -> Double.parseDouble(t);
                                            case FIELD_TYPE_LONG -> Long.parseLong(t);
                                            case FIELD_TYPE_BOOLEAN -> Boolean.parseBoolean(t);
                                            case FIELD_TYPE_DATE -> LocalDate.parse(t);
                                            case FIELD_TYPE_TIMESTAMP -> LocalDateTime.parse(t);
                                            case FIELD_TYPE_BIGDECIMAL -> BigDecimal.valueOf(Double.parseDouble(t));
                                            case FIELD_TYPE_BYTE -> Byte.parseByte(t);
                                            case FIELD_TYPE_FLOAT -> Float.parseFloat(t);
                                            case FIELD_TYPE_SHORT -> Short.parseShort(t);
                                            case FIELD_TYPE_CHAR -> t.charAt(0);
                                        };
                                    }
                                    b.put(f.getFieldName(), o);
                                }

                        );
                        editView.add(editField);
                    });
        }

        if (value != null) {
            this.oldValue = new HashMap<>();
            this.oldValue.putAll(value);
            binder.readBean(this.oldValue);
        } else {
            this.oldValue = null;
            binder.readBean(Collections.emptyMap());
        }
        open();
    }

    public void deleteValue(ReferenceData referenceData, Map<String, ?> value, EditActionCallback editActionCallback) {
        if (referenceData == null || value == null) {
            return;
        }
        crudService.delete(referenceData, value);
        if (editActionCallback != null) {
            editActionCallback.ok();
        }
    }

    @Override
    public void ok() {
        try {
            var newValue = new HashMap<String, Object>();
            binder.writeBean(newValue);
            crudService.save(referenceData, newValue, oldValue);
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
