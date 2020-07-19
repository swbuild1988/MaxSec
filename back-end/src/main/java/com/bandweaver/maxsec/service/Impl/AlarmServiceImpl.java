package com.bandweaver.maxsec.service.Impl;

import com.bandweaver.maxsec.TestRabbitMQ.MQSender;
import com.bandweaver.maxsec.constants.MessageTypeEnum;
import com.bandweaver.maxsec.dto.AlarmDto;
import com.bandweaver.maxsec.dto.Message;
import com.bandweaver.maxsec.dto.PageRequest;
import com.bandweaver.maxsec.dto.PageResult;
import com.bandweaver.maxsec.entity.Alarm;
import com.bandweaver.maxsec.mapper.AlarmMapper;
import com.bandweaver.maxsec.service.AlarmService;
import com.bandweaver.maxsec.util.PageUtils;
import com.bandweaver.maxsec.vo.AlarmVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class AlarmServiceImpl implements AlarmService {
    @Autowired
    private AlarmMapper alarmMapper;
    @Autowired
    private MQSender mqSender;

    @Override
    public Alarm selectByPrimaryKey(Integer id) {
        return alarmMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<Alarm> selectAll() {
        return alarmMapper.selectAll();
    }

    @Override
    public void insert(Alarm model) {
        alarmMapper.insert(model);
    }

    @Override
    public void update(Alarm model) {
        alarmMapper.update(model);
    }

    @Override
    public void delete(Integer id) {
        alarmMapper.delete(id);
    }

    @Override
    public void clean(Integer id, Date time) {
        alarmMapper.clean(id, time);
        // 发送MQ
        Alarm alarm = selectByPrimaryKey(id);
        Message message = new Message();
        message.setType(MessageTypeEnum.CleanAlarm);
        message.setBody(alarm);
        mqSender.sendMessageToBack(message);
    }

    @Override
    public List<Alarm> selectUncleaned(Integer objId, Integer alarmTypeId, String source) {
        return alarmMapper.selectUncleaned(objId, alarmTypeId, source);
    }

    @Override
    public Integer getCountByCondition(AlarmVo vo) {
        return alarmMapper.getCountByCondition(vo);
    }

    @Override
    public List<AlarmDto> getAlarmDtosByCondition(AlarmVo vo) {
        return alarmMapper.getAlarmsByCondition(vo);
    }

    @Override
    public PageResult getPageAlarmsByCondition(PageRequest pageRequest, AlarmVo vo) {
        int pageNum = pageRequest.getPageNum();
        int pageSize = pageRequest.getPageSize();
        PageHelper.startPage(pageNum, pageSize);

        List<AlarmDto> sysMenus = alarmMapper.getAlarmsByCondition(vo);
        PageInfo<AlarmDto> equipmentDtoPageInfo = new PageInfo<AlarmDto>(sysMenus);

        return PageUtils.getPageResult(pageRequest, equipmentDtoPageInfo);
    }
}
