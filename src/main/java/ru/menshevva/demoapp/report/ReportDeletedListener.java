package ru.menshevva.demoapp.report;


@FunctionalInterface
public interface ReportDeletedListener {

    void onReportDeleted(String id);

}
