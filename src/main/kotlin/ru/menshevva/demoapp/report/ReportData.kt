package ru.menshevva.demoapp.report

import org.springframework.http.MediaType
import java.nio.file.Path

data class ReportData(val fileName: String,
                      val mediaType: MediaType,
                      val filePath: Path)
