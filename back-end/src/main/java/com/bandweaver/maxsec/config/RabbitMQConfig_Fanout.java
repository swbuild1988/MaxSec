package com.bandweaver.maxsec.config;

import com.bandweaver.maxsec.constants.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class RabbitMQConfig_Fanout {
    /**
     * 创建广播形式的交换机
     *
     * @return 交换机实例
     */
    @Bean
    public FanoutExchange fanoutExchangeBack() {
        log.info("【【【交换机FANOUT_EXCHANGE_BACK创建成功】】】");
        return new FanoutExchange(Constants.FANOUT_EXCHANGE_BACK);
    }

    /**
     * 创建广播形式的交换机
     *
     * @return 交换机实例
     */
    @Bean
    public FanoutExchange fanoutExchangeFront() {
        log.info("【【【交换机FANOUT_EXCHANGE_FRONT创建成功】】】");
        return new FanoutExchange(Constants.FANOUT_EXCHANGE_FRONT);
    }

    /**
     * 后端告警队列
     *
     * @return 队列实例
     */
    @Bean
    public Queue queueAlarmBack() {
        log.info("【【【ALARM_QUEUE_BACK实例创建成功】】】");
        return new Queue(Constants.ALARM_QUEUE_BACK);
    }

    /**
     * 绑定后端告警队列到交换机
     *
     * @return 绑定对象
     */
    @Bean
    public Binding bingQueueAlarmBackToExchange() {
        log.info("【【【绑定ALARM_QUEUE_BACK到交换机成功】】】");
        return BindingBuilder.bind(queueAlarmBack()).to(fanoutExchangeBack());
    }
}
