package cn.itcast.sso.server.service;

import java.util.Set;

import cn.itcast.sso.server.model.Credential;
import cn.itcast.sso.server.model.DemoLoginUser;
import cn.itcast.sso.server.model.LoginUser;

/**
 * ClassName:DemoAuthenticationHandler
 * Function: 示例性的鉴权处理器，当用户名和密码都为admin时授权通过，返回的也是一个示例性Credential实例
 * 没有验证码处理
 * @author   zhangtian
 * @Date	 2016	2016年2月23日		下午3:14:35
 */
public class DemoAuthenticationHandler implements IAuthenticationHandler {

	@Override
	public LoginUser authenticate(Credential credential) throws Exception {
		if("admin".equals(credential.getParameter("admin"))
				&& "admin".equals(credential.getParameter("passwd"))) {
			DemoLoginUser demoLoginUser = new DemoLoginUser() ;
			demoLoginUser.setLoginName("admin");
			return demoLoginUser ;
		} else {
			credential.setError("帐号或密码错误");
			return null;
		}
	}

	@Override
	public Set<String> authedSystemIds(LoginUser loginUser) throws Exception {
		return null;
	}

	@Override
	public LoginUser autoLogin(String lt) throws Exception {
		return null;
	}

	@Override
	public String loginToken(LoginUser loginUser) throws Exception {
		return null;
	}

	@Override
	public void clearLoginToken(LoginUser loginUser) throws Exception {
	}

}
