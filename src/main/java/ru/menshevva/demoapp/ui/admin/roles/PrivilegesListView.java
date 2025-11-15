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
import com.vaadin.flow.spring.annotation.SpringComponent;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import ru.menshevva.demoapp.dto.privilege.PrivilegeListData;
import ru.menshevva.demoapp.service.roles.PrivilegeSearchFilter;
import ru.menshevva.demoapp.service.roles.RolePrivilegeSearchFilter;
import ru.menshevva.demoapp.service.roles.PrivilegeSearchService;

import ru.menshevva.demoapp.ui.FrontendConsts;
import ru.menshevva.demoapp.ui.admin.AdminLayout;
import ru.menshevva.demoapp.ui.components.EditActionCallback;
import ru.menshevva.demoapp.utils.StrUtils;

import java.util.Optional;
import java.util.stream.Stream;

@PermitAll
@SpringComponent
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Tag("privileges-list-view")
@JsModule("./src/admin/roles/privileges-list-view.ts")
@Route(value = FrontendConsts.PAGE_ADMIN_PRIVILEGE_LIST, layout = AdminLayout.class)
public class PrivilegesListView extends LitTemplate implements EditActionCallback,
        CallbackDataProvider.FetchCallback<PrivilegeListData, PrivilegeSearchFilter>, CallbackDataProvider.CountCallback<PrivilegeListData, PrivilegeSearchFilter> {

    private final PrivilegeSearchService privilegeSearchService;
    private final ConfigurableFilterDataProvider<PrivilegeListData, Void, PrivilegeSearchFilter> dataProvider;
    //
    private final PrivilegesEditDialog privilegesEditDialog;

    @Id
    private TextField privilegeId;

    @Id
    private TextField privilegeName;

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
    private Grid<PrivilegeListData> dataGrid;
    private Optional<PrivilegeListData> selectedItem;


    public PrivilegesListView(PrivilegeSearchService privilegeSearchService,
                              PrivilegesEditDialog privilegesEditDialog) {
        this.privilegeSearchService = privilegeSearchService;
        this.privilegesEditDialog = privilegesEditDialog;
        this.dataProvider = DataProvider
                .fromFilteringCallbacks(this, this)
                .withConfigurableFilter();
        initGrid();
        initActionHandlers();
        selectedItem = Optional.empty();
        setButtonVisible();
    }

    private void initActionHandlers() {
        addButton.addClickListener(this::addPrivilege);
        editButton.addClickListener(this::editPrivilege);
        deleteButton.addClickListener(this::deletePrivilege);
        searchButton.addClickListener(this::searchPrivileges);
        clearButton.addClickListener(this::clearSearch);
        dataGrid.addSelectionListener(event -> {
            this.selectedItem = event.getFirstSelectedItem();
            setButtonVisible();
        });
    }

    private void clearSearch(ClickEvent<Button> buttonClickEvent) {
        privilegeId.clear();
        privilegeName.clear();
        refresh();
    }

    private void setButtonVisible() {
        deleteButton.setEnabled(selectedItem.isPresent());
        editButton.setEnabled(selectedItem.isPresent());
    }

    private void searchPrivileges(ClickEvent<Button> buttonClickEvent) {
        refresh();
    }

    private void deletePrivilege(ClickEvent<Button> buttonClickEvent) {
        selectedItem.ifPresent(v ->
                privilegesEditDialog.deleteValue(v.getPrivilegeId(), this)
        );
    }

    private void editPrivilege(ClickEvent<Button> buttonClickEvent) {
        selectedItem.ifPresent(v ->
                privilegesEditDialog.editValue(v.getPrivilegeId(), this)
        );
    }

    private void addPrivilege(ClickEvent<Button> buttonClickEvent) {
        // Добавление новой привилегии
        privilegesEditDialog.editValue(null, this);
    }

    private void initGrid() {
        dataGrid.addColumn(PrivilegeListData::getPrivilegeId)
                .setKey(RolePrivilegeSearchFilter.FILTER_PRIVILEGE_ID)
                .setHeader("Идентификатор")
                .setSortable(true);
        dataGrid.addColumn(PrivilegeListData::getPrivilegeName)
                .setKey(RolePrivilegeSearchFilter.FILTER_PRIVILEGE_NAME)
                .setHeader("Наименование")
                .setSortable(true);
        dataGrid.addColumn(PrivilegeListData::getPrivilegeDescription)
                .setKey(RolePrivilegeSearchFilter.FILTER_PRIVILEGE_DESCRIPTION)
                .setHeader("Описание")
                .setSortable(true);
        dataGrid.setDataProvider(dataProvider);
    }

    private PrivilegeSearchFilter buildQueryFilter() {
        return PrivilegeSearchFilter.builder()
                .privilegeId(StrUtils.strToBigInt(privilegeId.getValue()))
                .privilegeName(privilegeName.getValue())
                .build();
    }

    private void refresh() {
        dataGrid.deselectAll();
        dataProvider.setFilter(buildQueryFilter());
        dataProvider.refreshAll();
    }


    @Override
    public Stream<PrivilegeListData> fetch(Query<PrivilegeListData, PrivilegeSearchFilter> query) {
        return privilegeSearchService.fetch(query);
    }

    @Override
    public int count(Query<PrivilegeListData, PrivilegeSearchFilter> query) {
        return privilegeSearchService.getCount(query);
    }

    @Override
    public void ok() {
        refresh();
    }
}
