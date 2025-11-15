package ru.menshevva.demoapp.ui.admin.roles;

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
import ru.menshevva.demoapp.dto.privilege.PrivilegeListData;
import ru.menshevva.demoapp.dto.roleprivilege.RolePrivilegeData;
import ru.menshevva.demoapp.service.roles.PrivilegeSearchFilter;
import ru.menshevva.demoapp.service.roles.PrivilegeSearchService;

import java.math.BigInteger;

@SpringComponent
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Tag("role-privilege-edit-view")
@JsModule("./src/admin/roles/role-privilege-edit-view.ts")
public class RolePrivilegesEditView extends LitTemplate {

    private final PrivilegeSearchService privilegeSearchService;

    @Id
    private ComboBox<PrivilegeListData> privilegeComboBox;

    private final Binder<RolePrivilegeData> binder = new Binder<>(RolePrivilegeData.class);

    public RolePrivilegesEditView(PrivilegeSearchService privilegeSearchService) {
        this.privilegeSearchService = privilegeSearchService;
        binder.forField(privilegeComboBox)
                .asRequired("Выберите привилегию")
                .bind(v -> findPrivilege(v.getPrivilegeId()),
                        (t, b) -> t.setPrivilegeId(b.getPrivilegeId()));
        privilegeComboBox.setItemLabelGenerator(PrivilegeListData::getPrivilegeName);
        privilegeComboBox.setDataProvider((filter, offset, limit) -> {
                    return privilegeSearchService.fetch(new Query<>(
                                    offset, limit, null, null,
                                    buildFilter(filter)))
                            .map(this::privilegeMap);
                }, (filter) -> privilegeSearchService.getCount(new Query<>(buildFilter(filter))
                )
        );
    }

    private PrivilegeSearchFilter buildFilter(String filter) {
        return PrivilegeSearchFilter.builder()
                .privilegeLabel(filter)
                .build();
    }

    private PrivilegeListData findPrivilege(BigInteger privilegeId) {
        if (privilegeId == null) {
            return null;
        }
        return privilegeSearchService.fetch(new Query<>(PrivilegeSearchFilter.builder().build()))
                .map(this::privilegeMap)
                .findFirst()
                .orElse(null);
    }

    private PrivilegeListData privilegeMap(PrivilegeListData v) {
        return PrivilegeListData
                .builder()
                .privilegeId(v.getPrivilegeId())
                .privilegeName("%s - %s".formatted(v.getPrivilegeName(), v.getPrivilegeDescription()))
                .build();

    }

    public void getValue(RolePrivilegeData value) throws ValidationException {
        binder.writeBean(value);
    }

    public void setValue(RolePrivilegeData value) {
        binder.readBean(value);

    }
}
