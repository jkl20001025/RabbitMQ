package com.minjiang.demo.config;

import com.rabbitmq.client.AMQP;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class TtlQueueConfig {
    public static final String QUEUENAME="delayed.queue";
//    有关插件的延迟交换机
    public static final String EXCHANGE="delayed.exchange";

    public static final String NORMAL_QUEUE="normal2.queue";

    public static final String NORMAL_CHANGE="normal2.change";

    public static final String NORMAL_ROUTINGKEY="normalroutingkey";
    @Bean
    public CustomExchange exchange(){
        Map<String,Object> map=new HashMap<>();
        map.put("x-delayed-type","direct");
        return new CustomExchange(EXCHANGE,"x-delayed-message",true,false,map);
    }
    @Bean
    public Queue queue(){
        return new Queue(QUEUENAME);
    }
    @Bean
    public Binding queueBindingExchange(@Qualifier("exchange") CustomExchange customExchange, @Qualifier("queue") Queue queue){
        return BindingBuilder.bind(queue).to(customExchange).with("delayed.routingkey").noargs();

    }
//    普通队列和普通交换机的声明绑定
    @Bean
    public DirectExchange directExchange(){
        return ExchangeBuilder.directExchange(NORMAL_CHANGE).durable(true).build();
    }
    @Bean
    public Queue normalQueue(){
        return QueueBuilder.durable(NORMAL_QUEUE).build();
    }
    @Bean
    public Binding nomalQueueBingExchange(@Qualifier("directExchange")DirectExchange directExchange,
                                          @Qualifier("normalQueue")Queue queue){
        return BindingBuilder.bind(queue).to(directExchange).with(NORMAL_ROUTINGKEY);
    }

}
