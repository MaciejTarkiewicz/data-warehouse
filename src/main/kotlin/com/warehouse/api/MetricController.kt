package com.warehouse.api

import com.warehouse.metric.ExtractedDataDto
import com.warehouse.metric.MetricFacade
import io.micronaut.core.convert.format.Format
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.QueryValue
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDate

@Controller("/api/metrics")
class MetricController(
        private val metricFacade: MetricFacade
) {

    @Get("/query")
    fun query(
            @QueryValue("metrics") metrics: Set<Metric>,
            @QueryValue("startDate") @Format("yyyy-MM-dd") startDate: LocalDate?,
            @QueryValue("endDate") @Format("yyyy-MM-dd") endDate: LocalDate?,
            @QueryValue("campaign") campaign: String?,
            @QueryValue("datasource") datasource: String?,
            @QueryValue("groupedBy") groupedBy: Set<Dimension>?
    ): Flux<ExtractedDataDto> =
            metricFacade.getData(
                    metrics = metrics,
                    startDate = startDate,
                    endDate = endDate,
                    campaign = campaign,
                    datasource = datasource,
                    groupedBy = groupedBy.orEmpty())

    @Post("/feed-data")
    fun post(@QueryValue("filename") filename: String): Mono<HttpResponse<FeedDataResponse>> =
            metricFacade.feedMetrics(filename)
                    .map { HttpResponse.created(FeedDataResponse("Successfully feed data to data storage")) }
}


enum class Dimension(val value: String) {
    CAMPAIGN("campaign"), DATASOURCE("datasource"), DATE("date")
}

enum class Metric(val value: String) {
    CLICKS("clicks"), IMPRESSIONS("impressions")
}

data class FeedDataResponse(
        val responseMessage: String
)