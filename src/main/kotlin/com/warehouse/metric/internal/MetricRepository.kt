package com.warehouse.metric.internal

import com.warehouse.api.Dimension
import com.warehouse.api.Metric
import com.mongodb.client.model.Accumulators.sum
import com.mongodb.client.model.Aggregates.group
import com.mongodb.client.model.Aggregates.match
import com.mongodb.client.model.BsonField
import com.mongodb.client.model.Filters.*
import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoCollection
import org.bson.Document
import org.bson.conversions.Bson
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDate

interface MetricRepository {
    fun saveAll(metrics: List<MetricDocument>): Mono<Boolean>
    fun getData(metrics: Set<Metric>,
                startDate: LocalDate?,
                endDate: LocalDate?,
                campaign: String?,
                datasource: String?,
                groupedBy: Set<Dimension>): Flux<TotalDocument>
}

class MetricRepositoryImpl(
        private val mongoClient: MongoClient,
        private val database: String,
        private val collectionName: String
) : MetricRepository {

    override fun saveAll(metrics: List<MetricDocument>): Mono<Boolean> =
            Mono.from(getMetricDocumentCollection()
                    .insertMany(metrics))
                    .map { it.wasAcknowledged() }


    override fun getData(metrics: Set<Metric>,
                         startDate: LocalDate?,
                         endDate: LocalDate?,
                         campaign: String?,
                         datasource: String?,
                         groupedBy: Set<Dimension>): Flux<TotalDocument> {

        val aggregationList = mutableListOf<Bson>()

        val match = buildMatch(
                startDate = startDate,
                endDate = endDate,
                campaign = campaign,
                datasource = datasource
        )
        val group = group(buildGroupId(groupedBy), buildGroupValue(metrics))

        match?.let { aggregationList.add(match) }
        group.let { aggregationList.add(group) }

        return Flux.from(getMetricDocumentCollection().aggregate(aggregationList, TotalDocument::class.java))
    }

    private fun buildMatch(
            startDate: LocalDate?,
            endDate: LocalDate?,
            campaign: String?,
            datasource: String?
    ): Bson? {
        val filters = mutableListOf<Bson>()

        startDate?.let { filters.add(gte(DATE_FIELD, it)) }
        endDate?.let { filters.add(lte(DATE_FIELD, it)) }
        campaign?.let { filters.add(eq(CAMPAIGN_FIELD, it)) }
        datasource?.let { filters.add(eq(DATASOURCE_FIELD, it)) }

        return filters.takeIf { it.isNotEmpty() }?.let { match(and(it)) }
    }

    private fun buildGroupId(groupedBy: Set<Dimension>): Document? {
        val multiIdMap = groupedBy.associate { it.value to "$${it.value}" }
        return if (multiIdMap.isNotEmpty()) Document(multiIdMap) else null
    }

    private fun buildGroupValue(metrics: Set<Metric>): List<BsonField> {
        val bsonFields = mutableListOf<BsonField>()
        val multiIdMap = metrics.associate { "$TOTAL_PREFIX${it.value}" to "$${it.value}" }
        multiIdMap.entries.forEach { bsonFields.add(sum(it.key, it.value)) }
        return bsonFields
    }

    private fun getMetricDocumentCollection(): MongoCollection<MetricDocument> =
            mongoClient
                    .getDatabase(database)
                    .getCollection(collectionName, MetricDocument::class.java)


    companion object {
        private const val TOTAL_PREFIX: String = "total-"
        private const val DATE_FIELD: String = "date"
        private const val CAMPAIGN_FIELD: String = "campaign"
        private const val DATASOURCE_FIELD: String = "datasource"
    }
}