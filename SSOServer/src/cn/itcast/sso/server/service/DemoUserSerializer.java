package cn.itcast.sso.server.service;

import cn.itcast.sso.server.model.DemoLoginUser;
import cn.itcast.sso.server.model.LoginUser;

public class DemoUserSerializer extends UserSerializer {

	@Override
	protected void translate(LoginUser loginUser, UserData userData)
			throws Exception {

		// 实现类型已知，可强制转换
		DemoLoginUser demoLoginUser = (DemoLoginUser) loginUser;
        userData.setId(demoLoginUser.getLoginName());
        userData.setProperty("name", demoLoginUser.getLoginName());
        userData.setProperty("dept", "信息部");
        userData.setProperty("post", "IT管理员");
	}

}
