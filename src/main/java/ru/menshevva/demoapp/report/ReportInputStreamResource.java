package ru.menshevva.demoapp.report;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;

@Slf4j
public class ReportInputStreamResource extends InputStreamResource {


    private final ReportData reportData;

    public ReportInputStreamResource(String uuid, ReportData reportData, ReportDeletedListener deletedListener) throws FileNotFoundException {
        super(new FileInputStream(reportData.getFilePath().toFile()) {
            @Override
            public void close() throws IOException {
                super.close();
                try {
                    Files.delete(reportData.getFilePath());
                } catch (IOException e) {
                    log.error("Ошибка удаления файла {}", reportData.getFilePath(), e);
                }
                if (deletedListener != null) {
                    deletedListener.onReportDeleted(uuid);
                }
            }
        });
        this.reportData = reportData;
    }

    @Override
    public String getFilename() {
        return reportData.getFileName();
    }


    public MediaType getMediaType() {
        return reportData.getMediaType();
    }
}
