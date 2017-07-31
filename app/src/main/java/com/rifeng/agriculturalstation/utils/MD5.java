package com.rifeng.agriculturalstation.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5加密、指定位数随机数生成
 * @author hanwen
 *
 *    2016-1-12
 *
 */
public class MD5 {

	/**
	 * MD5加密
	 * @param info   要加密的字符串
	 * @return   加密后的32位字符串
	 */
	public static String getMD5(String info){
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(info.getBytes("UTF-8"));
			byte[] encryption = md5.digest();
			
			StringBuffer buffer = new StringBuffer();
			for(int i = 0; i < encryption.length; i++){
				if(Integer.toHexString(0xff & encryption[i]).length() == 1){
					buffer.append("0").append(Integer.toHexString(0xff & encryption[i]));
				}else {
					buffer.append(Integer.toHexString(0xff & encryption[i]));
				}
			}
			return buffer.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return "";
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "";
		}
	}
	
	/**
	 * 获取指定位数随机数
	 * @return  指定位数随机数字符串
	 */
	public static String getRandom(int digits){
		int random = (int)((Math.random()) * digits);
		return random+"";
	}
}