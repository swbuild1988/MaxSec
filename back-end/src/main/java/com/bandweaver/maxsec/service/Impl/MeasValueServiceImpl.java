package com.bandweaver.maxsec.service.Impl;

import com.bandweaver.maxsec.entity.MeasObjectVal;
import com.bandweaver.maxsec.entity.MeasValue;
import com.bandweaver.maxsec.mapper.MeasValueMapper;
import com.bandweaver.maxsec.service.MeasObjectValService;
import com.bandweaver.maxsec.service.MeasValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class MeasValueServiceImpl implements MeasValueService {

    @Autowired
    private MeasValueMapper measValueMapper;
    @Autowired
    private MeasObjectValService measObjectValService;

    @Override
    public int receiveMeasValue(MeasValue measValue) {


        return 0;
    }

    @Override
    public int receiveMeasValueBatch(List<MeasValue> list) {
        return 0;
    }

    @Override
    public int addMeasValue(MeasValue measValue) {
        return measValueMapper.addMeasValue(measValue);
    }

    @Override
    public int addMeasValueBatch(List<MeasValue> list) {
        return measValueMapper.addMeasValueBatch(list);
    }

    @Override
    public List<MeasValue> getListBySnAndTime(Integer id, Date startTime, Date endTime) {
        return measValueMapper.getListBySnAndTime(id, startTime, endTime);
    }
}
