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
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import ru.menshevva.demoapp.dto.role.RoleListData;
import ru.menshevva.demoapp.service.roles.RoleSearchFilter;
import ru.menshevva.demoapp.service.roles.RoleSearchService;
import ru.menshevva.demoapp.ui.FrontendConsts;
import ru.menshevva.demoapp.ui.admin.AdminLayout;
import ru.menshevva.demoapp.ui.components.EditActionCallback;
import ru.menshevva.demoapp.utils.StrUtils;

import java.util.Optional;
import java.util.stream.Stream;

@AnonymousAllowed
@SpringComponent
@UIScope
@Tag("role-list-view")
@JsModule("./src/admin/roles/role-list-view.ts")
@Route(value = FrontendConsts.PAGE_ADMIN_ROLE_LIST, layout = AdminLayout.class)
public class RoleListView extends LitTemplate implements CallbackDataProvider.FetchCallback<RoleListData, RoleSearchFilter>,
        CallbackDataProvider.CountCallback<RoleListData, RoleSearchFilter>, EditActionCallback {

    private final RoleSearchService roleSearchService;
    private final RoleEditDialog roleEditDialog;

    @Id
    private TextField roleId;

    @Id
    private TextField roleName;

    @Id
    private Button addButton;

    @Id
    private Button editButton;

    @Id
    private Button deleteButton;

    @Id
    private Button searchButton;

    @Id
    private Button clearButton;

    @Id
    private Grid<RoleListData> dataGrid;

    private Optional<RoleListData> selectedItem;
    private final ConfigurableFilterDataProvider<RoleListData, Void, RoleSearchFilter> dataProvider;

    public RoleListView(RoleSearchService roleSearchService,
                        RoleEditDialog roleEditDialog) {
        this.roleSearchService = roleSearchService;
        this.roleEditDialog = roleEditDialog;
        this.dataProvider = DataProvider
                .fromFilteringCallbacks(this, this)
                .withConfigurableFilter();
        initGrid();
        initActionHandlers();
        selectedItem = Optional.empty();
        setButtonVisible();
    }

    private void initGrid() {
        dataGrid.addColumn(RoleListData::getRoleId)
                .setKey(RoleSearchFilter.FILTER_ROLE_ID)
                .setHeader("Идентификатор")
                .setSortable(true);
        dataGrid.addColumn(RoleListData::getRoleName)
                .setKey(RoleSearchFilter.FILTER_ROLE_NAME)
                .setHeader("Наименование")
                .setSortable(true);
        dataGrid.addColumn(RoleListData::getRoleDescription)
                .setKey(RoleSearchFilter.FILTER_ROLE_DESCRIPTION)
                .setHeader("Описание")
                .setSortable(true);
        dataGrid.setDataProvider(dataProvider);
    }

    private void initActionHandlers() {
        addButton.addClickListener(this::addRole);
        editButton.addClickListener(this::editRole);
        deleteButton.addClickListener(this::deleteRole);
        searchButton.addClickListener(this::searchRole);
        clearButton.addClickListener(this::clearSearch);
        dataGrid.addSelectionListener(event -> {
            this.selectedItem = event.getFirstSelectedItem();
            setButtonVisible();
        });
    }

    private void clearSearch(ClickEvent<Button> buttonClickEvent) {
        roleId.clear();
        roleName.clear();
        refresh();
    }

    private void searchRole(ClickEvent<Button> buttonClickEvent) {
        refresh();
    }

    private void deleteRole(ClickEvent<Button> buttonClickEvent) {
        selectedItem.ifPresent(roleListData ->
                roleEditDialog.deleteRole(roleListData.getRoleId(), this)
        );
    }

    private void editRole(ClickEvent<Button> buttonClickEvent) {
        selectedItem.ifPresent(roleListData ->
                roleEditDialog.editRole(roleListData.getRoleId(), this)
        );

    }

    private void addRole(ClickEvent<Button> buttonClickEvent) {
        roleEditDialog.editRole(null, this);
    }

    @Override
    public Stream<RoleListData> fetch(Query<RoleListData, RoleSearchFilter> query) {
        return roleSearchService.fetch(query);
    }


    @Override
    public int count(Query<RoleListData, RoleSearchFilter> query) {
        return roleSearchService.getCount(query);
    }

    private RoleSearchFilter buildQueryFilter() {
        return RoleSearchFilter.builder()
                .roleId(StrUtils.strToBigInt(roleId.getValue()))
                .roleName(roleName.getValue())
                .build();
    }

    private void setButtonVisible() {
        deleteButton.setEnabled(selectedItem.isPresent());
        editButton.setEnabled(selectedItem.isPresent());
    }

    private void refresh() {
        dataGrid.deselectAll();
        dataProvider.setFilter(buildQueryFilter());
        dataProvider.refreshAll();
    }

    @Override
    public void ok() {
        refresh();
    }

}
