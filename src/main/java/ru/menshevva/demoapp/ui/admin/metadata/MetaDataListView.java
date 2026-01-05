package ru.menshevva.demoapp.ui.admin.metadata;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import jakarta.annotation.security.PermitAll;
import ru.menshevva.demoapp.dto.metadata.ReferenceData;
import ru.menshevva.demoapp.service.metadata.ReferenceFilter;
import ru.menshevva.demoapp.service.metadata.ReferenceSearchService;
import ru.menshevva.demoapp.ui.FrontendConsts;
import ru.menshevva.demoapp.ui.admin.AdminLayout;
import ru.menshevva.demoapp.ui.components.EditActionCallback;

@PermitAll
@SpringComponent
@UIScope
@Route(value = FrontendConsts.PAGE_ADMIN_METADATA_LIST, layout = AdminLayout.class)
public class MetaDataListView extends VerticalLayout implements EditActionCallback {

    private final ReferenceSearchService searchService;
    private final ConfigurableFilterDataProvider<ReferenceData, Void, ReferenceFilter> dataProvider;
    private final MetaDataEditDialog editDialog;
    private Grid<ReferenceData> dataGrid;
    private HorizontalLayout actionPanel;

    public MetaDataListView(ReferenceSearchService searchService, MetaDataEditDialog editDialog) {
        this.searchService = searchService;
        this.editDialog = editDialog;
        this.dataProvider = DataProvider
                .fromFilteringCallbacks(searchService::fetch,
                        searchService::count)
                .withConfigurableFilter();
        createGrid(dataProvider);
        createActionPanel();
        add(actionPanel, dataGrid);
    }

    private void createActionPanel() {
        this.actionPanel = new HorizontalLayout();
        var leftPanel = new HorizontalLayout();
        var addButton = new Button("Добавить");
        addButton.addClickListener(this::addAction);
        var editButton = new Button("Изменить");
        var deleteButton = new Button("Удалить");
        leftPanel.add(addButton, editButton, deleteButton);
        var rightPanel = new HorizontalLayout();
        var searchButton = new Button("Найти");
        var clearButton = new Button("Очистить");
        rightPanel.add(searchButton, clearButton);
        rightPanel.setJustifyContentMode(JustifyContentMode.END);
        actionPanel.add(leftPanel, rightPanel);
        actionPanel.setFlexGrow(1, rightPanel);
        actionPanel.setWidthFull();
    }

    private void addAction(ClickEvent<Button> buttonClickEvent) {
        editDialog.editValue(null, this);
    }

    private void createGrid(ConfigurableFilterDataProvider<ReferenceData, Void, ReferenceFilter> dataProvider) {
        this.dataGrid = new Grid<>();
        dataGrid.addColumn(ReferenceData::getSchemaName)
                .setHeader("Схема");
        dataGrid.addColumn(ReferenceData::getSchemaName)
                .setHeader("Таблица");
        dataGrid.setDataProvider(dataProvider);
    }

    @Override
    public void ok() {
        refresh();
    }

    private void refresh() {
        dataGrid.deselectAll();
        //dataProvider.setFilter(buildQueryFilter());
        dataProvider.refreshAll();
    }
}
