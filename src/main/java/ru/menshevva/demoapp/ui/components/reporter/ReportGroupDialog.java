package ru.menshevva.demoapp.ui.components.reporter;


import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.extern.slf4j.Slf4j;
import ru.menshevva.demoapp.report.ApplicationReport;
import ru.menshevva.demoapp.report.ReportHolder;
import ru.menshevva.demoapp.report.ReportMetaData;
import ru.menshevva.demoapp.ui.components.ParamsCallback;
import ru.menshevva.demoapp.ui.components.ViewCallback;

import java.io.FileNotFoundException;
import java.util.List;

@Slf4j
@SpringComponent
@UIScope
public class ReportGroupDialog extends Dialog implements ViewCallback<ReportMetaData> {

    private final ReportGroupComponent reportGroupComponent;
    private final ReportHolder reportHolder;
    private ParamsCallback paramCallback;

    public ReportGroupDialog(ReportHolder reportHolder) {
        this.reportHolder = reportHolder;
        this.reportGroupComponent = new ReportGroupComponent(this);
        setCloseOnEsc(false);
        setCloseOnOutsideClick(false);
        setDraggable(true);
        add(reportGroupComponent);
    }

    public void runReport(List<ReportMetaData> reports, ParamsCallback paramsCallback) {
        this.reportGroupComponent.setReports(reports);
        this.paramCallback = paramsCallback;
        open();
    }

    @Override

    public void ok(ReportMetaData value) {
        if (value != null) {
            ApplicationReport report = value.getInstanceFactory().invoke();
            var reportData = report.execute(paramCallback);
            try {
                var uuid = reportHolder.put(reportData);
                log.debug("Создан отчет {}", uuid);
                UI.getCurrent().getPage().executeJs("window.open($0, '_blank')", "/report/" + uuid);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        cancel();
    }

    @Override
    public void cancel() {
        close();
    }
}
