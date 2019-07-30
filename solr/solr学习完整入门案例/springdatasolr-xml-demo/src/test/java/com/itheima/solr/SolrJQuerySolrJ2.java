package com.itheima.solr;

import java.util.List;
import java.util.Map;

import javax.jws.soap.SOAPBinding;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.GroupCommand;
import org.apache.solr.client.solrj.response.GroupResponse;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.GroupParams;
import org.junit.Test;

public class SolrJQuerySolrJ2 {
	/**
	 * 简单查询
	 * 
	 * @throws Exception
	 */
	@Test
	public void queryDocument() throws Exception {
		// 第一步：创建一个SolrServer对象
		 SolrServer solrServer = new HttpSolrServer("http://192.168.25.128:8080/solr");
		// 第二步：创建一个SolrQuery对象。
		SolrQuery query = new SolrQuery();
		// 第三步：向SolrQuery中添加查询条件、过滤条件。。。
		query.setQuery("*:*");
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
	 * 
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
		// 指定默认搜索域
		query.set("df", "item_keywords");
		// 开启高亮显示
		query.setHighlight(true);
		// 高亮显示的域
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
			// 取高亮显示
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
	 * solrJ 实现Facet查询
	 * 
	 * @throws Exception
	 */
	@Test
	public void queryFacetField() throws Exception {

		SolrServer solrServer = new HttpSolrServer("http://192.168.25.128:8080/solr");

		SolrQuery query = new SolrQuery();// 建立一个新的查询

		query.setQuery("item_title:三星");

		query.setFacet(true);// 设置facet=on

		// 分类信息分为：薪水，发布时间，教育背景，工作经验，公司类型，工作类型

		query.addFacetField(new String[] { "item_price", "item_brand", "item_category" });// 设置需要facet的字段

		query.setFacetLimit(10);// 限制facet返回的数量

		query.setFacetMissing(false);// 不统计null的值

		query.setFacetMinCount(1);// 设置返回的数据中每个分组的数据最小值，比如设置为1，则统计数量最小为1，不然不显示

		// query.addFacetQuery("publishDate:[2014-04-11T00:00:00Z
		// TO2014-04-13T00:00:00Z]");

		QueryResponse response = solrServer.query(query);

		System.out.println("查询时间：" + response.getQTime());

		List<FacetField> facets = response.getFacetFields();// 返回的facet列表

		for (FacetField facet : facets) {

			System.out.println(facet.getName());

			System.out.println("—————-");

			List<Count> counts = facet.getValues();

			for (Count count : counts) {

				System.out.println(count + "===" + count.getName() + ":" + count.getCount());

			}

			System.out.println();

		}
	}
	/**
	 * solrj 实现Group分组查询
	 * @throws SolrServerException
	 */
	@Test
	public void queryGroupField() throws SolrServerException {
		SolrServer solrServer = new HttpSolrServer("http://192.168.25.128:8080/solr");

		SolrQuery query = new SolrQuery("item_keywords:三星");

		// 设置通过facet查询为true，表示查询时使用facet机制

		query.setParam(GroupParams.GROUP, true);

		query.setParam(GroupParams.GROUP_FIELD, "item_category");

		// 设置每个quality对应的

		query.setParam(GroupParams.GROUP_LIMIT, "2");

		// 设置返回doc文档数据，因只需要数量，故设置为0

		query.setRows(10);

		QueryResponse response = solrServer.query(query);

		if (response != null) {

			GroupResponse groupResponse = response.getGroupResponse();

			if (groupResponse != null) {

				List<GroupCommand> groupList = groupResponse.getValues();

				for (GroupCommand groupCommand : groupList) {

					List<Group> groups = groupCommand.getValues();
				System.out.println("groupCommand"+groupCommand);

					for (Group group : groups) {

						System.out.println(group.getGroupValue() + "数量为：" + group.getResult().getNumFound());

					}

				}

			}

		}

	}

}
