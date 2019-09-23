package com.pinyougou.search.service.impl;

import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.util.Arrays;

@Component
public class ItemDeleListener implements MessageListener {


    @Autowired
    private ItemSearchService itemSearchService;
    @Override
    public void onMessage(Message message) {
        ObjectMessage objectMessage= (ObjectMessage) message;
        try {
            Long[] ids= (Long[]) objectMessage.getObject();
            itemSearchService.deleteByGoodsIds(Arrays.asList(ids));
            System.out.println("监听执行成功");
        } catch (JMSException e) {
            e.printStackTrace();
        }


    }
}
