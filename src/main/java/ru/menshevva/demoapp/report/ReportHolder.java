package ru.menshevva.demoapp.report;

import com.vaadin.flow.spring.annotation.SpringComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.web.context.WebApplicationContext;

import java.io.FileNotFoundException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Scope(value = WebApplicationContext.SCOPE_SESSION)
@SpringComponent
@Slf4j
public class ReportHolder implements ReportDeletedListener {

    private final Map<String, ReportInputStreamResource> reports = new ConcurrentHashMap<>();

    public ReportHolder() {
        log.debug("Создан ReportHolder");
    }

    public String put(ReportData reportData) throws FileNotFoundException {
        var uuid = UUID.randomUUID().toString();
        reports.put(uuid, new ReportInputStreamResource(uuid, reportData, this));
        return uuid;
    }

    @Override
    public void onReportDeleted(String id) {
        reports.remove(id);
    }

    public ReportInputStreamResource get(String id) {
        return reports.get(id);
    }
}
