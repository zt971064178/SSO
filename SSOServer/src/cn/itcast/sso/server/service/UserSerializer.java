package cn.itcast.sso.server.service;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import cn.itcast.sso.server.model.LoginUser;

/**
 * ClassName:UserSerializer
 * Function: 用户信息序列化工具
 * @author   zhangtian
 * @Date	 2016	2016年2月27日		下午1:57:04
 */
public abstract class UserSerializer {

	/**
	 * ClassName:UserDate
	 * Function: 用户数据类型
	 * 用户构建的数据类型与客户端反序列化保持一致，保证数据的序列化与反序列化的正确执行
	 * @author   zhangtian
	 * @Date	 2016	2016年2月27日		下午1:59:58
	 */
	protected class UserData {
		// 唯一标识
		private String id ;
		// 其它属性
		private Map<String, Object> properties = new HashMap<String, Object>() ;

		public Map<String, Object> getProperties() {
			return properties;
		}

		public void setProperties(Map<String, Object> properties) {
			this.properties = properties;
		}

		// 新增单个属性
		public void setProperty(String key, Object value) {
            this.properties.put(key, value);
        }

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}
	}

	/*
	 * 数据转换
	 */
	 protected abstract void translate(LoginUser loginUser, UserData userData) throws Exception;

	 /**
	  * serial:(序列化)
	  * @param loginUser
	  * @return
	  * @throws Exception
	  * @author zhangtian
	  */
	 public String serial(LoginUser loginUser) throws Exception {
	        final ObjectMapper mapper = new ObjectMapper();
	        UserData userData = new UserData();

	        // 在继承的具体实现类中完成转换
	        if (loginUser != null) {
	        	// 将loginuser转换为封装的客户端与服务端约定的统一格式
	            translate(loginUser, userData);
	        }

	        return mapper.writeValueAsString(userData);
	    }
}
