package com.warehouse.metric.internal.intrastructure

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.model.IndexOptions
import com.mongodb.client.model.Indexes
import com.mongodb.reactivestreams.client.MongoClients
import com.mongodb.reactivestreams.client.MongoCollection
import com.warehouse.metric.internal.*
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Value
import org.bson.codecs.configuration.CodecRegistries
import org.bson.codecs.pojo.PojoCodecProvider
import reactor.core.publisher.Mono

@Factory
class MetricRepositoryConfiguration {

    @Bean
    fun createMetricRepository(@Value("\${mongodb.uri}") mongoConnectionString: String,
                               @Value("\${mongodb.collection.metrics}") collectionName: String,
                               @Value("\${mongodb.database}") database: String): MetricRepository {
        val connectionString = ConnectionString(mongoConnectionString)
        val pojoCodec = PojoCodecProvider.builder().automatic(true)
                .register(TotalDocument::class.java)
                .register(GroupedBy::class.java)
                .build()
        val pojoCodecRegistry = CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), CodecRegistries.fromProviders(pojoCodec))

        val clientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .codecRegistry(pojoCodecRegistry)
                .build()
        val mongoClient = MongoClients.create(clientSettings)

        val metricCollection = mongoClient
                .getDatabase(database)
                .getCollection(collectionName, MetricDocument::class.java)

        createMatchIndex(metricCollection, DATE_FIELD).block()
        createMatchIndex(metricCollection, DATASOURCE_FIELD).block()
        createMatchIndex(metricCollection, CAMPAIGN_FIELD).block()

        return MetricRepositoryImpl(mongoClient, database, collectionName)
    }

    private fun createMatchIndex(collection: MongoCollection<MetricDocument>, attribute: String): Mono<String> {
        val option = IndexOptions().unique(false)
        return Mono.from(collection.createIndex(Indexes.ascending(attribute), option))
    }

    companion object {
        private const val DATE_FIELD: String = "date"
        private const val CAMPAIGN_FIELD: String = "campaign"
        private const val DATASOURCE_FIELD: String = "datasource"
    }
}