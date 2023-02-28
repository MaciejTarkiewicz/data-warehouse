package com.warehouse


import com.warehouse.api.Dimension
import com.warehouse.api.Metric
import com.warehouse.client.AppClient
import com.warehouse.configuration.TestContainerFixture
import com.warehouse.metric.ExtractedDataDto
import com.warehouse.metric.GroupedByDto
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Unroll

import java.time.LocalDate

@MicronautTest(transactional = false)
class QueryTest extends TestContainerFixture {

    @Inject
    AppClient appClient

    def setup() {
        mongoDBContainer.start()
        appClient.feedData("data-source")

    }

    @Unroll
    def "should properly calculate and extract data"() {
        when:
        def response = appClient.getData(metrics as Set<Metric>, startDate, endDate, campaign, datasource, groupedBy)

        then:
        response.contains(expectedResult)

        where:
        metrics                             | startDate    | endDate      | campaign                | datasource | groupedBy                                                    || expectedResult
        [Metric.CLICKS]                     | "2017-01-10" | "2022-10-18" | "Adventmarkt Touristik" | null       | null                                                         || buildExtractedDto(null, 9333, null)
        [Metric.CLICKS, Metric.IMPRESSIONS] | null         | null         | null                    | null       | [Dimension.CAMPAIGN, Dimension.DATASOURCE] as Set<Dimension> || buildExtractedDto(777460, 107990, 0.14, "Touristik Routenplaner", "Facebook Ads", null)
        [Metric.IMPRESSIONS]                | null         | null         | null                    | null       | [Dimension.DATE] as Set<Dimension>                           || buildExtractedDto(278175, null, null, null, null, "2019-04-05")

    }

    def buildExtractedDto(totalImpressions, totalClicks, ctr) {
        new ExtractedDataDto(totalImpressions, totalClicks, ctr, null)
    }

    def buildExtractedDto(totalImpressions, totalClicks, ctr, groupByCampaign, groupByDatasource, groupByDate) {
        def date = groupByDate != null ? LocalDate.parse(groupByDate) : null
        def groupBy = new GroupedByDto(groupByCampaign, groupByDatasource, date)
        new ExtractedDataDto(totalImpressions, totalClicks, ctr, groupBy)
    }

}