package ru.menshevva.demoapp.ui.admin.users;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import ru.menshevva.demoapp.security.dto.UserData;

@Tag("user-edit-view")
@JsModule("./src/admin/users/user-edit-view.ts")
public class UserEditView extends LitTemplate {

    @Id
    private TextField userLogin;
    @Id
    private TextField userName;


    private final Binder<UserData> binder = new Binder<>();

    public UserEditView() {
        binder.forField(userLogin)
                .asRequired("Заполните логин")
                .bind(UserData::getUserLogin, UserData::setUserLogin);
        binder.forField(userName)
                .asRequired("Заполните имя")
                .bind(UserData::getUserName, UserData::setUserName);
    }

    public void setValue(UserData userData) {
        binder.readBean(userData);
    }

    public void getValue(UserData userData) throws ValidationException {
        binder.writeBean(userData);
    }
}
