package cn.itcast.sso.server.web;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * ClassName:SystemInitListener
 * Function: 监听器获取系统初始信息以及上下文信息
 * @author   Administrator
 * @Date	 2016	2016年2月23日		下午4:31:43
 */
@WebListener
public class SystemInitListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {

	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		ServletContext servletContext = event.getServletContext();
		servletContext.setAttribute("appctx", servletContext.getContextPath());
		servletContext.setAttribute("sysName", servletContext.getInitParameter("sysName"));
	}
}
