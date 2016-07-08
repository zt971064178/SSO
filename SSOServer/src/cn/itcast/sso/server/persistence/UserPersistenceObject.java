package cn.itcast.sso.server.persistence;

import java.io.FileOutputStream;

import org.springframework.stereotype.Repository;

import cn.itcast.sso.server.model.DemoLoginUser;

/**
 * ClassName:UserPersistenceObject
 * Function: 登录用户信息持久化服务，相当于DAO对象的模拟
 * @author   zhangtian
 * @Date	 2016	2016年2月23日		上午11:34:03
 */
@Repository
public class UserPersistenceObject {

	/**
	 * 更新当前登录用户的lt标识
	 * @param loginName
	 * @param lt
	 * @throws Exception
	 */
	public void updateLoginToken(String loginName, String lt) throws Exception {

		//将信息写入存储文件test，格式为lt=loginName，如：02564fc6a02a35c689cbdf898458d2da=admin
		// 自动登录的模拟数据库更新登录标识
		FileOutputStream fos = new FileOutputStream("E:/test.txt");
		fos.write((lt + "=" + loginName).getBytes());
		fos.close();
	}

	/**
	 * 按登录账号查询用户信息
	 * @param parameter
	 * @return
	 */
	public DemoLoginUser getUser(String uname) {
		// 模拟访问数据库  查询用户信息
		if ("admin".equals(uname)) {
			DemoLoginUser loginUser = new DemoLoginUser();
			loginUser.setLoginName("admin");
			return loginUser;
		}
		return null;
	}
}
