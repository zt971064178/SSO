package cn.itcast.client.model;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * ClassName:JsonUserDeserailizer
 * Function: 反序列化JSON格式数据
 * @author   zhangtian
 * @Date	 2016	2016年2月27日		上午11:16:19
 */
public class JsonUserDeserailizer implements UserDeserializer {

	private final ObjectMapper mapper = new ObjectMapper();

	// 反序列化
	@SuppressWarnings("unchecked")
	@Override
	public SSOUser deserail(String userDate) throws Exception {

		JsonNode rootNode = mapper.readTree(userDate) ;

		String id = rootNode.get("id").textValue() ;
		if(id == null) {
			return null ;
		}else {
			SSOUserImpl user = new SSOUserImpl(id) ;
			JsonNode properties = rootNode.get("properties") ;
			Map<String, Object> propertyMap = mapper.readValue(properties.toString(), HashMap.class);
			user.setProperties(propertyMap);
			return user ;
		}
	}
}
