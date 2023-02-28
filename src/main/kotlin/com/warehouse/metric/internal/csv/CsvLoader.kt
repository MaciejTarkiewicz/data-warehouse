package com.warehouse.metric.internal.csv

import reactor.core.publisher.Flux
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class CsvLoader {

    fun loadMetricFromCsv(filename: String): Flux<MetricDto> {
        val resourceAsStream: InputStream = this::class.java.classLoader.getResourceAsStream("$filename.csv")!!
        val reader = BufferedReader(InputStreamReader(resourceAsStream))
        val lines: Flux<String> = Flux.fromStream(reader.lines())
        return lines.skip(1)
                .map { line: String ->
                    val fields = line.split(",")
                    buildCsvMetric(fields)
                }
    }

    private fun buildCsvMetric(fields: List<String>) = MetricDto(
            datasource = fields[0],
            campaign = fields[1],
            date = LocalDate.parse(fields[2], formatter),
            clicks = fields[3].toInt(),
            impressions = fields[4].toInt()
    )

    companion object {
        var formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yy")
    }
}

data class MetricDto(
        val date: LocalDate,
        val datasource: String,
        val campaign: String,
        val clicks: Int,
        val impressions: Int
)