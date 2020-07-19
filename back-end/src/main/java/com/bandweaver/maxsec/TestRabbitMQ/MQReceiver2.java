//package com.bandweaver.maxsec.TestRabbitMQ;
//
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.amqp.rabbit.annotation.RabbitHandler;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.stereotype.Component;
//
//@Slf4j
//@Component
//@RabbitListener(queues = "test.queue2.name")
//public class MQReceiver2 {
//
//    @RabbitHandler
//    public void receiveMessage(Object message) {
//        log.info("消息接收者接收到来自【队列二】的消息，消息内容: {}", message);
//    }
//}