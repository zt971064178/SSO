package cn.itcast.sso.server.model;

/**
 * ClassName:DemoLoginUser
 * Function: 该为具体的用户对象,持久化基本信息
 * @author   Administrator
 * @Date	 2016	2016年2月23日		上午11:35:55
 */
@SuppressWarnings("serial")
public class DemoLoginUser extends LoginUser {

	private String loginName;

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getLoginName() {
		return loginName;
	}

	public String getPasswd() {
		return "admin";
	}

	@Override
	public String toString() {
		return loginName;
	}
}
