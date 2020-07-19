package com.bandweaver.maxsec.service;

import com.bandweaver.maxsec.entity.MeasValue;

import java.util.Date;
import java.util.List;

public interface MeasValueService {

    /**
     * 接收一条数据
     */
    int receiveMeasValue(MeasValue measValue);

    /**
     * 接收多条数据
     */
    int receiveMeasValueBatch(List<MeasValue> list);

    /**
     * 添加一条数据
     */
    int addMeasValue(MeasValue measValue);

    /**
     * 添加多条数据
     */
    int addMeasValueBatch(List<MeasValue> list);

    /**
     * 获取一段时间的数据
     *
     * @return
     */
    List<MeasValue> getListBySnAndTime(Integer id, Date startTime, Date endTime);
}
