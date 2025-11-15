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
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import jakarta.annotation.security.PermitAll;
import lombok.extern.slf4j.Slf4j;
import ru.menshevva.demoapp.dto.UserListData;
import ru.menshevva.demoapp.report.userlistreports.UserListViewGroupReport;
import ru.menshevva.demoapp.service.users.UserSearchFilter;
import ru.menshevva.demoapp.service.users.UserSearchService;
import ru.menshevva.demoapp.ui.FrontendConsts;
import ru.menshevva.demoapp.ui.admin.AdminLayout;
import ru.menshevva.demoapp.ui.components.EditActionCallback;
import ru.menshevva.demoapp.ui.components.ParamsCallback;
import ru.menshevva.demoapp.ui.components.reporter.ReportGroupDialog;
import ru.menshevva.demoapp.utils.StrUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@PermitAll
@SpringComponent
@UIScope
@Tag("user-list-view")
@JsModule("./src/admin/users/user-list-view.ts")
@Route(value = FrontendConsts.PAGE_ADMIN_USER_LIST, layout = AdminLayout.class)
@Slf4j
public class UserListView extends LitTemplate implements CallbackDataProvider.FetchCallback<UserListData, UserSearchFilter>,
        CallbackDataProvider.CountCallback<UserListData, UserSearchFilter>, EditActionCallback, ParamsCallback  {

    private final UserSearchService userSearchService;
    private final UserEditDialog userEditDialog;
    private final UserListViewGroupReport userListViewGroupReport;


    @Id
    private TextField userId;

    @Id
    private TextField userLogin;

    @Id
    private TextField userName;

    @Id
    private Button addButton;

    @Id
    private Button editButton;

    @Id
    private Button deleteButton;

    @Id
    private Button reportButton;

    @Id
    private Button searchButton;

    @Id
    private Button clearButton;

    @Id
    private Grid<UserListData> dataGrid;

    private transient UserListData selectedItem;

    private final ConfigurableFilterDataProvider<UserListData, Void, UserSearchFilter> dataProvider;
    private final ReportGroupDialog reportGroupDialog;

    public UserListView(UserSearchService userSearchService,
                        UserEditDialog userEditDialog,
                        UserListViewGroupReport userListViewGroupReport,
                        ReportGroupDialog reportGroupDialog) {
        this.userListViewGroupReport = userListViewGroupReport;
        this.userSearchService = userSearchService;
        this.userEditDialog = userEditDialog;
        this.dataProvider = DataProvider
                .fromFilteringCallbacks(this, this)
                //.withConfigurableFilter(this::buildQueryFilter);
                .withConfigurableFilter();
        this.reportGroupDialog = reportGroupDialog;
        initGrid();
        initActionHandlers();
        selectedItem = null;
        setButtonVisible();
    }

    private void initActionHandlers() {
        addButton.addClickListener(this::addUser);
        editButton.addClickListener(this::editUser);
        deleteButton.addClickListener(this::deleteUser);
        searchButton.addClickListener(this::searchUsers);
        reportButton.addClickListener(this::runReports);
        clearButton.addClickListener(this::clearSearch);
        dataGrid.addSelectionListener(event -> {
            this.selectedItem = event.getFirstSelectedItem().orElse(null);
            setButtonVisible();
        });
    }

    private void runReports(ClickEvent<Button> buttonClickEvent) {
        reportGroupDialog.runReport(userListViewGroupReport.getGetReports(),this);
    }

    private void clearSearch(ClickEvent<Button> buttonClickEvent) {
        userId.clear();
        userLogin.clear();
        userName.clear();
        refresh();
    }

    private void refresh() {
        dataGrid.deselectAll();
        dataProvider.setFilter(buildQueryFilter());
        dataProvider.refreshAll();
    }

    private void searchUsers(ClickEvent<Button> buttonClickEvent) {
        refresh();
    }

    private void deleteUser(ClickEvent<Button> buttonClickEvent) {
        if (selectedItem != null) {
            userEditDialog.deleteUser(selectedItem.userId(), this);
        }
    }

    private void editUser(ClickEvent<Button> buttonClickEvent) {
        if (selectedItem != null) {
            userEditDialog.editUser(selectedItem.userId(), this);
        }
    }

    private void addUser(ClickEvent<Button> buttonClickEvent) {
        userEditDialog.editUser(null, this);
    }

    private UserSearchFilter buildQueryFilter() {
        return UserSearchFilter.builder()
                .userId(StrUtils.strToBigInt(userId.getValue()))
                .userLogin(userLogin.getValue())
                .userName(userName.getValue())
                .build();
    }

    private void initGrid() {
        dataGrid.addColumn(UserListData::userId)
                .setKey(UserSearchFilter.FILTER_USER_ID)
                .setHeader("Идентификатор")
                .setSortable(true);
        dataGrid.addColumn(UserListData::userLogin)
                .setKey(UserSearchFilter.FILTER_USER_LOGIN)
                .setHeader("Имя входа")
                .setSortable(true);
        dataGrid.addColumn(UserListData::userName)
                .setKey(UserSearchFilter.FILTER_USER_NAME)
                .setHeader("ФИО")
                .setSortable(true);
        dataGrid.setDataProvider(dataProvider);
    }

    @Override
    public Stream<UserListData> fetch(Query<UserListData, UserSearchFilter> query) {
        return userSearchService.fetch(query);
    }

    @Override
    public int count(Query<UserListData, UserSearchFilter> query) {
        return userSearchService.getCount(query);
    }

    @Override
    public void ok() {
        refresh();
    }

    private void setButtonVisible() {
        deleteButton.setEnabled(selectedItem != null);
        editButton.setEnabled(selectedItem != null);
    }

    @Override
    public Map<String, ?> getParams() {
        var params = new HashMap<String, Object>();
        params.put("selectedItem", selectedItem);
        return params;
    }
}
