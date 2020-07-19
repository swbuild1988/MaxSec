package com.bandweaver.maxsec.TestRabbitMQ;

import com.bandweaver.maxsec.constants.Constants;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class MQConsumer {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 创建队列
     * @param queueName
     */
    public void createQueue(String queueName) {
        Channel channel = getChannel();

        try {
            // 5分钟无连接，自动删除
            Map<String, Object> arguments = new HashMap<String, Object>();
            arguments.put("x-expires", 1*60*1000);

            // 是否持久化，即mq重启是否丢失消息
            boolean durable = true;
            // 是否唯一性，即一个队列是不是只能有一个消费者
            boolean exclusive = false;
            // 是否自动删除，没有消费者自动删除
            boolean auto_delete = false;
            channel.queueDeclare(queueName, durable, auto_delete, exclusive, arguments);

            /**
             * 关联 exchange 和 queue ，因为是广播无需指定routekey，routingKey设置为空字符串
             */
            channel.queueBind(queueName, Constants.FANOUT_EXCHANGE_BACK, "");

        } catch (Exception ex) {
        }
    }

    /**
     * 删除队列
     * @param queueName
     */
    public void deleteQueue(String queueName) {
        log.info("删除队列,queueName:" + queueName);
        Channel channel = getChannel();
        try {
            channel.queueDelete(queueName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获得和生产者同一个交换机
     *
     * @return
     */
    private Channel getChannel() {
        Channel channel = rabbitTemplate.getConnectionFactory().createConnection().createChannel(true); /**
         * 与生产者使用同一个交换机
         */
        try {
            channel.exchangeDeclare(Constants.FANOUT_EXCHANGE_BACK, "fanout", true);
        } catch (IOException e) {
        }

        return channel;
    }
}

