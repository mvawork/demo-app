package ru.menshevva.demoapp.report


data class ReportMetaData(val name: String, val instanceFactory: () -> ApplicationReport )
