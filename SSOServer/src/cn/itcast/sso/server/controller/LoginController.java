package cn.itcast.sso.server.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.itcast.sso.server.Config;
import cn.itcast.sso.server.TokenManager;
import cn.itcast.sso.server.model.ClientSystem;
import cn.itcast.sso.server.model.Credential;
import cn.itcast.sso.server.model.LoginUser;
import cn.itcast.sso.server.service.IPreLoginHandler;
import cn.itcast.utils.CookieUtil;
import cn.itcast.utils.StringUtil;

/**
 * ClassName:LoginController
 * Function: 服务端登陆入口
 * @author   zhangtian
 * @Date	 2016	2016年2月23日		上午11:40:16
 */
@Controller
public class LoginController {

	// 注入config
	@Autowired
	private Config config ;

	/**
	 * login:(登陆入口，Get方式url携带参数登陆)
	 * @param request
	 * @param response
	 * @param backUrl
	 * @param modelMap
	 * @return
	 * @throws Exception
	 * @author Administrator
	 */
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login(HttpServletRequest request, HttpServletResponse response, String backUrl, ModelMap map, Boolean notLogin) throws Exception {

		// 从Cookie中获取登陆验证信息 如：可能存有的用户信息
		String vt = CookieUtil.getCookie("VT", request) ;

		// cookie不存在，即客户端登陆验证信息不存在
		if(StringUtils.isBlank(vt)) {
			// VT不存在,判断是否有自动登录
			String lt = CookieUtil.getCookie("LT", request) ;
			if(StringUtils.isBlank(lt)) {
				// 如果lt也为空，跳转到登陆页面
				return authFailed(notLogin, response, backUrl) ;
			}else {
				// VT不存在， LT存在,自动登录
				LoginUser loginUser = config.getAuthenticationHandler().autoLogin(lt) ;

				if(loginUser == null) {
					// 自动登录失败
					return authFailed(notLogin, response, backUrl) ;
				} else {
					// 自动登录成功  维护令牌
					vt = authSuccess(response, loginUser, true) ;
					return validateSuccess(backUrl, vt, loginUser, response, map) ;
				}
			}
		} else{
			// cookie存在,VT存在
			LoginUser loginUser = TokenManager.validate(vt) ;
			if(loginUser != null) {
				return validateSuccess(backUrl, vt, loginUser, response, map) ;
			} else {
				// VT失效  转入登录页
				return authFailed(notLogin, response, backUrl) ;
			}
		}
	}

	// 授权成功后的操作
	private String authSuccess(HttpServletResponse response,
			LoginUser loginUser, Boolean rememberMe) throws Exception {
		// 生成VT
		String vt = StringUtil.uniqueKey() ;
		// 生成LT
		if(rememberMe != null && rememberMe) {
			String lt = config.getAuthenticationHandler().loginToken(loginUser);
			setLtCookie(lt, response);
		}

		// 维护令牌  写入map
		TokenManager.addToken(vt, loginUser);
		// 写入Cookie
		Cookie cookie = new Cookie("VT", vt) ;

		// 是否仅https模式，如果是，设置cookie secure为true
		if (config.isSecureMode()) {
			cookie.setSecure(true);
		}

		response.setHeader("P3P",
                "CP=\"CURa ADMa DEVa PSAo PSDo OUR BUS UNI PUR INT DEM STA PRE COM NAV OTC NOI DSP COR\"");
		response.addCookie(cookie);
		return vt;
	}

	private void setLtCookie(String lt, HttpServletResponse response) {
		Cookie ltCookie = new Cookie("LT", lt) ;
		ltCookie.setMaxAge(config.getAutoLoginExpDays() * 24 * 60 * 60);
		if (config.isSecureMode()) {
			ltCookie.setSecure(true);
		}
		response.addCookie(ltCookie);
	}

	/*
	 * VT验证成功或登录成功后的操作
	 */
	public String validateSuccess (String backUrl, String vt, LoginUser loginUser, HttpServletResponse response, ModelMap map) throws Exception {

		// 有携带的请求url，跳转到请求url并携带令牌
		if(StringUtils.isNotBlank(backUrl)) {
			response.sendRedirect(StringUtil.appendUrlParameter(backUrl, "__vt_param__", vt));
			return null ;
		} else {
			// 如果是登录页  则存储用户信息
			map.put("sysList", config.getClientSystems(loginUser)) ;
			map.put("vt", vt) ;
			map.put("loginUser", loginUser) ;
			return config.getLoginViewName() ;
		}
	}

