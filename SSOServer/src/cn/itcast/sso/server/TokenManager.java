package cn.itcast.sso.server;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import cn.itcast.sso.server.model.ClientSystem;
import cn.itcast.sso.server.model.LoginUser;

/**
 * ClassName:TokenManager
 * Function: 令牌管理
 * 存储VT_USER信息，并提供操作方法
 * @author   zhangtian
 * @Date	 2016	2016年2月23日		上午11:53:12
 */
public class TokenManager {

	private static Logger logger = Logger.getLogger(TokenManager.class) ;

	// 定时器  启动执行
	private static final Timer timer = new Timer(true) ;
	private static final Config config = SpringContextUtil.getBean(Config.class) ;

	// 静态代码块  在类出事加载时执行
	// 定时器定时监听处理令牌是否有效 判断过期
	static {
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				for(Entry<String, Token> entry : DATA_MAP.entrySet()) {
					String vt = entry.getKey() ;
					Token token = entry.getValue() ;
					Date expired = token.expired ;
					Date now = new Date() ;

					// 当前时间大于过期时间 处理  否则不做任何处理
					if(now.compareTo(expired) > 0) {
						// 因为令牌支持自动延期服务，并且应用客户端缓存机制后，
						// 令牌最后访问时间是存储在客户端的，所以服务端向所有客户端发起一次timeout通知，
						// 客户端根据lastAccessTime + tokenTimeout计算是否过期，<br>
						// 若未过期，用各客户端最大有效期更新当前过期时间
						List<ClientSystem> clientSystems = config.getClientSystems() ;
						Date maxClientExpired = expired ;
						for(ClientSystem clientSystem : clientSystems) {
							// 通知所有客户端token过期，返回最近访问时间
							Date clientExpired = clientSystem.noticeTimeout(vt, config.getTokenTimeout()) ;
							if(clientExpired != null && clientExpired.compareTo(now) > 0) {
								// 取最大的时间
								maxClientExpired = maxClientExpired.compareTo(clientExpired) < 0 ? clientExpired : maxClientExpired ;
							}
						}

						// 客户端最大过期时间大于当前
						if(maxClientExpired.compareTo(now) > 0) {
							logger.debug("更新过期时间到" + maxClientExpired);
							token.expired = maxClientExpired;
						} else {
							logger.debug("清除过期token：" + vt);
							// 已过期，清除对应token
							DATA_MAP.remove(vt);
						}
					}
				}
			}
		}, 60 * 1000, 60 * 1000);
	}

	// 避免静态类被实例化
	public TokenManager() {

	}

	// 复合结构体，含loginUser与过期时间expried两个成员
	private static class Token {
		// 登录 用户对象
		private LoginUser loginUser;
		// 过期时间
		private Date expired;
	}

	// 令牌存储结构
	private static final Map<String, Token> DATA_MAP = new ConcurrentHashMap<String, TokenManager.Token>();

	/**
	 * 验证令牌有效性
	 * @param vt
	 * @return
	 */
	public static LoginUser validate(String vt) {
		Token token = DATA_MAP.get(vt);
		return token == null ? null : token.loginUser;
	}

	/**
	 * 用户授权成功后将授权信息存入
	 */
	public static void addToken(String vt, LoginUser loginUser) {
		Token token = new Token() ;
		token.loginUser = loginUser ;
		token.expired = new Date(new Date().getTime() + config.getTokenTimeout() * 60 * 1000) ;
		DATA_MAP.put(vt, token) ;
	}

	/**
	 * invalid:(令牌失效)
	 * @param vt
	 */
	public static void invalid(String vt) {
		if (vt != null) {
			DATA_MAP.remove(vt);
		}
	}
}
