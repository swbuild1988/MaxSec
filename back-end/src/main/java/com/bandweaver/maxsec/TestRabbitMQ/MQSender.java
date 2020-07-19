package com.bandweaver.maxsec.TestRabbitMQ;

import com.alibaba.fastjson.JSON;
import com.bandweaver.maxsec.constants.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MQSender {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendMessageToBack(Object message) {
        log.debug("【消息发送者】发送消息到FANOUT_EXCHANGE_BACK交换机，消息内容为: {}", message);
        rabbitTemplate.convertAndSend(Constants.FANOUT_EXCHANGE_BACK, "", message);
    }

    public void sendMessageToFront(Object message) {
        log.debug("【消息发送者】发送消息到FANOUT_EXCHANGE_FRONT交换机，消息内容为: {}", message);
        String json = (message instanceof String) ? message.toString() : JSON.toJSONStringWithDateFormat(message, "yyyy-MM-dd HH:mm:ss");
        rabbitTemplate.convertAndSend(Constants.FANOUT_EXCHANGE_FRONT, "", json);
    }

    public void sendMessage(Object message) {
        log.debug("【消息发送者】发送消息到全部交换机，消息内容为: {}", message);
        sendMessageToBack(message);
        sendMessageToFront(message);
    }
}
