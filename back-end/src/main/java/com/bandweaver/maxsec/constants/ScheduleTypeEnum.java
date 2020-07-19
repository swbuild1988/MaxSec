package com.bandweaver.maxsec.constants;

import lombok.Getter;

public enum ScheduleTypeEnum {
	PatrolPreset("定时巡检", 1, "com.bandweaver.maxsec.schedule.TaskEntrance", "startPatrolPreset"),

	ControlActived("防区定时控制布防撤防", 2, "com.bandweaver.maxsec.schedule.TaskEntrance", "controlActivedOfMeasObject"),

	SaveMeasObjectVal("定时保存监测数据", 3, "com.bandweaver.maxsec.schedule.TaskEntrance", "saveMeasObjectVal");

	@Getter
	private String name;
	@Getter
	private int value;
	@Getter
	private String className;
	@Getter
	private String methodName;

	ScheduleTypeEnum(String name, int value, String className, String methodName) {
		this.name = name;
		this.value = value;
		this.className = className;
		this.methodName = methodName;
	}

	/**
	 * 根据value值获取enum
	 */
	public static ScheduleTypeEnum getEnum(int value) {
		for (ScheduleTypeEnum tmp : ScheduleTypeEnum.values()) {
			if (value == tmp.getValue())
				return tmp;
		}
		return null;
	}
}
