package com.pinyougou.search.service.impl;

import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

@Component
public class SolrDeleteListener implements MessageListener {

    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message) {

        ObjectMessage objectMessage = (ObjectMessage) message;

        System.out.println("监听到Solr删除消息");
        try {
           Long [] goodsIds = (Long[]) objectMessage.getObject();

           itemSearchService.deleteListFromSolr(goodsIds);

            System.out.println("删除Solr成功！！！");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
