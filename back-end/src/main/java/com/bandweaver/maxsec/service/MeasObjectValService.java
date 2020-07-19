package com.bandweaver.maxsec.service;

import com.bandweaver.maxsec.entity.MeasObjectVal;
import com.bandweaver.maxsec.entity.MeasValue;
import com.bandweaver.maxsec.vo.MeasObjectVo;

import java.util.List;

public interface MeasObjectValService {
    MeasObjectVal selectByPrimaryKey(Integer id);

    List<MeasObjectVal> selectAll();

    List<MeasObjectVal> selectByCondition(MeasObjectVo vo);

    void insert(MeasObjectVal model);
    
    void saveValue(MeasObjectVal value);

    void update(MeasObjectVal model);

    void delete(Integer id);

    void updateValue(MeasValue value);

    void updateValues(List<MeasValue> values);

	List<MeasObjectVal> getMeasObjectVals();

}
