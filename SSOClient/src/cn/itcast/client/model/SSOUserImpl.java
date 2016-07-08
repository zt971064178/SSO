package cn.itcast.client.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * ClassName:SSOUserImpl
 * Function: SSOUser的实现，服务端生成此对象实例并序列化后传输给客户端
 * @author   zhangtian
 * @Date	 2016	2016年2月27日		上午11:32:24
 */
@SuppressWarnings("serial")
public class SSOUserImpl implements SSOUser {

	private static final Map<String, Object> PROPERTY_MAP = new HashMap<String, Object>();
	@SuppressWarnings("unused")
	private String id;

    public SSOUserImpl(String id) {
        this.id = id ;
    }

	@Override
	public String getId() {
		return null;
	}

	/*
	 * 写入属性
	 */
	public void setProperties(Map<String, Object> properties) {
        PROPERTY_MAP.putAll(properties);
    }

	@Override
	public Object getProperty(String propertyName) {
		return PROPERTY_MAP.get(propertyName);
	}

	@Override
	public Set<String> propertyNames() {
		return PROPERTY_MAP.keySet();
	}
}
