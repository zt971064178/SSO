package cn.itcast.sso.server;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * ClassName:SpringContextUtil
 * Function: 获取Spring容器中的Bean
 * @author   zhangtian
 * @Date	 2016	2016年2月23日		上午11:55:40
 */
@Component
public class SpringContextUtil implements ApplicationContextAware {

	private static ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext ctx) throws BeansException {
		System.out.println(ctx);
		SpringContextUtil.applicationContext = ctx ;
	}

	/*
	 * 获取ApplicationContext
	 */
	public static ApplicationContext getApplicationContext() {
		return applicationContext ;
	}

	/*
	 * 按名称获取spring bean实例
	 */
	public static Object getBean(String beanName) {
		return applicationContext.getBean(beanName) ;
	}

	/*
	 * 按类型获取spring bean 实例
	 */
	public static <T> T getBean(Class<T> beanType) {
		return applicationContext.getBean(beanType) ;
	}

}
