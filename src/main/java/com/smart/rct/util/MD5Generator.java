package com.smart.rct.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MD5Generator {
	static final Logger logger = LoggerFactory.getLogger(MD5Generator.class);

	/**
	 * password encryption based on the date
	 * 
	 * @param password
	 * @param userDate
	 * @return
	 */
	public String md5(String password, Date userDate) {
		String md5 = null;
		if (null == password) {
			return null;
		}
		try {
			password = addSaltToPassword(password, getSalt(userDate));
			byte[] bytesOfMessage = password.getBytes("UTF-8");
			// Create MessageDigest object for MD5
			MessageDigest digest = MessageDigest.getInstance("MD5");
			// Update input string in message digest
			digest.update(bytesOfMessage, 0, password.length());
			// Converts message digest value in base 16 (hex)
			md5 = new BigInteger(1, digest.digest()).toString(16);
		} catch (NoSuchAlgorithmException e) {
			logger.error("Exception md5 : " + ExceptionUtils.getFullStackTrace(e));
		} catch (Exception e) {
			logger.error("Exception md5 : " + ExceptionUtils.getFullStackTrace(e));
		}
		return md5;
	}

	/**
	 * encrypt the password
	 * 
	 * @param password
	 * @return
	 */
	public String encryptPassword(String password) {
		// encryption logic for password come here
		return password;
	}

	/**
	 * decrypt password
	 * 
	 * @param password
	 * @return
	 */
	public String decryptPassword(String password) {
		// decryption logic for password come here
		try {
			byte[] decoded = Base64.decodeBase64(password.getBytes());
			password = new String(decoded, "UTF-8");
		} catch (Exception e) {
			logger.error("Exception : decryptPassword " + ExceptionUtils.getFullStackTrace(e));
		}
		return password;
	}

	private String addSaltToPassword(String password, String salt) {
		StringBuilder sb = new StringBuilder();
		try {
			return sb.append(password).append(salt).toString();
		} catch (Exception e) {
			logger.error("Exception addSaltToPassword : " + ExceptionUtils.getFullStackTrace(e));
		}
		return null;
	}

	private String getSalt(Date userDate) {
		String salt = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("HHmmssyyyyMMdd");
			return sdf.format(userDate);
		} catch (Exception e) {
			logger.error("Exception addSaltToPassword : " + ExceptionUtils.getFullStackTrace(e));
		}
		return salt;
	}
}
