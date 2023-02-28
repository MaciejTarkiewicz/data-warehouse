package com.warehouse.metric

import com.warehouse.api.Metric
import com.warehouse.api.Dimension
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDate

interface MetricFacade {
    fun getData(metrics: Set<Metric>,
                startDate: LocalDate?,
                endDate: LocalDate?,
                campaign: String?,
                datasource: String?,
                groupedBy: Set<Dimension>): Flux<ExtractedDataDto>

    fun feedMetrics(filename: String): Mono<Boolean>

}