package cn.itcast.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * ClassName:MD5
 * Function: MD5工具
 * @author   zhangtian
 * @Date	 2016	2016年2月23日		上午10:05:14
 */
public class MD5 {

	private static final String[] HEX_DIGITS = new String[] { "0", "1", "2",
			"3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

	/*
	 * 私有构造器，禁止被实例化
	 */
	private MD5() {

	}

	/*
	 * 对字符串MD5编码
	 */
	public static String encode(String text) {
		if (text == null) {
			throw new IllegalArgumentException("text can't be null");
		}

		try {
			// md5编码
			byte[] source = text.getBytes("UTF-8");
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(source);
			byte[] dest = md.digest();

			// 结果转为hex string
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < dest.length; ++i) {
				builder.append(HEX_DIGITS[dest[i] >>> 4 & 0x0F]);
				builder.append(HEX_DIGITS[dest[i] & 0x0F]);
			}

			return builder.toString();
		} catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void main(String[] args) {
		System.out.println(MD5.encode("test"));
	}
}
