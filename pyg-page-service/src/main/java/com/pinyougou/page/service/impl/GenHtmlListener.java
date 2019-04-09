package com.pinyougou.page.service.impl;

import com.pinyougou.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

@Component
public class GenHtmlListener implements MessageListener {

    @Autowired
    private ItemPageService itemPageService;

    @Override
    public void onMessage(Message message) {

        ObjectMessage objectMessage = (ObjectMessage) message;

        System.out.println("接受消息，正在进行 网页静态化 ");
        try {

           Long  [] goodsIds = (Long[]) objectMessage.getObject();

            for (Long goodsId: goodsIds) {
                itemPageService.genHtml(goodsId);
            }

            System.out.println("网页静态化生成成功！！！！");


        } catch (JMSException e) {
            e.printStackTrace();
        }


    }
}
