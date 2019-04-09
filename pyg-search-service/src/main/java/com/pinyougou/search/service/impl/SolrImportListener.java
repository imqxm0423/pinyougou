package com.pinyougou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;

@Component
public class SolrImportListener implements MessageListener {

    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message) {

        TextMessage textMessage = (TextMessage) message;

        System.out.println("监听到Solr导入的消息");
        try {
            String jsonString = textMessage.getText();
            List<TbItem> tbItemList = JSON.parseArray(jsonString, TbItem.class);

            itemSearchService.importList(tbItemList);

            System.out.println("Solr导入成功！！");

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
