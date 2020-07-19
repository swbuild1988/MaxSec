package com.bandweaver.maxsec.schedule;

import com.bandweaver.maxsec.config.schedule.QuartzConfigration;
import com.bandweaver.maxsec.util.BeanUtils;
import org.springframework.scheduling.SchedulingException;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.config.TriggerTask;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 定时任务配置
 *
 * @author ya.liu
 * @date 2019年12月23日
 */
@Component
@EnableScheduling
public class DefaultSchedulingConfigurer implements SchedulingConfigurer {


	private final String FIELD_SCHEDULED_TASKS = "scheduledTasks";
	private ScheduledTaskRegistrar taskRegistrar;
	private Set<ScheduledTask> scheduledTasks = null;
	private Map<Integer, ScheduledTask> tasks = new ConcurrentHashMap<>();

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		this.taskRegistrar = taskRegistrar;
		init();
	}

	// 加载所有已启用的任务列表
	private void init() {
//		new Thread() {
//			public void run() {
//				try {
//					// 等待任务调度初始化完成
//					while (!inited()) {
//						Thread.sleep(1000);
//					}
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				// 初始化定时任务
//				List<Schedule> list = scheduleService.selectAll();
//				for (Schedule schedule : list) {
//					if (!schedule.getActived()) {
//						continue;
//					}
//					ScheduleTypeEnum scheduleType = ScheduleTypeEnum.getEnum(schedule.getType());
//					if (scheduleType != null) {
//						String className = scheduleType.getClassName();
//						String methodName = scheduleType.getMethodName();
//						String cron = schedule.getCron();
//						String param = schedule.getParam();
//						addTriggerTask(schedule.getId(), getTriggerTask(className, methodName, param, cron));
//					}
//				}
//			}
//		}.start();
	}

	@SuppressWarnings("unchecked")
	private Set<ScheduledTask> getScheduledTasks() {
		if (scheduledTasks == null) {
			try {
				scheduledTasks = (Set<ScheduledTask>) BeanUtils.getProperty(taskRegistrar, FIELD_SCHEDULED_TASKS);
			} catch (NoSuchFieldException e) {
				throw new SchedulingException("not found scheduledTasks field. " + e);
			}
		}
		return scheduledTasks;
	}

	/**
	 * 添加任务
	 *
	 * @param taskId
	 * @param triggerTask
	 */
	public void addTriggerTask(Integer taskId, TriggerTask triggerTask) {
		if (tasks.containsKey(taskId)) {
			throw new SchedulingException("the taskId[" + taskId + "] was added.");
		}
		ScheduledTask task = taskRegistrar.scheduleTriggerTask(triggerTask);
		getScheduledTasks().add(task);
		tasks.put(taskId, task);
	}

	/**
	 * 取消任务
	 *
	 * @param taskId
	 */
	public void cancelTriggerTask(Integer taskId) {
		ScheduledTask task = tasks.get(taskId);
		if (task != null) {
			task.cancel();
		}
		tasks.remove(taskId);
		getScheduledTasks().remove(task);
	}

	/**
	 * 重置任务
	 *
	 * @param taskId
	 * @param triggerTask
	 */
	public void resetTriggerTask(Integer taskId, TriggerTask triggerTask) {
		cancelTriggerTask(taskId);
		addTriggerTask(taskId, triggerTask);
	}

	/**
	 * 生成TriggerTask
	 *
	 * @return
	 * @author ya.liu
	 * @date 2019年12月24日
	 */
	public TriggerTask getTriggerTask(String className, String methodName, String args, String cron) {
		TriggerTask task = new TriggerTask(getRunnable(className, methodName, args), getTrigger(cron));
		return task;
	}

	/**
	 * 所有任务编号
	 *
	 * @return
	 */
	public Set<Integer> taskIds() {
		return tasks.keySet();
	}

	/**
	 * 是否存在任务
	 *
	 * @param taskId
	 * @return
	 */
	public boolean hasTask(Integer taskId) {
		return this.tasks.containsKey(taskId);
	}

	/**
	 * 任务调度是否已经初始化完成
	 *
	 * @return
	 */
	public boolean inited() {
		return this.taskRegistrar != null && this.taskRegistrar.getScheduler() != null;
	}

	/**
	 * 转换首字母小写
	 *
	 * @param str
	 * @return
	 */
	public static String lowerFirstCapse(String str) {
		char[] chars = str.toCharArray();
		chars[0] += 32;
		return String.valueOf(chars);
	}

	/**
	 * runnable
	 *
	 * @param className,methodName
	 * @return
	 */
	private Runnable getRunnable(String className, String methodName, String args) {
		return new Runnable() {
			@Override
			public void run() {
				Class<?> clazz;
				try {
					clazz = Class.forName(className);
					String className = lowerFirstCapse(clazz.getSimpleName());
					Object bean = QuartzConfigration.getBean(className);
					Method method = ReflectionUtils.findMethod(bean.getClass(), methodName, String.class);
					ReflectionUtils.invokeMethod(method, bean, args);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		};
	}

	/**
	 * Trigger
	 *
	 * @param cron
	 * @return
	 */
	private Trigger getTrigger(String cron) {
		return new Trigger() {
			@Override
			public Date nextExecutionTime(TriggerContext triggerContext) {
				CronTrigger trigger = new CronTrigger(cron);
				Date nextExec = trigger.nextExecutionTime(triggerContext);
				return nextExec;
			}
		};
	}
}
