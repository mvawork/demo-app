package ru.menshevva.demoapp.report.userlistreports

import com.vaadin.flow.data.provider.Query
import org.apache.poi.xssf.streaming.SXSSFWorkbook
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import ru.menshevva.demoapp.dto.UserListData
import ru.menshevva.demoapp.report.ApplicationReport
import ru.menshevva.demoapp.report.ReportData
import ru.menshevva.demoapp.report.utils.ReportUtils.getCharacterWidth
import ru.menshevva.demoapp.service.users.UserSearchFilter
import ru.menshevva.demoapp.service.users.UserSearchService
import ru.menshevva.demoapp.ui.components.ParamsCallback
import java.io.FileOutputStream
import java.nio.file.Files


@Service
class UserListReport(userSearchService: UserSearchService) : ApplicationReport {

    var userSearchService: UserSearchService = userSearchService;

    companion object {
        private val log: Logger = LoggerFactory.getLogger(UserListReport::class.java)
    }


    override fun execute(paramsCallback: ParamsCallback): ReportData {
        log.debug("Вызван метод execute")
        val tempFile = Files.createTempFile("doc", ".tmp")
        SXSSFWorkbook(1).use { wb ->
            val sh = wb.createSheet("Список пользователей")
            sh.setColumnWidth(0, getCharacterWidth(14))
            sh.setColumnWidth(1, getCharacterWidth(100))
            sh.setColumnWidth(2, getCharacterWidth(255))
            val headerRow = sh.createRow(0)
            headerRow.createCell(0).setCellValue("Идентификатор")
            headerRow.createCell(1).setCellValue("Имя входа")
            headerRow.createCell(2).setCellValue("Имя пользователя")
            // Получить данные о пользователях из сервиса
            val query: Query<UserListData, UserSearchFilter> = Query()
            val data = userSearchService.fetch(query)
            var rowNumber = 1
            data.forEach { v ->
                val newRow = sh.createRow(rowNumber)
                newRow.createCell(0).setCellValue(v.userId().toDouble())
                newRow.createCell(1).setCellValue(v.userLogin)
                newRow.createCell(2).setCellValue(v.userName)
                rowNumber++
            }
            FileOutputStream(tempFile.toFile()).use { out ->
                wb.write(out)
            }
        }

        return ReportData(
            "Список пользователей.xlsx",
            MediaType("application", "vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
            tempFile
        )
    }

}