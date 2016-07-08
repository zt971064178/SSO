package cn.itcast.client;

import javax.servlet.http.HttpServletRequest;

import cn.itcast.client.model.SSOUser;

/**
 * ClassName:UserHolder
 * Function: 供业务系统使用的用户对象获取工具类
 * @author   zhangtian
 * @Date	 2016	2016年2月27日		上午11:41:53
 */
public class UserHolder {

	// 记录存放到request属性中的SSOUser对象
	private static final String REQ_USER_ATTR_NAME = "__current_sso_user";
	// 将当前登录用户信息存放到ThreadLocal中，这样在没有单独开线程的情况下，业务系统任意代码位置都可以取得当前user
    private static final ThreadLocal<SSOUser> userThreadLocal = new ThreadLocal<SSOUser>();

    private UserHolder() {

    }

    /**
     * getUser:(获取SSOUser实例)
     * @return
     * @author zhangtian
     */
    public static SSOUser getUser() {
    	return userThreadLocal.get() ;
    }

    /**
     * getUser:(从当前请求属性中获取SSOUser)
     * @param request
     * @return
     * @author zhangtian
     */
    public static SSOUser getUser(HttpServletRequest request) {
    	return (SSOUser) request.getAttribute(REQ_USER_ATTR_NAME) ;
    }

    /**
     * setUser:
     * 用户加入到request和threadLocal供业务系统调用<br>
     * 以default为方法作用范围，仅本包内代码可访问，将此方法对用户代码隐藏
     * 该方法只允许在本包内操作，固方法设置默认属性隔离
     * @param user
     * @param request
     * @author zhangtian
     */
    static void setUser(SSOUser user, HttpServletRequest request) {
    	request.setAttribute(REQ_USER_ATTR_NAME, user);
    	userThreadLocal.set(user);
    }
}
