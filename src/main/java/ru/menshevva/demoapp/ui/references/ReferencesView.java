package ru.menshevva.demoapp.ui.references;

import com.vaadin.flow.component.Unit;
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

import java.util.Comparator;
import java.util.Map;

import static ru.menshevva.demoapp.ui.FrontendConsts.TITLE_PAGE_REFERENCES;

@PermitAll
@PageTitle(TITLE_PAGE_REFERENCES)
@Route(value = FrontendConsts.PAGE_REFERENCES, layout = MainMenuLayout.class)
public class ReferencesView extends HorizontalLayout {

    private final ReferenceSearchService searchService;
    private final ConfigurableFilterDataProvider<ReferenceData, Void, ReferenceFilter> dataProvider;
    private final Grid<Map<String, ?>> referenceDataGrid;
    private final ReferenceDataSearchService referenceDataSearchService;
    private final ConfigurableFilterDataProvider<Map<String, ?>, Void, Map<String, ?>> referenceDataProvider;
    private ReferenceData referenceData;

    public ReferencesView(ReferenceSearchService searchService,
                          ReferenceDataSearchService referenceDataSearchService) {
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
        this.referenceDataGrid = new Grid<>();
        referenceDataGrid.setDataProvider(referenceDataProvider);
        referenceDataGrid.setSizeFull();
        var rightBlock = new VerticalLayout(referenceDataGrid);
        add(leftBlock, rightBlock);
        setFlexGrow(1, rightBlock);
        setSizeFull();

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
                                        case Long longValue -> Long.toString(longValue);
                                        default -> "???";
                                    };
                                })
                                .setHeader(v.getFieldTitle())
                                .setKey(v.getFieldName())
                                .setWidth("%d%s".formatted(v.getFieldLength(), Unit.PIXELS));
                    });
        }
        referenceDataGrid.getDataProvider().refreshAll();
    }
}
