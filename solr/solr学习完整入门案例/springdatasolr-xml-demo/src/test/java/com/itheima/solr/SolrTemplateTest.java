package com.itheima.solr;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.FilterQuery;
import org.springframework.data.solr.core.query.GroupOptions;
import org.springframework.data.solr.core.query.HighlightOptions;
import org.springframework.data.solr.core.query.HighlightQuery;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleFilterQuery;
import org.springframework.data.solr.core.query.SimpleHighlightQuery;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.GroupEntry;
import org.springframework.data.solr.core.query.result.GroupPage;
import org.springframework.data.solr.core.query.result.GroupResult;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightEntry.Highlight;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext-solr.xml")
public class SolrTemplateTest {
	@Autowired
	private SolrTemplate solrTemplate;

	// 测试创建插入document
	@Test
	public void addTest() {
		Collection<TbItem> arrayList = new ArrayList<TbItem>();
		TbItem item = new TbItem();
		item.setId(666666666L);
		item.setBrand("测试7373773--卡华为");
		item.setCategory("测试7373773--手机");
		item.setGoodsId(1L);
		item.setSeller("测试7373773--华为 2 号专卖店");
//		item.setTitle("测试7373773--华为 Mate9");
		item.setPrice(new BigDecimal(2000));
		TbItem item2 = new TbItem();
		item2.setId(8888888L);
		item2.setBrand("测试宝哥--宝哥");
		item2.setCategory("测试宝哥--宝哥");
		item2.setGoodsId(1L);
		item2.setSeller("测试宝哥--宝哥2 号专卖店");
//		item2.setTitle("测试宝哥--宝哥Mate9");
		item2.setPrice(new BigDecimal(2000));
		arrayList.add(item);
		arrayList.add(item2);
		
//		solrTemplate.saveBean(item);
		
		solrTemplate.saveBeans(arrayList);
		solrTemplate.commit();
	}

	// 根据document unique key来检索具体的document
	@Test
	public void testFindOne() {
//		TbItem item = solrTemplate.getById(1, TbItem.class);
		TbItem tbItem = solrTemplate.getById(666666666l, TbItem.class);
//		solrTemplate.getById(ids, clazz)
		System.out.println(tbItem.getTitle());
	}

	// 删除指定的document
	@Test
	public void testDelete() {
		solrTemplate.deleteById("666666666");
		solrTemplate.commit();
	}

	// 批量插入一批document
	@Test
	public void testAddList() {
		List<TbItem> list = new ArrayList<TbItem>();
		for (int i = 0; i < 100; i++) {
			TbItem item = new TbItem();
			item.setId(i + 1L);
			item.setBrand("测试---华为" + i);
			item.setCategory("测试---手机" + i);
			item.setGoodsId(1L);
			item.setSeller("测试---华为 2 号专卖店" + i);
//			item.setTitle("测试---华为 Mate" + i);
			item.setPrice(new BigDecimal(2000 + i));
			list.add(item);
		}
		solrTemplate.saveBeans(list);
		solrTemplate.commit();
	}

	// 分页查询  第2页，每页显示10条
	@Test
	public void testPageQuery() {
		Query query = new SimpleQuery("*:*");
		query.setOffset(10);// 开始索引（默认 0）
		query.setRows(10);// 每页记录数(默认 10)
		ScoredPage<TbItem> page = solrTemplate.queryForPage(query, TbItem.class);
		System.out.println("总记录数：" + page.getTotalElements());
		List<TbItem> list = page.getContent();
		showList(list);
	}

