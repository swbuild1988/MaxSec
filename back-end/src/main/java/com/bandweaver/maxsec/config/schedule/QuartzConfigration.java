package com.bandweaver.maxsec.config.schedule;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 获取bean
 * @author ya.liu
 * @date 2019年12月23日
 */
@Component
public class QuartzConfigration implements ApplicationContextAware {

	private static ApplicationContext applicationContext;
	
	private QuartzConfigration() {
		
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		QuartzConfigration.applicationContext = applicationContext;
	}
	
	public static Object getBean(String beanName) {
        return applicationContext != null ? applicationContext.getBean(beanName) : null;
    }

}
