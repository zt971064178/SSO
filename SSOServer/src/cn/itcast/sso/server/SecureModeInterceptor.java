package cn.itcast.sso.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * ClassName:SecureModeInterceptor
 * Function: 用于安全模式拦截判断的拦截器
 * @author   zhangtian
 * @Date	 2016	2016年2月23日		上午10:38:24
 */
public class SecureModeInterceptor implements HandlerInterceptor {

	@Autowired
	private Config config ;

	@Override
	public void afterCompletion(HttpServletRequest arg0,
			HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {

		// do nothing
	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1,
			Object arg2, ModelAndView arg3) throws Exception {

		// do nothing
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
			Object object) throws Exception {

		boolean ret = !config.isSecureMode() || request.isSecure() ;
		if(!ret) {
			response.getWriter().write("must https");
		}
		return ret;
	}

}
