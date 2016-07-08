package cn.itcast.client;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * ClassName:ServerNoticeServlet
 * Function: 接收服务端发送的通知
 * @author   zhangtian
 * @Date	 2016	2016年2月27日		下午2:29:09
 */
@WebServlet("/notice/*")
@SuppressWarnings("serial")
public class ServerNoticeServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		// notice后路径为notice类型，如/notice/timeout，则当前通知为timeout类型
		String uri = req.getRequestURI() ;
		String cmd = uri.substring(uri.lastIndexOf("/") + 1) ;
		String vt = req.getParameter("vt") ;

		switch (cmd) {
		case "timeout":
			// 超时过期
			int tokenTimeout = Integer.parseInt(req.getParameter("tokenTimeout")) ;
			Date expires = TokenManager.timeout(vt, tokenTimeout) ;
			resp.getWriter().write(expires == null ? "" : String.valueOf(expires.getTime()));
			break;

		case "logout":
			// 退出销毁
            TokenManager.invalidate(vt);
            resp.getWriter().write("true");
			break;

		case "shutdown" :
			// 服务器关闭销毁
			TokenManager.destroy();
            resp.getWriter().write("true");
			break ;
		}
	}

}
