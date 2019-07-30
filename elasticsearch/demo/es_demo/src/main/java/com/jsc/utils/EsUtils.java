package com.jsc.utils;

import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
 
public class EsUtils {
    //ES client
    private static TransportClient client;//获取一个ES的客户端对象叫client
    private static final String CLUSTER_NAME = "elasticsearch";
    private static final String HOST_IP = "127.0.0.1";
    private static final int TCP_PORT = 9300;
    static Settings sttings = Settings.builder()
            .put("cluster.name", CLUSTER_NAME)//集群名字CLUSTER_NAME
            .build();

    /**
     * 获取client对象
     */
    public static TransportClient getCLient() {
        if (client == null) {
            synchronized (TransportClient.class) {
                try {
                    client = new PreBuiltTransportClient(sttings)
                            .addTransportAddress(
                                    new TransportAddress(InetAddress.getByName(HOST_IP), TCP_PORT) //ES的master的主机名和端口号默认9300
                            );
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("连接成功" + client.toString());
        return client;
    }

    /**
     * 通过ES的客户端对象，获取索引管理对象
     * 获取index  admin  对象
     */

    public static IndicesAdminClient getIndicesAdminClient() {
        return getCLient().admin().indices();
    }

    /**
     * 创建一个 index  库
     * 注意：es里面索引库的名称 都需要小写
     */
    public static boolean createIndex(String indexName) {
        CreateIndexResponse response = getIndicesAdminClient()
                .prepareCreate(indexName.toLowerCase()) //通过索引管理对象创建索引
                .setSettings(
                        Settings.builder()
                                .put("index.number_of_shards", 3) //分片个数
                                .put("index.number_of_replicas", 2) //副分片个数
                ).execute().actionGet();
        return response.isShardsAcked();//返回boolean值，是否创建成功
    }

    /**
     * 创建index，对上面方法的优化
     *
     * @param indexName
     * @param numberShards
     * @param numberreplicas
     * @return
     */
    public static boolean createIndex(String indexName, int numberShards, int numberreplicas) {
        CreateIndexResponse response = getIndicesAdminClient()
                .prepareCreate(indexName.toLowerCase())
                .setSettings(
                        Settings.builder()
                                .put("index.number_of_shards", numberShards)
                                .put("index.number_of_replicas", numberreplicas)
                ).execute().actionGet();
        return response.isShardsAcked();
    }

    /**
     * 删除index
     */
    public static boolean deleteIndex(String indexName) {
        DeleteIndexResponse response = getIndicesAdminClient()
                .prepareDelete(indexName.toLowerCase())
                .execute().actionGet();
        return response.isAcknowledged();
    }

    /**
     * 设置mapping   建表语句
     *
     * @param indexName
     * @param typeName
     * @param mappingStr
     * @return
     */
    public static boolean setIndexMapping(String indexName, String typeName, XContentBuilder mappingStr) {
        IndicesAdminClient indicesAdminClient = getIndicesAdminClient();

        PutMappingResponse putMappingResponse = indicesAdminClient.preparePutMapping(indexName.toLowerCase())
                .setType(typeName)
                .setSource(mappingStr)
                .execute()
                .actionGet();

        return putMappingResponse.isAcknowledged();
    }

    @Test
    public void test1() {
        /**
         * PUT my_index/_mapping/type_1709x
         {
         "properties": {
         "user":{
         "type": "text"
         },
         "postDate":{
         "type": "date"
         },
         "message":{
         "type": "text"
         }
         }
         }
         *
         */

        try {
            XContentBuilder xContentBuilder = jsonBuilder().startObject()
                    .startObject("properties")
                    .startObject("user")
                    .field("type", "text")
                    .endObject()
                    .startObject("postDate")
                    .field("type", "date")
                    .endObject()
                    .startObject("message")
                    .field("type", "text")
                    .endObject()
                    .endObject()
                    .endObject();

            setIndexMapping("my_index", "type_1709x", xContentBuilder);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 往type里面添加一条数据
     */
    @Test
    public void test4() {
        String json = "{\n" +
                "  \"user\":\"malaoshi\",\n" +
                "  \"postdate\":\"2018-11-11\",\n" +
                "  \"message\":\"haokaixin\"\n" +
                "}";
        TransportClient client = getCLient();
        IndexResponse indexResponse = client.prepareIndex("my_index", "type_1709x", "1")
                .setSource(json, XContentType.JSON)
                .get();
        System.out.println(indexResponse.status().getStatus());
    }

    /**
     * 往type里面添加一条数据
     */
    @Test
    public void test5() {
        HashMap<String, String> json = new HashMap<String, String>();
        json.put("user", "Xiao li");
        json.put("postdate", "2017-12-12");
        json.put("message", "Xiao li trying out ES");

        TransportClient client = getCLient();
        IndexResponse indexResponse = client.prepareIndex("my_index", "type_1709x", "2")
                .setSource(json, XContentType.JSON)
                .get();
        System.out.println(indexResponse.status().getStatus());

    }

    /**
     * 查询数据
     */
    @Test
    public void test6() {
        TransportClient client = getCLient();
        GetResponse getFields = client.prepareGet("my_index", "type_1709x", "2").execute().actionGet();
        //{"postdate":"2017-12-12","message":"Xiao li trying out ES","user":"Xiao li"}
        System.out.println(getFields.getSourceAsString());
        //{postdate=2017-12-12, message=Xiao li trying out ES, user=Xiao li}
        System.out.println(getFields.getSourceAsMap());
    }

    /**
     * 修改数据
     */
    @Test
    public void test7() {
        HashMap<String, String> json = new HashMap<String, String>();
        json.put("user", "xiao li");
        json.put("postdate", "2017-11-11");
        json.put("message", "xiao li trying out elasticsearch");

        TransportClient client = getCLient();

        UpdateResponse updateResponse = client.prepareUpdate("my_index", "type_1709x", "2")
                .setDoc(json)
                .execute().actionGet();
        System.out.println(updateResponse.status().getStatus());
    }

    /**
     * 删除
     */
    @Test
    public void test8() {
        TransportClient client = getCLient();
        DeleteResponse deleteResponse = client.prepareDelete("my_index", "type_1709x", "2").execute().actionGet();
        System.out.println(deleteResponse.status().getStatus());

    }

    /**
     * 具有条件的查询
     */
    @Test
    public void test9() {
        TransportClient client = getCLient();
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("user", "malaoshi");


        SearchResponse searchResponse = client.prepareSearch("my_index")
                .setTypes("type_1709x")
                .setQuery(termQueryBuilder)
                .execute().actionGet();

        SearchHits hits = searchResponse.getHits();
        System.out.println(hits.totalHits);
        for (SearchHit hit : hits) {
            System.out.println(hit.getScore());
            System.out.println(hit.getSourceAsString());
        }

    }


    public static void setMapping() {
        try {
            XContentBuilder xContentBuilder1 = jsonBuilder().startObject()
                    .startObject("properties")
                    .startObject("id")
                    .field("type", "long")
                    .endObject()
                    .startObject("title")
                    .field("type", "text")
                    .field("analyzer", "ik_max_word")
                    .field("search_analyzer", "ik_max_word")
                    .endObject()
                    .startObject("content")
                    .field("type", "text")
                    .field("analyzer", "ik_max_word")
                    .field("search_analyzer", "ik_max_word")
                    .endObject()

                    .startObject("url")
                    .field("type", "keyword")
                    .endObject()

                    .startObject("reply")
                    .field("type", "long")
                    .endObject()

                    .startObject("source")
                    .field("type", "keyword")
                    .endObject()

                    .startObject("postDate")
                    .field("type", "date")
                    .field("format", "yyyy-MM-dd HH:mm:ss")
                    .endObject()

                    .endObject()
                    .endObject();

            setIndexMapping("sportnews", "news", xContentBuilder1);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static void main(String[] args) {
        // createIndex("my_index",3,2);
        // deleteIndex("test1709");
        /**
         * 第一步：创建Index 库
         */
        // createIndex("sportnews",3,1);
        /**
         * 第二步：创建表
         *   设置mapping
         */
        // setMapping();
        /**
         * 第三步：
         * 从MySQL导入数据到ES
                */
//        Dao dao = new Dao();
//        dao.getConnection();
//        dao.mysqlToEs();

    }
}