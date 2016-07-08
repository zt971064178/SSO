package cn.itcast.client.model;

/**
 * ClassName:UserDeserializer
 * Function: 将服务端传来的user数据反序列化
 * @author   zhangtian
 * @Date	 2016	2016年2月27日		上午11:12:04
 */
public interface UserDeserializer {

	/**
	 * deserail:(反序列化)
	 * @param userDate
	 * @return
	 * @throws Exception
	 * @author zhangtian
	 */
	public SSOUser deserail(String userDate) throws Exception;
}
