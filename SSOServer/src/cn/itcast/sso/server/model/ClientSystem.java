package cn.itcast.sso.server.model;

import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import cn.itcast.utils.StringUtil;

@SuppressWarnings("serial")
public class ClientSystem implements Serializable {
	private String id; // 唯一标识
	private String name; // 系统名称
	private String baseUrl; // 应用基路径，代表应用访问起始点
	private String homeUrl; // 应用主页面URI，baseUrl + homeUri = 主页URL
	private String innerAddress; // 系统间内部通信地址

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getInnerAddress() {
		return innerAddress;
	}

	public void setInnerAddress(String innerAddress) {
		this.innerAddress = innerAddress;
	}

	/**
	 * 与客户端系统通信，通知客户端token过期
	 *
	 * @param tokenTimeout
	 * @return 延期的有效期
	 */
	public Date noticeTimeout(String vt, int tokenTimeout) {

		try {
			String url = innerAddress + "/notice/timeout?vt=" + vt + "&tokenTimeout=" + tokenTimeout;
			String ret = httpAccess(url) ;

			if(StringUtil.isEmpty(ret)) {
				// 令牌过期
				return null ;
			} else {
				// 最新有效时间
				return new Date(Long.parseLong(ret)) ;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null ;
		}
	}

	private String httpAccess(String theUrl) throws Exception {

		URL url = new URL(theUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(500);
        InputStream is = conn.getInputStream();
        conn.connect();

        byte[] buff = new byte[is.available()];
        is.read(buff);
        String ret = new String(buff, "utf-8");

        conn.disconnect();
        is.close();

        return ret;
	}

	/**
	 * 通知客户端用户退出
	 */
	public boolean noticeLogout(String vt) {

		try {
			String url = innerAddress + "/notice/logout?vt=" + vt;
            String ret = httpAccess(url);
            return Boolean.valueOf(ret) ;
		} catch (Exception e) {
			e.printStackTrace();
			return false ;
		}
	}

	/**
	 * 通知客户端服务端关闭，客户端收到信息后执行清除缓存操作
	 */
	public boolean noticeShutdown() {

		try {
            String url = innerAddress + "/notice/shutdown";
            String ret = httpAccess(url);

            return Boolean.parseBoolean(ret);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
	}

	public String getHomeUrl() {
		return homeUrl;
	}

	public void setHomeUrl(String homeUrl) {
		this.homeUrl = homeUrl;
	}
}
