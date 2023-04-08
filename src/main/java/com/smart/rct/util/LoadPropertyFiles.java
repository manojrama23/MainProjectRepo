package com.smart.rct.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import com.smart.rct.constants.Constants;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoadPropertyFiles {

	final static Logger logger = LoggerFactory.getLogger(LoadPropertyFiles.class);

	private static Properties properties = null;

	private static LoadPropertyFiles loadPropertyFiles = null;

	private static String configDetailsPath = null;

	private static Properties errorCodeProperties = new Properties();

	private static Properties serverProperties = new Properties();
	private static Properties applicaProperties = new Properties();

	private static Properties unAuthorizedAccessProperties = new Properties();

	private LoadPropertyFiles() {

	}

	/*
	 * public static void initbb() { FileInputStream fileInput = null;
	 * 
	 * try { if(configDetailsPath != null) { File file = new
	 * File(configDetailsPath); fileInput = new FileInputStream(file);
	 * if(properties == null) properties = new Properties();
	 * 
	 * properties.load(fileInput); } } catch(FileNotFoundException e) {
	 * logger.error(" LoadPropertyFiles ::init():: File Not Found " +
	 * ExceptionUtils.getFullStackTrace(e)); } catch(IOException e) {
	 * logger.error(" LoadPropertyFiles ::init():: " +
	 * ExceptionUtils.getFullStackTrace(e)); } finally { try { if(fileInput !=
	 * null) fileInput.close(); } catch(IOException e) {
	 * logger.error(" LoadPropertyFiles ::init():: " +
	 * ExceptionUtils.getFullStackTrace(e)); }
	 * 
	 * fileInput = null; } }
	 */

	public static void init() {
		FileInputStream fileInput = null;
		String path = System.getProperty("user.dir");
		logger.info(path);
		try {
			/*
			 * if(configDetailsPath != null) {
			 */
			File file = new File(configDetailsPath);
			fileInput = new FileInputStream(file);
			if(properties == null)
				properties = new Properties();

			properties.load(fileInput);
		/*	File file = new File(path + "/src/main/resources/application.properties");
			fileInput = new FileInputStream(file);
			if (properties == null)
				properties = new Properties();

			properties.load(fileInput);
			 } */
		} catch (FileNotFoundException e) {
			logger.error(" LoadPropertyFiles ::init():: File Not Found " + ExceptionUtils.getFullStackTrace(e));
		} catch (IOException e) {
			logger.error(" LoadPropertyFiles ::init():: " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			try {
				if (fileInput != null)
					fileInput.close();
			} catch (IOException e) {
				logger.error(" LoadPropertyFiles ::init():: " + ExceptionUtils.getFullStackTrace(e));
			}

			fileInput = null;
		}
	}

	public static LoadPropertyFiles getInstance() {
		synchronized (LoadPropertyFiles.class) {
			if (loadPropertyFiles == null) {
				loadPropertiesFiles();
				loadPropertyFiles = new LoadPropertyFiles();
				properties = getPropInstance();
				errorCodeProperties = getErrorCodeInstance();
				applicaProperties = getAppCodeInstance();
				init();
			}
		}
		return loadPropertyFiles;
	}

	private static Properties getAppCodeInstance() {
		if (applicaProperties == null) {
			applicaProperties = new Properties();
		}
		return applicaProperties;
	}

	private static void loadPropertiesFiles() {

		InputStream inputStream = null;
		InputStream errorinputStream = null;
		InputStream inputStreamToSystemProperty = null;
		InputStream userAccessStream = null;
		InputStream applicationProps = null;
		try {
			errorinputStream = LoadPropertyFiles.class.getResourceAsStream("/errorcode.properties");
			// userAccessStream =
			applicationProps = LoadPropertyFiles.class.getResourceAsStream("/application.properties");
			String serverConfigDirPath = System.getProperty("catalina.base");
			try {

				if (serverConfigDirPath != null) {
					StringBuilder sb = new StringBuilder();
					sb.append(serverConfigDirPath).append(File.separator);
					sb.append(Constants.CONF_DIRECTORY);
					sb.append(File.separator);
					sb.append(Constants.SERVER_PROPERTY_FILE);
					inputStreamToSystemProperty = new FileInputStream(sb.toString());

				}
				errorCodeProperties.load(errorinputStream);
				applicaProperties.load(applicationProps);
				serverProperties.load(inputStreamToSystemProperty);
				// unAuthorizedAccessProperties.load(userAccessStream);

				/*
				 * if(serverProperties.containsKey("config_path"))
				 * configDetailsPath =
				 * serverProperties.getProperty("config_path");
				 */
				  if(serverProperties.containsKey("config_path"))
				  configDetailsPath =serverProperties.getProperty("config_path");
			} finally {
				if (inputStreamToSystemProperty != null)
					inputStreamToSystemProperty.close();
				if (errorinputStream != null)
					errorinputStream.close();
				if (userAccessStream != null)
					userAccessStream.close();
			}
		} catch (Exception e) {
			logger.error(" Failed to load the property file: " + ExceptionUtils.getStackTrace(e));
		} finally {
			try {
				if (inputStream != null)
					inputStream.close();
			} catch (IOException e) {
				logger.error(" Failed to close the resource: " + ExceptionUtils.getStackTrace(e));
			}

			inputStream = null;
		}
	}

	public static Properties getPropInstance() {
		if (properties == null) {
			properties = new Properties();
		}
		return properties;
	}

	public static Properties getErrorCodeInstance() {
		if (errorCodeProperties == null) {
			errorCodeProperties = new Properties();
		}
		return errorCodeProperties;
	}

	public String getProperty(String key) {
		String retVal = null;

		if (key == null)
			return null;

		if (properties != null)
			retVal = properties.getProperty(key);

		return retVal == null ? null : retVal.trim();
	}

	public String getErrorCodeProperty(String key) {
		String retVal = null;

		if (key == null)
			return null;

		if (errorCodeProperties != null)
			retVal = errorCodeProperties.getProperty(key);

		if (retVal != null)
			return retVal.trim();
		else
			return null;
	}

	public String getAppCodeProperty(String key) {
		String retVal = null;

		if (key == null)
			return null;

		if (applicaProperties != null)
			// applicaProperties = new Properties();
			// loadPropertiesFiles();
			retVal = applicaProperties.getProperty(key);

		if (retVal != null)
			return retVal.trim();
		else
			return null;
	}

	public void setAppCodeProperty(String key, String value) throws ConfigurationException {
		String path = System.getProperty("user.dir");
		PropertiesConfiguration config = new PropertiesConfiguration(
				path + "/src/main/resources/application.properties");
		config.setProperty(key, value);
		config.save();
		logger.debug("Config Property Successfully Updated.. for key  " + key + " value : " + value);
		// applicaProperties.setProperty(key,value);

	}

	public void setConfigProperties(String key, String value) {
		try {
			if (configDetailsPath != null) {
				PropertiesConfiguration config = new PropertiesConfiguration(configDetailsPath);
				config.setProperty(key, value);
				config.save();
				logger.debug("Config Property Successfully Updated.. for key  " + key + " value : " + value);
			}
		} catch (Exception e) {
			logger.error("Failed to Update Config Property for key  " + key + " value : " + value);
		}

	}

	public Properties getUnAuthorizedAccessPropInstance() {
		if (unAuthorizedAccessProperties == null) {
			unAuthorizedAccessProperties = new Properties();
		}
		return unAuthorizedAccessProperties;
	}

}
