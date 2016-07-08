package cn.itcast.sso.server.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.sso.server.Config;
import cn.itcast.sso.server.SpringContextUtil;
import cn.itcast.sso.server.TokenManager;
import cn.itcast.sso.server.model.LoginUser;
import cn.itcast.sso.server.service.UserSerializer;

/**
 * ClassName:ValidateServiceServlet
 * Function: 提供系统内网间VT验证服务
 * @author   zhangtian
 * @Date	 2016	2016年2月27日		下午1:52:05
 */
@WebServlet("/validate_service")
public class ValidateServiceServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");

		// 客户端传来的VT
		String vt = req.getParameter("vt") ;
		LoginUser loginUser = null ;

		// 验证vt有效性
		if(vt != null) {
			loginUser = TokenManager.validate(vt) ;
		}

		// 返回结果
		Config config = SpringContextUtil.getBean(Config.class) ;
		// 序列化用户信息
		UserSerializer userSerializer = config.getUserSerializer() ;

		try {
			// 将服务端获取的用户令牌信息响应给客户端
			resp.getWriter().write(userSerializer.serial(loginUser));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
