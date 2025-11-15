package ru.menshevva.demoapp.ui.components.reporter;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.template.Id;
import ru.menshevva.demoapp.report.ReportMetaData;
import ru.menshevva.demoapp.ui.components.ViewCallback;

import java.util.List;

@Tag("report-group-component")
@JsModule("./src/components/reporter/report-group-component.ts")
public class ReportGroupComponent extends LitTemplate {

    private final ViewCallback<ReportMetaData> viewCallback;

    @Id
    private Grid<ReportMetaData> dataGrid;
    @Id
    private Button runButton;
    @Id
    private Button cancelButton;

    private ReportMetaData selectedItem;

    public ReportGroupComponent(ViewCallback<ReportMetaData> viewCallback) {
        this.viewCallback = viewCallback;
        initGrid();
        initButton();
    }

    private void initButton() {
        runButton.addClickListener(event -> {
           if (selectedItem == null) {
               return;
           }
           if (viewCallback != null) {
               viewCallback.ok(selectedItem);
           }
        });
        cancelButton.addClickListener(event -> {
            if (viewCallback != null) {
                viewCallback.cancel();
            }
        });
    }

    private void initGrid() {
        dataGrid.addColumn(ReportMetaData::getName)
                .setHeader("Имя отчета")
                .setSortable(true);
        dataGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        dataGrid.addSelectionListener(event -> this.selectedItem = event.getFirstSelectedItem().orElse(null));
    }

    public void setReports(List<ReportMetaData> reports) {
        dataGrid.deselectAll();
        dataGrid.setItems(reports);
    }
}
