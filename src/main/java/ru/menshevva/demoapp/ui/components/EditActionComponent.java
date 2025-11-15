package ru.menshevva.demoapp.ui.components;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.template.Id;

@Tag("edit-action-component")
@JsModule("./src/components/edit-action-component.ts")
public class EditActionComponent extends LitTemplate {

    @Id
    private Button saveButton;

    @Id
    private Button cancelButton;

    public EditActionComponent(EditActionCallback editActionCallback) {
        if (editActionCallback != null) {
            saveButton.addClickListener(event -> {
                editActionCallback.ok();
            });
            cancelButton.addClickListener(event -> {
                editActionCallback.cancel();
            });
        }
    }


}
