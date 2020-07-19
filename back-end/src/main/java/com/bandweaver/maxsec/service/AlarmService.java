package com.bandweaver.maxsec.service;

import com.bandweaver.maxsec.dto.AlarmDto;
import com.bandweaver.maxsec.dto.PageRequest;
import com.bandweaver.maxsec.dto.PageResult;
import com.bandweaver.maxsec.entity.Alarm;
import com.bandweaver.maxsec.vo.AlarmVo;

import java.util.Date;
import java.util.List;

public interface AlarmService {
    Alarm selectByPrimaryKey(Integer id);

    List<Alarm> selectAll();

    void insert(Alarm model);

    void update(Alarm model);

    void delete(Integer id);

    void clean(Integer id, Date time);

    List<Alarm> selectUncleaned(Integer objId, Integer alarmTypeId, String source);

    Integer getCountByCondition(AlarmVo vo);

    List<AlarmDto> getAlarmDtosByCondition(AlarmVo vo);

    PageResult getPageAlarmsByCondition(PageRequest pageRequest, AlarmVo vo);
}
