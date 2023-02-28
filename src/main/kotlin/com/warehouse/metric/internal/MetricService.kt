package com.warehouse.metric.internal

import com.warehouse.api.Metric
import com.warehouse.api.Dimension
import com.warehouse.metric.MetricFacade
import com.warehouse.metric.ExtractedDataDto
import com.warehouse.metric.internal.csv.CsvLoader
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDate

class MetricService(
        private val csvLoader: CsvLoader,
        private val metricRepository: MetricRepository
) : MetricFacade {

    override fun getData(metrics: Set<Metric>,
                         startDate: LocalDate?,
                         endDate: LocalDate?,
                         campaign: String?,
                         datasource: String?,
                         groupedBy: Set<Dimension>): Flux<ExtractedDataDto> =
            metricRepository.getData(
                    metrics = metrics,
                    startDate = startDate,
                    endDate = endDate,
                    campaign = campaign,
                    datasource = datasource,
                    groupedBy = groupedBy
            ).map { it.toExtractedDataDto() }

    override fun feedMetrics(filename: String): Mono<Boolean> {
        return csvLoader.loadMetricFromCsv(filename)
                .map { MetricDocument(it) }
                .collectList()
                .flatMap { metricRepository.saveAll(it.toList()) }
    }

}