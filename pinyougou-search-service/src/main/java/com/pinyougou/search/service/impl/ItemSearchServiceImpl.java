package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//默认dubbo超时1秒
@Service(timeout=3000)
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    @Override
    public Map<String, Object> search(Map searchMap) {
        Map map=new HashMap();

        //搜索关键字去空格
        String keywords= (String) searchMap.get("keywords");
        searchMap.put("keywords",keywords.replace(" ",""));

        //1.查询列表
        map.putAll(searchList(searchMap));
        //2.分组查询分类名称
        List<String> categoryList  = searchCategoryList(searchMap);
        map.put("categoryList",categoryList);
        //3.根据分类名称(第一个名称)在redis中查询品牌以及规格和规格选项
        String category = (String) searchMap.get("category");
        if("".equals(category)){//如果没有分类名称，按照第一个查询
            //System.out.println(categoryList.size());
            if(categoryList.size()>0){
                map.putAll(searchBrandAndSpecList(categoryList.get(0)));
            }
        }else {//如果有分类名称
            map.putAll(searchBrandAndSpecList(category));
        }



        return map;
    }

    @Override
    public void importList(List list) {
        solrTemplate.saveBeans(list);
        solrTemplate.commit();
    }

    @Override
    public void deleteByGoodsIds(List goodsIdList) {
        System.out.println("删除商品ID"+goodsIdList);
        Query query=new SimpleQuery();
        Criteria criteria=new Criteria("item_goodsid").in(goodsIdList);
        query.addCriteria(criteria);
        solrTemplate.delete(query);
        solrTemplate.commit();
    }

    //查询列表

    /**返回数据类型
     * "rows": [{
     * 		"barcode": null,
     * 		"brand": "三星",
     * 		"cartThumbnail": null,
     * 		"category": "手机",
     * 		"categoryid": null,
     * 		"costPirce": null,
     * 		"createTime": null,
     * 		"goodsId": 1,
     * 		"id": 1099192,
     * 		"image": "http://img10.360buyimg.com/n1/s450x450_jfs/t3457/294/236823024/102048/c97f5825/58072422Ndd7e66c4.jpg",
     * 		"isDefault": null,
     * 		"itemSn": null,
     * 		"marketPrice": null,
     * 		"num": null,
     * 		"price": 1899.0,
     * 		"sellPoint": null,
     * 		"seller": "三星",
     * 		"sellerId": null,
     * 		"spec": null,
     * 		"specMap": {
     * 			"网络": "联通4G",
     * 			"机身内存": "16G"
     *                },
     * 		"status": null,
     * 		"stockCount": null,
     * 		"title": "<em style='color:red'>三星</em> Galaxy S4 (I9507V) 黑色 联通4G手机",
     * 		"updateTime": null* 	}]
     */
    private  Map searchList(Map searchMap){
        Map map=new HashMap();

        //设置高亮查询的域
        HighlightQuery query=new SimpleHighlightQuery();
        HighlightOptions options=new HighlightOptions().addField("item_title");  //设置高亮字段
        options.setSimplePrefix("<em style='color:red'>");//设置高亮前缀
        options.setSimplePostfix("</em>");//设置高亮后缀
        query.setHighlightOptions(options);  //设置高亮选项


        //1.1按照关键字查询
        Criteria criteria=new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);  //添加关键字查询

        //1.2按分类筛选
        if(!"".equals(searchMap.get("category"))){
            FilterQuery filterQuery=new SimpleFilterQuery();
            Criteria filterCriteria=new Criteria();
            filterCriteria.and("item_category").is(searchMap.get("category"));
            filterQuery.addCriteria(filterCriteria);
        }

        //1.3按品牌筛选
        if(!"".equals(searchMap.get("brand"))){
            Criteria filterCriteria=new Criteria("item_brand").is(searchMap.get("brand"));
            FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
            query.addFilterQuery(filterQuery);
        }

        //1.4按规格选项筛选
        if(searchMap.get("spec")!=null){
            Map<String,String> specMap= (Map<String, String>) searchMap.get("spec");
            for (String key : specMap.keySet()) {
                Criteria filterCriteria=new Criteria("item_spec_"+key).is(specMap.get(key));
                FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }

        //1.5按价格筛选
        if(!"".equals(searchMap.get("price"))){
            String price= (String) searchMap.get("price"); //100-500
            String[] prices = price.split("-");
            /*System.out.println(prices[0]+"---------"+prices[1]);*/
            FilterQuery filterQuery=new SimpleFilterQuery();
            if(!prices[0].equals("0")){//如果区间终点不等于0
                Criteria filterCriteria=new Criteria("item_price").greaterThanEqual(prices[0]);//springdateslor底层会自动将字符串转化为数字
                filterQuery.addCriteria(filterCriteria);
            }
            if(!prices[1].equals("*")){//如果区间终点不等于*
                Criteria filterCriteria=new Criteria("item_price").lessThanEqual(prices[1]);
                filterQuery.addCriteria(filterCriteria);
            }
            query.addFilterQuery(filterQuery);
        }

        //1.6分页查询
        Integer pageNo= (Integer) searchMap.get("pageNo");//要查询的当前页数
        if(pageNo==null){
            pageNo=1;
        }
        Integer pageSize= (Integer) searchMap.get("pageSize");//每页的记录数
        if(pageSize==null){
            pageSize=20;
        }
        query.setOffset((pageNo-1)*pageSize);//设置查询的起始的记录数


        //1.7排序
        String sortValue= (String) searchMap.get("sort");  //ASC DESC
        String sortField= (String) searchMap.get("sortField");   //price
        if(sortValue!=null &&  !"".equals(sortValue)){
            if("ASC".equals(sortValue)){
                Sort sort=new Sort(Sort.Direction.ASC,"item_"+sortField);
                query.addSort(sort);
            }else if("DESC".equals(sortValue)){
                Sort sort=new Sort(Sort.Direction.DESC,"item_"+sortField);
                query.addSort(sort);
            }
        }




        //************************  获取高亮结果集  ***********************
        //返回每条记录的集合
        HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);
        //page.getHighlighted()  获取高亮的记录集合
        for (HighlightEntry<TbItem> h : page.getHighlighted()) {
            TbItem entity = h.getEntity();        //获取源实体类，与page.getContent()一致
            //h.getHighlights()  返回每一个高亮域的集合  高亮的字段不止一个
            //h.getHighlights().get(0).getSnipplets()   返回高亮复合域中的每一个域的集合     复合域是有多个域组合而成
            if(h.getHighlights().size()>0 && h.getHighlights().get(0).getSnipplets().size()>0){
                String s = h.getHighlights().get(0).getSnipplets().get(0);
                entity.setTitle(s);  //设置高亮结果
            }
        }
        map.put("rows",page.getContent());

        map.put("total",page.getTotalElements());//总记录数
        map.put("totalPages",page.getTotalPages());//总页数
        return map;
    }

    //分组查询分类名称
    private List<String> searchCategoryList(Map searchMap){

        List<String> list=new ArrayList<>();
        Query query=new SimpleQuery();
        //按照关键字查询
        Criteria criteria=new Criteria("item_keywords").is(searchMap.get("keywords"));  //相当于  where条件查询
        query.addCriteria(criteria);
        //设置分组选项
        GroupOptions groupOptions=new GroupOptions();
        groupOptions.addGroupByField("item_category");   //相当于 group by
        query.setGroupOptions(groupOptions);
        //返回全部字段分组后的结果对象
        GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
        //获取item_category分组后的结果对象
        GroupResult<TbItem> itemCategory = page.getGroupResult("item_category");
        //获取分组后所有的结果集
        Page<GroupEntry<TbItem>> groupEntries = itemCategory.getGroupEntries();
        //遍历循环取值
        for (GroupEntry<TbItem> groupEntry : groupEntries) {
            String groupValue = groupEntry.getGroupValue();
            list.add(groupValue);
        }
    return list; 
    }



    @Autowired
    private RedisTemplate redisTemplate;
    //查询品牌列表以及规格列表
    private Map searchBrandAndSpecList(String category){
        Map map=new HashMap();
        //根据模板名称获得模板id
        Long categoryId= (Long) redisTemplate.boundHashOps("itemCat").get(category);

        if(categoryId!=null){
            //根据模板ID查询品牌列表
            List brandList= (List) redisTemplate.boundHashOps("brandList").get(categoryId);
            map.put("brandList", brandList);//返回值添加品牌列表
            //根据模板ID查询规格列表
            List specList= (List) redisTemplate.boundHashOps("specList").get(categoryId);
            map.put("specList", specList);
    }
        return map;
    }
}
