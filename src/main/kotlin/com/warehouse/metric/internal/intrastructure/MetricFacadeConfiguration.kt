package com.warehouse.metric.internal.intrastructure

import com.warehouse.metric.MetricFacade
import com.warehouse.metric.internal.MetricRepository
import com.warehouse.metric.internal.MetricService
import com.warehouse.metric.internal.csv.CsvLoader
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory


@Factory
class MetricFacadeConfiguration {

    @Bean
    fun createMetricFacade(metricRepository: MetricRepository): MetricFacade {
        val csvLoader = CsvLoader()
        return MetricService(csvLoader, metricRepository)
    }
}