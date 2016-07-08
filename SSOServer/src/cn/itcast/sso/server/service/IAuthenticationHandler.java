package cn.itcast.sso.server.service;

import java.util.Set;

import cn.itcast.sso.server.model.Credential;
import cn.itcast.sso.server.model.LoginUser;

/**
 * ClassName:IAuthenticationHandler
 * Function: 鉴权处理器   接口隔离抽象
 * @author   Administrator
 * @Date	 2016	2016年2月23日		上午11:18:45
 */
public interface IAuthenticationHandler {

	/**
	 * 鉴权
	 *
	 * @param params
	 *            页面传递过来的参数
	 * @param sessionAttr
	 *            特定session属性值，这个值是在跳到login页面前，loginPreHandler通过setSessionVal()
	 *            方法写入的
	 * @param errors
	 *            授权失败时，将失败信息写入此对象
	 * @return 授权成功返回Credentail, 否则返回null
	 */
	public LoginUser authenticate(Credential credential) throws Exception;

	/**
	 * 获取当前登录用户可用系统ID列表
	 *
	 * @param loginUser
	 * @return 返回null表示全部
	 * @throws Exception
	 */
	public Set<String> authedSystemIds(LoginUser loginUser) throws Exception;

	/**
	 * 自动登录
	 * @param lt
	 * @return
	 * @throws Exception
	 */
	public LoginUser autoLogin(String lt) throws Exception;

	/**
	 * 生成自动登录标识
	 * @param loginUser
	 * @return
	 * @throws Exception
	 */
	public String loginToken(LoginUser loginUser) throws Exception;

	/**
	 * 清除用户自动登录信息
	 * @param loginUser
	 * @return
	 * @throws Exception
	 */
	public void clearLoginToken(LoginUser loginUser) throws Exception;
}
