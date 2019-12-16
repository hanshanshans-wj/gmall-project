package com.wuju.gmall.gmalllistservice.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.wuju.gmall.Service.ListService;
import com.wuju.gmall.SkuLsInfo;
import com.wuju.gmall.SkuLsParams;
import com.wuju.gmall.SkuLsResult;
import com.wuju.gmall.config.RedisUtil;
import io.searchbox.client.JestClient;


import io.searchbox.core.*;
import io.searchbox.core.search.aggregation.TermsAggregation;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.AggregationStreams;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.redisson.Redisson;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class SkuLsServiceImpl implements ListService {
    @Autowired
    private JestClient jestClient;
    @Autowired
    private RedisUtil redisUtil;
    public static final String ES_INDEX="gmall";

    public static final String ES_TYPE="SkuInfo";
    @Override
    public void saveSkuInfo(SkuLsInfo skuLsInfo) {
        Index build = new Index.Builder(skuLsInfo).index(ES_INDEX).type(ES_TYPE).id(skuLsInfo.getId()).build();
        try {
            DocumentResult execute = jestClient.execute(build);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public SkuLsResult search(SkuLsParams skuLsParams) {
        String query = makeQueryStringForSearch(skuLsParams);
        Search build = new Search.Builder(query).addIndex(ES_INDEX).addType(ES_TYPE).build();
        SearchResult searchResult=null;
        try {
            searchResult = jestClient.execute(build);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SkuLsResult skuLsResult = makeResultForSearch(skuLsParams, searchResult);


        return skuLsResult;
    }

    @Override
    public void incrHotScore(String skuId) {
        Jedis jedis = redisUtil.getJedis();
        String key = "hotScore";
        //加完之后的数据
        Double hotScore = jedis.zincrby(key, 1, "skuId:" + skuId);
        if (hotScore%10==0){
            updateHotScore(skuId,  Math.round(hotScore));
        }
    }

    private void updateHotScore(String skuId, long hotScore) {
        String updateJson="{\n" +
                "  \"doc\": {\n" +
                "    \"hotScore\":"+hotScore+"\n" +
                "  }\n" +
                "}";
        Update update=new Update.Builder(updateJson).index(ES_INDEX).type(ES_TYPE).id(skuId).build();
        try {
            jestClient.execute(update);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 制作返回值数据：
    private SkuLsResult makeResultForSearch(SkuLsParams skuLsParams, SearchResult searchResult) {
        SkuLsResult skuLsResult = new SkuLsResult();
        // 页面显示商品新
        // List<SkuLsInfo> skuLsInfoList;
        List<SkuLsInfo> skuLsInfoList=new ArrayList<>(skuLsParams.getPageSize());
        // 给skuLsInfoList 赋值 从es 的查询结果集中获取！
        List<SearchResult.Hit<SkuLsInfo, Void>> hits = searchResult.getHits(SkuLsInfo.class);
        if (hits!=null&&hits.size()>0){
            for (SearchResult.Hit<SkuLsInfo, Void> hit : hits) {
// 如果全文检索：则skuame 并非高亮字段 ，获取高亮的skuName {highlight}
                SkuLsInfo skuLsInfo = hit.source;
                if (hit.highlight!=null&&hit.highlight.size()>0){
                    List<String> skuName = hit.highlight.get("skuName");
                    String s = skuName.get(0);
                    skuLsInfo.setSkuName(s);
                }
                skuLsInfoList.add(skuLsInfo);
            }
        }
        // 将结果集中的skuLsInfo 添加到集合
        // 该集合用于页面渲染！
        skuLsResult.setSkuLsInfoList(skuLsInfoList);
        // 查询出来的总条数
        // long total;
        Long total = searchResult.getTotal();
        skuLsResult.setTotal(total);
        // 总页数
        // long totalPages;
        // 10 3 4 | 9 3 3
        long totalPages =(searchResult.getTotal()+skuLsParams.getPageSize()-1)/skuLsParams.getPageSize();
        skuLsResult.setTotalPages(totalPages);
        List<String> attrValueIdList=new ArrayList<>();
        TermsAggregation groupby_attr = searchResult.getAggregations().getTermsAggregation("groupby_attr");
        List<TermsAggregation.Entry> buckets = groupby_attr.getBuckets();
        if (buckets!=null&&buckets.size()>0){
            for (TermsAggregation.Entry bucket : buckets) {
                String key = bucket.getKey();
                attrValueIdList.add(key);
            }
        }
        skuLsResult.setAttrValueIdList(attrValueIdList);
        return skuLsResult;
    }

    private String makeQueryStringForSearch(SkuLsParams skuLsParams) {
        // {} 查询器
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // { bool}
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        // {filter --- term}
        // 说明用户第一次访问查询的时候，是通过三级分类Id检索的
        if (skuLsParams.getCatalog3Id()!=null&&skuLsParams.getCatalog3Id().length()>0){
            // "filter": [{"term": {"catalog3Id": "61"}}
            TermQueryBuilder termQueryBuilder = new TermQueryBuilder("catalog3Id", skuLsParams.getCatalog3Id());
        // {bool -- filter -- term }
            boolQueryBuilder.filter(termQueryBuilder);
        }
        // 判断平台属性值Id 是否为空   // {"term": {"skuAttrValueList.valueId": "80"}}
        if (skuLsParams.getValueId()!=null&&skuLsParams.getValueId().length>0){
            for (String valueId : skuLsParams.getValueId()) {
                // {"term": {"skuAttrValueList.valueId": "80"}}
                TermQueryBuilder termQueryBuilder = new TermQueryBuilder("skuAttrValueList.valueId", valueId);
                boolQueryBuilder.filter(termQueryBuilder);
            }

        }
        // 用户第一次查询是通过全文检索方式进行查询的！
        // {bool -- must} 通过全文检索查询数据！

        if (skuLsParams.getKeyword()!=null&&skuLsParams.getKeyword().length()>0){
             /*
         "must": [
            {"match": {
                "skuName": "手机"
            }}
              ]
             */
            MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("skuName", skuLsParams.getKeyword());
            //  {bool -- must -- match}
            boolQueryBuilder.must(matchQueryBuilder);
            // 判断高亮
            HighlightBuilder highlightBuilder = new HighlightBuilder();
             /*
            "pre_tags": ["<span style=color:red>"],
            "fields": {"skuName": {}},
            "post_tags": ["</span>"]
             */
             highlightBuilder.field("skuName");
             highlightBuilder.preTags("<span style=color:red>");
             highlightBuilder.postTags("</span>");
             searchSourceBuilder.highlight(highlightBuilder);

        }
        //  设置排序
        searchSourceBuilder.sort("hotScore", SortOrder.DESC);
        // 分页
        int from=(skuLsParams.getPageNo()-1)*skuLsParams.getPageSize();
        searchSourceBuilder.from(from);
        // 每页的大小 | 查询之前修改每页显示的条数
        searchSourceBuilder.size(skuLsParams.getPageSize());
        // 聚合 平台属性值过滤
        /*
        "aggs": {
            "groupby_attr": {
              "terms": {
                "field": "skuAttrValueList.valueId"
              }
            }
          }
         */
        // 如果在查询的时候，出现 {skuAttrValueList.valueId} 字段 要你修改fielddata = true 的话。
        // 解决方案：skuAttrValueList.valueId.keyword
        TermsBuilder groupby_attr = AggregationBuilders.terms("groupby_attr").field("skuAttrValueList.valueId");
        searchSourceBuilder.aggregation(groupby_attr);
        // { query -- bool }
        searchSourceBuilder.query(boolQueryBuilder);
        String query = searchSourceBuilder.toString();
        // 打印：
        System.out.println("query:"+query);
        return query;
    }
}
