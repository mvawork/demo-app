package ru.menshevva.demoapp.ui.admin.roles;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import ru.menshevva.demoapp.dto.roleprivilege.RolePrivilegeData;
import ru.menshevva.demoapp.dto.roleprivilege.RolePrivilegeListData;
import ru.menshevva.demoapp.service.roles.RolePrivilegeSearchFilter;
import ru.menshevva.demoapp.service.roles.RolePrivilegeSearchService;
import ru.menshevva.demoapp.ui.components.EditActionCallback;
import ru.menshevva.demoapp.utils.StrUtils;

import java.math.BigInteger;
import java.util.Optional;
import java.util.stream.Stream;

@SpringComponent
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Tag("role-privileges-list-view")
@JsModule("./src/admin/roles/role-privileges-list-view.ts")
public class RolePrivilegesListView extends LitTemplate implements EditActionCallback,
        CallbackDataProvider.FetchCallback<RolePrivilegeListData, RolePrivilegeSearchFilter>, CallbackDataProvider.CountCallback<RolePrivilegeListData, RolePrivilegeSearchFilter> {

    private final RolePrivilegeSearchService searchService;
    private final ConfigurableFilterDataProvider<RolePrivilegeListData, Void, RolePrivilegeSearchFilter> dataProvider;
    //
    private final RolePrivilegesEditDialog editDialog;

    @Id
    private TextField privilegeId;

    @Id
    private TextField privilegeName;

    @Id
    private Button addButton;

    @Id
    private Button deleteButton;

    @Id
    private Button searchButton;

    @Id
    private Button clearButton;


    @Id
    private Grid<RolePrivilegeListData> dataGrid;

    private Optional<RolePrivilegeListData> selectedItem;

    private BigInteger roleId;

    public void setRoleId(BigInteger roleId) {
        this.roleId = roleId;
        refresh();
    }

    public RolePrivilegesListView(RolePrivilegeSearchService searchService,
                                  RolePrivilegesEditDialog rolePrivilegesEditDialog) {
        this.searchService = searchService;
        this.editDialog = rolePrivilegesEditDialog;
        this.dataProvider = DataProvider
                .fromFilteringCallbacks(this, this)
                .withConfigurableFilter();
        initGrid();
        initActionHandlers();
        selectedItem = Optional.empty();
        setButtonVisible();
    }

    private void initActionHandlers() {
        addButton.addClickListener(this::addRolePrivilege);
        deleteButton.addClickListener(this::deleteRolePrivilege);
        searchButton.addClickListener(this::searchRolePrivileges);
        clearButton.addClickListener(this::clearSearch);
        dataGrid.addSelectionListener(event -> {
            this.selectedItem = event.getFirstSelectedItem();
            setButtonVisible();
        });
    }

    private void setButtonEnabled() {
        deleteButton.setEnabled(this.selectedItem != null);
    }

    private void clearSearch(ClickEvent<Button> buttonClickEvent) {
        privilegeId.clear();
        privilegeName.clear();
        refresh();
    }

    private void searchRolePrivileges(ClickEvent<Button> buttonClickEvent) {
        refresh();
    }

    private void deleteRolePrivilege(ClickEvent<Button> buttonClickEvent) {
        selectedItem.ifPresent(RolePrivilegeListData ->
                editDialog.delete(RolePrivilegeData.builder()
                        .roleId(RolePrivilegeListData.getRoleId())
                        .privilegeId(RolePrivilegeListData.getPrivilegeId())
                        .build(), this)
        );
    }

    private void addRolePrivilege(ClickEvent<Button> buttonClickEvent) {
        editDialog.edit(RolePrivilegeData.builder().roleId(roleId).build(), this);
    }

    private void initGrid() {
        dataGrid.addColumn(RolePrivilegeListData::getPrivilegeId)
                .setKey(RolePrivilegeSearchFilter.FILTER_PRIVILEGE_ID)
                .setHeader("Идентификатор")
                .setSortable(true);
        dataGrid.addColumn(RolePrivilegeListData::getPrivilegeName)
                .setKey(RolePrivilegeSearchFilter.FILTER_PRIVILEGE_NAME)
                .setHeader("Наименование")
                .setSortable(true);
        dataGrid.addColumn(RolePrivilegeListData::getPrivilegeDescription)
                .setKey(RolePrivilegeSearchFilter.FILTER_PRIVILEGE_DESCRIPTION)
                .setHeader("Описание")
                .setSortable(true);
        dataGrid.setDataProvider(dataProvider);
    }

    private void refresh() {
        dataGrid.deselectAll();
        dataProvider.setFilter(buildQueryFilter());
        dataProvider.refreshAll();
    }

    private void setButtonVisible() {
        deleteButton.setEnabled(selectedItem.isPresent());
    }

    private RolePrivilegeSearchFilter buildQueryFilter() {
        return RolePrivilegeSearchFilter.builder()
                .roleId(roleId)
                .privilegeId(StrUtils.strToBigInt(privilegeId.getValue()))
                .privilegeName(privilegeName.getValue())
                .build();
    }


    @Override
    public int count(Query<RolePrivilegeListData, RolePrivilegeSearchFilter> query) {
        return searchService.getCount(query);
    }

    @Override
    public Stream<RolePrivilegeListData> fetch(Query<RolePrivilegeListData, RolePrivilegeSearchFilter> query) {
        return searchService.fetch(query);
    }

    @Override
    public void ok() {
        refresh();
    }
}
