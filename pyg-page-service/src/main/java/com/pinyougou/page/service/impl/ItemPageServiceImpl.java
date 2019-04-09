package com.pinyougou.page.service.impl;


import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.*;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemPageServiceImpl implements ItemPageService {

   @Autowired
   private FreeMarkerConfigurer freeMarkerConfigurer;

   @Autowired
   private TbGoodsMapper goodsMapper;

   @Autowired
   private TbGoodsDescMapper goodsDescMapper;

   @Autowired
   private TbItemMapper itemMapper;


   @Autowired
   private TbItemCatMapper itemCatMapper;

   @Value("${pageDir}")
   private String pageDir;

    @Override
    public boolean genHtml(Long goodsId) {

        Configuration configuration = freeMarkerConfigurer.getConfiguration();

        try {

            //模板对象
            Template template = configuration.getTemplate("item.ftl");
            //数据流
            Map dataMap = new HashMap<>();
            //创建输出对象
            Writer out= new FileWriter(pageDir+goodsId+".html");
            OutputStreamWriter writer = new OutputStreamWriter(
                    new FileOutputStream(pageDir+goodsId+".html")
                    , "UTF-8");


            //把数据存入数据流中
            //1.1 Goods
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(goodsId);
            dataMap.put("goods",tbGoods);
            //1.2 GoodsDesc
            TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);

            dataMap.put("goodsDesc",tbGoodsDesc);
            //1.3 Cat
            String cat1 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory1Id()).getName();
            String cat2 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory2Id()).getName();
            String cat3 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id()).getName();

            dataMap.put("cat1",cat1);
            dataMap.put("cat2",cat2);
            dataMap.put("cat3",cat3);

            // 1.4 SKU
            TbItemExample example = new TbItemExample();
            TbItemExample.Criteria criteria = example.createCriteria();
            criteria.andGoodsIdEqualTo(goodsId);
            criteria.andStatusEqualTo("1");
            example.setOrderByClause("is_default desc");
            List<TbItem> skuList = itemMapper.selectByExample(example);

            dataMap.put("skuList",skuList);


            template.process(dataMap,writer);

            return true;

        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }


    }

    @Override
    public boolean DeleteHtml(Long[] goodsIds) {

        try{
            for (Long goodsId:goodsIds) {
                new File(pageDir+goodsId+".html").delete();
                System.out.println(pageDir+goodsId);
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }
}
