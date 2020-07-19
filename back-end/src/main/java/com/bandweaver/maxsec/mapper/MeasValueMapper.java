package com.bandweaver.maxsec.mapper;

import com.bandweaver.maxsec.entity.MeasValue;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface MeasValueMapper {

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
     * @return
     */
    List<MeasValue> getListBySnAndTime(@Param("id") Integer id, @Param("startTime") Date startTime, @Param("endTime") Date endTime);
}
