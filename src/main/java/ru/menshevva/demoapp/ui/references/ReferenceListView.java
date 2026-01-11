package ru.menshevva.demoapp.ui.references;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import lombok.extern.slf4j.Slf4j;
import ru.menshevva.demoapp.dto.metadata.ReferenceData;
import ru.menshevva.demoapp.service.metadata.ReferenceFilter;
import ru.menshevva.demoapp.service.metadata.ReferenceSearchService;
import ru.menshevva.demoapp.ui.FrontendConsts;
import ru.menshevva.demoapp.ui.MainMenuLayout;
import ru.menshevva.demoapp.ui.components.AppDataGrid;
import ru.menshevva.demoapp.ui.components.EditActionCallback;

import java.util.Map;

import static ru.menshevva.demoapp.ui.FrontendConsts.TITLE_PAGE_REFERENCES;

@PermitAll
@PageTitle(TITLE_PAGE_REFERENCES)
@Route(value = FrontendConsts.PAGE_REFERENCES, layout = MainMenuLayout.class)
@Slf4j
public class ReferenceListView extends HorizontalLayout implements EditActionCallback {


    private final AutoEditReferenceDataEditDialog autoEditReferenceDataEditDialog;
    private final Button editReferenceButton;
    private final Button deleteReferenceButton;
    private final AppDataGrid dataGrid;

    private Map<String, ?> selectedReferenceData;

    private ReferenceData referenceData;


    public ReferenceListView(ReferenceSearchService searchService,
                             AppDataGrid appDataGrid,
                             AutoEditReferenceDataEditDialog autoEditReferenceDataEditDialog) {
        this.autoEditReferenceDataEditDialog = autoEditReferenceDataEditDialog;
        this.dataGrid = appDataGrid;

        var dataProvider = DataProvider
                .fromFilteringCallbacks(searchService::fetch, searchService::count)
                .withConfigurableFilter();
        var metaDataGrid = new Grid<ReferenceData>();
        var nameColumn = metaDataGrid.addColumn(ReferenceData::getReferenceName)
                .setKey(ReferenceFilter.FILTER_REFERENCE_NAME)
                //.setHeader("Название")
                .setAutoWidth(true)
                .setSortable(true);
        //
        var filterRow = metaDataGrid.appendHeaderRow();
        var filterLayout = new HorizontalLayout();
        TextField referenceNameFilter = new TextField();
        referenceNameFilter.setPlaceholder("Фильтр по названию");
        referenceNameFilter.setWidthFull();
        referenceNameFilter.setClearButtonVisible(true);
        var filterSearchReferenceButton = new Button("Ок");
        filterSearchReferenceButton.addClickListener(event -> {
                    metaDataGrid.deselectAll();
                    dataProvider.setFilter(ReferenceFilter.builder().referenceName(referenceNameFilter.getValue()).build());
                    dataProvider.refreshAll();
                }
        );
        filterLayout.add(referenceNameFilter, filterSearchReferenceButton);
        filterLayout.setWidthFull();
        filterLayout.setFlexGrow(1, referenceNameFilter);
        filterRow.getCell(nameColumn).setComponent(filterLayout);

        metaDataGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        metaDataGrid.addSelectionListener(event -> {
            referenceData = event.getFirstSelectedItem().orElse(null);
            appDataGrid.setReferenceData(referenceData);
        });
        metaDataGrid.setDataProvider(dataProvider);
        var leftBlock = new VerticalLayout(metaDataGrid);
        leftBlock.setWidth(300, Unit.PIXELS);
        //
        var referenceDataAction = new HorizontalLayout();
        referenceDataAction.setWidthFull();
        Button addReferenceButton = new Button("Добавить");
        addReferenceButton.addClickListener(this::addReferenceData);
        this.editReferenceButton = new Button("Изменить");
        editReferenceButton.addClickListener(this::editReferenceData);
        this.deleteReferenceButton = new Button("Удалить");
        deleteReferenceButton.addClickListener(this::deleteReferenceData);
        var leftReferenceDataActionBlock = new HorizontalLayout();
        leftReferenceDataActionBlock.add(addReferenceButton, editReferenceButton, deleteReferenceButton);
        var searchReferenceButton = new Button("Найти");
        searchReferenceButton.addClickListener(event -> ok());
        var clearReferenceButton = new Button("Очистить");
        clearReferenceButton.addClickListener(event -> dataGrid.clearFilter());

        var rightReferenceDataActionBlock = new HorizontalLayout(searchReferenceButton, clearReferenceButton);
        rightReferenceDataActionBlock.setJustifyContentMode(JustifyContentMode.END);
        referenceDataAction.add(leftReferenceDataActionBlock, rightReferenceDataActionBlock);
        referenceDataAction.setWidthFull();
        referenceDataAction.setFlexGrow(1, rightReferenceDataActionBlock);
        //

        appDataGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        appDataGrid.addSelectionListener(event ->
                setSelectedReferenceData(event.getFirstSelectedItem().orElse(null))
        );
        setSelectedReferenceData(null);
        var rightBlock = new VerticalLayout(referenceDataAction, appDataGrid);
        rightBlock.setSizeFull();
        rightBlock.setFlexGrow(1, appDataGrid);
        add(leftBlock, rightBlock);
        setFlexGrow(1, rightBlock);
        setSizeFull();
    }


    private void setSelectedReferenceData(Map<String, ?> value) {
        this.selectedReferenceData = value;
        editReferenceButton.setEnabled(selectedReferenceData != null);
        deleteReferenceButton.setEnabled(selectedReferenceData != null);

    }

    private void deleteReferenceData(ClickEvent<Button> buttonClickEvent) {
        autoEditReferenceDataEditDialog.deleteValue(referenceData, selectedReferenceData, this);
    }

    private void editReferenceData(ClickEvent<Button> buttonClickEvent) {
        autoEditReferenceDataEditDialog.editValue(referenceData, selectedReferenceData, this);
    }

    private void addReferenceData(ClickEvent<Button> buttonClickEvent) {
        autoEditReferenceDataEditDialog.editValue(referenceData, null, this);
    }


    @Override
    public void ok() {
        this.dataGrid.refresh();
    }

}
