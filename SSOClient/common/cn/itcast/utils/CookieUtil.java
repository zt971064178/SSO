package cn.itcast.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * ClassName:CookieUtil
 * Function: 操作Cookie
 * @author   zhangtian
 * @Date	 2016	2016年2月23日		上午10:16:52
 */
public class CookieUtil {
	private CookieUtil() {

	}

	/**
	 * 查找特定cookie值
	 * @param cookieName
	 * @param request
	 * @return
	 */
	public static String getCookie(String cookieName, HttpServletRequest request) {

		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(cookieName)) {
					return cookie.getValue();
				}
			}
		}

		return null;
	}

	/**
     * 删除cookie
     *
     * @param string
     * @param response
     */
    public static void deleteCookie(String cookieName, HttpServletResponse response, String path) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setMaxAge(0);
        if (path != null) {
            cookie.setPath("/");
        }
        response.addCookie(cookie);
    }
}
