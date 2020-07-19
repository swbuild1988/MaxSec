package com.bandweaver.maxsec.controller;

import com.bandweaver.maxsec.TestRabbitMQ.MQSender;
import com.bandweaver.maxsec.constants.MessageTypeEnum;
import com.bandweaver.maxsec.dto.AlarmDto;
import com.bandweaver.maxsec.dto.Message;
import com.bandweaver.maxsec.dto.R;
import com.bandweaver.maxsec.service.AlarmService;
import com.bandweaver.maxsec.vo.AlarmVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class RabbitController {

    @Autowired
    private MQSender mqSender;
    @Autowired
    private AlarmService alarmService;

    @GetMapping("/mq/sender")
    public String send() {
        String message = "lallalalala";
        AlarmVo vo = new AlarmVo();
        List<AlarmDto> alarms = alarmService.getAlarmDtosByCondition(vo);
        AlarmDto alarm = alarms.get(0);
        Message message1 = new Message();
//        message1.setType(MessageTypeEnum.IntercomAlarm);
//        alarm.setAlarmTypeId(3003);
        message1.setType(MessageTypeEnum.VideoAlarm);
        alarm.setAlarmTypeId(1004);
        alarm.setObjId(10203042);
        alarm.setStationId(1);
        alarm.setLevel(2);
        alarm.setSource("0");
        message1.setBody(alarm);
        log.info("开始发送消息：" + message1.toString());
        mqSender.sendMessageToFront(message1);

        return message;
    }

    @GetMapping("/mq/sender2")
    public R send2() {
        AlarmVo vo = new AlarmVo();
        List<AlarmDto> alarms = alarmService.getAlarmDtosByCondition(vo);
        AlarmDto alarm = alarms.get(0);
        Message message1 = new Message();
        message1.setType(MessageTypeEnum.IntercomAlarm);
        alarm.setAlarmTypeId(3003);
        message1.setBody(alarm);
        log.info("开始发送消息：" + message1.toString());
        mqSender.sendMessageToFront(message1);

        return new R();
    }


}
