package com.itheima.solr;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

public class SolrJWriteTest {
    /**
     * 使用SolrJ SolrServer添加索引
     * @throws SolrServerException
     * @throws IOException
     */
    @Test
    public void testSolrJAddDocument() throws SolrServerException, IOException {
     // 第一步：把solrJ的jar包添加到工程中。
        // 第二步：创建一个SolrServer，使用HttpSolrServer创建对象。
        SolrServer solrServer = new HttpSolrServer("http://192.168.25.128:8080/solr");
        // 第三步：创建一个文档对象SolrInputDocument对象。
        SolrInputDocument document = new SolrInputDocument();
        // 第四步：向文档中添加域。必须有id域，域的名称必须在schema.xml中定义。
        document.addField("id", "73737373");
        document.addField("item_title", "测试商品7373");
        document.addField("item_price", "7373");
        // 第五步：把文档添加到索引库中。
        solrServer.add(document);
        // 第六步：提交。
        solrServer.commit();
    }
    /**
     * 调用SolrServer对象的根据id删除的方法
     * @throws Exception
     */
    @Test
    public void deleteDocumentById() throws Exception {
        // 第一步：创建一个SolrServer对象。
        SolrServer solrServer = new HttpSolrServer("http://192.168.25.128:8080/solr");
        // 第二步：调用SolrServer对象的根据id删除的方法。
        solrServer.deleteById("1");
        // 第三步：提交。
        solrServer.commit();
    }
    /**
     * 删除查询的索引
     * @throws Exception
     */
    @Test
    public void deleteDocumentByQuery() throws Exception {
        SolrServer solrServer = new HttpSolrServer("http://192.168.25.128:8080/solr");
        solrServer.deleteByQuery("title:change.me");
        solrServer.commit();
    }
    /**
     * 简单查询
     * @throws Exception
     */
    @Test
    public void queryDocument() throws Exception {
        // 第一步：创建一个SolrServer对象
        SolrServer solrServer = new HttpSolrServer("http://192.168.25.128:8080/solr");
        // 第二步：创建一个SolrQuery对象。
        SolrQuery query = new SolrQuery();
        // 第三步：向SolrQuery中添加查询条件、过滤条件。。。
        query.setQuery("item_price:[1000 TO  2000]");
        // 第四步：执行查询。得到一个Response对象。
        QueryResponse response = solrServer.query(query);
        // 第五步：取查询结果。
        SolrDocumentList solrDocumentList = response.getResults();
        System.out.println("查询结果的总记录数：" + solrDocumentList.getNumFound());
        // 第六步：遍历结果并打印。
        for (SolrDocument solrDocument : solrDocumentList) {
            System.out.println(solrDocument.get("id"));
            System.out.println(solrDocument.get("item_title"));
            System.out.println(solrDocument.get("item_price"));
        }
    }
    /**
     * 带高亮显示
     * @throws Exception
     */
    @Test
    public void queryDocumentWithHighLighting() throws Exception {
        // 第一步：创建一个SolrServer对象
        SolrServer solrServer = new HttpSolrServer("http://192.168.25.128:8080/solr");
        // 第二步：创建一个SolrQuery对象。
        SolrQuery query = new SolrQuery();
        // 第三步：向SolrQuery中添加查询条件、过滤条件。。。
        query.setQuery("三星");
        //指定默认搜索域
        query.set("df", "item_keywords");
        //开启高亮显示
        query.setHighlight(true);
        //高亮显示的域
        query.addHighlightField("item_title");
        query.setHighlightSimplePre("<em>");
        query.setHighlightSimplePost("</em>");
        // 第四步：执行查询。得到一个Response对象。
        QueryResponse response = solrServer.query(query);
        // 第五步：取查询结果。
        SolrDocumentList solrDocumentList = response.getResults();
        System.out.println("查询结果的总记录数：" + solrDocumentList.getNumFound());
        // 第六步：遍历结果并打印。
        for (SolrDocument solrDocument : solrDocumentList) {
            System.out.println(solrDocument.get("id"));
            //取高亮显示
            Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();
            List<String> list = highlighting.get(solrDocument.get("id")).get("item_title");
            String itemTitle = null;
            if (list != null && list.size() > 0) {
                itemTitle = list.get(0);
            } else {
                itemTitle = (String) solrDocument.get("item_title");
            }
            System.out.println(itemTitle);
            System.out.println(solrDocument.get("item_price"));
        }
    }
    /**
     * solrcloud添加索引
     * @throws Exception
     */
    @Test
    public void testSolrCloudAddDocument() throws Exception {
        // 第一步：把solrJ相关的jar包添加到工程中。
        // 第二步：创建一个SolrServer对象，需要使用CloudSolrServer子类。构造方法的参数是zookeeper的地址列表。
        //参数是zookeeper的地址列表，使用逗号分隔
        CloudSolrServer solrServer = new CloudSolrServer("192.168.25.128:2182,192.168.25.128:2183,192.168.25.128:2184");
        // 第三步：需要设置DefaultCollection属性。
        solrServer.setDefaultCollection("collection2");
        // 第四步：创建一SolrInputDocument对象。
        SolrInputDocument document = new SolrInputDocument();
        // 第五步：向文档对象中添加域
        document.addField("item_title", "测试商品");
        document.addField("item_price", "100");
        document.addField("id", "test001");
        // 第六步：把文档对象写入索引库。
        solrServer.add(document);
        // 第七步：提交。
        solrServer.commit();
    }
    /**
     * solrcloud删除索引
     * @throws Exception
     */
    @Test
    public void toDeleteDocument() throws Exception {
        // 第一步：把solrJ相关的jar包添加到工程中。
        // 第二步：创建一个SolrServer对象，需要使用CloudSolrServer子类。构造方法的参数是zookeeper的地址列表。
        //参数是zookeeper的地址列表，使用逗号分隔
        CloudSolrServer solrServer = new CloudSolrServer("192.168.25.128:2182,192.168.25.128:2183,192.168.25.128:2184");
        // 第三步：需要设置DefaultCollection属性。
        solrServer.setDefaultCollection("collection2");
        // 第四步：删除所有
        solrServer.deleteByQuery("*:*");
        // 第七步：提交。
        solrServer.commit();
    }
}
