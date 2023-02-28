package com.warehouse.client;

import com.warehouse.api.Dimension;
import com.warehouse.api.FeedDataResponse;
import com.warehouse.api.Metric;
import com.warehouse.metric.ExtractedDataDto;
import com.warehouse.api.Dimension;
import com.warehouse.api.FeedDataResponse;
import com.warehouse.api.Metric;
import com.warehouse.metric.ExtractedDataDto;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.client.annotation.Client;

import java.util.List;
import java.util.Set;

@Client("/")
public interface AppClient {
    @Get("/api/metrics/query")
    List<ExtractedDataDto> getData(
            @QueryValue("metrics") Set<Metric> metric,
            @Nullable @QueryValue("startDate") String startDate,
            @Nullable @QueryValue("endDate") String endDate,
            @Nullable @QueryValue("campaign") String campaign,
            @Nullable @QueryValue("datasource") String datasource,
            @Nullable @QueryValue("groupedBy") Set<Dimension> groupedBy
    );

    @Post("/api/metrics/feed-data")
    FeedDataResponse feedData(@QueryValue("filename") String metric);

}
