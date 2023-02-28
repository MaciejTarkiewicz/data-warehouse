package com.warehouse.metric.internal

import com.warehouse.metric.ExtractedDataDto
import com.warehouse.metric.GroupedByDto
import com.warehouse.metric.internal.csv.MetricDto
import org.bson.codecs.pojo.annotations.BsonCreator
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonProperty
import org.bson.types.ObjectId
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate

data class MetricDocument(
        @BsonId
        val id: ObjectId?,
        val date: LocalDate,
        val datasource: String,
        val campaign: String,
        val clicks: Int,
        val impressions: Int
) {
    constructor(metricDto: MetricDto) : this(
            null,
            metricDto.date,
            metricDto.datasource,
            metricDto.campaign,
            metricDto.clicks,
            metricDto.impressions
    )

}

data class TotalDocument @BsonCreator constructor(
        @BsonProperty("total-impressions")
        val totalImpressions: Int?,
        @BsonProperty("total-clicks")
        val totalClicks: Int?,
        @BsonProperty("_id")
        val groupedBy: GroupedBy?
) {
    fun toExtractedDataDto(): ExtractedDataDto =
            ExtractedDataDto(
                    totalImpressions = totalImpressions,
                    totalClicks = totalClicks,
                    ctr = buildCtr(),
                    groupedBy = if (groupedBy != null) GroupedByDto(
                            campaign = groupedBy.campaign,
                            datasource = groupedBy.datasource,
                            date = groupedBy.date
                    ) else null)

    private fun buildCtr() =
            if (totalImpressions != null && totalClicks != null) {
                BigDecimal(totalClicks).divide(BigDecimal(totalImpressions), 2, RoundingMode.HALF_UP)
            } else null
}

data class GroupedBy @BsonCreator constructor(
        @BsonProperty("campaign")
        val campaign: String?,
        @BsonProperty("datasource")
        val datasource: String?,
        @BsonProperty("date")
        val date: LocalDate?
)



