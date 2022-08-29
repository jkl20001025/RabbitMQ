package com.minjiang.demo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 设置不可路由的消息
 */
@Component
@Slf4j
public class MyBack implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnsCallback {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void init(){
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnsCallback(this);
    }
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {

            String id=correlationData.getReturned().getMessage().getMessageProperties().getAppId()!=null?
                    correlationData.getReturned().getMessage().getMessageProperties().getAppId():"1";
            if (ack){
                log.info("消息id为:{}",id);
            }else{
                log.warn("接受id为{}的消息失败了",id);
            }
    }

    @Override
    public void returnedMessage(ReturnedMessage returnedMessage) {
        //请注意!如果你使用了延迟队列插件，那么一定会调用该callback方法，因为数据并没有提交上去，而是提交在交换器中，过期时间到了才提交上去，并非是bug！你可以用if进行判断交换机名称来捕捉该报错
        if(returnedMessage.getExchange().equals(TtlQueueConfig.EXCHANGE)){
            return;
        }

        log.warn("不可路由消息相应码："+returnedMessage.getReplyCode());
        log.warn("不可路由消息主体message:"+returnedMessage.getMessage());
        log.warn("不可路由消息描述："+returnedMessage.getReplyText());
        log.warn("不可路由消息使用的交换器exchange:"+returnedMessage.getMessage());
        log.warn("不可路由消息使用的路由键routiAng:"+returnedMessage.getRoutingKey());
    }


}
