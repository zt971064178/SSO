package cn.itcast.sso.server.service;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;

/**
 * ClassName:CaptchaPreLoginHandler
 * Function: 图形验证码登录预处理，用于生成验证码
 * @author   zhangtian
 * @Date	 2016	2016年2月23日		下午2:58:30
 */
public class CaptchaPreLoginHandler implements IPreLoginHandler {

	private final static String CODES = "0123456789";
	private final static int LEN = 4;

	@Override
	public Map<String, Object> handle(HttpSession session) throws Exception {
		Map<String, Object> ret = new HashMap<String, Object>();

		// 生成位随机数
		String code = randomCode();
		// 将验证码的值放入session中
		session.setAttribute(SESSION_ATTR_NAME, code);
		// Base64处理图片之后不必将图片生成在硬盘上，直接从内存读取
		ret.put("imgData", "data:image/png;base64,"
				+ Base64.encodeBase64String(generateImg(code)));
		return ret;
	}

	/*
	 * 4位随机数字字符串
	 *
	 * @return
	 */
	private String randomCode() {
		Random random = new Random();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < LEN; ++i) {
			sb.append(CODES.charAt(random.nextInt(CODES.length())));
		}
		return sb.toString();
	}

	/*
	 * 绘制PNG图片
	 *
	 * @return
	 */
	private byte[] generateImg(String code) throws IOException {

		final int width = 75;
		final int height = 30;

		BufferedImage bimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bimg.createGraphics();

		// 背景
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, width, height);

		g.setColor(Color.GRAY);
		g.setFont(new Font("黑体", Font.BOLD, 25));

		// 干扰线
		Random random = new Random();
		for (int i = 0; i < 10; ++i) {
			int x1 = random.nextInt(width);
			int y1 = random.nextInt(height);
			int x2 = random.nextInt(width);
			int y2 = random.nextInt(height);

			g.drawLine(x1, y1, x2, y2);
		}

		for (int i = 0; i < LEN; ++i) {
			g.drawString(String.valueOf(code.charAt(i)), 5 + 16 * i, 25);
		}

		g.dispose();

		// 输出
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(bimg, "png", baos);
		baos.close();

		return baos.toByteArray();
	}
}
