package cn.itcast.sso.server.service;

import java.io.FileInputStream;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import cn.itcast.sso.server.model.Credential;
import cn.itcast.sso.server.model.DemoLoginUser;
import cn.itcast.sso.server.model.LoginUser;
import cn.itcast.sso.server.persistence.UserPersistenceObject;
import cn.itcast.utils.MD5;
import cn.itcast.utils.StringUtil;

public class CaptchaAuthenticationHandler implements IAuthenticationHandler {

	/*
	 * 注入持久化对象
	 */
	@Autowired
	private UserPersistenceObject userPersistenceObject;

	@Override
	public LoginUser authenticate(Credential credential) throws Exception {
		// 获取session中保存的验证码
		String sessionCode = (String) credential.getSettedSessionValue() ;
		// 获取用户输入的验证码
		String captcha = credential.getParameter("captcha") ;

		if (!captcha.equalsIgnoreCase(sessionCode)) {
			credential.setError("验证码错误");
			return null;
		}

		// 从持久化中查询登录账号对应的用户对象  从数据库中查找用户信息
		DemoLoginUser loginUser = userPersistenceObject.getUser(credential.getParameter("name"));

		if(loginUser != null) {
			// 用户输入的密码从客户端传入是已经经过加密处理
			String passwd = credential.getParameter("passwd");
			// 密码MD5之后与验证码同时MD5增加破解难度
			String passwd2 = MD5.encode(MD5.encode(loginUser.getPasswd()) + sessionCode);
			if(StringUtils.equals(passwd, passwd2)) {
				// 验证通过  返回用户信息
				return loginUser ;
			}
		}

		credential.setError("帐号或密码错误");
		return null;
	}

	@Override
	public Set<String> authedSystemIds(LoginUser loginUser) throws Exception {
		return null;
	}

	// 自动登录
	@Override
	public LoginUser autoLogin(String lt) throws Exception {

		// 从持久化存储中按lt查找对应loginUser
		FileInputStream fis = new FileInputStream("d:/test");
		byte[] buff = new byte[fis.available()];
		fis.read(buff);
		fis.close();

		String tmp = new String(buff);
		String[] tmps = tmp.split("=");

		// 相当于从存储中找个了与lt匹配的数据记录
		// 类似于从数据库中查找记录，tmps[0]为一个随机的字符串，根据其查找到用户名
		if (lt.equals(tmps[0])) {
			// 将匹配的数据装配成loginUser对象
			DemoLoginUser loginUser = userPersistenceObject.getUser(tmps[1]);
			return loginUser;
		}

		// 没有匹配项则表示自动登录标识无效
		return null;
	}

	// 生成自动登录标识
	@Override
	public String loginToken(LoginUser loginUser) throws Exception {
		DemoLoginUser demoLoginUser = (DemoLoginUser) loginUser;
		// 生成一个唯一标识用作lt
		String lt = StringUtil.uniqueKey();
		// 将新lt更新到当前user对应字段
		userPersistenceObject.updateLoginToken(demoLoginUser.getLoginName(), lt);
		return lt;
	}

	// 更新持久化的lt
	@Override
	public void clearLoginToken(LoginUser loginUser) throws Exception {
		DemoLoginUser demoLoginUser = (DemoLoginUser) loginUser;
		userPersistenceObject.updateLoginToken(demoLoginUser.getLoginName(), null);
	}

}
