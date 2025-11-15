package ru.menshevva.demoapp.ui.admin.users;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import ru.menshevva.demoapp.dto.role.RoleListData;
import ru.menshevva.demoapp.dto.userrole.UserRoleData;
import ru.menshevva.demoapp.service.roles.RoleSearchFilter;
import ru.menshevva.demoapp.service.roles.RoleSearchService;

import java.math.BigInteger;

@SpringComponent
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Tag("user-role-edit-view")
@JsModule("./src/admin/users/user-role-edit-view.ts")
public class UserRoleEditView extends LitTemplate {

    private final RoleSearchService roleSearchService;

    @Id
    private ComboBox<RoleListData> roleComboBox;

    private final Binder<UserRoleData> binder = new Binder<>(UserRoleData.class);

    public UserRoleEditView(RoleSearchService roleSearchService) {
        this.roleSearchService = roleSearchService;
        binder.forField(roleComboBox)
                .asRequired("Выберите роль")
                .bind(v -> findRole(v.getRoleId()),
                        (t, b) -> t.setRoleId(b.getRoleId()));
        roleComboBox.setItemLabelGenerator(RoleListData::getRoleName);
        roleComboBox.setDataProvider((filter, offset, limit) -> roleSearchService.fetch(new Query<>(
                        offset, limit, null, null,
                        buildFilter(filter)))
                .map(this::roleMap), (filter) -> roleSearchService.getCount(new Query<>(buildFilter(filter))
                )
        );
    }

    private RoleListData roleMap(RoleListData roleListData) {
        return RoleListData
                .builder()
                .roleId(roleListData.getRoleId())
                .roleName("%s - %s".formatted(roleListData.getRoleName(), roleListData.getRoleDescription()))
                .build();
    }

    private RoleSearchFilter buildFilter(String filter) {
        return RoleSearchFilter.builder()
                .roleLabel(filter)
                .build();
    }

    private RoleListData findRole(BigInteger roleId) {
        if (roleId == null) {
            return null;
        }
        return roleSearchService.fetch(new Query<>(RoleSearchFilter.builder().build()))
                .map(this::roleMap)
                .findFirst()
                .orElse(null);
    }

    public void setValue(UserRoleData value) {
        binder.readBean(value);
    }

    public void getValue(UserRoleData value) throws ValidationException {
        binder.writeBean(value);
    }
}
