package com.bandweaver.maxsec.service.Impl;

import com.bandweaver.maxsec.constants.Constants;
import com.bandweaver.maxsec.entity.MeasObjectVal;
import com.bandweaver.maxsec.entity.MeasValue;
import com.bandweaver.maxsec.mapper.MeasObjectMapper;
import com.bandweaver.maxsec.mapper.MeasObjectValMapper;
import com.bandweaver.maxsec.mapper.MeasValueMapper;
import com.bandweaver.maxsec.service.MeasObjectValService;
import com.bandweaver.maxsec.util.RedisUtil;
import com.bandweaver.maxsec.vo.MeasObjectVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class MeasObjectValServiceImpl implements MeasObjectValService {
	@Autowired
	private MeasObjectValMapper measObjectValMapper;
	@Autowired
	private MeasObjectMapper measObjectMapper;
	@Autowired
	private MeasValueMapper measValueMapper;
	@Autowired
	private RedisUtil redisUtil;

	// 缓存
	private ConcurrentHashMap<Integer, MeasObjectVal> map = new ConcurrentHashMap<>();

	// 获取缓存数据
	@Override
	public List<MeasObjectVal> getMeasObjectVals() {
		return new ArrayList<>(map.values());
	}

	@Override
	public void saveValue(MeasObjectVal value) {
		// 修改当前值到数据库
		measObjectValMapper.update(value);
		// 新添加数据到数据表
		MeasValue measValue = new MeasValue();
		measValue.setId(value.getId());
		measValue.setTime(value.getLastTime() == null ? new Date() : value.getLastTime());
		measValue.setValue(value.getLastValue() == null ? 0 : value.getLastValue());
		measValueMapper.addMeasValue(measValue);
	}

	@Override
	public MeasObjectVal selectByPrimaryKey(Integer id) {

		MeasObjectVal val;

//        if (redisUtil.hasKey(Constants.REDIS_MEAS_OBJECT_VAL + id)) {
//            log.info("redis 获取");
//            val = MeasObjectVal.fromJson(redisUtil.get(Constants.REDIS_MEAS_OBJECT_VAL + id).toString());
//        } else {
//            log.info("mysql 获取");
//            val = measObjectValMapper.selectByPrimaryKey(id);
//            insertRedis(val);
//        }

		if (map.contains(id)) {
			val = map.get(id);
		} else {
			val = measObjectValMapper.selectByPrimaryKey(id);
			map.put(id, val);
		}

		return val;
	}

	@Override
	public List<MeasObjectVal> selectAll() {

		List<MeasObjectVal> measObjectVals = measObjectValMapper.selectAll();

		for (MeasObjectVal val : measObjectVals) {
//            if (!redisUtil.hasKey(Constants.REDIS_MEAS_OBJECT_VAL + val.getId())) {
//                insertRedis(val);
//            }
			if (!map.contains(val.getId().intValue())) {
				map.put(val.getId(), val);
			}
		}

		return measObjectVals;
	}

	/**
	 * 缓存中获取
	 *
	 * @param vo
	 * @return
	 */
	@Override
	public List<MeasObjectVal> selectByCondition(MeasObjectVo vo) {
		List<MeasObjectVal> measObjectVals = map.values().stream().collect(Collectors.toList());

		if (vo.getStationId() != null)
			measObjectVals = measObjectVals.stream()
					.filter(a -> a.getStationId().intValue() == vo.getStationId().intValue())
					.collect(Collectors.toList());
		if (vo.getDataType() != null)
			measObjectVals = measObjectVals.stream()
					.filter(a -> a.getDataType().intValue() == vo.getDataType().intValue())
					.collect(Collectors.toList());
		if (vo.getObjectType() != null)
			measObjectVals = measObjectVals.stream()
					.filter(a -> a.getObjectType().intValue() == vo.getObjectType().intValue())
					.collect(Collectors.toList());

		measObjectVals.sort((a, b) -> Integer.compare(a.getId(), b.getId()));

		return measObjectVals;
	}

	@Override
	public void insert(MeasObjectVal model) {
		measObjectMapper.insert(model);
		measObjectValMapper.insert(model);
		map.put(model.getId(), model);
	}

	@Override
	public void update(MeasObjectVal model) {
		measObjectMapper.update(model);
		measObjectValMapper.update(model);
		map.put(model.getId(), model);
	}

	@Override
	public void delete(Integer id) {
//        redisUtil.del(Constants.REDIS_MEAS_OBJECT_VAL + id);
		measObjectValMapper.delete(id);
		measObjectMapper.delete(id);
		map.remove(id);
	}

	@Override
	public void updateValue(MeasValue value) {
		MeasObjectVal val = selectByPrimaryKey(value.getId());
		val.setLastValue(value.getValue());
		val.setLastTime(value.getTime());
//        insertRedis(val);
		map.put(val.getId().intValue(), val);
	}

	@Override
	public void updateValues(List<MeasValue> values) {
		for (MeasValue value : values) {
			MeasObjectVal val = selectByPrimaryKey(value.getId());
			val.setLastValue(value.getValue());
			val.setLastTime(value.getTime());
//            insertRedis(val);
			map.put(val.getId().intValue(), val);
		}
	}

	private void insertRedis(MeasObjectVal val) {
		redisUtil.set(Constants.REDIS_MEAS_OBJECT_VAL + val.getId(), val.toJson());
	}
}
