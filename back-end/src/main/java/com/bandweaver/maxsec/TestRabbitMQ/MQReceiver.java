package com.bandweaver.maxsec.TestRabbitMQ;

import com.bandweaver.maxsec.constants.Constants;
import com.bandweaver.maxsec.constants.MessageTypeEnum;
import com.bandweaver.maxsec.dto.IntercomCard;
import com.bandweaver.maxsec.dto.Message;
import com.bandweaver.maxsec.entity.Alarm;
import com.bandweaver.maxsec.dto.VideoAlarm;
import com.bandweaver.maxsec.service.VideoManagerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MQReceiver {

    @Autowired
    private VideoManagerService videoManagerService;

    @RabbitListener(queues = Constants.ALARM_QUEUE_BACK)
    public void receiveMessage(Message message) {
        log.debug("消息接收者接收到来自【视频告警队列】的消息，消息内容: {}", message);
        if (message.getType() == MessageTypeEnum.VideoOriginAlarm || message.getType() == MessageTypeEnum.IntercomAlarm) {
            // 处理原始视频告警
            VideoAlarm videoAlarm = (VideoAlarm) (message.getBody());
            videoManagerService.processAlarm(videoAlarm);
        }
        if (message.getType() == MessageTypeEnum.IntercomGetFace) {
            // 处理人脸图片
            IntercomCard intercomCard = (IntercomCard) (message.getBody());
            videoManagerService.processFace(intercomCard);
        } else if (message.getType() == MessageTypeEnum.IntrusionAlarm) {
            // 处理告警联动
            Alarm alarm = (Alarm) (message.getBody());
            videoManagerService.processAlarmLinkage(alarm);
        } else if (message.getType() == MessageTypeEnum.CleanAlarm) {
            // 处理默认预置位
            Alarm alarm = (Alarm) (message.getBody());
            videoManagerService.cleanAlarm(alarm);
        } else if (message.getType() == MessageTypeEnum.IntercomOffline) {
            // 处理可视对讲错误回调
            String ip = (String) (message.getBody());
            videoManagerService.disconnectIntercom(ip);
        }
    }
}
