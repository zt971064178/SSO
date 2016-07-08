package cn.itcast.sso.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.itcast.sso.server.Config;

/**
 * ClassName:ConfigManageController
 * Function: 只是一个演示性的配置管理控制器
 * 刷新配置参数
 * @author   zhangtian
 * @Date	 2016	2016年2月23日		上午11:41:33
 */
@Controller
public class ConfigManageController {


	@Autowired
	private Config config;

	@RequestMapping("/config")
	public void configPage() {
	}

	/**
	 * config:(刷新配置参数，动态热部署获取配置的数据信息)
	 * @param code
	 * @return
	 * @throws Exception
	 * @author Administrator
	 */
	@RequestMapping("/config/refresh")
	@ResponseBody
	public boolean config(String code) throws Exception {
		// 模拟授权用户，即这里模拟只有test用户可以执行刷新配置
		if ("test".equals(code)) {
			config.refreshConfig();
			return true;
		}
		return false;
	}
}
