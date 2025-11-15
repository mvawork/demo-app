package ru.menshevva.demoapp.ui.admin.users;

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
import ru.menshevva.demoapp.dto.userrole.UserRoleData;
import ru.menshevva.demoapp.dto.userrole.UserRoleListData;
import ru.menshevva.demoapp.service.users.UserRoleSearchFilter;
import ru.menshevva.demoapp.service.users.UserRoleSearchService;
import ru.menshevva.demoapp.ui.components.EditActionCallback;
import ru.menshevva.demoapp.utils.StrUtils;

import java.math.BigInteger;
import java.util.Optional;
import java.util.stream.Stream;


@SpringComponent
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Tag("user-role-list-view")
@JsModule("./src/admin/users/user-role-list-view.ts")
public class UserRoleListView extends LitTemplate implements CallbackDataProvider.FetchCallback<UserRoleListData, UserRoleSearchFilter>,
        CallbackDataProvider.CountCallback<UserRoleListData, UserRoleSearchFilter>, EditActionCallback {

    private final UserRoleSearchService userRoleSearchService;
    private final ConfigurableFilterDataProvider<UserRoleListData, Void, UserRoleSearchFilter> dataProvider;
    private final UserRoleEditDialog editDialog;

    @Id
    private Grid<UserRoleListData> dataGrid;

    @Id
    private Button addButton;

    @Id
    private Button deleteButton;

    @Id
    private Button searchButton;

    @Id
    private Button clearButton;

    @Id
    private TextField roleId;

    @Id
    private TextField roleName;

    @Id
    private TextField roleDescription;


    private Optional<UserRoleListData> selectedItem;


    public UserRoleListView(UserRoleSearchService userRoleSearchService,
                            UserRoleEditDialog editDialog) {
        this.userRoleSearchService = userRoleSearchService;
        this.editDialog = editDialog;
        this.dataProvider = DataProvider
                .fromFilteringCallbacks(this, this)
                .withConfigurableFilter();
        initGrid();
        initActionHandlers();
        selectedItem = Optional.empty();
        setButtonVisible();
    }

    private void initActionHandlers() {
        addButton.addClickListener(this::addUserRole);
        deleteButton.addClickListener(this::deleteUserRole);
        searchButton.addClickListener(this::searchUserRole);
        clearButton.addClickListener(this::clearSearch);
        dataGrid.addSelectionListener(event -> {
            this.selectedItem = event.getFirstSelectedItem();
            setButtonVisible();
        });

    }

    private void setButtonVisible() {
        deleteButton.setEnabled(selectedItem.isPresent());
    }

    private void clearSearch(ClickEvent<Button> buttonClickEvent) {
        roleId.clear();
        roleName.clear();
        roleDescription.clear();
        refresh();
    }

    private void searchUserRole(ClickEvent<Button> buttonClickEvent) {
        refresh();
    }

    private void deleteUserRole(ClickEvent<Button> buttonClickEvent) {
        selectedItem.ifPresent(UserRoleListData ->
                editDialog.delete(UserRoleData.builder()
                        .userId(userId)
                        .roleId(UserRoleListData.getRoleId())
                        .build(), this)
        );
    }

    private void addUserRole(ClickEvent<Button> event) {
        editDialog.addUserRole(userId, this);
    }

    private void initGrid() {
        dataGrid.addColumn(UserRoleListData::getRoleId)
                .setKey(UserRoleSearchFilter.FILTER_ROLE_ID)
                .setHeader("Идентификатор")
                .setSortable(true);
        dataGrid.addColumn(UserRoleListData::getRoleName)
                .setKey(UserRoleSearchFilter.FILTER_ROLE_NAME)
                .setHeader("Название роли")
                .setSortable(true);
        dataGrid.addColumn(UserRoleListData::getRoleDescription)
                .setKey(UserRoleSearchFilter.FILTER_ROLE_DESCRIPTION)
                .setHeader("Описание роли")
                .setSortable(true);
        dataGrid.setDataProvider(dataProvider);

    }

    private void refresh() {
        dataGrid.deselectAll();
        dataProvider.setFilter(buildQueryFilter());
        dataProvider.refreshAll();
    }


    private UserRoleSearchFilter buildQueryFilter() {
        return UserRoleSearchFilter.builder()
                .userId(userId)
                .roleId(StrUtils.strToBigInt(roleId.getValue()))
                .roleName(roleName.getValue())
                .roleDescription(roleDescription.getValue())
                .build();
    }

    @Override
    public int count(Query<UserRoleListData, UserRoleSearchFilter> query) {
        return userRoleSearchService.getCount(query);
    }

    @Override
    public Stream<UserRoleListData> fetch(Query<UserRoleListData, UserRoleSearchFilter> query) {
        return userRoleSearchService.fetch(query);
    }

    @Override
    public void ok() {
        refresh();
    }


    private BigInteger userId;

    public void setUserId(BigInteger userId) {
        this.userId = userId;
        refresh();
    }
}
