package cn.itcast.test;

import org.junit.Test;

import cn.itcast.sso.server.TokenManager;
import cn.itcast.sso.server.model.DemoLoginUser;

public class TestDemo {

	@Test
	public void testTokenManager() {
		System.out.println(TokenManager.class); ;
		TokenManager.addToken("1211", new DemoLoginUser());
		System.out.println("------------");
	}

}
