package com.smart.common.util;

import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;
/**
 * MD5加密工具
 * 爪哇笔记：https://blog.52itstyle.vip
 */
public class MD5Utils {

	private static final String SALT = "123qwedcxzaq";

	private static final String ALGORITHM_NAME = "md5";

	private static final int HASH_ITERATIONS = 2;

	/**
	 * 使用md5生成加密后的密码
	 * @return
	 */
	public static String encrypt(String password) {
		String newPassword = new SimpleHash(ALGORITHM_NAME, password, ByteSource.Util.bytes(SALT), HASH_ITERATIONS).toHex();
		return newPassword;
	}

	/**
	 * 使用md5生成加密后的密码
	 * @return
	 */
	public static String encrypt(String username, String password) {
		String newPassword = new SimpleHash(ALGORITHM_NAME, password, ByteSource.Util.bytes(username + SALT), HASH_ITERATIONS).toHex();
		return newPassword;
	}

	public static void main(String[] args) {

	    System.out.println(encrypt("admin","admin"));
	}
}
