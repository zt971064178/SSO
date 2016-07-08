package cn.itcast.client;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.itcast.client.model.SSOUser;
import cn.itcast.client.model.UserDeserailizerFactory;
import cn.itcast.client.model.UserDeserializer;
import cn.itcast.utils.StringUtil;

/**
 * ClassName:TokenManager
 * Function: 令牌管理工具
 * @author   zhangtian
 * @Date	 2016	2016年2月27日		上午11:54:26
 */
public class TokenManager {

	// 复合结构体，含SSOUser与最后访问时间lastAccessTime两个成员
	// 可用于更新Cookie的过期时间判断
    private static class Token {
        private SSOUser user;
        private Date lastAccessTime;
    }

    // 缓存Map  本地的缓存
    private final static Map<String, Token> LOCAL_CACHE = new HashMap<String, TokenManager.Token>();

    static String serverIndderAddress; // 服务端内网通信地址

    private TokenManager() {

    }

    /**
     * validate:(验证vt有效性)
     * @param vt
     * @return
     * @throws Exception
     * @author zhangtian
     */
    public static SSOUser validate(String vt) throws Exception {

    	SSOUser user = localValidate(vt) ;

    	if(user == null) {
    		// 如果查询本地缓存为空  说明本地缓存中用户信息已经失效
    		// 远程校验服务器端的用户信息
    		user = remoteValidate(vt);
    	}
    	return user ;
    }

    // 远程验证vt有效性
    private static SSOUser remoteValidate(String vt) throws Exception {

    	URL url = new URL(serverIndderAddress + "validate_service?vt="+vt) ;
    	HttpURLConnection conn = (HttpURLConnection) url.openConnection() ;

    	InputStream in = conn.getInputStream() ;
    	conn.connect();

    	byte[] buff = new byte[in.available()] ;
    	in.read(buff) ;
    	String ret = new String(buff, "UTF-8") ;

    	conn.disconnect();
    	in.close();

    	// 将从远程获取的校验用户信息反序列化
    	UserDeserializer userDeserializer = UserDeserailizerFactory.create() ;
    	SSOUser user = StringUtil.isEmpty(ret) ? null : userDeserializer.deserail(ret) ;

    	// user不为空，则需要将服务端返回的用户信息存入到本地缓存中
    	if(user != null) {
    		// 处理本地缓存
    		cacheUser(vt ,user) ;
    	}
    	return user;
	}

    // 远程验证成功后将信息写入本地缓存
	private static void cacheUser(String vt, SSOUser user) throws Exception {
		Token token = new Token() ;
		token.user = user ;
		token.lastAccessTime = new Date() ;
		LOCAL_CACHE.put(vt, token) ;
	}

	// 在本地缓存验证有效性
    private static SSOUser localValidate(String vt) {
    	// 从缓存中查找数据
        Token token = LOCAL_CACHE.get(vt);

        // 用户存在
        if(token != null) {
        	// 更新最后访问时间
        	token.lastAccessTime = new Date() ;
        	// 返回用户信息
        	return token.user ;
        }

        return null ;
    }

    /**
     * timeout:(处理服务端发送的timeout通知)
     * @param vt
     * @param tokenTimeout
     * @return
     * @author zhangtian
     */
    public static Date timeout(String vt, int tokenTimeout) {

    	Token token = LOCAL_CACHE.get(vt) ;

    	if(token != null) {
    		Date laseAccessTime = token.lastAccessTime ;
    		// 最终过期时间
    		Date expires = new Date(laseAccessTime.getTime() + tokenTimeout * 60 * 1000) ;
    		Date now = new Date() ;

    		if(expires.compareTo(now) < 0) {// 已过期
    			// 从本地缓存移除
    			LOCAL_CACHE.remove(vt) ;
    			// 返回null表示此客户端缓存已过期
    			return null ;
    		} else {
    			return expires ;
    		}
    	} else {
    		return null ;
    	}
    }

    /**
     * invalidate:(用户退出时失效对应缓存)
     * @param vt
     * @author zhangtian
     */
    public static void invalidate(String vt) {
    	// 从本地缓存移除
    	LOCAL_CACHE.remove(vt) ;
    }

    /**
     * 服务端应用关闭时清空本地缓存，失效所有信息
     */
    public static void destroy() {
        LOCAL_CACHE.clear();
    }
}
