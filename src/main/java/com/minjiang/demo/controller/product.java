package com.minjiang.demo.controller;

import com.minjiang.demo.config.TtlQueueConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@RestController
@Slf4j
public class product {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @GetMapping("/delayed/{message}/{timeout}")
    public void delayed(@PathVariable("message")String message,@PathVariable("timeout")Integer timeout){
        log.info("当前时间:{},延迟了{}，发送一条信息给队列:{}", new Date().toString(),timeout, message);
        CorrelationData correlationData = new CorrelationData();
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setAppId(UUID.randomUUID().toString());
        messageProperties.setContentEncoding("UTF-8");
        messageProperties.setContentType("application/text");
        messageProperties.setDelay(timeout);
        Message message2 = new Message(message.getBytes(StandardCharsets.UTF_8),messageProperties);
        ReturnedMessage returnedMessage = new ReturnedMessage(message2,200,"这是delayed传递的",
                TtlQueueConfig.EXCHANGE,"delayed.routingkey");
        correlationData.setReturned(returnedMessage);
        rabbitTemplate.convertAndSend(TtlQueueConfig.EXCHANGE,"delayed.routingkey",message2,correlationData);
//        rabbitTemplate.convertAndSend(TtlQueueConfig.EXCHANGE,"delayed.routingkey",message,message1 -> {
//            message1.getMessageProperties().setDelay(timeout);
//            message1.getMessageProperties().setAppId(UUID.randomUUID().toString());
//            return message1;
//        });
    }
    @GetMapping("/normal/{message}")
    public void delayed(@PathVariable("message")String message){
        CorrelationData correlationData = new CorrelationData("1");
        log.info("当前时间:{},发送一条信息给队列:{}", new Date().toString(), message);
        rabbitTemplate.convertAndSend(TtlQueueConfig.NORMAL_CHANGE, TtlQueueConfig.NORMAL_ROUTINGKEY+"aaa",message,correlationData);
    }
}
