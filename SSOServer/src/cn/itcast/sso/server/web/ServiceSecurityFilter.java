package cn.itcast.sso.server.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import org.apache.log4j.Logger;

/**
 * ClassName:ServiceSecurityFilter
 * Function: 系统间内网通信的安全拦截器
 * @author   zhangtian
 * @Date	 2016	2016年2月27日		下午2:14:11
 */
@WebFilter({ "/validate_service", "/other_request" })
public class ServiceSecurityFilter implements Filter {

	private static final Logger logger = Logger.getLogger(ServiceSecurityFilter.class);

	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		// 此处执行安全认证，常用认证方式

        // 1. 从request的paramter,header中获取安全认证凭证，按凭证验证
        //   request.getParameter("credential_name");
        //   ((HttpServletRequest)request).getHeader("credential_name");

        // 2. 根据已配置的客户端列表得到客户端IP列表，仅限列表内IP访问
        //   Config config = SpringContextUtil.getBean(Config.class);
        //   config.getClientSystems(); 得到clientList
        //   request.getRemoteAddr() 等到用户地址
        logger.debug("service security filter已执行！");
        chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig config) throws ServletException {

	}

}