	// criteria 条件查询
	@Test
	public void testPageQueryMutil() {
		Query query = new SimpleQuery("*:*");
		Criteria criteria = new Criteria("item_title").is("三星").and("item_price").between(0, 500);
//		criteria = criteria.and("item_title").contains("5");
//		criteria.and("item_price").between(0, 500);
		
		FilterQuery filterQuery = new SimpleFilterQuery();
		Criteria filterCriteria=new Criteria("item_price").between(0, 500);
		filterQuery.addCriteria(filterCriteria);
		query.addCriteria(criteria);
		query.addFilterQuery(filterQuery);
		
		// query.setOffset(20);//开始索引（默认 0）
		// query.setRows(20);//每页记录数(默认 10)
		ScoredPage<TbItem> page = solrTemplate.queryForPage(query, TbItem.class);
		System.out.println("总记录数：" + page.getTotalElements());
		List<TbItem> list = page.getContent();
		showList(list);
	}

	// 删除所有document
	@Test
	public void testDeleteAll() {
		Query query = new SimpleQuery("*:*");
		solrTemplate.delete(query);
		solrTemplate.commit();
	}

	private void showList(List<TbItem> list) {
		for (TbItem item : list) {
			System.out.println(item.getTitle().get(0) + item.getPrice());
		}
	}

	/**
	 * select count(*) from tb_item group by item_category_id facet分组 group分组
	 * 
	 * 
	 */
	@Test
	public void testGroupQuery() {		
		List<String> list = new ArrayList<String>();

		Query query = new SimpleQuery("*:*");
		// 根据关键字查询
		Criteria criteria = new Criteria("item_keywords").is("三星");

		query.addCriteria(criteria);
		// 设置分组选项
		GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");
		groupOptions.setLimit(2);

		query.setGroupOptions(groupOptions);
		// 获取分组页
		GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
		// 获取分组结果对象
		GroupResult<TbItem> groupResult = page.getGroupResult("item_category");
		// 获取分组入口页
		Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
		// 获取分组入口集合
		List<GroupEntry<TbItem>> entryList = groupEntries.getContent();

		for (GroupEntry<TbItem> entry : entryList) {
			list.add(entry.getGroupValue()); // 将分组的结果添加到返回值中
			Page<TbItem> result = entry.getResult();
			List<TbItem> content = result.getContent();
			for (TbItem tbItem : content) {
				System.out.println("分组查询到的对象" + tbItem);
			}
		}
		System.out.println(list);
	}

	@Test
	public void testHighlightQuery() {
		// 高亮选项初始化
		HighlightQuery query = new SimpleHighlightQuery();
		HighlightOptions highlightOptions = new HighlightOptions().addField("item_title").addField("item_brand").addHighlightParameter("hl.snippets", 2);// 高亮域
		highlightOptions.setSimplePrefix("<em style='color:red'>");// 前缀
		highlightOptions.setSimplePostfix("</em>");
		query.setHighlightOptions(highlightOptions);// 为查询对象设置高亮选项

		// 1.1 关键字查询s
		Criteria criteria = new Criteria("item_keywords").is("三星");
		query.addCriteria(criteria);
		// 高亮页对象
		HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);
		// 高亮入口集合(每条记录的高亮入口)
		List<HighlightEntry<TbItem>> entryList = page.getHighlighted();
		for (HighlightEntry<TbItem> entry : entryList) {
			// 获取高亮列表(高亮域的个数)
			List<Highlight> highlightList = entry.getHighlights();
			/*
			 * for(Highlight h:highlightList){ List<String> sns =
			 * h.getSnipplets();//每个域有可能存储多值 System.out.println(sns); }
			 */
			if (highlightList.size() > 0 && highlightList.get(0).getSnipplets().size() > 0) {
				TbItem item = entry.getEntity();
				item.setTitle(highlightList.get(0).getSnipplets());
				System.out.println(item.getTitle());
			}

		}

	}

	@Test
	public void testPutAll() {
		Map map1 = new HashMap();
		map1.put("01", "张三");
		map1.put("02", "李四");

		Map map2 = new HashMap();
		map2.put("02", "王五");
		map2.put("04", "赵六");

		map2.putAll(map1);
		System.out.println("map1中的元素===" + map1);
		System.out.println("map2中的元素===" + map2);
	}
}
