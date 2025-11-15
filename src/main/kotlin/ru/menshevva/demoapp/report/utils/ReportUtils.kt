package ru.menshevva.demoapp.report.utils

object ReportUtils {
    @JvmStatic
    fun getCharacterWidth(value: Int): Int {
        return value * 256
    }
}