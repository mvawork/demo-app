package ru.menshevva.demoapp.report.userlistreports

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ru.menshevva.demoapp.report.GroupReport
import ru.menshevva.demoapp.report.ReportMetaData

@Component
class UserListViewGroupReport : GroupReport {

    @Autowired
    private lateinit var userListReport: UserListReport


    override val getReports = listOf(
        ReportMetaData("Информация о пользователе", {
            userListReport
        }),
        ReportMetaData("Список групп пользователя", {
            UserInfoReport()
        })
    );

}