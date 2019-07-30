package com.jsc.service;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.json.UTF8JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsc.pojo.Product;
import org.apache.lucene.search.Query;
import org.assertj.core.util.Lists;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.ElasticsearchClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.*;
import org.elasticsearch.join.aggregations.InternalChildren;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.*;
import org.elasticsearch.search.aggregations.bucket.nested.InternalNested;
import org.elasticsearch.search.aggregations.bucket.range.*;
import org.elasticsearch.search.aggregations.bucket.sampler.InternalSampler;
import org.elasticsearch.search.aggregations.bucket.terms.DoubleTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.avg.InternalAvg;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ResultsExtractor;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private TransportClient esClient;

    private static final ObjectMapper om = new ObjectMapper();

    /**
     * 使用springdata的elasticSearchTemplate模板查找所有商品
     */
    public void getAll() {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        queryBuilder = queryBuilder.withQuery(QueryBuilders.matchAllQuery());

        NativeSearchQuery build = queryBuilder.build();

        List<Product> productList = elasticsearchTemplate.queryForList(build, Product.class);

        /* List<Product> productList = elasticsearchTemplate.query(build, new ResultsExtractor<List<Product>>() {
           @Override
            public List<Product> extract(SearchResponse searchResponse) {
                SearchHits hits = searchResponse.getHits();
                List<Product> list = new ArrayList<>();
                for (SearchHit hit : hits) {
                    String s = hit.getSourceAsString();
                    try {
                        Product product = om.readValue(s, Product.class);
                        list.add(product);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return list;
            }
        });*/
        for (Product product : productList) {
            System.out.println("product = " + product);
        }
    }

    /**
     * 使用es原生TransportClient进行查询，查询所有product
     */
    public void getAllEs() {
        SearchRequestBuilder requestBuilder = esClient.prepareSearch("ecommerce").setTypes("product").setQuery(
                QueryBuilders.matchAllQuery()
        );
        System.out.println("查询语句：requestBuilder = " + requestBuilder);
        SearchResponse searchResponse = requestBuilder.get();
        String sourceAsString = searchResponse.getHits().getHits()[0].getSourceAsString();
        try {
            System.out.println(om.readValue(sourceAsString, Product.class));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * springdata分页查询
     */
    public void queryForPage() {
        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();
        searchQueryBuilder = searchQueryBuilder.withQuery(QueryBuilders.matchQuery("name", "chenyi"))
                .withFields("name", "desc")
                .withSort(SortBuilders.fieldSort("price").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0, 2));
        AggregatedPage<Product> productsAgg = elasticsearchTemplate.queryForPage(searchQueryBuilder.build(), Product.class);
        List<Product> productList = productsAgg.get().collect(Collectors.toList());
        System.out.println("productList = " + productList);
    }

    /**
     * 原生es查询
     */
    public void queryForPageEs() {
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("name", "chenyi");
        SearchRequestBuilder requestBuilder = esClient.prepareSearch("ecommerce").setTypes("product").setQuery(matchQueryBuilder)
                .setFrom(0).setSize(2)
                .addSort("price", SortOrder.DESC)
                .setFetchSource(new String[]{"name", "desc"}, null);
        System.out.println("requestBuilder = " + requestBuilder);
        SearchResponse searchResponse = requestBuilder.get();
        SearchHits hits = searchResponse.getHits();
        List<Product> productList = new ArrayList<>();
        for (SearchHit hit : hits) {
            String s = hit.getSourceAsString();
            try {
                productList.add(om.readValue(s, Product.class));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        productList.forEach(p -> System.out.println(p));
    }

    /**
     * 原生范围查询
     */
    public void queryWithRange() {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery().filter(QueryBuilders.rangeQuery("price").gt(250))
                .must(QueryBuilders.matchQuery("name", "chenyi"));
        SearchRequestBuilder requestBuilder = esClient.prepareSearch("ecommerce").setTypes("product").setQuery(boolQueryBuilder)
                .setFrom(0).setSize(2).addSort("price", SortOrder.DESC);
        System.out.println("requestBuilder = " + requestBuilder);
        SearchResponse searchResponse = requestBuilder.get();
        SearchHits hits = searchResponse.getHits();
        List<Product> productList = new ArrayList<>();
        for (SearchHit hit : hits) {
            String source = hit.getSourceAsString();
            try {
                Product product = om.readValue(source, Product.class);
                productList.add(product);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        productList.forEach(p -> System.out.println("p = " + p));
    }

    /**
     * 高亮查询
     */
    public void queryWithHighLight() {
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("name", "kama");
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("name").preTags("<span>").postTags("</span>").fragmentSize(150).numOfFragments(3);

        SearchRequestBuilder highlighter = esClient.prepareSearch("ecommerce").setTypes("product").setQuery(matchQueryBuilder)
                .highlighter(highlightBuilder);
        System.out.println("highlighter = " + highlighter);

        SearchResponse searchResponse = highlighter.get();
        SearchHits hits = searchResponse.getHits();
        for (SearchHit hit : hits) {
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            System.out.println("highlightFields = " + highlightFields);
        }
    }

    /**
     * 聚合查询
     */
    public void queryWithAgg() {
        TermsAggregationBuilder aggregationBuilder = AggregationBuilders.terms("tag_agg").field("tags").size(10);
        SearchRequestBuilder requestBuilder = esClient.prepareSearch("ecommerce").setTypes("product").addAggregation(aggregationBuilder);
        System.out.println("requestBuilder = " + requestBuilder);
        SearchResponse searchResponse = requestBuilder.get();
        Aggregations aggregations = searchResponse.getAggregations();
        StringTerms tagAgg = aggregations.get("tag_agg");
        List<StringTerms.Bucket> buckets = tagAgg.getBuckets();
        for (StringTerms.Bucket bucket : buckets) {
            String keyAsString = bucket.getKeyAsString();
            long docCount = bucket.getDocCount();
            System.out.println("keyAsString--->docCount = " + keyAsString + "---->" + docCount);
        }
    }

    /**
     * 聚合中嵌套聚合
     */
    public void queryAggWithAgg() {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery().must(QueryBuilders.termQuery("name", "chenyi"));
        TermsAggregationBuilder aggregationBuilder = AggregationBuilders.
                terms("tag_agg").field("tags").order(BucketOrder.aggregation("avg_price", false))
                .subAggregation(AggregationBuilders.avg("avg_price").field("price"));

        SearchRequestBuilder requestBuilder = esClient.prepareSearch("ecommerce").setTypes("product")
                .setQuery(boolQueryBuilder).addAggregation(aggregationBuilder);

        System.out.println("requestBuilder = " + requestBuilder);
        SearchResponse searchResponse = requestBuilder.get();
        Aggregations aggregations = searchResponse.getAggregations();
        StringTerms tagAgg = aggregations.get("tag_agg");
        Aggregation tag_agg = aggregations.get("tag_agg");
        List<StringTerms.Bucket> buckets = tagAgg.getBuckets();
        for (StringTerms.Bucket bucket : buckets) {
            String key = bucket.getKeyAsString();
            Aggregations subagg = bucket.getAggregations();
            InternalAvg internalAvg = subagg.get("avg_price");
            double avgPrice = internalAvg.getValue();
            System.out.println("key ----> avgPrice = " + key + " ------>" + avgPrice);
        }
    }

    /**
     * 按范围分组之后再进行分组
     */
    public void queryWithRangeAggSubAgg(){
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("producer", "producer");
        RangeAggregationBuilder rangeAggregationBuilder = AggregationBuilders.range("price_range_agg").field("price")
                .addRange(50, 350).addRange(350, 5000).addRange(5000, 50000)
                .subAggregation(AggregationBuilders.terms("tags_agg").field("tags"));


        SearchRequestBuilder requestBuilder = esClient.prepareSearch("ecommerce").setTypes("product")
                .setQuery(matchQueryBuilder).addAggregation(rangeAggregationBuilder);
        System.out.println("requestBuilder = " + requestBuilder);

        Aggregations aggregations = requestBuilder.get().getAggregations();
        Range price_range_agg = aggregations.get("price_range_agg");
        List<? extends Range.Bucket> buckets = price_range_agg.getBuckets();
        for ( Range.Bucket bucket : buckets) {
            String priceRange = bucket.getKeyAsString();
            StringTerms tags_agg = bucket.getAggregations().get("tags_agg");
            List<StringTerms.Bucket> tagsBucket = tags_agg.getBuckets();
            for (StringTerms.Bucket b : tagsBucket) {
                String keyAsString = b.getKeyAsString();
                long docCount = b.getDocCount();
                System.out.println("priceRange: {keyAsString : docCount} = "+priceRange+ ": {"+keyAsString+" : "+docCount+"}");
            }
        }
    }

    /**
     * 上传数据
     */
    public void insertEs() throws Exception{
        Product product = new Product("ningyao", "tongjingwudi", "999", "jin", Lists.list("beau", "sword"));
        String s = om.writeValueAsString(product);
        IndexRequestBuilder indexRequestBuilder = esClient.prepareIndex("jianlai", "xuezhong")
                .setSource(s,XContentType.JSON);
        IndexResponse indexResponse = indexRequestBuilder.get();
        int status = indexResponse.status().getStatus();
        System.out.println("status = " + status);
    }
}
