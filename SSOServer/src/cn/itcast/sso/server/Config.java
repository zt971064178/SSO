package cn.itcast.sso.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import cn.itcast.sso.server.model.ClientSystem;
import cn.itcast.sso.server.model.LoginUser;
import cn.itcast.sso.server.service.IAuthenticationHandler;
import cn.itcast.sso.server.service.IPreLoginHandler;
import cn.itcast.sso.server.service.UserSerializer;

/**
 * ClassName:Config
 * Function: 应用配置信息
 * @author   zhangtian
 * @Date	 2016	2016年2月23日		上午10:41:47
 */
public class Config implements ResourceLoaderAware {
	private static Logger logger = Logger.getLogger(Config.class);

	private ResourceLoader resourceLoader;
	// 鉴权处理器
	private IAuthenticationHandler authenticationHandler;
	// 登录前预处理器
	private IPreLoginHandler preLoginHandler;
	// 登录页面视图名称
	private String loginViewName = "/login";
	// 令牌有效期，单位为分钟，默认30分钟
	private int tokenTimeout = 30;
	// 是否必须为https
	private boolean secureMode = false;
	// 自动登录状态有效期限，默认一年
	private int autoLoginExpDays = 365;
	// 子系统列表
	private List<ClientSystem> clientSystems = new ArrayList<ClientSystem>();

	// 用户信息转换序列化实现  Spring注入
    private UserSerializer userSerializer;

	/**
	 * refreshConfig:(重新加载配置，以支持热部署)
	 * @author zhangtian
	 */
	public void refreshConfig() throws Exception {

		// 加载config.properties
		Properties configProperties = new Properties() ;

		/*
		 * 执行重新加载读取属性文件内容信息
		 */
		try {
			Resource resource = resourceLoader.getResource("classpath:config.properties") ;
			configProperties.load(resource.getInputStream());
		} catch (IOException e) {
			logger.warn("在classpath下未找到配置文件config.properties");
		}

		// vt有效期参数,调整令牌有效期
		String configTokenTimeout = (String) configProperties.get("tokenTimeout");
		if(StringUtils.isNotBlank(configTokenTimeout)) {
			try {
				tokenTimeout = Integer.parseInt(configTokenTimeout);
				logger.debug("config.properties设置tokenTimeout={}" +tokenTimeout);
			} catch (NumberFormatException e) {
				logger.warn("tokenTimeout参数配置不正确");
			}
		}

		// 是否仅https安全模式下运行,设置服务器是否启用安全模式
		String configScureMode = configProperties.getProperty("secureMode");
		if (StringUtils.isNotBlank(configScureMode)) {
			this.secureMode = Boolean.parseBoolean(configScureMode);
			logger.debug("config.properties设置secureMode={}" +this.secureMode);
		}

		// 自动登录有效期,设置自动登录的有效期
		String configAutoLoginExpDays = configProperties.getProperty("autoLoginExpDays");
		if (StringUtils.isNotBlank(configAutoLoginExpDays)) {
			try {
				autoLoginExpDays = Integer.parseInt(configAutoLoginExpDays);
				logger.debug("config.properties设置autoLoginExpDays={}" +autoLoginExpDays);
			} catch (NumberFormatException e) {
				logger.warn("autoLoginExpDays参数配置不正确");
			}
		}

		// 加载客户端系统配置列表,为xml中定义的客户端系统列表
		try {
			loadClientSystems();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("加载client system配置失败");
		}
	}

	// 加载客户端系统配置列表
	@SuppressWarnings("unchecked")
	private void loadClientSystems() throws Exception {
		Resource resource = resourceLoader
				.getResource("classpath:client_systems.xml");
		// dom4j
		SAXReader reader = new SAXReader();
		Document doc = reader.read(resource.getInputStream());

		Element rootElement = doc.getRootElement();
		List<Element> systemElements = rootElement.elements();

		clientSystems.clear();
		for (Element element : systemElements) {
			ClientSystem clientSystem = new ClientSystem();

			clientSystem.setId(element.attributeValue("id"));
			clientSystem.setName(element.attributeValue("name"));
			clientSystem.setBaseUrl(element.elementText("baseUrl"));
			clientSystem.setHomeUrl(element.elementText("homeUrl"));
			clientSystem.setInnerAddress(element.elementText("innerAddress"));

			clientSystems.add(clientSystem);
		}
	}

	/**
	 * 应用停止时执行，做清理性工作，如通知客户端logout
	 */
	public void destroy() {
		for (ClientSystem clientSystem : clientSystems) {
			clientSystem.noticeShutdown();
		}
	}

	/*
	 * 获取当前鉴权处理器  通过Spring配置文件注入，与具体的业务系统隔离
	 */
	public IAuthenticationHandler getAuthenticationHandler() {
		return authenticationHandler;
	}

	public void setAuthenticationHandler(
			IAuthenticationHandler authenticationHandler) {
		this.authenticationHandler = authenticationHandler;
	}

	/*
	 * 获取登录前预处理器 通过Spring配置文件注入，与具体的业务系统隔离
	 */
	public IPreLoginHandler getPreLoginHandler() {
		return preLoginHandler;
	}

	public void setPreLoginHandler(IPreLoginHandler preLoginHandler) {
		this.preLoginHandler = preLoginHandler;
	}

	/*
	 * 获取登录页面视图名称 通过Spring配置文件注入，与具体的业务系统隔离
	 */
	public String getLoginViewName() {
		return loginViewName;
	}

	public void setLoginViewName(String loginViewName) {
		this.loginViewName = loginViewName;
	}

	/*
	 * 获取令牌有效期，单位为分钟
	 */
	public int getTokenTimeout() {
		return tokenTimeout;
	}

	public void setTokenTimeout(int tokenTimeout) {
		this.tokenTimeout = tokenTimeout;
	}

	public int getAutoLoginExpDays() {
		return autoLoginExpDays;
	}

	public void setAutoLoginExpDays(int autoLoginExpDays) {
		this.autoLoginExpDays = autoLoginExpDays;
	}

	public List<ClientSystem> getClientSystems() {
		return clientSystems;
	}

	/**
	 * 获取指定用户的可用系统列表
	 * @param loginUser
	 * @return
	 * @throws Exception
	 */
	public List<ClientSystem> getClientSystems(LoginUser loginUser)
			throws Exception {
		// 鉴前处理器使用接口隔离抽象
		Set<String> authedSysIds = getAuthenticationHandler().authedSystemIds(loginUser);

		// null表示允许全部
		if (authedSysIds == null) {
			return clientSystems;
		}

		List<ClientSystem> auhtedSystems = new ArrayList<ClientSystem>();
		for (ClientSystem clientSystem : clientSystems) {
			if (authedSysIds.contains(clientSystem.getId())) {
				auhtedSystems.add(clientSystem);
			}
		}

		return auhtedSystems;
	}

	public void setClientSystems(List<ClientSystem> clientSystems) {
		this.clientSystems = clientSystems;
	}

	public boolean isSecureMode() {
		return secureMode;
	}

	// 获取resourceLoader对象 执行重新加载属性文件
	@Override
	public void setResourceLoader(ResourceLoader loader) {
		this.resourceLoader = loader ;
	}

	public UserSerializer getUserSerializer() {
		return userSerializer;
	}

	public void setUserSerializer(UserSerializer userSerializer) {
		this.userSerializer = userSerializer;
	}

}
