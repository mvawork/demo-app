package ru.menshevva.demoapp.report.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.menshevva.demoapp.report.ReportHolder;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
@Slf4j
public abstract class ReportController {

    @GetMapping("/{id}")
    public ResponseEntity<ByteArrayResource> getReport(@PathVariable("id") String id) throws IOException {
        log.debug("Получен запрос на получение отчета {}", id);
        var streamResource = getReportHolder().get(id);
        if (streamResource == null) {
            return ResponseEntity.notFound().build();
        }
        var contentDispositionFileName = "attachment; filename=\"" +
                URLDecoder.decode(URLEncoder.encode(streamResource.getFilename(), StandardCharsets.UTF_8), "ISO8859_1")
                + "\"";
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDispositionFileName)
                .contentType(streamResource.getMediaType())
                .body(new ByteArrayResource(IOUtils.toByteArray(streamResource.getInputStream())));
    }

    @Lookup
    protected abstract ReportHolder getReportHolder();

}
