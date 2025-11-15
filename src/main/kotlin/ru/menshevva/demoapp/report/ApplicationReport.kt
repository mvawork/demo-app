package ru.menshevva.demoapp.report

import ru.menshevva.demoapp.ui.components.ParamsCallback

interface ApplicationReport {

    /**
     * Метод выполнения отчета
     * @param params параметры для выполнения
     * @return информация о сгенерированном отчете
     */
    fun execute(paramsCallback: ParamsCallback): ReportData

}