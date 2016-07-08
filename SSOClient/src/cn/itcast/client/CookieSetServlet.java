package cn.itcast.client;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.utils.StringUtil;

/**
 * ClassName:CookieSetServlet
 * Function: 用于服务端登录后跨域写Cookie
 * @author   zhangtian
 * @Date	 2016	2016年2月27日		下午2:24:34
 */
@WebServlet("/cookie_set")
@SuppressWarnings("serial")
public class CookieSetServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String vt = req.getParameter("vt") ;

		if(!StringUtil.isEmpty(vt)) {
			// P3P信息
            resp.addHeader("P3P", "CP=CURa ADMa DEVa PSAo PSDo OUR BUS UNI PUR INT DEM STA PRE COM NAV OTC NOI DSP COR");
            // 写cookie
            Cookie cookie = new Cookie("VT", vt);
            cookie.setPath("/");
            cookie.setHttpOnly(true);;
            resp.addCookie(cookie);
		}
	}

}
