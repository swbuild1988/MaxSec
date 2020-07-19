package com.bandweaver.maxsec.mapper;

import com.bandweaver.maxsec.dto.AlarmDto;
import com.bandweaver.maxsec.entity.Alarm;
import com.bandweaver.maxsec.vo.AlarmVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface AlarmMapper {
    Alarm selectByPrimaryKey(Integer id);

    List<Alarm> selectAll();

    void insert(Alarm model);

    void update(Alarm model);

    void delete(Integer id);

    void clean(@Param("id")Integer id, @Param("time")Date time);

    List<Alarm> selectUncleaned(@Param("objId") Integer objId, @Param("alarmTypeId") Integer alarmTypeId, @Param("source") String source);

    Integer getCountByCondition(AlarmVo vo);

    List<AlarmDto> getAlarmsByCondition(AlarmVo vo);
}
