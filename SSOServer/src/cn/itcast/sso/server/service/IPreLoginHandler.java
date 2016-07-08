package cn.itcast.sso.server.service;

import java.util.Map;

import javax.servlet.http.HttpSession;

/**
 * ClassName:IPreLoginHandler
 * Function: 登录页前置处理器
 * @author   zhangtian
 * @Date	 2016	2016年2月23日		上午11:32:01
 */
public interface IPreLoginHandler {

	// 验证码存放进去的session键值
	public static final String SESSION_ATTR_NAME = "login_session_attr_name";

	/**
	 * 前置处理
	 */
	public abstract Map<?, ?> handle(HttpSession session) throws Exception;
}
