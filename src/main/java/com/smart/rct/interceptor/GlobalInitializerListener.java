package com.smart.rct.interceptor;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class GlobalInitializerListener implements ApplicationListener<ContextRefreshedEvent> {
	final static Logger logger = LoggerFactory.getLogger(GlobalInitializerListener.class);

	public static HashMap<String, String> faultCodeMap = new HashMap<String, String>();
	public static List<String> tailNumberList = new ArrayList<String>();
	public static HashMap<String, List<String>> unAuthorizedAccessUrlMap = new HashMap<String, List<String>>();

	@Value("${sessionTimeOut}")
	public String sessionTime;

	public static long MAX_INACTIVE_SESSION_TIMEOUT = 3600000; // Default
																// value.. 1
																// Hour = 60 min
																// * 60 sec *
																// 1000
																// Milliseconds
	String msg = "------====== SETTING SESSION TIMEOUT TO A DEFAULT VALUE. PLEASE SET A VALID TIMEOUT VALUE. =====------";

	BufferedReader bufferedReader = null;

	/**
	 * This method will load fault codes
	 * 
	 * @param arg0
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws Exception
	 */
	@Override
	public void onApplicationEvent(ContextRefreshedEvent arg0) {

		if (arg0.getApplicationContext().getParent() == null) {
			logger.info("..................ApplicationListener Initialization of Fault codes Started ............");
			String filePath = "/errorcode.properties";
			String line = null;

			try {
				/*
				 * if(System.getProperty("jboss.server.config.dir") != null) {
				 */
				logger.info(
						"..................ApplicationListener Initialization of RCT properties Started ............");
				// LoadPropertyFiles.getInstance();
				// String sessionTimeout =
				// LoadPropertyFiles.getInstance().getProperty("SESSION_TIMEOUT");

				try {
					int intVal = Integer.parseInt(sessionTime);

					if (intVal > 0) {
						MAX_INACTIVE_SESSION_TIMEOUT = intVal * 60000;
					} else {
						logger.warn(msg);
					}
				} catch (NumberFormatException e) {
					logger.error("Failed to Load Configured Session Timeout::: " + e);
					logger.warn(msg);
					/*
					 * if(sessionTimeout != null &&
					 * sessionTimeout.matches("[+-]?\\d*\\.?\\d+")) { double
					 * doubleVal = Double.parseDouble(sessionTimeout);
					 * 
					 * if(doubleVal > 0) { MAX_INACTIVE_SESSION_TIMEOUT = (long)
					 * (doubleVal * 60000); } else { logger.warn(msg); }
					 * 
					 * }
					 */
					/*
					 * else { logger.
					 * error("Failed to Load Configured Session Timeout::: " +
					 * e); logger.warn(msg); }
					 */
				}

				/*
				 * else { logger.
				 * info("..................Do not load RCT properties for test cases ............"
				 * ); }
				 */
				Resource resource = new ClassPathResource(filePath);
				InputStream resourceInputStream = resource.getInputStream();
				bufferedReader = new BufferedReader(new InputStreamReader(resourceInputStream));
				while ((line = bufferedReader.readLine()) != null) {
					if (!line.trim().isEmpty() && !line.trim().startsWith("#") && !line.equals("")) {
						String[] errorFaultCodes = line.split("=");
						faultCodeMap.put(errorFaultCodes[0].trim(), errorFaultCodes[1].trim());
					}
				}
				/*
				 * // User Access Map Properties userAccessProperties =
				 * LoadPropertyFiles.getInstance().
				 * getUnAuthorizedAccessPropInstance(); if(userAccessProperties
				 * != null) { List<String> urlList = new ArrayList<>();
				 * for(String key : userAccessProperties.stringPropertyNames())
				 * { String userUrls = userAccessProperties.getProperty(key);
				 * urlList = getUrlList(userUrls);
				 * unAuthorizedAccessUrlMap.put(key, urlList); } }
				 */

			} catch (FileNotFoundException e) {
				logger.error("Failed to load the error.properties file", e);
			} catch (IOException e) {
				logger.error("Failed to load fault codes ", e);
			} catch (Exception e) {
				logger.error("Exception in onApplicationEvent()  GlobalInitializerListener:: ", e);
			}
			logger.info("..................ApplicationListener Initialization of Fault codes End............");
		}
	}

	/*
	 * private List<String> getUrlList(String userUrls) { List<String> urlList =
	 * new ArrayList<>(); try { String[] Listurls = userUrls.split(",");
	 * for(String strTemp : Listurls) { urlList.add(strTemp.trim()); } }
	 * catch(Exception e) { logger.error("Exception in getUrlList" +
	 * ExceptionUtils.getFullStackTrace(e)); } return urlList; }
	 */
}
