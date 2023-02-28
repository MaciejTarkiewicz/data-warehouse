package com.warehouse.metric

import java.math.BigDecimal
import java.time.LocalDate

data class ExtractedDataDto(
        val totalImpressions: Int?,
        val totalClicks: Int?,
        val ctr : BigDecimal?,
        val groupedBy: GroupedByDto?
)

data class GroupedByDto(
        val campaign: String?,
        val datasource: String?,
        val date: LocalDate?
)