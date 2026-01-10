package ru.menshevva.demoapp.ui.references;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import static ru.menshevva.demoapp.ui.FrontendConsts.TITLE_PAGE_REFERENCES;

@PermitAll
@PageTitle(TITLE_PAGE_REFERENCES)
@Route(value = FrontendConsts.PAGE_REFERENCES, layout = MainMenuLayout.class)
@Slf4j
public class ReferenceListView extends HorizontalLayout implements EditActionCallback {

    private final ReferenceSearchService searchService;
    private final ConfigurableFilterDataProvider<ReferenceData, Void, ReferenceFilter> dataProvider;
    private final Grid<Map<String, ?>> referenceDataGrid;
    private final ReferenceDataSearchService referenceDataSearchService;
    private final ConfigurableFilterDataProvider<Map<String, ?>, Void, Map<String, ?>> referenceDataProvider;
    private final AutoEditReferenceDataEditDialog autoEditReferenceDataEditDialog;
    private final Button addReferenceButton;
    private final Button editReferenceButton;
    private final Button deleteReferenceButton;

    private Map<String, ?> selectedReferenceData;

    private ReferenceData referenceData;
    private final Map<String, TextField> filters = new HashMap<>();

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
        metaDataGrid.addSelectionListener(event -> initReferenceDataGrid(event.getFirstSelectedItem().orElse(null)));
        metaDataGrid.setDataProvider(dataProvider);
        var leftBlock = new VerticalLayout(metaDataGrid);
        leftBlock.setWidth(300, Unit.PIXELS);
        //
        var referenceDataAction = new HorizontalLayout();
        referenceDataAction.setWidthFull();
        this.addReferenceButton = new Button("Добавить");
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
        clearReferenceButton.addClickListener(event -> {
            clearFilter();
        });

        var rightReferenceDataActionBlock = new HorizontalLayout(searchReferenceButton, clearReferenceButton);
        rightReferenceDataActionBlock.setJustifyContentMode(JustifyContentMode.END);
        referenceDataAction.add(leftReferenceDataActionBlock, rightReferenceDataActionBlock);
        referenceDataAction.setWidthFull();
        referenceDataAction.setFlexGrow(1, rightReferenceDataActionBlock);
        //
        this.referenceDataGrid = new Grid<>();


        referenceDataGrid.setDataProvider(referenceDataProvider);
        referenceDataGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        referenceDataGrid.addSelectionListener(event -> {
            setSelectedReferenceData(event.getFirstSelectedItem().orElse(null));
        });
        setSelectedReferenceData(null);
        var rightBlock = new VerticalLayout(referenceDataAction, referenceDataGrid);
        rightBlock.setSizeFull();
        rightBlock.setFlexGrow(1, referenceDataGrid);
        add(leftBlock, rightBlock);
        setFlexGrow(1, rightBlock);
        setSizeFull();

    }

    private void clearFilter() {
        filters.forEach((s, v) -> {
                    if (!v.getValue().isEmpty()) {
                        v.clear();
                    }
                }
        );
        ok();
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

    private void initReferenceDataGrid(ReferenceData referenceData) {
        this.referenceData = referenceData;
        referenceDataGrid.removeAllColumns();
        referenceDataGrid.removeAllHeaderRows();
        filters.clear();
        if (referenceData != null) {
            final HeaderRow[] referenceDataGridFilterRow = {null};
            referenceData
                    .getMetaDataFieldsList()
                    .stream()
                    .sorted(Comparator.comparingInt(ReferenceFieldData::getFieldOrder))
                    .forEach(v -> {
                        var column = referenceDataGrid.addColumn(f -> {
                                    var o = f.get(v.getFieldName());
                                    if (o == null) {
                                        return "";
                                    }
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

                        var filterField = new TextField();
                        filterField.setWidth("100%");
                        filterField.setPlaceholder("Фильтр по " + v.getFieldName());

                        if (referenceDataGridFilterRow[0] == null) {
                            referenceDataGridFilterRow[0] = referenceDataGrid.appendHeaderRow();
                        }
                        referenceDataGridFilterRow[0].getCell(column).setComponent(filterField);
                        filters.put(v.getFieldName(), filterField);
                    });

        }
        referenceDataGrid.getDataProvider().refreshAll();
    }

    @Override
    public void ok() {
        referenceDataGrid.deselectAll();
        referenceDataProvider.setFilter(buildQueryFilter());
        referenceDataProvider.refreshAll();
    }

    private Map<String, ?> buildQueryFilter() {
        if (referenceData == null) {
            return Collections.emptyMap();
        }
        var params = new HashMap<String, Object>();
        filters.forEach((s, v) -> {
                    if (!v.getValue().isEmpty()) {
                        referenceData.getMetaDataFieldsList()
                                .stream()
                                .filter(f -> f.getFieldName().equals(s)).findFirst()
                                .ifPresent(f -> {
                                    try {
                                        var o = switch (f.getFieldType()) {
                                            case FIELD_TYPE_STRING -> v.getValue();
                                            case FIELD_TYPE_LONG -> Long.parseLong(v.getValue());
                                            case FIELD_TYPE_INTEGER -> Integer.parseInt(v.getValue());
                                            case FIELD_TYPE_SHORT -> Short.parseShort(v.getValue());
                                            case FIELD_TYPE_BYTE -> Byte.parseByte(v.getValue());
                                            case FIELD_TYPE_CHAR -> v.getValue().charAt(0);
                                            case FIELD_TYPE_DATE -> LocalDate.parse(v.getValue());
                                            case FIELD_TYPE_TIMESTAMP -> LocalDateTime.parse(v.getValue());
                                            case FIELD_TYPE_FLOAT -> Float.parseFloat(v.getValue());
                                            case FIELD_TYPE_DOUBLE -> Double.parseDouble(v.getValue());
                                            case FIELD_TYPE_BOOLEAN -> Boolean.parseBoolean(v.getValue());
                                            case FIELD_TYPE_BIGDECIMAL -> new BigDecimal(v.getValue());
                                        };
                                        params.put(f.getFieldName(), o);
                                    } catch (RuntimeException ignore) {
                                    }
                                });

                    }
                }
        );
        return params;
    }

}
