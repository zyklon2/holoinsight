/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.engine.elasticsearch.service.impl;

import io.holoinsight.server.storage.common.constants.Const;
import io.holoinsight.server.storage.common.model.query.ResponseMetric;
import io.holoinsight.server.storage.engine.elasticsearch.model.EndpointRelationEsDO;
import io.holoinsight.server.storage.engine.elasticsearch.model.ServiceRelationEsDO;
import io.holoinsight.server.storage.engine.elasticsearch.model.SpanEsDO;
import io.opentelemetry.proto.trace.v1.Status;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregator;
import org.elasticsearch.search.aggregations.bucket.filter.Filter;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.ParsedCardinality;
import org.elasticsearch.search.aggregations.metrics.Percentiles;
import org.elasticsearch.search.aggregations.metrics.ValueCount;

import java.util.Map;

public class CommonBuilder {

  public static void addTermParams(BoolQueryBuilder queryBuilder, Map<String, String> termParams) {
    if (termParams != null && termParams.size() > 0) {
      termParams.keySet().forEach(k -> {
        queryBuilder.must(QueryBuilders.termQuery(k, termParams.get(k)));
      });
    }
  }

  public static void addTermParamsWithAttr(BoolQueryBuilder queryBuilder,
      Map<String, String> termParams) {
    if (termParams != null && termParams.size() > 0) {
      termParams.keySet().forEach(k -> {
        String value = termParams.get(k);
        if (k.equals(EndpointRelationEsDO.APPP_ID)) {
          k = Const.APPID;
        } else if (k.equals(EndpointRelationEsDO.ENV_ID)) {
          k = Const.ENVID;
        }
        queryBuilder.must(QueryBuilders.termQuery(SpanEsDO.attributes(k), value));
      });
    }
  }

  public static TermsAggregationBuilder buildAgg(String aggField) {
    TermsAggregationBuilder aggregationBuilder = AggregationBuilders.terms(aggField).field(aggField)
        .subAggregation(AggregationBuilders.terms(ServiceRelationEsDO.COMPONENT)
            .field(ServiceRelationEsDO.COMPONENT).executionHint("map")
            .collectMode(Aggregator.SubAggCollectionMode.BREADTH_FIRST))
        .subAggregation(AggregationBuilders.avg("avg_latency").field(ServiceRelationEsDO.LATENCY))
        .subAggregation(AggregationBuilders.percentiles("p95_latency")
            .field(ServiceRelationEsDO.LATENCY).percentiles(95.0))
        .subAggregation(AggregationBuilders.percentiles("p99_latency")
            .field(ServiceRelationEsDO.LATENCY).percentiles(99.0))
        .subAggregation(
            AggregationBuilders.count("total_count").field(ServiceRelationEsDO.TRACE_ID))
        .subAggregation(AggregationBuilders
            .filter(ServiceRelationEsDO.TRACE_STATUS,
                QueryBuilders.termQuery(ServiceRelationEsDO.TRACE_STATUS,
                    Status.StatusCode.STATUS_CODE_ERROR_VALUE))
            .subAggregation(AggregationBuilders.count("error_count").field(aggField)))
        .executionHint("map").collectMode(Aggregator.SubAggCollectionMode.BREADTH_FIRST).size(1000);

    return aggregationBuilder;
  }

  public static ResponseMetric buildMetric(Terms.Bucket bucket) {
    Avg avgLatency = bucket.getAggregations().get("avg_latency");
    double latency = Double.valueOf(avgLatency.getValue());

    Percentiles p95Percentiles = bucket.getAggregations().get("p95_latency");
    double p95Latency = p95Percentiles.iterator().next().getValue();

    Percentiles p99Percentiles = bucket.getAggregations().get("p99_latency");
    double p99Latency = p99Percentiles.iterator().next().getValue();

    ValueCount totalTerm = bucket.getAggregations().get("total_count");
    int totalCount = (int) totalTerm.getValue();

    Filter errFilter = bucket.getAggregations().get(ServiceRelationEsDO.TRACE_STATUS);
    ValueCount errorTerm = errFilter.getAggregations().get("error_count");
    int errorCount = (int) errorTerm.getValue();

    ResponseMetric metric =
        new ResponseMetric(latency, p95Latency, p99Latency, totalCount, errorCount);

    return metric;
  }

  public static ResponseMetric buildMetricWithDistinct(Terms.Bucket bucket) {
    Avg avgLatency = bucket.getAggregations().get("avg_latency");
    double latency = Double.valueOf(avgLatency.getValue());

    Percentiles p95Percentiles = bucket.getAggregations().get("p95_latency");
    double p95Latency = p95Percentiles.iterator().next().getValue();

    Percentiles p99Percentiles = bucket.getAggregations().get("p99_latency");
    double p99Latency = p99Percentiles.iterator().next().getValue();

    ParsedCardinality totalTerm = bucket.getAggregations().get("total_count");
    int totalCount = (int) totalTerm.getValue();

    Filter errFilter = bucket.getAggregations().get(ServiceRelationEsDO.TRACE_STATUS);
    ParsedCardinality errorTerm = errFilter.getAggregations().get("error_count");
    int errorCount = (int) errorTerm.getValue();

    ResponseMetric metric =
        new ResponseMetric(latency, p95Latency, p99Latency, totalCount, errorCount);

    return metric;
  }
}
