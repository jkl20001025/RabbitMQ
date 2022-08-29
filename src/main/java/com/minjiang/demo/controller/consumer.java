package com.minjiang.demo.controller;

import com.minjiang.demo.config.TtlQueueConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class consumer {
    @RabbitListener(queues=TtlQueueConfig.QUEUENAME)
    public void delayQueue(Message message){
        Integer delay = message.getMessageProperties().getDelay();
        String msg=new String(message.getBody());
        log.info("当前时间:{},延迟了{},当前延迟队列收到的消息为:{}",new Date().toString(),delay,msg);
    }
    @RabbitListener(queues = TtlQueueConfig.NORMAL_QUEUE)
    public void nomalQueue(Message message){
        String msg=new String(message.getBody());
        log.info("当前时间:{},当前普通队列收到的消息为:{}",new Date().toString(),msg);
    }
}
