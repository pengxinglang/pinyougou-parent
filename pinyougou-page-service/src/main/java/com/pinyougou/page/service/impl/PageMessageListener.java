package com.pinyougou.page.service.impl;


import com.pinyougou.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

@Component
public class PageMessageListener implements MessageListener {

    @Autowired
    private ItemPageService itemPageService;

    @Override
    public void onMessage(Message message) {
        TextMessage textMessage= (TextMessage) message;
        try {
            String goodId = textMessage.getText();
            boolean b = itemPageService.genItemHtml(Long.parseLong(goodId));
            System.out.println("页面生成成功："+b);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
