package ru.menshevva.demoapp.ui.components;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.selection.SelectionListener;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import ru.menshevva.demoapp.dto.metadata.ReferenceData;
import ru.menshevva.demoapp.dto.metadata.ReferenceFieldData;
import ru.menshevva.demoapp.service.metadata.AppDataGridSearchService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

@SpringComponent
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class AppDataGrid extends Composite<VerticalLayout>  {

    private final Grid<Map<String, ?>> dataGrid;
    private final ConfigurableFilterDataProvider<Map<String, ?>, Void, Map<String, ?>> dataProvider;
    private final Map<String, TextField> filters = new HashMap<>();

    private ReferenceData referenceData;

    public AppDataGrid(AppDataGridSearchService referenceDataSearchService) {
        this.dataProvider = DataProvider.
                <Map<String, ?>, Map<String, ?>>fromFilteringCallbacks
                (query -> referenceDataSearchService.search(referenceData, query),
                        query -> referenceDataSearchService.count(referenceData, query))
                .withConfigurableFilter();
        dataGrid = new Grid<>();
        dataGrid.setDataProvider(dataProvider);
        var content = getContent();
        //dataGrid.setSizeFull();
        content.add(dataGrid);
        content.setPadding(false);
        content.setFlexGrow(1, dataGrid);
    }

    public void setReferenceData(ReferenceData referenceData) {
        this.referenceData = referenceData;
        dataGrid.removeAllColumns();
        dataGrid.removeAllHeaderRows();
        filters.clear();
        if (referenceData != null) {
            final HeaderRow[] referenceDataGridFilterRow = {null};
            referenceData
                    .getMetaDataFieldsList()
                    .stream()
                    .sorted(Comparator.comparingInt(ReferenceFieldData::getFieldOrder))
                    .forEach(v -> {
                        var column = dataGrid.addColumn(f -> {
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
                        filterField.setPlaceholder("Фильтр по " + v.getFieldTitle());

                        if (referenceDataGridFilterRow[0] == null) {
                            referenceDataGridFilterRow[0] = dataGrid.appendHeaderRow();
                        }
                        referenceDataGridFilterRow[0].getCell(column).setComponent(filterField);
                        filters.put(v.getFieldName(), filterField);
                    });

        }
        refresh();
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

    public void refresh() {
        dataGrid.deselectAll();
        dataProvider.setFilter(buildQueryFilter());
        dataProvider.refreshAll();
    }

    public void clearFilter() {
        filters.forEach((s, v) -> {
                    if (!v.getValue().isEmpty()) {
                        v.clear();
                    }
                }
        );
        refresh();
    }


    public void setSelectionMode(Grid.SelectionMode selectionMode) {
        dataGrid.setSelectionMode(selectionMode);
    }

    public Registration addSelectionListener(SelectionListener<Grid<Map<String, ?>>, Map<String, ?>> listener) {
        return dataGrid.addSelectionListener(listener);
    }
}
