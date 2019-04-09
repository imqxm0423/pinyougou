package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.mapper.TbTypeTemplateMapper;
import com.pinyougou.pojo.*;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.*;

@Service
public class ItemSerchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private TbItemCatMapper itemCatMapper;

    @Autowired
    private TbTypeTemplateMapper typeTemplateMapper;

    @Autowired
    private TbSpecificationOptionMapper specificationOptionMapper;

    @Override
    public Map itemSearch(Map searchMap) {
        //创建一个最终返回类
        Map map = new HashMap();


        //放入返回值中
        map.putAll(searchList(searchMap));

        String category = (String) searchMap.get("category");
        if (!"".equals(category)) {
            map.putAll(searchBrandAndSpecList(category));
        } else {
            List<String> categoryList = searchCategoryList(searchMap);
            map.put("categoryList", categoryList);
            if (categoryList.size() > 0) {
                map.putAll(searchBrandAndSpecList(categoryList.get(0)));
            }
        }

        return map;
    }

    private Map searchList(Map searchMap) {

        Map map = new HashMap();

        String keywords = (String) searchMap.get("keywords");
        searchMap.put("keywords", keywords.replace(" ", ""));

        //查询条件
        HighlightQuery query = new SimpleHighlightQuery();

        HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");

        highlightOptions.setSimplePrefix("<em style='color:red'>");
        highlightOptions.setSimplePostfix("</em>");

        query.setHighlightOptions(highlightOptions);

        //1.1 关键词条件设置
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);

        //1.2 增加分类显示
        if (!"".equals(searchMap.get("category"))) {
            Criteria filterCriteria = new Criteria("item_category").is(searchMap.get("category"));
            FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
            query.addFilterQuery(filterQuery);
        }
        //1.3 品牌过滤显示
        if (!"".equals(searchMap.get("brand"))) {
            Criteria filterCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
            FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
            query.addFilterQuery(filterQuery);
        }

        //1.4过滤规格
        if (searchMap.get("spec") != null) {
            Map<String, String> specMap = (Map) searchMap.get("spec");
            for (String key : specMap.keySet()) {
                Criteria filterCriteria = new Criteria("item_spec_" + key).is(specMap.get(key));
                FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }

        //1.5 价格过滤
        if (!"".equals(searchMap.get("price"))) {
            String[] prices = ((String) searchMap.get("price")).split("-");
            //低价格
            if (!prices[0].equals("0")) {
                Criteria filterCriteria = new Criteria("item_price").greaterThanEqual(prices[0]);
                FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }

            //高价格
            if (!prices[1].equals("*")) {
                Criteria filterCriteria = new Criteria("item_price").lessThanEqual(prices[1]);
                FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }

        //1.6 分页

        Integer pageNo = (Integer) searchMap.get("pageNo");
        Integer pageSize = (Integer) searchMap.get("pageSize");

        if (pageNo == null) {
            pageNo = 1;
        }
        if (pageSize == null) {
            pageSize = 20;
        }

        //起始查询
        query.setOffset((pageNo - 1) * pageSize);
        //查询记录数
        query.setRows(pageSize);

        //1.7 排序
        String sortValue = (String) searchMap.get("sort");
        String sortField = (String) searchMap.get("sortField");

        if (sortValue != null && !sortValue.equals("")) {
            if (sortValue.equals("ASC")) {
                Sort sort = new Sort(Sort.Direction.ASC, "item_" + sortField);
                query.addSort(sort);
            }
            if (sortValue.equals("DESC")) {
                Sort sort = new Sort(Sort.Direction.DESC, "item_" + sortField);
                query.addSort(sort);
            }

        }


        //************************************************

        //查询
        HighlightPage<TbItem> highlightPage = solrTemplate.queryForHighlightPage(query, TbItem.class);

        for (HighlightEntry<TbItem> h : highlightPage.getHighlighted()) {

            TbItem tbItem = h.getEntity();

            if (h.getHighlights().size() > 0 && h.getHighlights().get(0).getSnipplets().size() > 0) {

                tbItem.setTitle(h.getHighlights().get(0).getSnipplets().get(0));

            }
        }
        map.put("rows", highlightPage.getContent());

        //返回总页码
        map.put("totalPages", highlightPage.getTotalPages());

        //返回总记录数
        map.put("total", highlightPage.getTotalElements());

        return map;
    }

    private List<String> searchCategoryList(Map searchMap) {

        List<String> list = new ArrayList<>();

        Query query = new SimpleQuery("*:*");
        //按照关键字查询

        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);

        //设置分组选项
        GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");
        query.setGroupOptions(groupOptions);

        //得到分组页
        GroupPage<TbItem> groupPage = solrTemplate.queryForGroupPage(query, TbItem.class);

        //根据列得到分组结果集
        GroupResult<TbItem> groupResult = groupPage.getGroupResult("item_category");

        //得到分组结果入口页
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        //得到分组入口集合
        List<GroupEntry<TbItem>> content = groupEntries.getContent();

        for (GroupEntry<TbItem> entry : content) {

            list.add(entry.getGroupValue());
        }

        return list;
    }


    @Autowired
    private RedisTemplate redisTemplate;


    private Map searchBrandAndSpecList(String category) {

        Map map = new HashMap();

        Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(category);

        //如果缓存有，从缓存拿
        if (typeId != null) {
            List brandList = (List) redisTemplate.boundHashOps("brandList").get(typeId);
            List specList = (List) redisTemplate.boundHashOps("specList").get(typeId);
            map.put("brandList", brandList);//返回值添加品牌列表
            map.put("specList", specList);

        } else {
            //如果缓存没有 ，从数据库拿
            TbItemCatExample example = new TbItemCatExample();
            TbItemCatExample.Criteria criteria = example.createCriteria();
            criteria.andNameEqualTo(category);
            List<TbItemCat> catList = itemCatMapper.selectByExample(example);
            if (catList.size() > 0) {
                Long typeId1 = catList.get(0).getTypeId();

                List brandList = (List) redisTemplate.boundHashOps("brandList").get(typeId1);

                List<Map> specList = (List) redisTemplate.boundHashOps("specList").get(typeId1);

                //如果缓存没有品牌
                if (brandList == null){
                    TbTypeTemplate typeTemplate = typeTemplateMapper.selectByPrimaryKey(typeId1);
                    brandList = JSON.parseArray(typeTemplate.getBrandIds(), Map.class);
                    //放入缓存
                    redisTemplate.boundHashOps("brandList").put(typeTemplate.getId(),brandList);
                }
                //如果缓存没有规格
                if (specList == null){
                    TbTypeTemplate typeTemplate = typeTemplateMapper.selectByPrimaryKey(typeId1);
                    String specIdsString = typeTemplate.getSpecIds();

                    specList = JSON.parseArray(specIdsString, Map.class);

                    //查询详细规格内部资料
                    for (Map specMap : specList) {

                        TbSpecificationOptionExample speExample = new TbSpecificationOptionExample();
                        TbSpecificationOptionExample.Criteria speCria = speExample.createCriteria();
                        speCria.andSpecIdEqualTo(new Long((Integer) specMap.get("id")));
                        List<TbSpecificationOption> tbSpecificationOptions = specificationOptionMapper.selectByExample(speExample);
                        specMap.put("options", tbSpecificationOptions);
                    }

                    redisTemplate.boundHashOps("specList").put(typeTemplate.getId(),specList);

                }

                map.put("brandList", brandList);//返回值添加品牌列表
                map.put("specList", specList);
            }

        }

        return map;
    }

    /**
     * 导入solr
     *
     * @param list
     */
    @Override
    public void importList(List list) {

        solrTemplate.saveBeans(list);
        solrTemplate.commit();
    }

    /**
     * 从solr删除
     * @param goodsId
     */
    @Override
    public void deleteListFromSolr(Long[] goodsId) {

        Query query = new SimpleQuery("*:*");

        Criteria criteria = new Criteria("item_goodsid").in(Arrays.asList(goodsId));

        query.addCriteria(criteria);
        solrTemplate.delete(query);
        solrTemplate.commit();

    }
}
