package ru.menshevva.demoapp.ui.references;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import ru.menshevva.demoapp.dto.metadata.ReferenceData;
import ru.menshevva.demoapp.dto.metadata.ReferenceFieldData;
import ru.menshevva.demoapp.service.metadata.ReferenceDataSearchService;
import ru.menshevva.demoapp.service.metadata.ReferenceFilter;
import ru.menshevva.demoapp.service.metadata.ReferenceSearchService;
import ru.menshevva.demoapp.ui.FrontendConsts;
import ru.menshevva.demoapp.ui.MainMenuLayout;
import ru.menshevva.demoapp.ui.components.EditActionCallback;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Map;

import static ru.menshevva.demoapp.ui.FrontendConsts.TITLE_PAGE_REFERENCES;

@PermitAll
@PageTitle(TITLE_PAGE_REFERENCES)
@Route(value = FrontendConsts.PAGE_REFERENCES, layout = MainMenuLayout.class)
public class ReferenceListView extends HorizontalLayout implements EditActionCallback {

    private final ReferenceSearchService searchService;
    private final ConfigurableFilterDataProvider<ReferenceData, Void, ReferenceFilter> dataProvider;
    private final Grid<Map<String, ?>> referenceDataGrid;
    private final ReferenceDataSearchService referenceDataSearchService;
    private final ConfigurableFilterDataProvider<Map<String, ?>, Void, Map<String, ?>> referenceDataProvider;
    private final AutoEditReferenceDataEditDialog autoEditReferenceDataEditDialog;
    private  Map<String, ?> selectedReferenceData;

    private ReferenceData referenceData;

    public ReferenceListView(ReferenceSearchService searchService,
                             ReferenceDataSearchService referenceDataSearchService,
                             AutoEditReferenceDataEditDialog autoEditReferenceDataEditDialog) {
        this.autoEditReferenceDataEditDialog = autoEditReferenceDataEditDialog;
        this.searchService = searchService;
        this.referenceDataSearchService = referenceDataSearchService;

        this.referenceDataProvider = DataProvider.
                <Map<String, ?>, Map<String, ?>>fromFilteringCallbacks
                (query -> referenceDataSearchService.search(referenceData, query),
                        query -> referenceDataSearchService.count(referenceData, query))
                .withConfigurableFilter();

        this.dataProvider = DataProvider
                .fromFilteringCallbacks(searchService::fetch, searchService::count)
                .withConfigurableFilter();
        var metaDataGrid = new Grid<ReferenceData>();
        metaDataGrid.addColumn(ReferenceData::getTableName);
        metaDataGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        metaDataGrid.addSelectionListener(event -> {
            initReferenceDataGrid(event.getFirstSelectedItem().orElse(null));
        });
        metaDataGrid.setDataProvider(dataProvider);
        var leftBlock = new VerticalLayout(metaDataGrid);
        leftBlock.setWidth(300, Unit.PIXELS);
        //
        var referenceDataAction = new HorizontalLayout();
        referenceDataAction.setWidthFull();
        var addReferenceButton = new Button("Добавить");
        addReferenceButton.addClickListener(this::addReferenceData);
        var editReferenceButton = new Button("Изменить");
        editReferenceButton.addClickListener(this::editReferenceData);
        var deleteReferenceButton =  new Button("Удалить");
        deleteReferenceButton.addClickListener(this::deleteReferenceData);
        var leftReferenceDataActionBlock = new HorizontalLayout();
        leftReferenceDataActionBlock.add(addReferenceButton, editReferenceButton, deleteReferenceButton);
        var rightReferenceDataActionBlock = new HorizontalLayout();
        referenceDataAction.add(leftReferenceDataActionBlock, rightReferenceDataActionBlock);
        //
        this.referenceDataGrid = new Grid<>();
        referenceDataGrid.setDataProvider(referenceDataProvider);
        referenceDataGrid.setSizeFull();
        referenceDataGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        referenceDataGrid.addSelectionListener(event -> {
            this.selectedReferenceData = event.getFirstSelectedItem().orElse(null);
            editReferenceButton.setEnabled(selectedReferenceData != null);
            deleteReferenceButton.setEnabled(selectedReferenceData != null);
        });

        var rightBlock = new VerticalLayout(referenceDataAction, referenceDataGrid);
        add(leftBlock, rightBlock);
        setFlexGrow(1, rightBlock);
        setSizeFull();

    }

    private void deleteReferenceData(ClickEvent<Button> buttonClickEvent) {

    }

    private void editReferenceData(ClickEvent<Button> buttonClickEvent) {
        autoEditReferenceDataEditDialog.editValue(referenceData, selectedReferenceData, this);

    }

    private void addReferenceData(ClickEvent<Button> buttonClickEvent) {

    }

    private void initReferenceDataGrid(ReferenceData referenceData) {
        this.referenceData = referenceData;
        referenceDataGrid.removeAllColumns();
        if (referenceData != null) {
            referenceData
                    .getMetaDataFieldsList()
                    .stream()
                    .sorted(Comparator.comparingInt(ReferenceFieldData::getFieldOrder))
                    .forEach(v -> {
                        referenceDataGrid.addColumn(f -> {
                                    var o = f.get(v.getFieldName());
                                    return switch (o) {
                                        case String stringValue -> stringValue;
                                        case Integer integerValue -> Integer.toString(integerValue);
                                        case Long longValue -> Long.toString(longValue);
                                        case Double doubleValue -> Double.toString(doubleValue);
                                        case Boolean booleanValue -> Boolean.toString(booleanValue);
                                        case LocalDate localDateValue -> localDateValue.toString();
                                        case LocalDateTime localDateTimeValue -> localDateTimeValue.toString();
                                        case BigDecimal bigDecimalValue -> bigDecimalValue.toString();
                                        case Byte byteValue -> byteValue.toString();
                                        case Float floatValue -> floatValue.toString();
                                        default -> "";
                                    };
                                })
                                .setHeader(v.getFieldTitle())
                                .setKey(v.getFieldName())
                                .setWidth("%d%s".formatted(v.getFieldLength(), Unit.PIXELS));
                    });
        }
        referenceDataGrid.getDataProvider().refreshAll();
    }

    @Override
    public void ok() {
        referenceDataGrid.deselectAll();
        //referenceDataProvider.setFilter(buildQueryFilter());
        referenceDataProvider.refreshAll();
    }

}
