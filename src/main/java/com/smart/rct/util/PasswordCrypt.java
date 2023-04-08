package com.smart.rct.util;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PasswordCrypt {
	final static Logger logger = LoggerFactory.getLogger(PasswordCrypt.class);
	public static final String ServersecretKey = "Bar12345Bar12345"; // // 128
																		// bit
																		// key

	public static final String initVector = "RandomInitVector"; // 16 bytes IV

	private static SecretKeySpec secretKey = null;

	/**
	 * Instantiates a new common utility.
	 */

	public static SecretKeySpec setKey(String myKey) {
		try {
			secretKey = new SecretKeySpec(myKey.getBytes(), "AES");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return secretKey;
	}

	public static String encrypt(String strToEncrypt) {
		try {
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, setKey(ServersecretKey));
			return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
		} catch (Exception e) {
			logger.error("Error while encrypting: " + e.toString());
		}
		return null;
	}

	public static String decrypt(String strToDecrypt) {
		try {
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, setKey(ServersecretKey));
			return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt.getBytes("UTF-8"))));
		} catch (Exception e) {
			logger.error("Error while decrypting: " + e.toString());
		}
		return null;
	}

	/**
	 * decrypt password
	 * 
	 * @param password
	 * @return
	 */
	public static String decryptPasswordUI(String password) {
		// decryption logic for password come here
		try {
			byte[] decoded = org.apache.tomcat.util.codec.binary.Base64.decodeBase64(password.getBytes());
			password = new String(decoded, "UTF-8");
		} catch (Exception e) {
			logger.error("Exception : decryptPassword " + ExceptionUtils.getFullStackTrace(e));
		}
		return password;
	}

	public static String encryptPasswordUI(String password) {
		// decryption logic for password come here
		try {
			byte[] dencoded = org.apache.tomcat.util.codec.binary.Base64.encodeBase64(password.getBytes());
			password = new String(dencoded, "UTF-8");
		} catch (Exception e) {
			logger.error("Exception : encryptPassword " + ExceptionUtils.getFullStackTrace(e));
		}
		return password;
	}

}