	/**
	 * preLogin:(登陆之前预处理,登陆之前已经将验证码信息存入session中)
	 * @param httpSession
	 * @return
	 * @throws Exception
	 * @author Administrator
	 */
	@RequestMapping("preLogin")
	@ResponseBody
	public Object preLogin(HttpSession httpSession) throws Exception {
		IPreLoginHandler preLoginHandler = config.getPreLoginHandler() ;
		if(preLoginHandler == null) {
			throw new Exception("没有配置preLoginHandler,无法执行预处理");
		}
		// 传递session用于存放验证码
		return preLoginHandler.handle(httpSession);
	}

	/**
	 * login:(登录验证)
	 * @param backUrl
	 * @param rememberMe
	 * @param request
	 * @param session
	 * @param response
	 * @param map
	 * @return
	 * @throws Exception
	 * @author Administrator
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/login")
	public String login(String backUrl, Boolean rememberMe,
			HttpServletRequest request, HttpSession session,
			HttpServletResponse response, ModelMap map) throws Exception {

		// 存放的用户名 密码等用户信息
		final Map<String, String[]> params = request.getParameterMap();
		// 获取验证码保存的session中的值
		final Object sessionVal = session.getAttribute(IPreLoginHandler.SESSION_ATTR_NAME);

		Credential credential = new Credential() {
			@Override
			public Object getSettedSessionValue() {
				return sessionVal;
			}

			@Override
			public String[] getParameterValue(String name) {
				return params.get(name);
			}

			@Override
			public String getParameter(String name) {
				String[] tmp = params.get(name);
				return tmp != null && tmp.length > 0 ? tmp[0] : null;
			}
		};

		// 对用户信息进行鉴权处理  抽象处理
		// 包括处理验证码  用户名  密码的匹配
		// 鉴前处理器与预登陆处理器在Spring配置文件中已经注入  可以插拔替换
		LoginUser loginUser = config.getAuthenticationHandler().authenticate(credential);
		if (loginUser == null) {
			map.put("errorMsg", credential.getError());
			return config.getLoginViewName();
		} else {
			String vt = authSuccess(response, loginUser, rememberMe);
			return validateSuccess(backUrl, vt, loginUser, response, map);
		}
	}

	/**
	 * logout:(用户退出  单点退出)
	 * @param backUrl
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 * @author Administrator
	 */
	@RequestMapping("/logout")
	public String logout(String backUrl, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String vt = CookieUtil.getCookie("VT", request);
		// 清除自动登录信息
		LoginUser loginUser = TokenManager.validate(vt);
		if(loginUser != null) {
			// 清除服务端自动登录状态
			config.getAuthenticationHandler().clearLoginToken(loginUser);
			// 清除自动登录cookie
			Cookie ltCookie = new Cookie("LT", null);
			ltCookie.setMaxAge(0);
			response.addCookie(ltCookie);
		}

		// 移除token
		TokenManager.invalid(vt);

		// 移除server端vt cookie
		Cookie cookie = new Cookie("VT", null);
		cookie.setMaxAge(0);
		response.addCookie(cookie);

		// 通知各客户端logout
		for (ClientSystem clientSystem : config.getClientSystems()) {
			clientSystem.noticeLogout(vt);
		}

		// 退出到登出页面
		if (backUrl == null) {
			return "/logout";
		} else {
			// 如果独立系统有携带自己的登出链接  则登出到自己的页面
			response.sendRedirect(backUrl);
			return null;
		}
	}

	/**
	 * authFailed:(授权认证失败时返回的内容设置)
	 * @param notLogin
	 * @param response
	 * @param backUrl
	 * @return
	 * @throws IOException
	 * @author Administrator
	 */
	private String authFailed(Boolean notLogin, HttpServletResponse response, String backUrl) throws IOException {
		// notLogin用来用户自定义选择  令牌失效登陆失败的处理情况
		// 可以选择跳转到登录页  也可以选择在本次链接返回403错误等
		if (notLogin != null && notLogin) {
            response.sendRedirect(StringUtil.appendUrlParameter(backUrl, "__vt_param__", ""));
            return null;
        } else {
            return config.getLoginViewName();
        }
	}
}
