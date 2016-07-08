package cn.itcast.utils;
import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

import org.apache.commons.codec.binary.Base64;
/**
 * ClassName:DES
 * Function: DES工具
 * @author   zhangtian
 * @Date	 2016	2016年2月23日		上午10:11:47
 */
public class DES {

	/**
	 * 加密
	 * @param source
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String encrypt(String source, String key) throws Exception {

		Cipher cipher = Cipher.getInstance("DES");
		cipher.init(Cipher.ENCRYPT_MODE, getKey(key));
		byte[] dest = cipher.doFinal(source.getBytes("utf-8"));
		return Base64.encodeBase64String(dest);
	}

	/**
	 * 解密
	 * @param source
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String decrypt(String source, String key) throws Exception {

		Cipher cipher = Cipher.getInstance("DES");
		cipher.init(Cipher.DECRYPT_MODE, getKey(key));
		byte[] dest = cipher.doFinal(Base64.decodeBase64(source));
		return new String(dest, "utf-8");
	}

	private static Key getKey(String key) throws Exception {
		KeyGenerator keyGenerator = KeyGenerator.getInstance("DES");
		keyGenerator.init(new SecureRandom(key.getBytes("UTF-8")));
		Key skey = keyGenerator.generateKey();
		return skey;
	}

	public static void main(String[] args) throws Exception {

		String src = "哈罗hello,test，大厦";
		String key = "a--2++sdf";

		String dest = DES.encrypt(src, key);
		System.out.println(dest);

		System.out.println(DES.decrypt(dest, "a--2++sdf"));

	}
}
