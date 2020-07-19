package com.bandweaver.maxsec.service.Impl;

import com.bandweaver.maxsec.constants.ScheduleTypeEnum;
import com.bandweaver.maxsec.entity.Schedule;
import com.bandweaver.maxsec.service.ScheduleService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.bandweaver.maxsec.mapper.ScheduleMapper;
import com.bandweaver.maxsec.schedule.DefaultSchedulingConfigurer;

import java.util.List;

@Service
@Transactional
public class ScheduleServiceImpl implements ScheduleService {
	@Autowired
	private ScheduleMapper scheduleMapper;
	@Autowired
	private DefaultSchedulingConfigurer defaultSchedulingConfigurer;

	@Override
	public Schedule selectByPrimaryKey(Integer id) {
		return scheduleMapper.selectByPrimaryKey(id);
	}

	@Override
	public List<Schedule> selectAll() {
		return scheduleMapper.selectAll();
	}

	@Override
	public void insert(Schedule model) {
		scheduleMapper.insert(model);

		// 添加完任务，添加到定时任务列表中
		if (model.getActived() != null && model.getActived()) {
			ScheduleTypeEnum job = ScheduleTypeEnum.getEnum(model.getType());
			defaultSchedulingConfigurer.addTriggerTask(model.getId(), defaultSchedulingConfigurer
					.getTriggerTask(job.getClassName(), job.getMethodName(), model.getParam(), model.getCron()));
		}
	}

	@Override
	public void update(Schedule model) {
		scheduleMapper.update(model);
		Schedule schedule = scheduleMapper.selectByPrimaryKey(model.getId());

		// 修改任务，更新定时任务列表
		if (schedule != null && schedule.getActived()) {
			ScheduleTypeEnum job = ScheduleTypeEnum.getEnum(schedule.getType());
			defaultSchedulingConfigurer.resetTriggerTask(model.getId(), defaultSchedulingConfigurer
					.getTriggerTask(job.getClassName(), job.getMethodName(), schedule.getParam(), schedule.getCron()));
		} else {
			defaultSchedulingConfigurer.cancelTriggerTask(model.getId());
		}
	}

	@Override
	public void delete(Integer id) {
		scheduleMapper.delete(id);

		// 删除任务后，更新任务列表
		defaultSchedulingConfigurer.cancelTriggerTask(id);
	}
}
