package com.smart.rct.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import com.fasterxml.jackson.dataformat.csv.*;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.dataformat.csv.*;
import java.io.InputStream;
import java.util.ArrayList;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import java.util.stream.Stream;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.smart.rct.common.entity.AuditTrailEntity;
import com.smart.rct.common.entity.GrowConstantsEntity;
import com.smart.rct.common.entity.NetworkConfigDetailsEntity;
import com.smart.rct.common.entity.NetworkConfigEntity;
import com.smart.rct.common.entity.ProgramTemplateEntity;
import com.smart.rct.common.models.CiqMapValuesModel;
import com.smart.rct.common.service.AuditService;
import com.smart.rct.constants.Constants;
import com.smart.rct.constants.FaultCodes;
import com.smart.rct.constants.XmlCommandsConstants;
import com.smart.rct.exception.RctException;
import com.smart.rct.interceptor.GlobalInitializerListener;
import com.smart.rct.migration.entity.UploadFileEntity;
import com.smart.rct.premigration.controller.GenerateCsvController;
import com.smart.rct.premigration.models.CIQDetailsModel;
import com.smart.rct.premigration.service.FileUploadService;
import com.smart.rct.usermanagement.entity.UserSessionPool;
import com.smart.rct.usermanagement.models.User;

@Component
public class CommonUtil {

	final static Logger logger = LoggerFactory.getLogger(CommonUtil.class);

	@Autowired
	AuditService auditService;

	@Autowired
	MongoTemplate mongoTemplate;

	@Autowired
	FileUploadService fileUploadService;

	/**
	 * 
	 * return json object
	 * 
	 * @param jsonStringmongoTemplate
	 * @return
	 */
	public static JsonObject parseRequestDataToJson(String jsonString) {
		JsonParser parser = new JsonParser();
		return parser.parse(jsonString.trim()).getAsJsonObject();
	}

	/**
	 * 
	 * return json object
	 * 
	 * @param jsonString
	 * @return
	 * @throws ParseException
	 */
	public static JSONObject parseDataToJSON(String jsonString) throws ParseException {
		JSONParser parser = new JSONParser();
		JSONObject json = (JSONObject) parser.parse(jsonString);
		return json;
	}

	/**
	 * This method return expire data
	 * 
	 * @param sessionId
	 * @return
	 */

	public static String getSessionExpirationDetailsBack(String sessionId) {
		String expiredData = null;
		try {
			Map<String, Object> resultMap = new HashMap<String, Object>();
			User user = UserSessionPool.getInstance().getSessionUser(sessionId);
			if (sessionId == null || "".equals(sessionId) || user == null) {
				String seesion_error_code = GlobalInitializerListener.faultCodeMap.get(FaultCodes.SESSION_TIME_OUT);
				resultMap.put("dbDetails", seesion_error_code);
				resultMap.put("sessionId", seesion_error_code);
				resultMap.put("serviceToken", seesion_error_code);
				return new Gson().toJson(resultMap);
			}
		} catch (Exception e) {
			logger.error("Exception sessionExpired:" + e.getStackTrace());
		}
		return expiredData;
	}

	/**
	 * Config to json object
	 * 
	 * @param config
	 * @return
	 */
	public static String convertObjectToJson(Object config) {
		String requestBean = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			requestBean = mapper.writeValueAsString(config);
		} catch (Exception e) {
			// logger.error(" Failed to convert Object to Json ::::" +
			// ExceptionUtils.getFullStackTrace(e));
		}

		return requestBean;
	}

	/**
	 * This method is to construct the response object to UI(Success or Error
	 * Strings)
	 * 
	 * @param response
	 * @return
	 */
	public static JSONObject buildResponseJson(String status, String response, String sessionId, String serviceToken) {

		Map<String, String> mapObject = new HashMap<>();
		JSONObject jsonObject = null;
		try {
			mapObject.put("ram", status);
			mapObject.put("status", status);
			mapObject.put("reason", response);
			mapObject.put("sessionId", sessionId);
			mapObject.put("serviceToken", serviceToken);
			jsonObject = new JSONObject(mapObject);
			// result = convertObjectToJson(mapObject);
		} catch (Exception e) {
			// logger.error("File to build the Response ::::" +
			// ExceptionUtils.getFullStackTrace(e));
		}
		return jsonObject;
	}

	/**
	 * This method is to construct the response object to UI(Success or Error
	 * Strings)
	 * 
	 * @param response
	 * @return
	 */
	public static String getHomeDirectory() {
		String homeDirectory = null;
		try {
			homeDirectory = System.getProperty("user.home") + "/";
		} catch (Exception e) {
			logger.error("Exception homeDirectory() in  CommonUtil" + e.getStackTrace());
		}
		return homeDirectory;
	}

	public static boolean isValidObject(Object list) {
		boolean status = false;
		try {
			if (list != null) {
				status = true;
			}
		} catch (Exception e) {
			logger.error("Exception isValidObject() in  CommonUtil" + e.getStackTrace());
		}
		return status;
	}

	public static boolean isValidObjects(Object list) {
		boolean status = false;
		try {
			if (list != null && !"".equals(list)) {
				status = true;
			}
		} catch (Exception e) {
			logger.error("Exception isValidObject() in  CommonUtil" + e.getStackTrace());
		}
		return status;
	}

	/**
	 * This method return expire data
	 * 
	 * @param sessionId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static JSONObject getSessionExpirationDetails(String sessionId) {
		JSONObject resultMap = new JSONObject();
		try {
			User user = UserSessionPool.getInstance().getSessionUser(sessionId);
			if (sessionId == null || "".equals(sessionId) || user == null) {
				String seesion_error_code = GlobalInitializerListener.faultCodeMap.get(FaultCodes.SESSION_TIME_OUT);
				resultMap.put("dbDetails", seesion_error_code);
				resultMap.put("sessionId", seesion_error_code);
				resultMap.put("serviceToken", seesion_error_code);
				GlobalStatusMap.loginUsersDetails.remove(sessionId);
				return resultMap;
			} else {
				resultMap = null;
			}
		} catch (Exception e) {
			logger.error("Exception sessionExpired:" + e.getStackTrace());
		}
		return resultMap;
	}

	public static int getPageCount(int rowCount, int displaycount) {
		int pageCount = 0;
		try {
			if (displaycount != 0 && rowCount != 0) {
				if (rowCount % (Integer.valueOf(displaycount)) == 0) {
					pageCount = rowCount / (Integer.valueOf(displaycount));
				} else {
					pageCount = rowCount / (Integer.valueOf(displaycount)) + 1;
				}
			}
		} catch (Exception e) {
			logger.error("Exception getPageCount :" + ExceptionUtils.getFullStackTrace(e));
		}
		return pageCount;
	}

	public static String dateToString(Date date, String dateFormat) {
		String dateString = null;
		try {
			if (date != null && !"".equals(date)) {
				SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
				dateString = sdf.format(date);
			}
		} catch (Exception e) {
			logger.error("Exception stringToDate ::::" + ExceptionUtils.getFullStackTrace(e));
		}
		return dateString;
	}

	/**
	 * This method saves audit record
	 * 
	 * @param eventName,
	 *            eventSubName, actionPerformed, eventDescription, sessionId
	 * @return status
	 */
	public boolean saveAudit(String eventName, String eventSubName, String actionPerformed, String eventDescription,
			String sessionId) {
		boolean status = false;
		try {
			User user = UserSessionPool.getInstance().getSessionUser(sessionId);
			AuditTrailEntity auditTrailEntity = new AuditTrailEntity();
			auditTrailEntity.setEventName(eventName);
			auditTrailEntity.setEventSubName(eventSubName);
			auditTrailEntity.setActionPerformed(actionPerformed);
			auditTrailEntity.setEventDescription(eventDescription);
			auditTrailEntity.setUserName(user.getUserName());
			auditTrailEntity.setActionPerformedDate(new Date());
			status = auditService.savedetail(auditTrailEntity);
		} catch (Exception e) {
			logger.error("Exception saveAudit ::::" + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	public static String createMongoDbFileName(String programId, String fileName) {

		StringBuilder objBuilder = new StringBuilder();

		try {
			objBuilder.append(programId);
			objBuilder.append("_");
			objBuilder.append(fileName);

		} catch (Exception e) {
			logger.error("Exception createMongoDbFileName ::::" + ExceptionUtils.getFullStackTrace(e));
		}

		return objBuilder.toString();
	}

	public static String createMongoDbFileNameCheckList(String programId, String chekListfileName, String ciqFileName) {

		StringBuilder objBuilder = new StringBuilder();

		try {
			objBuilder.append(programId);
			objBuilder.append("_");
			objBuilder.append(chekListfileName);
			objBuilder.append("_");
			objBuilder.append(ciqFileName);

			if (objBuilder.toString().length() > 107) {
				String newCheckListForMongo = getchecklistNewName(chekListfileName);
				objBuilder.setLength(0);
				objBuilder.append(programId);
				objBuilder.append("_");
				objBuilder.append(newCheckListForMongo);
				objBuilder.append("_");
				objBuilder.append(ciqFileName);
			}

		} catch (Exception e) {
			logger.error("Exception createMongoDbFileName ::::" + ExceptionUtils.getFullStackTrace(e));
		}

		return objBuilder.toString();
	}

	public static Boolean createNewFile(String foldeName, String fileName) throws IOException {
		File f = new File(foldeName);
		f.mkdirs();
		Boolean result = null;
		File file1 = new File(foldeName + fileName); // initialize File object and passing path as argument
		file1.createNewFile();
		return result;
	}

	public static String persistFileInDb(String fileName, String folderName) throws IOException {
		createNewFile(folderName, fileName);
		return folderName + fileName;

	}

	public static String formatIPV6Address(String ipv) {
		String ip = ipv;
		String app_ip0 = "";
		String app_ip1 = "";
		String app_ip2 = "";
		String app_ip3 = "";
		String app_ip4 = "";
		String app_ip5 = "";
		String app_ip6 = "";
		String app_ip7 = "";
		String fmt_ip = ip;
		try {
			String s = ipv;
			String s1, s2, s3, s4, s5, s6, s7, s8;
			s1 = s.substring(0, s.indexOf(":"));
			s = s.substring(s.indexOf(":") + 1);

			s2 = s.substring(0, s.indexOf(":"));
			s = s.substring(s.indexOf(":") + 1);

			s3 = s.substring(0, s.indexOf(":"));
			s = s.substring(s.indexOf(":") + 1);

			s4 = s.substring(0, s.indexOf(":"));
			s = s.substring(s.indexOf(":") + 1);

			s5 = s.substring(0, s.indexOf(":"));
			s = s.substring(s.indexOf(":") + 1);
			if (s.length() > 0) {
				s6 = s.substring(0, s.indexOf(":"));
				s = s.substring(s.indexOf(":") + 1);
			} else {
				s6 = s;
			}
			if (s.length() > 0) {
				s7 = s.substring(0, s.indexOf(":"));
				s = s.substring(s.indexOf(":") + 1);
			} else {
				s7 = s;
			}
			s8 = s;

			String[] tmp_ip = { s1, s2, s3, s4, s5, s6, s7, s8 };
			String ip1 = ip.trim();

			if (ip1.length() < 39) {
				// tmp_ip = ip1.split(":");

				if ((tmp_ip[3].length()) < 4) {
					if ((tmp_ip[3].length()) == 3) {
						app_ip3 = "0" + tmp_ip[3];
					}
					if ((tmp_ip[3].length()) == 2) {
						app_ip3 = "00" + tmp_ip[3];
					}
					if ((tmp_ip[3].length()) == 1) {
						app_ip3 = "000" + tmp_ip[3];
					}
					if ((tmp_ip[3].length()) == 0) {
						app_ip3 = "0000";
					}
				} else {
					app_ip3 = tmp_ip[3];
				}

				if ((tmp_ip[2].length()) < 4) {
					if ((tmp_ip[2].length()) == 3) {
						app_ip2 = "0" + tmp_ip[2];
					}

					if ((tmp_ip[2].length()) == 2) {
						app_ip2 = "00" + tmp_ip[2];
					}

					if ((tmp_ip[2].length()) == 1) {
						app_ip2 = "000" + tmp_ip[2];
					}

					if ((tmp_ip[2].length()) == 0) {
						app_ip2 = "0000";
					}
				} else {
					app_ip2 = tmp_ip[2];
				}

				if ((tmp_ip[1].length()) < 4) {
					if ((tmp_ip[1].length()) == 3) {
						app_ip1 = "0" + tmp_ip[1];
					}

					if ((tmp_ip[1].length()) == 2) {
						app_ip1 = "00" + tmp_ip[1];
					}

					if ((tmp_ip[1].length()) == 1) {
						app_ip1 = "000" + tmp_ip[1];
					}

					if ((tmp_ip[1].length()) == 0) {
						app_ip1 = "0000";
					}
				} else {
					app_ip1 = tmp_ip[1];
				}

				if ((tmp_ip[0].length()) < 4) {
					if ((tmp_ip[0].length()) == 3) {
						app_ip0 = "0" + tmp_ip[0];
					}

					if ((tmp_ip[0].length()) == 2) {
						app_ip0 = "00" + tmp_ip[0];
					}

					if ((tmp_ip[0].length()) == 1) {
						app_ip0 = "000" + tmp_ip[0];
					}

					if ((tmp_ip[0].length()) == 0) {
						app_ip0 = "0000";
					}
				} else {
					app_ip0 = tmp_ip[0];
				}

				if ((tmp_ip[4].length()) < 4) {
					if ((tmp_ip[4].length()) == 3) {
						app_ip4 = "0" + tmp_ip[4];
					}
					if ((tmp_ip[4].length()) == 2) {
						app_ip4 = "00" + tmp_ip[4];
					}
					if ((tmp_ip[4].length()) == 1) {
						app_ip4 = "000" + tmp_ip[4];
					}
					if ((tmp_ip[4].length()) == 0) {
						app_ip4 = "0000";
					}
				} else {
					app_ip4 = tmp_ip[4];
				}

				if ((tmp_ip[5].length()) < 4) {
					if ((tmp_ip[5].length()) == 3) {
						app_ip5 = "0" + tmp_ip[5];
					}
					if ((tmp_ip[5].length()) == 2) {
						app_ip5 = "00" + tmp_ip[5];
					}
					if ((tmp_ip[5].length()) == 1) {
						app_ip5 = "000" + tmp_ip[5];
					}
					if ((tmp_ip[5].length()) == 0) {
						app_ip5 = "0000";
					}
				} else {
					app_ip5 = tmp_ip[5];
				}

				if ((tmp_ip[6].length()) < 4) {
					if ((tmp_ip[6].length()) == 3) {
						app_ip6 = "0" + tmp_ip[6];
					}
					if ((tmp_ip[6].length()) == 2) {
						app_ip6 = "00" + tmp_ip[6];
					}
					if ((tmp_ip[6].length()) == 1) {
						app_ip6 = "000" + tmp_ip[6];
					}
					if ((tmp_ip[6].length()) == 0) {
						app_ip6 = "0000";
					}
				} else {
					app_ip6 = tmp_ip[6];
				}

				if ((tmp_ip[7].length()) < 4) {
					if ((tmp_ip[7].length()) == 3) {
						app_ip7 = "0" + tmp_ip[7];
					}
					if ((tmp_ip[7].length()) == 2) {
						app_ip7 = "00" + tmp_ip[7];
					}
					if ((tmp_ip[7].length()) == 1) {
						app_ip7 = "000" + tmp_ip[7];
					}
					if ((tmp_ip[7].length()) == 0) {
						app_ip7 = "0000";
					}
				} else {
					app_ip7 = tmp_ip[7];
				}

				StringBuilder str = new StringBuilder();

				str.append(app_ip0);
				str.append(":");
				str.append(app_ip1);
				str.append(":");
				str.append(app_ip2);
				str.append(":");
				str.append(app_ip3);
				str.append(":");
				str.append(app_ip4);
				str.append(":");
				str.append(app_ip5);
				str.append(":");
				str.append(app_ip6);
				str.append(":");
				str.append(app_ip7);

				fmt_ip = str.toString();
			} else {
				fmt_ip = ip;
			}
			logger.info("formatIPV6Address() format ip:" + fmt_ip);
		} catch (Exception e) {
			logger.error("Exception formatIPV6Address ::::" + ExceptionUtils.getFullStackTrace(e));
			fmt_ip = ip;
		}
		return fmt_ip;

	}

	public static boolean createZipFile(String zipFilePathBuilder, String filePath) {
		boolean status = false;
		FileOutputStream fos = null;
		ZipOutputStream zos = null;
		FileInputStream fis = null;
		try {
			byte[] buffer = new byte[1024];
			fos = new FileOutputStream(zipFilePathBuilder.toString());
			zos = new ZipOutputStream(fos);
			File dataFile = new File(filePath.toString());
			fis = new FileInputStream(dataFile);
			zos.putNextEntry(new ZipEntry(dataFile.getName()));
			int length;
			while ((length = fis.read(buffer)) > 0) {
				zos.write(buffer, 0, length);
			}
			if (dataFile.exists()) {
				dataFile.delete();
			}
			zos.closeEntry();
			status = true;
		} catch (IOException e) {
			logger.error("Failed to create zip file", e);
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
				if (zos != null) {
					zos.close();
				}
				if (fos != null) {
					fos.close();
				}

			} catch (Exception e) {
				logger.error("Failed to Finally block to zip file", e);
			}
		}
		return status;
	}

	public static boolean createZipFileOfDirectory(String destinationZipFile, String srcFilePath) {

		boolean status = false;
		try {
			byte[] buffer = new byte[1024];
			FileOutputStream fos = new FileOutputStream(destinationZipFile);
			ZipOutputStream zos = new ZipOutputStream(fos);
			File dir = new File(srcFilePath);
			File[] files = dir.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					zipDirectory(files[i], files[i].getName(), zos);
				} else {
					FileInputStream fis = new FileInputStream(files[i]);
					zos.putNextEntry(new ZipEntry(files[i].getName()));
					int length;
					while ((length = fis.read(buffer)) > 0) {
						zos.write(buffer, 0, length);

					}
					zos.closeEntry();
					fis.close();
				}
				zos.closeEntry();
			}
			zos.close();
			status = true;
		} catch (Exception e) {
			logger.error("Failed to create zip file", e);
		}
		return status;
	}

	private static void zipDirectory(File folder, String parentFolder, ZipOutputStream zos)
			throws FileNotFoundException, IOException {
		for (File file : folder.listFiles()) {
			if (file.isDirectory()) {
				zipDirectory(file, parentFolder + "/" + file.getName(), zos);
				continue;
			}
			zos.putNextEntry(new ZipEntry(parentFolder + "/" + file.getName()));
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
			long bytesRead = 0;
			byte[] bytesIn = new byte[1024];
			int read = 0;
			while ((read = bis.read(bytesIn)) != -1) {
				zos.write(bytesIn, 0, read);
				bytesRead += read;
			}
			zos.closeEntry();
		}
	}

	public static String getLeftSubStringWithLen(String input, int len) {
		String result = "";
		try {
			if (CommonUtil.isValidObject(input) && input.length() > 0 && input.length() >= len) {
				result = input.substring(0, input.length() - len);
			}
		} catch (Exception e) {
			logger.error("Failed in getLeftSubStringWithLen()", e);
		}
		return result;
	}

	public static String decimalFormtter(float num) {
		String res = String.valueOf(num);
		try {
			DecimalFormat decimalFormat = new DecimalFormat("#.#");
			float twoDigitsF = Float.valueOf(decimalFormat.format(num));
			res = String.valueOf(twoDigitsF);
		} catch (Exception e) {
			logger.error("Failed in decimalFormtter()", e);
		}
		return res;
	}

	@SuppressWarnings("unchecked")
	public static JSONObject createBashFromXml5G(String xmlFilePath, String bashFilePath, String mcmip, String eNodeID,
			String file, String enbId, Integer programId, String ciqFileName,
			List<CIQDetailsModel> listCIQDetailsModel) {

		CIQDetailsModel ciqDetailsModel = listCIQDetailsModel.get(0);
		LinkedHashMap<String, CiqMapValuesModel> objMapDetails = ciqDetailsModel.getCiqMap();
		String data = null;
		if (file.contains("RFUsecase")) {
			String fileName = StringUtils.substringBeforeLast(file, ".");
			Pattern pat = Pattern.compile("(^\\d+)[\\-\\_]+(\\d+)[\\-\\_]+(eNB|ACPF|AUPF|AU|iAU)[\\-\\_]",Pattern.CASE_INSENSITIVE);
			Matcher mat = pat.matcher(fileName);
			if(mat.find()) {
				String sp[] = fileName.split("[\\_\\-]+");
				if(sp[2].toUpperCase().equals("AU")) {
					data = "DU" + "_" + sp[1].replaceFirst("^0+(?!$)", "");
					
				} 
				else if (sp[2].toUpperCase().equals("IAU")) {
					data = "DU" + "_" + sp[1].replaceFirst("^0+(?!$)", "");
				}
				
				else {
					data = sp[2] + "_" + sp[1].replaceFirst("^0+(?!$)", "");
				}
				
			} else {
				if (!ObjectUtils.isEmpty(objMapDetails) && objMapDetails.containsKey("NE ID AU")) {
					String t = "DU_" + objMapDetails.get("NE ID AU").getHeaderValue().replaceFirst("^0+(?!$)", "");
					if (t != null)
						data = "DU_" + objMapDetails.get("NE ID AU").getHeaderValue().replaceFirst("^0+(?!$)", "");
				}
			}
		} else if (file.contains("4G5G")) {
			String sp[] = file.split("_");
			String enb = sp[2];
			data = "eNB_" + enb;
		} else if (file.contains("ENDC")) {
			String sp[] = file.split("_");
			String enb = sp[3];
			data = "eNB_" + enb;
		} else if (file.contains("A1A2Config")) {
			if (!ObjectUtils.isEmpty(objMapDetails) && objMapDetails.containsKey("NE_ID ACPF")) {
				String t = objMapDetails.get("NE_ID ACPF").getHeaderValue();
				if (t != null) {
					if (t.length() > 4) {
						String lastFourDigits = t.substring(t.length() - 4);
						String afterTrim = lastFourDigits.replaceFirst("^0+(?!$)", "");
						data = "ACPF_" + afterTrim;
					} else if (t.length() <= 4) {
						String afterTrim = t.replaceFirst("^0+(?!$)", "");
						data = "ACPF_" + afterTrim;
					}
				}
			}

		} else if (file.contains("A1A2Create")) {
			if (!ObjectUtils.isEmpty(objMapDetails) && objMapDetails.containsKey("NE_ID ACPF")) {
				String t = objMapDetails.get("NE_ID ACPF").getHeaderValue();
				if (t != null) {
					if (t.length() > 4) {
						String lastFourDigits = t.substring(t.length() - 4);
						String afterTrim = lastFourDigits.replaceFirst("^0+(?!$)", "");
						data = "ACPF_" + afterTrim;
					} else if (t.length() <= 4) {
						String afterTrim = t.replaceFirst("^0+(?!$)", "");
						data = "ACPF_" + afterTrim;
					}
				}
			}
		} else if (file.contains("AU_CSL")) {
			if (!ObjectUtils.isEmpty(objMapDetails) && objMapDetails.containsKey("NE ID AU")) {

				String t = objMapDetails.get("NE ID AU").getHeaderValue();
				if (t != null) {
					String afterTrim = t.replaceFirst("^0+(?!$)", "");
					data = "DU_" + afterTrim;
				}
			}
		} else if (file.contains("AUPF_CSL")) {
			if (!ObjectUtils.isEmpty(objMapDetails) && objMapDetails.containsKey("NE_ID_AUPF")) {
				String t = objMapDetails.get("NE_ID_AUPF").getHeaderValue();
				if (t != null) {
					if (t.length() > 4) {
						String lastFourDigits = t.substring(t.length() - 4);
						String afterTrim = lastFourDigits.replaceFirst("^0+(?!$)", "");
						data = "AUPF_" + afterTrim;
					} else if (t.length() <= 4) {
						String afterTrim = t.replaceFirst("^0+(?!$)", "");
						data = "AUPF_" + afterTrim;
					}
				}
			}
		} else if (file.contains("ACPF_CSL")) {
			if (!ObjectUtils.isEmpty(objMapDetails) && objMapDetails.containsKey("NE_ID ACPF")) {
				String t = objMapDetails.get("NE_ID ACPF").getHeaderValue();
				if (t != null) {
					if (t.length() > 4) {
						String lastFourDigits = t.substring(t.length() - 4);
						String afterTrim = lastFourDigits.replaceFirst("^0+(?!$)", "");
						data = "ACPF_" + afterTrim;
					} else if (t.length() <= 4) {
						String afterTrim = t.replaceFirst("^0+(?!$)", "");
						data = "ACPF_" + afterTrim;
					}
				}
			}
		} else if (file.contains("AU_ROUTE") || file.contains("offset") || file.contains("tilt")) {
			if (!ObjectUtils.isEmpty(objMapDetails) && objMapDetails.containsKey("NE ID AU")) {
				String t = objMapDetails.get("NE ID AU").getHeaderValue();
				if (t != null) {
					String afterTrim = t.replaceFirst("^0+(?!$)", "");
					data = "DU_" + afterTrim;
				}
			}
		} else if (file.contains("DCM")) {
			if (!ObjectUtils.isEmpty(objMapDetails) && objMapDetails.containsKey("NE ID AU")) {
				String t = objMapDetails.get("NE ID AU").getHeaderValue();
				if (t != null) {
					String afterTrim = t.replaceFirst("^0+(?!$)", "");
					data = "DU_" + afterTrim;
				}
			}

		} else if (file.contains("param-config-Houston")) {
			if (!ObjectUtils.isEmpty(objMapDetails) && objMapDetails.containsKey("NE ID AU")) {
				String t = objMapDetails.get("NE ID AU").getHeaderValue();
				if (t != null) {
					String afterTrim = t.replaceFirst("^0+(?!$)", "");
					data = "DU_" + afterTrim;
				}
			}

		} else if (file.contains("Nola")) {
			if (!ObjectUtils.isEmpty(objMapDetails) && objMapDetails.containsKey("NE ID AU")) {
				String t = objMapDetails.get("NE ID AU").getHeaderValue();
				if (t != null) {
					String afterTrim = t.replaceFirst("^0+(?!$)", "");
					data = "DU_" + afterTrim;
				}
			}
		} else if (file.contains("Sacramento")) {
			if (!ObjectUtils.isEmpty(objMapDetails) && objMapDetails.containsKey("NE ID AU")) {
				String t = objMapDetails.get("NE ID AU").getHeaderValue();
				if (t != null) {
					String afterTrim = t.replaceFirst("^0+(?!$)", "");
					data = "DU_" + afterTrim;
				}
			}
		} else if (file.contains("Pensacola")) {
			if (!ObjectUtils.isEmpty(objMapDetails) && objMapDetails.containsKey("NE ID AU")) {
				String t = objMapDetails.get("NE ID AU").getHeaderValue();
				if (t != null) {
					String afterTrim = t.replaceFirst("^0+(?!$)", "");
					data = "DU_" + afterTrim;
				}
			}
		}

		else if (file.contains("ANCHOR")) {
			String s = StringUtils.substringBefore(file, ".");
			String s1 = StringUtils.substringBeforeLast(s, "_");
			String s2 = StringUtils.substringAfterLast(s1, "_");
			data = "eNB_" + s2;
		} else if (file.contains("AU_GPScript")) {
			if (!ObjectUtils.isEmpty(objMapDetails) && objMapDetails.containsKey("NE ID AU")) {
				String t = objMapDetails.get("NE ID AU").getHeaderValue();
				if (t != null) {
					String afterTrim = t.replaceFirst("^0+(?!$)", "");
					data = "DU_" + afterTrim;
				}
			}
		}

		StringBuilder objCommand = new StringBuilder();
		JSONObject resultMap = new JSONObject();
		try {
			boolean statusOFIpv4 = isIpv4Address(mcmip);
			String xmlfileContent = getXmlFileContent(xmlFilePath);
			xmlfileContent = xmlfileContent.replaceAll("\"", "\\\\\"");
			if (file.contains("DCM") || file.contains("param-config-Houston") || file.contains("Nola")
					|| file.contains("Sacramento") || file.contains("Pensacola")) {
				Pattern pat = Pattern.compile("\\$\\{cell-num=\\d\\}");

				Matcher mat = pat.matcher(xmlfileContent);
				while (mat.find()) {
					String cellnum = mat.group().replaceAll("[^0-9]", "");
					String temp = objMapDetails.get("CC" + cellnum + " Cell Identity").getHeaderValue();
					if (!temp.equalsIgnoreCase("tbd")) {
						xmlfileContent = xmlfileContent.replaceAll("\\$\\{cell-num=" + cellnum + "\\}",
								new Integer(Integer.parseInt(temp)).toString());
					}
				}
			}

			objCommand.append("send \"");
			// objCommand.append("curl -X POST -k -g -6 -u
			// \\\"ais176\\@samsung.com:S\\@msung0ssuser\\\" -H \\'Content-type:
			// application\\/xml\\\' -d \\'");
			objCommand.append(
					"curl -X POST -k -g -6 -u \\\'ossuser:osspasswd\\\' -H \\'Content-Type: application\\/xml\\\' -d \\");
			objCommand.append("'");
			objCommand.append(xmlfileContent);
			objCommand.append("'");
			objCommand.append("\\");
			if (statusOFIpv4) {
				if (file.contains("ANCHOR") || file.contains("ENDC") || file.contains("eNB"))
					objCommand.append(" https:\\/\\/" + mcmip + ":7443\\/oss\\/netconfByNeId\\/" + data);
				else
					objCommand.append(" https:\\/\\/" + mcmip + ":7443\\/oss\\/netconf\\/" + data);

			} else {
				if (file.contains("ANCHOR") || file.contains("ENDC") || file.contains("eNB"))
					objCommand.append(" https:\\/\\/\\[" + mcmip + "\\]:7443\\/oss\\/netconfByNeId\\/" + data);
				else
					objCommand.append(" https:\\/\\/\\[" + mcmip + "\\]:7443\\/oss\\/netconf\\/" + data);
			}
			objCommand.append("\\r\"\n\n");

			resultMap = saveConfiguredFileContent(bashFilePath, objCommand.toString());
			
			String curlCommand = getCurlCommandforXML5G(xmlFilePath, mcmip, data, file, objMapDetails);
			savecurlCommand(bashFilePath, curlCommand);
			

		} catch (Exception e) {
			logger.error("Failed in curlComandForm() in CommonUtil", e);
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason",
					GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GENERATE_BASH_FILE));
		}

		return resultMap;

	}

	@SuppressWarnings("unchecked")
	public static JSONObject createBashFromXml5GDSS(String xmlFilePath, String bashFilePath, String mcmip,
			String eNodeID, String file, String enbId, Integer programId, String ciqFileName,
			List<CIQDetailsModel> listCIQDetailsModel, List<CIQDetailsModel> listCIQsheetDetailsModel,
			String filetype,String id) {

		CIQDetailsModel ciqDetailsModel = listCIQDetailsModel.get(0);
		String data = null;
		StringBuilder objCommand = new StringBuilder();
		JSONObject resultMap = new JSONObject();
		try {
			if (filetype.contains("vDU")) {
				if(ciqDetailsModel.getCiqMap().containsKey("NEID")) {
					data = "ADPF_" + id.replaceAll("^0+(?!$)", "");;
				} else {
					resultMap.put("status", Constants.FAIL);
					resultMap.put("reason", "NE_ID not found");
					return resultMap;
				}
				
			} else if (filetype.contains("eNB")) {
				if(ciqDetailsModel.getCiqMap().containsKey("4GeNB")) {
					data = "eNB_" + id.replaceAll("^0+(?!$)", "");;
				} else {
					resultMap.put("status", Constants.FAIL);
					resultMap.put("reason", "4G_Enb not found");
					return resultMap;
				}
				
			} else if (filetype.contains("ACPF")) {

//				String id = ciqDetailsModel.getCiqMap().get("gNBID").getHeaderValue();
//				if (StringUtils.isNotEmpty(id) && id.length() >= 4) {
//					id = id.trim();
//					id = id.substring(id.length() - 4);
//					id = id.replaceAll("^0+(?!$)", "");
//				}
				if(ciqDetailsModel.getCiqMap().containsKey("ACPF_ID")) {
					//String id = ciqDetailsModel.getCiqMap().get("ACPF_ID").getHeaderValue();
					id = id.trim().replaceAll("^0+(?!$)", "");
					if(id.length() > 2 && NumberUtils.isNumber(id)) {
						data = "ACPF_" + id;
					} else {
						resultMap.put("status", Constants.FAIL);
						resultMap.put("reason", "ACPF_ID data not correct");
						return resultMap;
					}
					
				} else {
					resultMap.put("status", Constants.FAIL);
					resultMap.put("reason", "ACPF_ID not found");
					return resultMap;
				}
			} else if (filetype.contains("AUPF")) {
//				String id = ciqDetailsModel.getCiqMap().get("gNBID").getHeaderValue();
//				if (StringUtils.isNotEmpty(id) && id.length() >= 4) {
//					id = id.trim();
//					id = id.substring(id.length() - 4);
//					id = id.replaceAll("^0+(?!$)", "");
//				}
				if(ciqDetailsModel.getCiqMap().containsKey("AUPF_ID")) {
					//String id = ciqDetailsModel.getCiqMap().get("AUPF_ID").getHeaderValue();
					id = id.trim().replaceAll("^0+(?!$)", "");
					if(id.length() > 2 && NumberUtils.isNumber(id)) {
						data = "AUPF_" + id;
					} else {
						resultMap.put("status", Constants.FAIL);
						resultMap.put("reason", "AUPF_ID data not correct");
						return resultMap;
					}
					
				} else {
					resultMap.put("status", Constants.FAIL);
					resultMap.put("reason", "AUPF_ID not found");
					return resultMap;
				}
				
			} else if (filetype.contains("FSU")) {
				if(listCIQsheetDetailsModel!=null && !listCIQsheetDetailsModel.isEmpty() 
						&& listCIQsheetDetailsModel.get(0).getCiqMap().containsKey("FSUID")) {
					CIQDetailsModel ciqsheetDetailsModel = listCIQsheetDetailsModel.get(0);
					data = "FSU_" + id.replaceAll("^0+(?!$)", "");
				} else {
					resultMap.put("status", Constants.FAIL);
					resultMap.put("reason", "FSU_ID not found");
					return resultMap;
				}
				
			}

			boolean statusOFIpv4 = isIpv4Address(mcmip);
			String xmlfileContent = getXmlFileContent(xmlFilePath);
			if (xmlfileContent.contains("<?xml")) {
				xmlfileContent = StringUtils.substringAfter(xmlfileContent, "?>");
			}
			xmlfileContent = xmlfileContent.replaceAll("\"", "\\\\\"");

			objCommand.append("send \"");
			// objCommand.append("curl -X POST -k -g -6 -u
			// \\\"ais176\\@samsung.com:S\\@msung0ssuser\\\" -H \\'Content-type:
			// application\\/xml\\\' -d \\'");
			objCommand.append(
					"curl -X POST -k -g -6 -u \\\'ossuser:osspasswd\\\' -H \\'Content-Type: application\\/xml\\\' -d \\");
			objCommand.append("'");
			objCommand.append(xmlfileContent);
			objCommand.append("'");
			objCommand.append("\\");
			if (statusOFIpv4) {
				if (filetype.contains("eNB")) {
					objCommand.append(" https:\\/\\/" + mcmip + ":7443\\/oss\\/netconfByNeId\\/" + data);
				} else {
					objCommand.append(" https:\\/\\/" + mcmip + ":7443\\/oss\\/netconf\\/" + data);
				}
			} else {
				if (filetype.contains("eNB")) {
					objCommand.append(" https:\\/\\/\\[" + mcmip + "\\]:7443\\/oss\\/netconfByNeId\\/" + data);
				} else {
					objCommand.append(" https:\\/\\/\\[" + mcmip + "\\]:7443\\/oss\\/netconf\\/" + data);
				}
			}

			objCommand.append("\\r\"\n\n");

			resultMap = saveConfiguredFileContent(bashFilePath, objCommand.toString());
			String curlCommand = getCurlCommandforXML5GDSS(xmlFilePath, mcmip, data);
			savecurlCommand(bashFilePath, curlCommand);

		} catch (Exception e) {
			logger.error("Failed in curlComandForm() in CommonUtil", e);
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason",
					GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GENERATE_BASH_FILE));
		}

		return resultMap;

	}

	@SuppressWarnings("unchecked")
	public static JSONObject createBashFromXml5GCBAND(String xmlFilePath, String bashFilePath, String mcmip,
			String eNodeID, String file, String enbId, Integer programId, String ciqFileName,
			List<CIQDetailsModel> listCIQDetailsModel, List<CIQDetailsModel> listCIQsheetDetailsModel,
			String filetype, String id) {

		//CIQDetailsModel ciqDetailsModel = listCIQDetailsModel.get(0);
		String data = null;
		StringBuilder objCommand = new StringBuilder();
		JSONObject resultMap = new JSONObject();
		try {
			if (filetype.contains("vDU")) {
				data = "ADPF_" + id.replaceAll("^0+(?!$)", "");;
			} else if (filetype.contains("eNB")) {
				data = "eNB_" + id.replaceAll("^0+(?!$)", "");;
				
			} else if (filetype.contains("ACPF")) {
				data = "ACPF_" + id.replaceAll("^0+(?!$)", "");;
			} else if (filetype.contains("AUPF")) {
				data = "AUPF_" + id.replaceAll("^0+(?!$)", "");;				
			}
			
			boolean statusOFIpv4 = isIpv4Address(mcmip);
			String xmlfileContent = getXmlFileContent(xmlFilePath);
			if (xmlfileContent.contains("<?xml")) {
				xmlfileContent = StringUtils.substringAfter(xmlfileContent, "?>");
			}
			xmlfileContent = xmlfileContent.replaceAll("\"", "\\\\\"");

			objCommand.append("send \"");
			// objCommand.append("curl -X POST -k -g -6 -u
			// \\\"ais176\\@samsung.com:S\\@msung0ssuser\\\" -H \\'Content-type:
			// application\\/xml\\\' -d \\'");
			objCommand.append(
					"curl -X POST -k -g -6 -u \\\'ossuser:osspasswd\\\' -H \\'Content-Type: application\\/xml\\\' -d \\");
			objCommand.append("'");
			objCommand.append(xmlfileContent);
			objCommand.append("'");
			objCommand.append("\\");
			if (statusOFIpv4) {
				if (filetype.contains("eNB")) {
					objCommand.append(" https:\\/\\/" + mcmip + ":7443\\/oss\\/netconfByNeId\\/" + data);
				} else {
					objCommand.append(" https:\\/\\/" + mcmip + ":7443\\/oss\\/netconf\\/" + data);
				}
			} else {
				if (filetype.contains("eNB")) {
					objCommand.append(" https:\\/\\/\\[" + mcmip + "\\]:7443\\/oss\\/netconfByNeId\\/" + data);
				} else {
					objCommand.append(" https:\\/\\/\\[" + mcmip + "\\]:7443\\/oss\\/netconf\\/" + data);
				}
			}

			objCommand.append("\\r\"\n\n");

			resultMap = saveConfiguredFileContent(bashFilePath, objCommand.toString());
			String curlCommand = getCurlCommandforXML5GDSS(xmlFilePath, mcmip, data);
			savecurlCommand(bashFilePath, curlCommand);

		} catch (Exception e) {
			logger.error("Failed in curlComandForm() in CommonUtil", e);
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason",
					GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GENERATE_BASH_FILE));
		}

		return resultMap;

	}
//*******************************************************fsu**************************************************//
	
	@SuppressWarnings("unchecked")
	public static JSONObject createBashFromXmlFSU(String xmlFilePath, String bashFilePath, String mcmip, String eNodeID) {
		StringBuilder objCommand = new StringBuilder();
		JSONObject resultMap = new JSONObject();
		try {
			boolean statusOFIpv4 = isIpv4Address(mcmip);
			String xmlfileContent = getXmlFileContent(xmlFilePath);
			xmlfileContent = xmlfileContent.replaceAll("\"", "\\\\\"");

			objCommand.append("send \"");
			// objCommand.append("curl -X POST -k -g -6 -u
			// \\\"ais176\\@samsung.com:S\\@msung0ssuser\\\" -H \\'Content-type:
			// application\\/xml\\\' -d \\'");
			objCommand.append(
					"curl -X POST -k -g -6 -u \\\'ossuser:osspasswd\\\' -H \\'Content-Type: application\\/xml\\\' -d \\");
			objCommand.append("'");
			objCommand.append(xmlfileContent);
			objCommand.append("'");
			objCommand.append("\\");
			if (statusOFIpv4) {
				objCommand.append(" https:\\/\\/" + mcmip + ":7443\\/oss\\/netconf\\/FSU_" + eNodeID + "");
			} else {
				objCommand.append(" https:\\/\\/\\[" + mcmip + "\\]:7443\\/oss\\/netconf\\/FSU_" + eNodeID + "");
			}
			objCommand.append("\\r\"\n\n");

			resultMap = saveConfiguredFileContent(bashFilePath, objCommand.toString());
			String curlCommand = getCurlCommandforXMLFSU(xmlFilePath, mcmip, "FSU_" + eNodeID);
			savecurlCommand(bashFilePath, curlCommand);

		} catch (Exception e) {
			logger.error("Failed in curlComandForm() in CommonUtil", e);
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason",
					GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GENERATE_BASH_FILE));
		}

		return resultMap;

	}
	
	
//*******************************************************fsu**************************************************//
	
	
	
	
	@SuppressWarnings("unchecked")
	public static JSONObject createBashFromXml(String xmlFilePath, String bashFilePath, String mcmip, String eNodeID) {
		StringBuilder objCommand = new StringBuilder();
		JSONObject resultMap = new JSONObject();
		try {
			boolean statusOFIpv4 = isIpv4Address(mcmip);
			String xmlfileContent = getXmlFileContent(xmlFilePath);
			xmlfileContent = xmlfileContent.replaceAll("\"", "\\\\\"");

			objCommand.append("send \"");
			// objCommand.append("curl -X POST -k -g -6 -u
			// \\\"ais176\\@samsung.com:S\\@msung0ssuser\\\" -H \\'Content-type:
			// application\\/xml\\\' -d \\'");
			objCommand.append(
					"curl -X POST -k -g -6 -u \\\'ossuser:osspasswd\\\' -H \\'Content-Type: application\\/xml\\\' -d \\");
			objCommand.append("'");
			objCommand.append(xmlfileContent);
			objCommand.append("'");
			objCommand.append("\\");
			if (statusOFIpv4) {
				objCommand.append(" https:\\/\\/" + mcmip + ":7443\\/oss\\/netconfByNeId\\/eNB_" + eNodeID + "");
			} else {
				objCommand.append(" https:\\/\\/\\[" + mcmip + "\\]:7443\\/oss\\/netconfByNeId\\/eNB_" + eNodeID + "");
			}
			objCommand.append("\\r\"\n\n");

			resultMap = saveConfiguredFileContent(bashFilePath, objCommand.toString());
			String curlCommand = getCurlCommandforXML(xmlFilePath, mcmip, "eNB_" + eNodeID);
			savecurlCommand(bashFilePath, curlCommand);

		} catch (Exception e) {
			logger.error("Failed in curlComandForm() in CommonUtil", e);
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason",
					GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GENERATE_BASH_FILE));
		}

		return resultMap;

	}

	public static String getXmlFileContent(String sourcePath)

	{
		BufferedReader bufferedReader = null;
		String line = null;
		StringBuilder sb = new StringBuilder();
		FileReader fileReader = null;
		try {
			fileReader = new FileReader(sourcePath);
			bufferedReader = new BufferedReader(fileReader);
			while ((line = bufferedReader.readLine()) != null) {
				if (line.trim().isEmpty()) {
					// sb.append("\n");
					continue;
				}
				sb.append(line.trim());// .append("\n");
			}
		} catch (FileNotFoundException e) {
			logger.error(" CommonUtil ::getFileContent():: ::" + ExceptionUtils.getFullStackTrace(e));
		} catch (Exception e) {
			logger.error(" CommonUtil ::getFileContent():: ::" + ExceptionUtils.getFullStackTrace(e));

		} finally {
			try {
				if (bufferedReader != null) {
					bufferedReader.close();
				}
				if (fileReader != null) {
					fileReader.close();
				}
			} catch (IOException e) {
				logger.error("Exception in Finally", e);
			}

		}
		return sb.toString().trim();

	}

	@SuppressWarnings("unused")
	public static JSONObject createBashFromCSV(String csvFilePath, String bashFilePath, String mcmip, String eNodeID,
			List<CIQDetailsModel> listCIQDetailsModel, String endbName) {
		StringBuilder objCommand = new StringBuilder();
		JSONObject resultMap = new JSONObject();
		String NE_ID_AU = null;
		if (eNodeID.length() > 6) {
			CIQDetailsModel ciqDetailsModel = listCIQDetailsModel.get(0);
			NE_ID_AU = ciqDetailsModel.getCiqMap().get("NE ID AU").getHeaderValue();
		}
		try {
			// String id = ciqDetailsModel.getCiqMap().get("gNBID").getHeaderValue();

			boolean statusOFIpv4 = isIpv4Address(mcmip);
			String csvfileContentAsJSON = null;
			if (csvFilePath.contains("AU_20B_") && csvFilePath.contains("AU_CaCell_")) {
				String enbb = eNodeID + "/";
				String check = StringUtils.substringAfter(csvFilePath, enbb);
				String checkPath = StringUtils.substringBefore(csvFilePath, enbb);

				String[] check1 = check.split(",");
				if (check1[0].contains("AU_20B_")) {
					String csvfileContentAsJSON1 = getJsonfromCSV(checkPath + eNodeID + "/" + check1[0]);
					String csvfileContentAsJSON2 = getJsonfromCSV(checkPath + eNodeID + "/" + check1[1]);
					csvfileContentAsJSON = csvfileContentAsJSON1.substring(0, csvfileContentAsJSON1.length() - 1) + ","
							+ csvfileContentAsJSON2.substring(1);
				} else {
					String csvfileContentAsJSON1 = getJsonfromCSV(checkPath + eNodeID + "/" + check1[1]);
					String csvfileContentAsJSON2 = getJsonfromCSV(checkPath + eNodeID + "/" + check1[0]);
					csvfileContentAsJSON = csvfileContentAsJSON1.substring(0, csvfileContentAsJSON1.length() - 1) + ","
							+ csvfileContentAsJSON2.substring(1);
				}

			} else if (csvFilePath.contains("AU_20A_") && csvFilePath.contains("AU_CaCell_")) {
				String enbb = eNodeID + "/";
				String check = StringUtils.substringAfter(csvFilePath, enbb);
				String checkPath = StringUtils.substringBefore(csvFilePath, enbb);

				String[] check1 = check.split(",");
				if (check1[0].contains("AU_20A_")) {
					String csvfileContentAsJSON1 = getJsonfromCSV(checkPath + eNodeID + "/" + check1[0]);
					String csvfileContentAsJSON2 = getJsonfromCSV(checkPath + eNodeID + "/" + check1[1]);
					csvfileContentAsJSON = csvfileContentAsJSON1.substring(0, csvfileContentAsJSON1.length() - 1) + ","
							+ csvfileContentAsJSON2.substring(1);
				} else {
					String csvfileContentAsJSON1 = getJsonfromCSV(checkPath + eNodeID + "/" + check1[1]);
					String csvfileContentAsJSON2 = getJsonfromCSV(checkPath + eNodeID + "/" + check1[0]);
					csvfileContentAsJSON = csvfileContentAsJSON1.substring(0, csvfileContentAsJSON1.length() - 1) + ","
							+ csvfileContentAsJSON2.substring(1);
				}

			} else if (csvFilePath.contains("AU_20C_") && csvFilePath.contains("AU_CaCell_")) {
				String enbb = eNodeID + "/";
				String check = StringUtils.substringAfter(csvFilePath, enbb);
				String checkPath = StringUtils.substringBefore(csvFilePath, enbb);

				String[] check1 = check.split(",");
				if (check1[0].contains("AU_20C_")) {
					String csvfileContentAsJSON1 = getJsonfromCSV(checkPath + eNodeID + "/" + check1[0]);
					String csvfileContentAsJSON2 = getJsonfromCSV(checkPath + eNodeID + "/" + check1[1]);
					csvfileContentAsJSON = csvfileContentAsJSON1.substring(0, csvfileContentAsJSON1.length() - 1) + ","
							+ csvfileContentAsJSON2.substring(1);
				} else {
					String csvfileContentAsJSON1 = getJsonfromCSV(checkPath + eNodeID + "/" + check1[1]);
					String csvfileContentAsJSON2 = getJsonfromCSV(checkPath + eNodeID + "/" + check1[0]);
					csvfileContentAsJSON = csvfileContentAsJSON1.substring(0, csvfileContentAsJSON1.length() - 1) + ","
							+ csvfileContentAsJSON2.substring(1);
				}

			}
			else if (csvFilePath.contains("AU_21A_") && csvFilePath.contains("AU_CaCell_")) {
				String enbb = eNodeID + "/";
				String check = StringUtils.substringAfter(csvFilePath, enbb);
				String checkPath = StringUtils.substringBefore(csvFilePath, enbb);

				String[] check1 = check.split(",");
				if (check1[0].contains("AU_21A_")) {
					String csvfileContentAsJSON1 = getJsonfromCSV(checkPath + eNodeID + "/" + check1[0]);
					String csvfileContentAsJSON2 = getJsonfromCSV(checkPath + eNodeID + "/" + check1[1]);
					csvfileContentAsJSON = csvfileContentAsJSON1.substring(0, csvfileContentAsJSON1.length() - 1) + ","
							+ csvfileContentAsJSON2.substring(1);
				} else {
					String csvfileContentAsJSON1 = getJsonfromCSV(checkPath + eNodeID + "/" + check1[1]);
					String csvfileContentAsJSON2 = getJsonfromCSV(checkPath + eNodeID + "/" + check1[0]);
					csvfileContentAsJSON = csvfileContentAsJSON1.substring(0, csvfileContentAsJSON1.length() - 1) + ","
							+ csvfileContentAsJSON2.substring(1);
				}

			}else if (csvFilePath.contains("AU_21B_") && csvFilePath.contains("AU_CaCell_")) {
				String enbb = eNodeID + "/";
				String check = StringUtils.substringAfter(csvFilePath, enbb);
				String checkPath = StringUtils.substringBefore(csvFilePath, enbb);

				String[] check1 = check.split(",");
				if (check1[0].contains("AU_21B_")) {
					String csvfileContentAsJSON1 = getJsonfromCSV(checkPath + eNodeID + "/" + check1[0]);
					String csvfileContentAsJSON2 = getJsonfromCSV(checkPath + eNodeID + "/" + check1[1]);
					csvfileContentAsJSON = csvfileContentAsJSON1.substring(0, csvfileContentAsJSON1.length() - 1) + ","
							+ csvfileContentAsJSON2.substring(1);
				} else {
					String csvfileContentAsJSON1 = getJsonfromCSV(checkPath + eNodeID + "/" + check1[1]);
					String csvfileContentAsJSON2 = getJsonfromCSV(checkPath + eNodeID + "/" + check1[0]);
					csvfileContentAsJSON = csvfileContentAsJSON1.substring(0, csvfileContentAsJSON1.length() - 1) + ","
							+ csvfileContentAsJSON2.substring(1);
				}

			} else if (csvFilePath.contains("AU_21C_") && csvFilePath.contains("AU_CaCell_")) {
				String enbb = eNodeID + "/";
				String check = StringUtils.substringAfter(csvFilePath, enbb);
				String checkPath = StringUtils.substringBefore(csvFilePath, enbb);

				String[] check1 = check.split(",");
				if (check1[0].contains("AU_21C_")) {
					String csvfileContentAsJSON1 = getJsonfromCSV(checkPath + eNodeID + "/" + check1[0]);
					String csvfileContentAsJSON2 = getJsonfromCSV(checkPath + eNodeID + "/" + check1[1]);
					csvfileContentAsJSON = csvfileContentAsJSON1.substring(0, csvfileContentAsJSON1.length() - 1) + ","
							+ csvfileContentAsJSON2.substring(1);
				} else {
					String csvfileContentAsJSON1 = getJsonfromCSV(checkPath + eNodeID + "/" + check1[1]);
					String csvfileContentAsJSON2 = getJsonfromCSV(checkPath + eNodeID + "/" + check1[0]);
					csvfileContentAsJSON = csvfileContentAsJSON1.substring(0, csvfileContentAsJSON1.length() - 1) + ","
							+ csvfileContentAsJSON2.substring(1);
				}

			}else if (csvFilePath.contains("AU_21D_") && csvFilePath.contains("AU_CaCell_")) {
				String enbb = eNodeID + "/";
				String check = StringUtils.substringAfter(csvFilePath, enbb);
				String checkPath = StringUtils.substringBefore(csvFilePath, enbb);

				String[] check1 = check.split(",");
				if (check1[0].contains("AU_21D_")) {
					String csvfileContentAsJSON1 = getJsonfromCSV(checkPath + eNodeID + "/" + check1[0]);
					String csvfileContentAsJSON2 = getJsonfromCSV(checkPath + eNodeID + "/" + check1[1]);
					csvfileContentAsJSON = csvfileContentAsJSON1.substring(0, csvfileContentAsJSON1.length() - 1) + ","
							+ csvfileContentAsJSON2.substring(1);
				} else {
					String csvfileContentAsJSON1 = getJsonfromCSV(checkPath + eNodeID + "/" + check1[1]);
					String csvfileContentAsJSON2 = getJsonfromCSV(checkPath + eNodeID + "/" + check1[0]);
					csvfileContentAsJSON = csvfileContentAsJSON1.substring(0, csvfileContentAsJSON1.length() - 1) + ","
							+ csvfileContentAsJSON2.substring(1);
				}

			} //22A
			else if (csvFilePath.contains("AU_22A_") && csvFilePath.contains("AU_CaCell_")) {
				String enbb = eNodeID + "/";
				String check = StringUtils.substringAfter(csvFilePath, enbb);
				String checkPath = StringUtils.substringBefore(csvFilePath, enbb);

				String[] check1 = check.split(",");
				if (check1[0].contains("AU_22A_")) {
					String csvfileContentAsJSON1 = getJsonfromCSV(checkPath + eNodeID + "/" + check1[0]);
					String csvfileContentAsJSON2 = getJsonfromCSV(checkPath + eNodeID + "/" + check1[1]);
					csvfileContentAsJSON = csvfileContentAsJSON1.substring(0, csvfileContentAsJSON1.length() - 1) + ","
							+ csvfileContentAsJSON2.substring(1);
				} else {
					String csvfileContentAsJSON1 = getJsonfromCSV(checkPath + eNodeID + "/" + check1[1]);
					String csvfileContentAsJSON2 = getJsonfromCSV(checkPath + eNodeID + "/" + check1[0]);
					csvfileContentAsJSON = csvfileContentAsJSON1.substring(0, csvfileContentAsJSON1.length() - 1) + ","
							+ csvfileContentAsJSON2.substring(1);
				}

			}else if (csvFilePath.contains("AU_22C_") && csvFilePath.contains("AU_CaCell_")) {
				String enbb = eNodeID + "/";
				String check = StringUtils.substringAfter(csvFilePath, enbb);
				String checkPath = StringUtils.substringBefore(csvFilePath, enbb);

				String[] check1 = check.split(",");
				if (check1[0].contains("AU_22C_")) {
					String csvfileContentAsJSON1 = getJsonfromCSV(checkPath + eNodeID + "/" + check1[0]);
					String csvfileContentAsJSON2 = getJsonfromCSV(checkPath + eNodeID + "/" + check1[1]);
					csvfileContentAsJSON = csvfileContentAsJSON1.substring(0, csvfileContentAsJSON1.length() - 1) + ","
							+ csvfileContentAsJSON2.substring(1);
				} else {
					String csvfileContentAsJSON1 = getJsonfromCSV(checkPath + eNodeID + "/" + check1[1]);
					String csvfileContentAsJSON2 = getJsonfromCSV(checkPath + eNodeID + "/" + check1[0]);
					csvfileContentAsJSON = csvfileContentAsJSON1.substring(0, csvfileContentAsJSON1.length() - 1) + ","
							+ csvfileContentAsJSON2.substring(1);
				}

			}
	
	
			else if (csvFilePath.contains("pnp_macro")) {
				String csvFilePath1 = csvFilePath.replace("pnp_macro_indoor_dist_", "GROW_ENB_");
				String csvfileContentAsJSON1 = getJsonfromCSV(csvFilePath1);
				String csvFilePath2 = csvFilePath.replace("pnp_macro_indoor_dist_", "GROW_CELL_");
				String csvfileContentAsJSON2 = getJsonfromCSV(csvFilePath2);
				csvfileContentAsJSON = csvfileContentAsJSON1.substring(0, csvfileContentAsJSON1.length() - 1) + ","
						+ csvfileContentAsJSON2.substring(1);
			} else {

				csvfileContentAsJSON = getJsonfromCSV(csvFilePath);
			}

			// csvfileContentAsJSON = csvfileContent.replaceAll("\"", "\\\\\"");

			objCommand.append("send \"");
			// objCommand.append("curl -X POST -k -g -6 -u
			// \\\"ais176\\@samsung.com:S\\@msung0ssuser\\\" -H \\'Content-type:
			// application\\/xml\\\' -d \\'");
			objCommand.append(
					"curl -X POST -k -g -6 -u \\\'ossuser:osspasswd\\\' -H \\'Content-Type: application\\/json\\\' -d \\");
			objCommand.append("'");
			objCommand.append(csvfileContentAsJSON);
			objCommand.append("'");
			objCommand.append("\\");
			if ((csvFilePath.contains("GROW_CELL") || csvFilePath.contains("AU_CaCell_"))
					&& !csvFilePath.contains("AU_20") && !csvFilePath.contains("AU_21") && !csvFilePath.contains("AU_22") ) {
				if (!csvFilePath.contains("AU_CaCell_")) {
					if (statusOFIpv4) {
						objCommand.append(" https:\\/\\/" + mcmip + ":7443\\/oss\\/ne\\/" + endbName + "\\/cells" + "");

						// oss\/ne\/eNB_71167\/cells\r"
					} else {
						objCommand.append(
								" https:\\/\\/\\[" + mcmip + "\\]:7443\\/oss\\/ne\\/" + endbName + "\\/cells" + "");
					}
				} else {
					
					String s = endbName.substring(0, endbName.indexOf("_"));
					if(s.length() < 11) {
						endbName = "0"+ endbName;
					}
					
					if (statusOFIpv4) {
						
						objCommand.append(
								" https:\\/\\/" + mcmip + ":7443\\/oss\\/ne\\/" + "GROW_" + endbName + "\\/cells" + "");

						// oss\/ne\/eNB_71167\/cells\r"
					} else {
						
						objCommand.append(" https:\\/\\/\\[" + mcmip + "\\]:7443\\/oss\\/ne\\/" + "GROW_" + endbName
								+ "\\/cells" + "");
					}
				}
			} else {
				if (statusOFIpv4) {
					objCommand.append(" https:\\/\\/" + mcmip + ":7443\\/oss\\/ne" + "");
				} else {
					objCommand.append(" https:\\/\\/\\[" + mcmip + "\\]:7443\\/oss\\/ne" + "");
				}

			}

			objCommand.append("\\r\"\n\n");
			resultMap = saveConfiguredCsvFileContent(bashFilePath, objCommand.toString());
			String curlCommand = getCurlCommandforNeGrow(csvfileContentAsJSON, mcmip, endbName, csvFilePath);
			savecurlCommand(bashFilePath, curlCommand);
			

		} catch (Exception e) {
			logger.error("Failed in curlComandForm() in CommonUtil", e);
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason",
					GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GENERATE_BASH_FILE));
		}

		return resultMap;

	}
	
	//degrow
	
	public static JSONObject createBashFromDegrow(String filePath, String csvFilePath, String mcmip, String enbId,
            String enbName, String type) {
        StringBuilder objCommand = new StringBuilder();
        JSONObject resultMap = new JSONObject();
       objCommand.append("send \"");
        objCommand.append( "curl -X DELETE -k -g -u \\\'ossuser:osspasswd\\\' -H \\'Content-Type: application\\/json\\\'");
    	objCommand.append(" https:\\/\\/\\[" + mcmip + "\\]:7443\\/oss\\/ne\\/"+type+enbId+"\\/"+"");
        objCommand.append("\"\n\n");
        //resultMap = saveConfiguredCsvFileContent(bashFilePath, objCommand.toString());
        
        savecurlCommandDeGrow(csvFilePath,objCommand.toString());
        String curlCommand = getCurlCommandforDeGrow(mcmip, enbName,enbId, csvFilePath,type);
        savecurlCommand(csvFilePath, curlCommand);
        return resultMap;
    }
	public static JSONObject createBashFromXmlForYang(String xmlFilePath, String bashFilePath, String mcmip, String enbId,String Type,String Date) {
		// TODO Auto-generated method stub
		StringBuilder objCommand = new StringBuilder();
		JSONObject resultMap = new JSONObject();
		try {
			String date2=DateUtil.dateToString(new Date(), "yyyy-MM-dd");
        	String endTime =DateUtil.dateToString(new Date(), "yyyy-MM-dd");
			objCommand.append("send \"");
			
			objCommand.append("curl -X GET -k -g -6 -u \\\'ossuser:osspasswd\\\'");
			objCommand.append(" 'https:\\/\\/\\[" + mcmip + "\\]:7443\\/oss\\/neAlarmsByNeName\\/"+Type+enbId +"?startTime="+Date+"%2000:00:00"+"&endTime="+Date+"%2022:00:00'");
			objCommand.append("\"\n\n");
			resultMap = saveConfiguredFileContent(bashFilePath, objCommand.toString());
		
			String curlCommand = getCurlCommandforCreation(xmlFilePath, mcmip,enbId,Type,Date);
			savecurlCommand(bashFilePath, curlCommand);
	

		} catch (Exception e) {
			logger.error("Failed in curlComandForm() in CommonUtil", e);
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason",
					GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GENERATE_BASH_FILE));
		}

		return null;
	}
	public static JSONObject createBashFromXmlForpackageinventory(String xmlFilePath, String bashFilePath, String mcmip, String enbId,String Type,String Date) {
		// TODO Auto-generated method stub
		StringBuilder objCommand = new StringBuilder();
		JSONObject resultMap = new JSONObject();
		try {
			boolean statusOFIpv4 = isIpv4Address(mcmip);
			String xmlfileContent="";
			if("enb_".equals(Type)) {
        	 xmlfileContent="<nc:rpc xmlns:nc=\"urn:ietf:params:xml:ns:netconf:base:1.0\"><mid:retrieve-package-inventory xmlns:mid=\"http://www.samsung.com/global/business/4GvRAN/ns/macro_indoor_dist\"/></nc:rpc>";
			}else if("DU_".equals(Type)) {
				xmlfileContent="<nc:rpc xmlns:nc=\"urn:ietf:params:xml:ns:netconf:base:1.0\"><gnbau:retrieve-package-inventory xmlns:gnbau=\"http://www.samsung.com/global/business/5GvRAN/ns/gnb-au\"/></nc:rpc>";
			}else if("FSU_".equals(Type)) {
				xmlfileContent="<nc:rpc xmlns:nc=\"urn:ietf:params:xml:ns:netconf:base:1.0\"><cfsu:retrieve-package-inventory xmlns:cfsu=\"http://www.samsung.com/global/business/5GvRAN/ns/cfsu\"/></nc:rpc>";
			}
        	 xmlfileContent = xmlfileContent.replaceAll("\"", "\\\\\"");
			objCommand.append("send \"");
			
			objCommand.append(
					"curl -X POST -k -g -6 -u \\\'ossuser:osspasswd\\\' -H \\'Content-Type: application\\/xml\\\' -d \\");
			objCommand.append("'");
			objCommand.append(xmlfileContent);
			objCommand.append("'");
			objCommand.append("\\");
			if (statusOFIpv4) {
				objCommand.append(" https:\\/\\/" + mcmip + ":7443\\/oss\\/netconfByNeId\\/" +Type +enbId );
			} else {
				objCommand.append(" https:\\/\\/\\[" + mcmip + "\\]:7443\\/oss\\/netconfByNeId\\/" +Type +enbId);
			}
			objCommand.append("\\r\"\n\n");
			resultMap = saveConfiguredFileContent(bashFilePath, objCommand.toString());
		
			String curlCommand = getCurlCommandforpackageinventory(xmlFilePath, mcmip,enbId,Type,Date);
			savecurlCommand(bashFilePath, curlCommand);
	

		} catch (Exception e) {
			logger.error("Failed in curlComandForm() in CommonUtil", e);
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason",
					GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GENERATE_BASH_FILE));
		}

		return null;
	}

	private static String getCurlCommandforDeGrow(String mcmip, String enbName,String enbId, String csvFilePath, String type) {
        StringBuilder objCommand = new StringBuilder();

        try {
        	/*objCommand.append(
					"curl -X POST -k -g -6 -u 'ossuser:osspasswd' -H 'Content-Type: application/json' -d ");*/
        objCommand.append( "curl -X DELETE -k -g -u 'ossuser:osspasswd' -H 'Content-Type: application/json'");
        objCommand.append(" https://[" + mcmip +"]:7443/oss/ne/"+type +enbId +"");
 
        } catch (Exception e) {
            logger.error("Failed in getCurlCommandforDeGrow() in CommonUtil", e);
        }

        return objCommand.toString();
    }
    private static void savecurlCommandDeGrow(String csvFilePath,String curlCommand) {
        try {
        //    String filename = FilenameUtils.removeExtension(bashFile);
            saveConfiguredFileContentDeGrow(csvFilePath, curlCommand);
        } catch (Exception e) {
            logger.error(" CommonUtil::savecurlCommand():: ::" + ExceptionUtils.getFullStackTrace(e));
        }
 
    
    }
 
    private static JSONObject saveConfiguredFileContentDeGrow(String csvFilePath,String curlCommand) {
        JSONObject resultMap = new JSONObject();
        String sourceFilePath = csvFilePath.replace("\"", "").trim();
        try {
            File newFile = new File(sourceFilePath);
            FileUtils.writeStringToFile(newFile, curlCommand);
            resultMap.put("status", Constants.SUCCESS);
            resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.SUCCESSFULLY_CREATED_FILE));
        } catch (Exception e) {
            logger.error(" CommonUtil::saveConfiguredFileContent():: ::" + ExceptionUtils.getFullStackTrace(e));
        }
 
        return resultMap;

    }
    
    
    
    //end for degrow
	

	public static String getCSVFileContent(String sourcePath)

	{
		String JSONObject = null;
		if (sourcePath.contains("GROW_ENB")) {
			GsonBuilder gsonMapBuilder = new GsonBuilder();
			Gson gsonObject = gsonMapBuilder.create();
			BufferedReader fileReader = null;
			CSVParser csvParser = null;
			try {
				fileReader = new BufferedReader(new FileReader(sourcePath));

				csvParser = new CSVParser(fileReader,
						CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());
				Iterable<CSVRecord> csvRecords = csvParser.getRecords();

				// Map<String, List<Map<String, String>>> csvToMap = new LinkedHashMap<>();
				Map<String, Map<String, List<String>>> csvToMapForMultiple = new LinkedHashMap<>();
				Map<String, Map<String, String>> csvToMapForSingle = new LinkedHashMap<>();
				String JSONData6 = null;
				String prevHeader = null;
				String currentHeader = null;
				String firstHeader = "@ENB";
				long rowNum = 0;
				long newRowNum = 0;
				int size1 = 0;
				String neVersion = null;
				List<List<String>> multiRow = new LinkedList<>();
				for (CSVRecord csvRecord : csvRecords) {
					List<String> singleRow = new LinkedList<>();
					List<String> singleRow1 = new LinkedList<>();
					List<String> singleRow2 = new LinkedList<>();
					List<String> singleRow3 = new LinkedList<>();
					List<String> singleRow4 = new LinkedList<>();
					List<String> singleRow5 = new LinkedList<>();
					List<String> singleRow6 = new LinkedList<>();
					List<String> singleRow7 = new LinkedList<>();
					List<String> singleRow8 = new LinkedList<>();
					List<String> singleRow9 = new LinkedList<>();
					List<String> singleRow10 = new LinkedList<>();
					List<String> singleRow11 = new LinkedList<>();
					List<String> singleRow12 = new LinkedList<>();
					List<String> singleRow13 = new LinkedList<>();
					Map<String, List<String>> map12 = new LinkedHashMap<>();
					// List<Map<String, String>> list = new LinkedList<>();
					Map<String, String> map1 = new LinkedHashMap<>();

					long recordNumber = csvRecord.getRecordNumber();

					if (csvRecord.get(0).toString().contains("@") || csvRecord.getRecordNumber() == 1) {
						rowNum = csvRecord.getRecordNumber();
						if (rowNum <= 4 || rowNum >= 56) {
							newRowNum = rowNum;
						} else {
							newRowNum = rowNum + 1;
						}
						if (csvRecord.getRecordNumber() == 1) {
							prevHeader = firstHeader;
							currentHeader = "@ENB";

						} else {
							prevHeader = currentHeader;
							currentHeader = csvRecord.get(0).toString();
						}
					}
					if (currentHeader.equalsIgnoreCase("@ENB") && csvRecord.getRecordNumber() > newRowNum) {
						String header1 = "@ENB";
						map1.put("NE ID", csvRecord.get(0));
						map1.put("NE Type", csvRecord.get(1));
						map1.put("NE Version", csvRecord.get(2));
						neVersion = csvRecord.get(2).toString();
						map1.put("Release Version", csvRecord.get(3));
						map1.put("Network", csvRecord.get(4));
						map1.put("NE Name", csvRecord.get(5));
						if (neVersion.equalsIgnoreCase("20.C.0")) {
							map1.put("GPL Version", csvRecord.get(6));
							map1.put("Customer NE Type", csvRecord.get(7));
							map1.put("Rack ID", csvRecord.get(8));
							map1.put("Local Time Offset", csvRecord.get(9));
							map1.put("CBRS Mode", csvRecord.get(10));
							map1.put("CBRS User ID", csvRecord.get(11));
							map1.put("CBRS Measure Unit", csvRecord.get(12));
							map1.put("FW Auto Fusing", csvRecord.get(13));

						} else {
							map1.put("Customer NE Type", csvRecord.get(6));
							map1.put("Rack ID", csvRecord.get(7));
							map1.put("Time Offset", csvRecord.get(8));
							map1.put("CBRS Mode", csvRecord.get(9));
							map1.put("CBRS User ID", csvRecord.get(10));
							map1.put("CBRS Measure Unit", csvRecord.get(11));
						}
						csvToMapForSingle.put(header1, map1);
						JSONData6 = gsonObject.toJson(csvToMapForSingle);
						JSONData6 = JSONData6.substring(1, JSONData6.length() - 1);
					}

					else if (currentHeader.equalsIgnoreCase("@SERVER_INFORMATION")
							&& csvRecord.getRecordNumber() > newRowNum) {

						String header1 = "@SERVER_INFORMATION";
						map1.put("NE ID", "");
						map1.put("CFM", "");
						map1.put("PSM", "");
						map1.put("CDP", "");
						csvToMapForSingle.clear();
						csvToMapForSingle.put(header1, map1);
						JSONData6 = JSONData6 + "," + gsonObject.toJson(csvToMapForSingle).substring(1,
								gsonObject.toJson(csvToMapForSingle).length() - 1);

					}

					else if (currentHeader.equalsIgnoreCase("@SON_INFORMATION")
							&& csvRecord.getRecordNumber() > newRowNum) {
						String header1 = "@SON_INFORMATION";
						map1.put("NE ID", csvRecord.get(0));
						map1.put("Initial PCI", csvRecord.get(1));
						map1.put("Initial RSI", csvRecord.get(2));
						map1.put("Initial Intra-LTE NRT", csvRecord.get(3));
						map1.put("Initial Inter-RAT 1XRTT NRT", csvRecord.get(4));
						map1.put("Initial Inter-RAT HRPD NRT", csvRecord.get(5));
						map1.put("Initial SRS Nrt", csvRecord.get(6));
						map1.put("Initial SRS Pool Index", csvRecord.get(7));
						map1.put("Initial Inter-RAT NRT NR", csvRecord.get(8));
						csvToMapForSingle.clear();
						csvToMapForSingle.put(header1, map1);
						JSONData6 = JSONData6 + "," + gsonObject.toJson(csvToMapForSingle).substring(1,
								gsonObject.toJson(csvToMapForSingle).length() - 1);
					} else if (currentHeader.equalsIgnoreCase("@EXTERNAL_LINK_INFORMATION")
							&& csvRecord.getRecordNumber() > newRowNum) {
						String header1 = "@EXTERNAL_LINK_INFORMATION";
						map1.put("NE ID", csvRecord.get(0));
						map1.put("Unit Type", csvRecord.get(1));
						map1.put("Unit ID", csvRecord.get(2));
						map1.put("Port Id", csvRecord.get(3));
						map1.put("VR ID", csvRecord.get(4));
						map1.put("Admin State", csvRecord.get(5));
						map1.put("Connect Type", csvRecord.get(6));
						map1.put("UDE Type", csvRecord.get(7));

						if (neVersion.equalsIgnoreCase("20.C.0")) {
							map1.put("Speed Duplex", csvRecord.get(8));
							map1.put("MTU", csvRecord.get(9));
						} else {
							map1.put("MTU", csvRecord.get(8));
						}
						csvToMapForSingle.clear();
						csvToMapForSingle.put(header1, map1);
						JSONData6 = JSONData6 + "," + gsonObject.toJson(csvToMapForSingle).substring(1,
								gsonObject.toJson(csvToMapForSingle).length() - 1);

					} else if (currentHeader.equalsIgnoreCase("@CLOCK_SOURCE_INFORMATION")
							&& csvRecord.getRecordNumber() > newRowNum) {
						String header1 = "@CLOCK_SOURCE_INFORMATION";
						map1.put("NE ID", csvRecord.get(0));
						map1.put("ID", csvRecord.get(1));
						map1.put("Clock Source", csvRecord.get(2));
						map1.put("Priority Level", csvRecord.get(3));
						map1.put("Quality Level", csvRecord.get(4));
						csvToMapForSingle.clear();
						csvToMapForSingle.put(header1, map1);
						JSONData6 = JSONData6 + "," + gsonObject.toJson(csvToMapForSingle).substring(1,
								gsonObject.toJson(csvToMapForSingle).length() - 1);
					}

					else if (currentHeader.equalsIgnoreCase("@PTP_INFORMATION")
							&& csvRecord.getRecordNumber() > newRowNum) {
						String header1 = "@PTP_INFORMATION";
						map1.put("NE ID", csvRecord.get(0));
						map1.put("IP Version", csvRecord.get(1));
						map1.put("First Master IP", csvRecord.get(2));
						map1.put("Second Master IP", csvRecord.get(3));
						map1.put("Clock Profile", csvRecord.get(4));
						map1.put("PTP Domain", csvRecord.get(5));
						csvToMapForSingle.clear();
						csvToMapForSingle.put(header1, map1);
						JSONData6 = JSONData6 + "," + gsonObject.toJson(csvToMapForSingle).substring(1,
								gsonObject.toJson(csvToMapForSingle).length() - 1);

					} else if (currentHeader.equalsIgnoreCase("@INTER_CONNECTION_INFORMATION")
							&& csvRecord.getRecordNumber() > newRowNum) {
						String header1 = "@INTER_CONNECTION_INFORMATION";
						map1.put("NE ID", csvRecord.get(0));
						map1.put("Inter Connection Group ID", csvRecord.get(1));
						map1.put("Inter Connection Switch", csvRecord.get(2));
						map1.put("Inter Connection Node ID", csvRecord.get(3));
						csvToMapForSingle.clear();
						csvToMapForSingle.put(header1, map1);
						JSONData6 = JSONData6 + "," + gsonObject.toJson(csvToMapForSingle).substring(1,
								gsonObject.toJson(csvToMapForSingle).length() - 1);

					}

					else if (currentHeader.equalsIgnoreCase("@INTER_ENB_INFORMATION")
							&& csvRecord.getRecordNumber() > newRowNum) {
						String header1 = "@INTER_ENB_INFORMATION";
						map1.put("NE ID", csvRecord.get(0));
						map1.put("Inter Node ID", csvRecord.get(1));
						map1.put("Admin State", csvRecord.get(2));
						csvToMapForSingle.clear();
						csvToMapForSingle.put(header1, map1);
						JSONData6 = JSONData6 + "," + gsonObject.toJson(csvToMapForSingle).substring(1,
								gsonObject.toJson(csvToMapForSingle).length() - 1);

					} else if (currentHeader.equalsIgnoreCase("@SYSTEM_LOCATION_INFORMATION")
							&& csvRecord.getRecordNumber() > newRowNum) {
						String header1 = "@SYSTEM_LOCATION_INFORMATION";
						map1.put("NE ID", csvRecord.get(0));
						map1.put("User Defined Mode", csvRecord.get(1));
						map1.put("Latitude", csvRecord.get(2));
						map1.put("Longitude", csvRecord.get(3));
						map1.put("Height", csvRecord.get(4));
						csvToMapForSingle.clear();
						csvToMapForSingle.put(header1, map1);
						JSONData6 = JSONData6 + "," + gsonObject.toJson(csvToMapForSingle).substring(1,
								gsonObject.toJson(csvToMapForSingle).length() - 1);

					}

					if (currentHeader.equalsIgnoreCase("@MME_INFORMATION") && csvRecord.getRecordNumber() > newRowNum) {
						for (int i = 0; i < csvRecord.size(); i++) {
							singleRow.add(csvRecord.get(i));
							size1 = csvRecord.size();
						}

						multiRow.add(singleRow);
					} else if (prevHeader.equalsIgnoreCase("@MME_INFORMATION")
							&& csvRecord.get(0).toString().contains("@")) {
						Map<String, List<String>> map11 = new LinkedHashMap<>();
						for (int i = 0; i < multiRow.size(); i++) {

							for (int j = 0; j < size1; j++) {
								if (j == 0) {
									singleRow.add(multiRow.get(i).get(j));
								} else if (j == 1) {
									singleRow1.add(multiRow.get(i).get(j));
								} else if (j == 2) {
									singleRow2.add(multiRow.get(i).get(j));
								} else if (j == 3) {
									singleRow3.add(multiRow.get(i).get(j));
								} else if (j == 4) {
									singleRow4.add(multiRow.get(i).get(j));
								} else if (j == 5) {
									singleRow5.add(multiRow.get(i).get(j));
								} else if (j == 6) {
									singleRow6.add(multiRow.get(i).get(j));
								} else if (j == 7) {
									singleRow7.add(multiRow.get(i).get(j));
								}
							}
						}

						map12.put("NE ID", singleRow);
						map12.put("Index", singleRow1);
						map12.put("IP Type", singleRow2);
						map12.put("IP", singleRow3);
						map12.put("Service Purpose", singleRow4);
						map12.put("Attach Without PDN Connectivity", singleRow5);
						map12.put("CP Optimization", singleRow6);
						map12.put("UP Optimization", singleRow7);

						String header1 = "@MME_INFORMATION";
						csvToMapForMultiple.put(header1, map12);
						JSONData6 = JSONData6 + "," + gsonObject.toJson(csvToMapForMultiple).substring(1,
								gsonObject.toJson(csvToMapForMultiple).length() - 1);
						multiRow.clear();

					}

					if (currentHeader.equalsIgnoreCase("@MAIN_BOARD_INFORMATION")
							&& csvRecord.getRecordNumber() > newRowNum) {

						String header1 = "@MAIN_BOARD_INFORMATION";
						map1.put("NE ID", csvRecord.get(0));
						map1.put("Unit Type", csvRecord.get(1));
						map1.put("Unit ID", csvRecord.get(2));
						map1.put("Board Type", csvRecord.get(3));
						csvToMapForSingle.clear();
						csvToMapForSingle.put(header1, map1);
						JSONData6 = JSONData6 + "," + gsonObject.toJson(csvToMapForSingle).substring(1,
								gsonObject.toJson(csvToMapForSingle).length() - 1);

					}
					if (currentHeader.equalsIgnoreCase("@EXTERNAL_INTERFACE_INFORMATION")
							&& csvRecord.getRecordNumber() > newRowNum) {
						for (int i = 0; i < csvRecord.size(); i++) {
							singleRow.add(csvRecord.get(i));
							size1 = csvRecord.size();
						}

						multiRow.add(singleRow);
					} else if (prevHeader.equalsIgnoreCase("@EXTERNAL_INTERFACE_INFORMATION")
							&& csvRecord.get(0).toString().contains("@")) {
						csvToMapForMultiple.clear();
						Map<String, List<String>> map11 = new LinkedHashMap<>();
						for (int i = 0; i < multiRow.size(); i++) {

							for (int j = 0; j < size1; j++) {
								if (j == 0) {
									singleRow.add(multiRow.get(i).get(j));
								} else if (j == 1) {
									singleRow1.add(multiRow.get(i).get(j));
								} else if (j == 2) {
									singleRow2.add(multiRow.get(i).get(j));
								} else if (j == 3) {
									singleRow3.add(multiRow.get(i).get(j));
								} else if (j == 4) {
									singleRow4.add(multiRow.get(i).get(j));
								} else if (j == 5) {
									singleRow5.add(multiRow.get(i).get(j));
								} else if (j == 6) {
									singleRow6.add(multiRow.get(i).get(j));
								} else if (j == 7) {
									singleRow7.add(multiRow.get(i).get(j));
								} else if (j == 8) {
									singleRow8.add(multiRow.get(i).get(j));
								} else if (j == 9) {
									singleRow9.add(multiRow.get(i).get(j));
								} else if (j == 10) {
									singleRow10.add(multiRow.get(i).get(j));
								} else if (j == 11) {
									singleRow11.add(multiRow.get(i).get(j));
								} else if (j == 12) {
									singleRow12.add(multiRow.get(i).get(j));
								} else if (j == 13) {
									singleRow13.add(multiRow.get(i).get(j));
								}
							}
						}

						map12.put("NE ID", singleRow);
						map12.put("IF Name", singleRow1);
						map12.put("IP", singleRow2);
						map12.put("Prefix Length", singleRow3);
						map12.put("IP Get Type", singleRow4);
						map12.put("Management", singleRow5);
						map12.put("Signal S1", singleRow6);
						map12.put("Signal X2", singleRow7);
						map12.put("Bearer S1", singleRow8);
						map12.put("Bearer X2", singleRow9);
						map12.put("Bearer M1", singleRow10);
						map12.put("Signal M2", singleRow11);
						map12.put("IEEE1588", singleRow12);
						map12.put("Smart scheduler", singleRow13);
						csvToMapForMultiple.clear();
						String header1 = "@EXTERNAL_INTERFACE_INFORMATION";
						csvToMapForMultiple.put(header1, map12);
						JSONData6 = JSONData6 + "," + gsonObject.toJson(csvToMapForMultiple).substring(1,
								gsonObject.toJson(csvToMapForMultiple).length() - 1);
						multiRow.clear();
					}

					if (currentHeader.equalsIgnoreCase("@STATIC_ROUTE_INFORMATION")
							&& csvRecord.getRecordNumber() > newRowNum) {
						for (int i = 0; i < csvRecord.size(); i++) {
							singleRow.add(csvRecord.get(i));
							size1 = csvRecord.size();
						}

						multiRow.add(singleRow);
					} else if (prevHeader.equalsIgnoreCase("@STATIC_ROUTE_INFORMATION")
							&& csvRecord.get(0).toString().contains("@")) {
						Map<String, List<String>> map11 = new LinkedHashMap<>();

						for (int i = 0; i < multiRow.size(); i++) {

							for (int j = 0; j < size1; j++) {
								if (j == 0) {
									singleRow.add(multiRow.get(i).get(j));
								} else if (j == 1) {
									singleRow1.add(multiRow.get(i).get(j));
								} else if (j == 2) {
									singleRow2.add(multiRow.get(i).get(j));
								} else if (j == 3) {
									singleRow3.add(multiRow.get(i).get(j));
								} else if (j == 4) {
									singleRow4.add(multiRow.get(i).get(j));
								} else if (j == 5) {
									singleRow5.add(multiRow.get(i).get(j));
								}
							}
						}
						map12.put("NE ID", singleRow);
						map12.put("VR ID", singleRow1);
						map12.put("IP Type", singleRow2);
						map12.put("IP Prefix", singleRow3);
						map12.put("IP GW", singleRow4);
						map12.put("Route Interface Name", singleRow5);
						String header1 = "@STATIC_ROUTE_INFORMATION";

						csvToMapForMultiple.clear();
						csvToMapForMultiple.put(header1, map12);
						JSONData6 = JSONData6 + "," + gsonObject.toJson(csvToMapForMultiple).substring(1,
								gsonObject.toJson(csvToMapForMultiple).length() - 1);
						multiRow.clear();

					}

					if (currentHeader.equalsIgnoreCase("@ENB_SCHEDULAR_INFORMATION")
							&& csvRecord.getRecordNumber() > newRowNum) {

						String header1 = "@ENB_SCHEDULAR_INFORMATION";
						map1.put("NE ID", csvRecord.get(0));
						map1.put("RCC ID", csvRecord.get(1));
						map1.put("Cluster ID", csvRecord.get(2));
						map1.put("IP Version", csvRecord.get(3));
						map1.put("Scheduler IP", csvRecord.get(4));
						map1.put("Scheduler 2nd IP", csvRecord.get(5));

						csvToMapForSingle.clear();
						csvToMapForSingle.put(header1, map1);
						JSONData6 = JSONData6 + "," + gsonObject.toJson(csvToMapForSingle).substring(1,
								gsonObject.toJson(csvToMapForSingle).length() - 1);

					}
					if (currentHeader.equalsIgnoreCase("@VIRTUAL_ROUTING_INFORMATION")
							&& csvRecord.getRecordNumber() > newRowNum) {
						String header1 = "@VIRTUAL_ROUTING_INFORMATION";
						map1.put("NE ID", csvRecord.get(0));
						map1.put("VR ID", csvRecord.get(1));
						csvToMapForSingle.clear();
						csvToMapForSingle.put(header1, map1);
						JSONData6 = JSONData6 + "," + gsonObject.toJson(csvToMapForSingle).substring(1,
								gsonObject.toJson(csvToMapForSingle).length() - 1);

					}
					if (currentHeader.equalsIgnoreCase("@VLAN_INFORMATION")
							&& csvRecord.getRecordNumber() > newRowNum) {
						for (int i = 0; i < csvRecord.size(); i++) {
							singleRow.add(csvRecord.get(i));
							size1 = csvRecord.size();
						}

						multiRow.add(singleRow);
					} else if (prevHeader.equalsIgnoreCase("@VLAN_INFORMATION")
							&& csvRecord.get(0).toString().contains("@")) {
						Map<String, List<String>> map11 = new LinkedHashMap<>();

						for (int i = 0; i < multiRow.size(); i++) {

							for (int j = 0; j < size1; j++) {
								if (j == 0) {
									singleRow.add(multiRow.get(i).get(j));
								} else if (j == 1) {
									singleRow1.add(multiRow.get(i).get(j));
								} else if (j == 2) {
									singleRow2.add(multiRow.get(i).get(j));
								} else if (j == 3) {
									singleRow3.add(multiRow.get(i).get(j));
								}
							}
						}
						map12.put("NE ID", singleRow);
						map12.put("VLAN ID", singleRow1);
						map12.put("VR ID", singleRow2);
						map12.put("VLAN Interface Name", singleRow3);

						String header1 = "@VLAN_INFORMATION";

						csvToMapForMultiple.clear();
						csvToMapForMultiple.put(header1, map12);
						JSONData6 = JSONData6 + "," + gsonObject.toJson(csvToMapForMultiple).substring(1,
								gsonObject.toJson(csvToMapForMultiple).length() - 1);
						multiRow.clear();

					}

					if (currentHeader.equalsIgnoreCase("@LAG_INFORMATION") && csvRecord.getRecordNumber() > newRowNum) {

						String header1 = "@LAG_INFORMATION";
						map1.put("NE ID", csvRecord.get(0));
						map1.put("LAG ID", csvRecord.get(1));
						map1.put("VR ID", csvRecord.get(2));
						map1.put("LAG Interface Name", csvRecord.get(3));
						csvToMapForSingle.clear();
						csvToMapForSingle.put(header1, map1);
						JSONData6 = JSONData6 + "," + gsonObject.toJson(csvToMapForSingle).substring(1,
								gsonObject.toJson(csvToMapForSingle).length() - 1);

					} else if (currentHeader.equalsIgnoreCase("@IPSEC_INFORMATION")
							&& csvRecord.getRecordNumber() > newRowNum) {
						String header1 = "@IPSEC_INFORMATION";
						map1.put("NE ID", "");
						if (neVersion.equalsIgnoreCase("20.C.0")) {
							map1.put("VR ID", "");
						}
						map1.put("Interface Name1", "");
						map1.put("Peer IP Version", "");
						map1.put("First Peer IP", "");
						map1.put("Second Peer IP", "");
						map1.put("Inner IP Version", "");
						map1.put("Tunnel Mode", "");
						map1.put("Interface Name2", "");
						map1.put("Interface Name3", "");
						map1.put("Crypto Algorithm", "");
						map1.put("Hash Algorithm", "");
						map1.put("Local ID Type", "");
						map1.put("Local ID", "");
						csvToMapForSingle.clear();
						csvToMapForSingle.put(header1, map1);
						JSONData6 = JSONData6 + "," + gsonObject.toJson(csvToMapForSingle).substring(1,
								gsonObject.toJson(csvToMapForSingle).length() - 1);

					}

					else if (currentHeader.equalsIgnoreCase("@PKI_INFORMATION")
							&& csvRecord.getRecordNumber() > newRowNum) {
						String header1 = "@PKI_INFORMATION";
						map1.put("NE ID", "");
						map1.put("IP Address", "");
						map1.put("FQDN", "");
						map1.put("Port", "");
						map1.put("Path", "");
						map1.put("DN", "");
						map1.put("DN Domain", "");
						map1.put("CA DN", "");
						map1.put("Hash Algorithm", "");
						csvToMapForSingle.clear();
						csvToMapForSingle.put(header1, map1);
						JSONData6 = JSONData6 + "," + gsonObject.toJson(csvToMapForSingle).substring(1,
								gsonObject.toJson(csvToMapForSingle).length() - 1);

					}

				}

				char c1 = '"';
				String v1 = Character.toString(c1);
				String v3 = "\\\\" + Character.toString(c1);
				JSONData6 = JSONData6.replaceAll(Character.toString(c1), v3).replaceAll("]", "\\\\]").replace("[",
						"\\[");
				JSONObject = "{" + JSONData6 + "}";
				System.out.println(JSONData6);

			} catch (Exception e) {
				e.printStackTrace();
			}

		} else if (sourcePath.contains("AU_CaCell_20C") || sourcePath.contains("AU_CaCell_21A") 
				|| sourcePath.contains("AU_CaCell_21B") || sourcePath.contains("AU_CaCell_21C")) {
			try {
				int size1 = 0;
				GsonBuilder gsonMapBuilder = new GsonBuilder();
				Gson gsonObject = gsonMapBuilder.create();
				BufferedReader fileReader = null;
				CSVParser csvParser = null;
				fileReader = new BufferedReader(new FileReader(sourcePath));
				csvParser = new CSVParser(fileReader,
						CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());

				Iterable<CSVRecord> csvRecords = csvParser.getRecords();
				Map<String, List<Map<String, String>>> csvToMap = new LinkedHashMap<>();
				Map<String, Map<String, List<String>>> csvToMapForMultiple = new LinkedHashMap<>();
				Map<String, Map<String, String>> csvToMapForSingle = new LinkedHashMap<>();
				List<List<String>> multiRow = new LinkedList<>();
				// JSONObject = null;
				String prevHeader = null;
				String currentHeader = null;
				String firstHeader = "@CELL_INFORMATION";
				long rowNum = 0;
				long newRowNum = 0;

				for (CSVRecord csvRecord : csvRecords) {
					List<String> singleRow = new LinkedList<>();
					List<String> singleRow1 = new LinkedList<>();
					List<String> singleRow2 = new LinkedList<>();
					List<String> singleRow3 = new LinkedList<>();
					List<String> singleRow4 = new LinkedList<>();
					List<String> singleRow5 = new LinkedList<>();
					List<String> singleRow6 = new LinkedList<>();
					List<String> singleRow7 = new LinkedList<>();
					List<String> singleRow8 = new LinkedList<>();
					List<String> singleRow9 = new LinkedList<>();
					List<String> singleRow10 = new LinkedList<>();
					List<String> singleRow11 = new LinkedList<>();
					List<String> singleRow12 = new LinkedList<>();
					List<String> singleRow13 = new LinkedList<>();
					List<String> singleRow14 = new LinkedList<>();
					List<String> singleRow15 = new LinkedList<>();
					List<String> singleRow16 = new LinkedList<>();
					List<String> singleRow17 = new LinkedList<>();
					List<String> singleRow18 = new LinkedList<>();
					List<String> singleRow19 = new LinkedList<>();
					List<String> singleRow20 = new LinkedList<>();

					Map<String, List<String>> map12 = new LinkedHashMap<>();
					List<Map<String, String>> list = new LinkedList<>();
					Map<String, String> map1 = new LinkedHashMap<>();

					long recordNumber = csvRecord.getRecordNumber();
					if (csvRecord.get(0).toString().contains("@") || csvRecord.getRecordNumber() == 1) {
						rowNum = csvRecord.getRecordNumber();
						if (csvRecord.getRecordNumber() == 1) {
							newRowNum = rowNum;
						} else {
							newRowNum = rowNum + 1;
						}
						if (csvRecord.getRecordNumber() == 1) {
							prevHeader = firstHeader;
							currentHeader = "@CELL_INFORMATION";

						} else {
							prevHeader = currentHeader;
							currentHeader = csvRecord.get(0).toString();
						}
					}
					if (currentHeader.equalsIgnoreCase("@CELL_INFORMATION")
							&& csvRecord.getRecordNumber() > newRowNum) {
						for (int i = 0; i < csvRecord.size(); i++) {
							singleRow.add(csvRecord.get(i));
							size1 = csvRecord.size();
						}
						multiRow.add(singleRow);

					} else if (prevHeader.equalsIgnoreCase("@CELL_INFORMATION")
							&& csvRecord.get(0).toString().contains("@")) {
						for (int i = 0; i < multiRow.size(); i++) {
							for (int j = 0; j < size1; j++) {
								if (j == 0) {
									singleRow.add(multiRow.get(i).get(j));
								} else if (j == 1) {
									singleRow1.add(multiRow.get(i).get(j));
								} else if (j == 2) {
									singleRow2.add(multiRow.get(i).get(j));
								} else if (j == 3) {
									singleRow3.add(multiRow.get(i).get(j));
								} else if (j == 4) {
									singleRow4.add(multiRow.get(i).get(j));
								} else if (j == 5) {
									singleRow5.add(multiRow.get(i).get(j));
								} else if (j == 6) {
									singleRow6.add(multiRow.get(i).get(j));
								} else if (j == 7) {
									singleRow7.add(multiRow.get(i).get(j));
								} else if (j == 8) {
									singleRow8.add(multiRow.get(i).get(j));
								} else if (j == 9) {
									singleRow9.add(multiRow.get(i).get(j));
								} else if (j == 10) {
									singleRow10.add(multiRow.get(i).get(j));
								} else if (j == 11) {
									singleRow11.add(multiRow.get(i).get(j));
								} else if (j == 12) {
									singleRow12.add(multiRow.get(i).get(j));
								} else if (j == 13) {
									singleRow13.add(multiRow.get(i).get(j));
								} else if (j == 14) {
									singleRow14.add(multiRow.get(i).get(j));
								} else if (j == 15) {
									singleRow15.add(multiRow.get(i).get(j));
								} else if (j == 16) {
									singleRow16.add(multiRow.get(i).get(j));
								}else if (j == 16) {
									singleRow16.add(multiRow.get(i).get(j));
								}else if (j == 17) {
									singleRow17.add(multiRow.get(i).get(j));
								}else if (j == 18) {
									singleRow18.add(multiRow.get(i).get(j));
								}else if (j == 19) {
									singleRow19.add(multiRow.get(i).get(j));
								}else if (j == 20) {
									singleRow20.add(multiRow.get(i).get(j));
								}

							}
						}
						map12.put("State", singleRow);
						map12.put("Sector ID", singleRow1);
						map12.put("Carrier ID", singleRow2);
						map12.put("Cell Num", singleRow3);
						map12.put("NR Frequency Band", singleRow4);
						map12.put("NR DL Arfcn", singleRow5);
						map12.put("NR UL Arfcn", singleRow6);
						map12.put("NR DL Bandwidth", singleRow7);
						map12.put("NR UL Bandwidth", singleRow8);
						map12.put("NR Physical Cell ID", singleRow9);
						map12.put("DL Antenna Count", singleRow10);
						map12.put("UL Antenna Count", singleRow11);
						map12.put("Number of Rx Paths per RU", singleRow12);
						map12.put("PRACH RSI", singleRow13);
						map12.put("PRACH ZCZC", singleRow14);
						map12.put("PRACH Configuration Index", singleRow15);
						map12.put("PRACH SSB Per RO", singleRow16);
						map12.put("Number of Tx SSB", singleRow17);
						map12.put("Tracking Area Code Usage", singleRow18);
						map12.put("Tracking Area Code", singleRow19);
						map12.put("Beambook Type", singleRow20);
						
						String header1 = "@CELL_INFORMATION";
						csvToMapForMultiple.put(header1, map12);
						gsonObject.toJson(csvToMapForMultiple).substring(1,
								gsonObject.toJson(csvToMapForMultiple).length() - 1);
					}

					if (currentHeader.equalsIgnoreCase("@SON_INFORMATION") && csvRecord.getRecordNumber() > newRowNum) {
						String header1 = "@SON_INFORMATION";
						map1.put("Initial PCI", csvRecord.get(0));
						map1.put("Initial RSI", csvRecord.get(1));
						map1.put("Initial NCRT", csvRecord.get(2));
						csvToMapForSingle.put(header1, map1);
						gsonObject.toJson(csvToMapForSingle).substring(1,
								gsonObject.toJson(csvToMapForSingle).length() - 1);

					}

				}
				JSONObject = gsonObject.toJson(csvToMapForMultiple).substring(0,
						gsonObject.toJson(csvToMapForMultiple).length() - 1) + ","
						+ gsonObject.toJson(csvToMapForSingle).substring(1);
				System.out.println(JSONObject);

				char c1 = '"';
				String v1 = Character.toString(c1);
				String v3 = "\\\\" + Character.toString(c1);
				JSONObject = JSONObject.replaceAll(Character.toString(c1), v3).replaceAll("]", "\\\\]").replace("[",
						"\\[");
				System.out.println(JSONObject);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}else if (sourcePath.contains("AU_CaCell")) {
			try {
				int size1 = 0;
				GsonBuilder gsonMapBuilder = new GsonBuilder();
				Gson gsonObject = gsonMapBuilder.create();
				BufferedReader fileReader = null;
				CSVParser csvParser = null;
				fileReader = new BufferedReader(new FileReader(sourcePath));
				csvParser = new CSVParser(fileReader,
						CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());

				Iterable<CSVRecord> csvRecords = csvParser.getRecords();
				Map<String, List<Map<String, String>>> csvToMap = new LinkedHashMap<>();
				Map<String, Map<String, List<String>>> csvToMapForMultiple = new LinkedHashMap<>();
				Map<String, Map<String, String>> csvToMapForSingle = new LinkedHashMap<>();
				List<List<String>> multiRow = new LinkedList<>();
				// JSONObject = null;
				String prevHeader = null;
				String currentHeader = null;
				String firstHeader = "@CELL_INFORMATION";
				long rowNum = 0;
				long newRowNum = 0;

				for (CSVRecord csvRecord : csvRecords) {
					List<String> singleRow = new LinkedList<>();
					List<String> singleRow1 = new LinkedList<>();
					List<String> singleRow2 = new LinkedList<>();
					List<String> singleRow3 = new LinkedList<>();
					List<String> singleRow4 = new LinkedList<>();
					List<String> singleRow5 = new LinkedList<>();
					List<String> singleRow6 = new LinkedList<>();
					List<String> singleRow7 = new LinkedList<>();
					List<String> singleRow8 = new LinkedList<>();
					List<String> singleRow9 = new LinkedList<>();
					List<String> singleRow10 = new LinkedList<>();
					List<String> singleRow11 = new LinkedList<>();
					List<String> singleRow12 = new LinkedList<>();
					List<String> singleRow13 = new LinkedList<>();
					List<String> singleRow14 = new LinkedList<>();
					List<String> singleRow15 = new LinkedList<>();
					List<String> singleRow16 = new LinkedList<>();

					Map<String, List<String>> map12 = new LinkedHashMap<>();
					List<Map<String, String>> list = new LinkedList<>();
					Map<String, String> map1 = new LinkedHashMap<>();

					long recordNumber = csvRecord.getRecordNumber();
					if (csvRecord.get(0).toString().contains("@") || csvRecord.getRecordNumber() == 1) {
						rowNum = csvRecord.getRecordNumber();
						if (csvRecord.getRecordNumber() == 1) {
							newRowNum = rowNum;
						} else {
							newRowNum = rowNum + 1;
						}
						if (csvRecord.getRecordNumber() == 1) {
							prevHeader = firstHeader;
							currentHeader = "@CELL_INFORMATION";

						} else {
							prevHeader = currentHeader;
							currentHeader = csvRecord.get(0).toString();
						}
					}
					if (currentHeader.equalsIgnoreCase("@CELL_INFORMATION")
							&& csvRecord.getRecordNumber() > newRowNum) {
						for (int i = 0; i < csvRecord.size(); i++) {
							singleRow.add(csvRecord.get(i));
							size1 = csvRecord.size();
						}
						multiRow.add(singleRow);

					} else if (prevHeader.equalsIgnoreCase("@CELL_INFORMATION")
							&& csvRecord.get(0).toString().contains("@")) {
						for (int i = 0; i < multiRow.size(); i++) {
							for (int j = 0; j < size1; j++) {
								if (j == 0) {
									singleRow.add(multiRow.get(i).get(j));
								} else if (j == 1) {
									singleRow1.add(multiRow.get(i).get(j));
								} else if (j == 2) {
									singleRow2.add(multiRow.get(i).get(j));
								} else if (j == 3) {
									singleRow3.add(multiRow.get(i).get(j));
								} else if (j == 4) {
									singleRow4.add(multiRow.get(i).get(j));
								} else if (j == 5) {
									singleRow5.add(multiRow.get(i).get(j));
								} else if (j == 6) {
									singleRow6.add(multiRow.get(i).get(j));
								} else if (j == 7) {
									singleRow7.add(multiRow.get(i).get(j));
								} else if (j == 8) {
									singleRow8.add(multiRow.get(i).get(j));
								} else if (j == 9) {
									singleRow9.add(multiRow.get(i).get(j));
								} else if (j == 10) {
									singleRow10.add(multiRow.get(i).get(j));
								} else if (j == 11) {
									singleRow11.add(multiRow.get(i).get(j));
								} else if (j == 12) {
									singleRow12.add(multiRow.get(i).get(j));
								} else if (j == 13) {
									singleRow13.add(multiRow.get(i).get(j));
								} else if (j == 14) {
									singleRow14.add(multiRow.get(i).get(j));
								} else if (j == 15) {
									singleRow15.add(multiRow.get(i).get(j));
								} else if (j == 16) {
									singleRow16.add(multiRow.get(i).get(j));
								}

							}
						}
						map12.put("State", singleRow);
						map12.put("Sector ID", singleRow1);
						map12.put("Carrier ID", singleRow2);
						map12.put("Cell Num", singleRow3);
						map12.put("NR Physical Cell ID", singleRow4);
						map12.put("NR DL Arfcn", singleRow5);
						map12.put("NR UL Arfcn", singleRow6);
						map12.put("NR Bandwidth", singleRow7);
						map12.put("NR Frequency Band", singleRow8);
						map12.put("DL Antenna Count", singleRow9);
						map12.put("UL Antenna Count", singleRow10);
						map12.put("Number of Rx Paths per RU", singleRow11);
						map12.put("PRACH RSI", singleRow12);
						map12.put("PRACH ZCZC", singleRow13);
						map12.put("Tracking Area Code Usage", singleRow14);
						map12.put("Tracking Area Code", singleRow15);
						map12.put("Beambook Type", singleRow16);
						String header1 = "@CELL_INFORMATION";
						csvToMapForMultiple.put(header1, map12);
						gsonObject.toJson(csvToMapForMultiple).substring(1,
								gsonObject.toJson(csvToMapForMultiple).length() - 1);
					}

					if (currentHeader.equalsIgnoreCase("@SON_INFORMATION") && csvRecord.getRecordNumber() > newRowNum) {
						String header1 = "@SON_INFORMATION";
						map1.put("Initial PCI", csvRecord.get(0));
						map1.put("Initial RSI", csvRecord.get(1));
						map1.put("Initial NCRT", csvRecord.get(2));
						csvToMapForSingle.put(header1, map1);
						gsonObject.toJson(csvToMapForSingle).substring(1,
								gsonObject.toJson(csvToMapForSingle).length() - 1);

					}

				}
				JSONObject = gsonObject.toJson(csvToMapForMultiple).substring(0,
						gsonObject.toJson(csvToMapForMultiple).length() - 1) + ","
						+ gsonObject.toJson(csvToMapForSingle).substring(1);
				System.out.println(JSONObject);

				char c1 = '"';
				String v1 = Character.toString(c1);
				String v3 = "\\\\" + Character.toString(c1);
				JSONObject = JSONObject.replaceAll(Character.toString(c1), v3).replaceAll("]", "\\\\]").replace("[",
						"\\[");
				System.out.println(JSONObject);
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else if (sourcePath.contains("AU_20C") || sourcePath.contains("AU_21A") 
				|| sourcePath.contains("AU_21B") || sourcePath.contains("AU_21C")) {

			try {
				GsonBuilder gsonMapBuilder = new GsonBuilder();
				Gson gsonObject = gsonMapBuilder.create();
				BufferedReader fileReader = null;
				CSVParser csvParser = null;
				fileReader = new BufferedReader(new FileReader(sourcePath));

				csvParser = new CSVParser(fileReader,
						CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());
				Iterable<CSVRecord> csvRecords = csvParser.getRecords();

				// Map<String, List<Map<String, String>>> csvToMap = new LinkedHashMap<>();
				Map<String, Map<String, List<String>>> csvToMapForMultiple = new LinkedHashMap<>();
				Map<String, Map<String, String>> csvToMapForSingle = new LinkedHashMap<>();
				String JSONData6 = null;
				String prevHeader = null;
				String currentHeader = null;
				String firstHeader = "@DU";
				long rowNum = 0;
				long newRowNum = 0;
				int size1 = 0;

				List<List<String>> multiRow = new LinkedList<>();
				for (CSVRecord csvRecord : csvRecords) {
					List<String> singleRow = new LinkedList<>();
					List<String> singleRow1 = new LinkedList<>();
					List<String> singleRow2 = new LinkedList<>();
					List<String> singleRow3 = new LinkedList<>();
					List<String> singleRow4 = new LinkedList<>();
					List<String> singleRow5 = new LinkedList<>();
					List<String> singleRow6 = new LinkedList<>();
					List<String> singleRow7 = new LinkedList<>();
					List<String> singleRow8 = new LinkedList<>();
					List<String> singleRow9 = new LinkedList<>();
					
					Map<String, List<String>> map12 = new LinkedHashMap<>();
					// List<Map<String, String>> list = new LinkedList<>();
					Map<String, String> map1 = new LinkedHashMap<>();

					long recordNumber = csvRecord.getRecordNumber();
					if (csvRecord.get(0).toString().contains("@") || csvRecord.getRecordNumber() == 1) {
						rowNum = csvRecord.getRecordNumber();

						newRowNum = rowNum + 1;

						if (csvRecord.getRecordNumber() == 1) {
							prevHeader = firstHeader;
							currentHeader = "@DU";

						} else {
							prevHeader = currentHeader;
							currentHeader = csvRecord.get(0).toString();
						}
					}
					if (currentHeader.equalsIgnoreCase("@DU")) {

						String header1 = "@DU";
						map1.put("NE ID", csvRecord.get(0));
						map1.put("NE Type", csvRecord.get(1));
						map1.put("NE Version", csvRecord.get(2));
						map1.put("Release Version", csvRecord.get(3));
						map1.put("Network", csvRecord.get(4));
						map1.put("NE Name", csvRecord.get(5));
						map1.put("GPL Version", csvRecord.get(6));
						
						map1.put("AdministrativeState", csvRecord.get(7));
						map1.put("gNB ID", csvRecord.get(8));
						map1.put("gNB ID Length", csvRecord.get(9));
						map1.put("gNB DU ID", csvRecord.get(10));
						map1.put("gNB DU Name", csvRecord.get(11));
						map1.put("Endpoint CU IP address", csvRecord.get(12));
						map1.put("Local Time Offset", csvRecord.get(13));
						map1.put("NE Serial Number", csvRecord.get(14));
						map1.put("FW Auto Fusing", csvRecord.get(15));

						csvToMapForSingle.put(header1, map1);
						JSONData6 = gsonObject.toJson(csvToMapForSingle);
						JSONData6 = JSONData6.substring(1, JSONData6.length() - 1);

					} else if (currentHeader.equalsIgnoreCase("@SERVER_INFORMATION")
							&& csvRecord.getRecordNumber() > newRowNum) {
						String header1 = "@SERVER_INFORMATION";
						map1.put("NE ID", csvRecord.get(0));
						map1.put("CFM", csvRecord.get(1));
						map1.put("PSM", csvRecord.get(2));
						csvToMapForSingle.clear();
						csvToMapForSingle.put(header1, map1);
						JSONData6 = JSONData6 + "," + gsonObject.toJson(csvToMapForSingle).substring(1,
								gsonObject.toJson(csvToMapForSingle).length() - 1);

					}else if (currentHeader.equalsIgnoreCase("@MAIN_BOARD_INFORMATION")
							&& csvRecord.getRecordNumber() > newRowNum) {
						String header1 = "@MAIN_BOARD_INFORMATION";
						map1.put("NE ID", csvRecord.get(0));
						map1.put("Unit Type", csvRecord.get(1));
						map1.put("Unit ID", csvRecord.get(2));
						map1.put("Board Type", csvRecord.get(3));
						csvToMapForSingle.clear();
						csvToMapForSingle.put(header1, map1);
						JSONData6 = JSONData6 + "," + gsonObject.toJson(csvToMapForSingle).substring(1,
								gsonObject.toJson(csvToMapForSingle).length() - 1);

					}

					else if (currentHeader.equalsIgnoreCase("@CLOCK_INFORMATION")
							&& csvRecord.getRecordNumber() > newRowNum) {

						String header1 = "@CLOCK_INFORMATION";
						map1.put("NE ID", csvRecord.get(0));
						map1.put("Clock Source ID", csvRecord.get(1));
						map1.put("Clock Source", csvRecord.get(2));
						map1.put("Priority Level", csvRecord.get(3));
						map1.put("Quality Level", csvRecord.get(4));
						csvToMapForSingle.clear();
						csvToMapForSingle.put(header1, map1);
						JSONData6 = JSONData6 + "," + gsonObject.toJson(csvToMapForSingle).substring(1,
								gsonObject.toJson(csvToMapForSingle).length() - 1);
					}

					else if (currentHeader.equalsIgnoreCase("@PTP_INFORMATION")
							&& csvRecord.getRecordNumber() > newRowNum) {
						String header1 = "@PTP_INFORMATION";
						map1.put("NE ID", csvRecord.get(0));
						map1.put("IP Version", csvRecord.get(1));
						map1.put("First Master IP", csvRecord.get(2));
						map1.put("Second Master IP", csvRecord.get(3));
						map1.put("Clock Profile", csvRecord.get(4));
						map1.put("PTP Domain", csvRecord.get(5));
						csvToMapForSingle.clear();
						csvToMapForSingle.put(header1, map1);
						JSONData6 = JSONData6 + "," + gsonObject.toJson(csvToMapForSingle).substring(1,
								gsonObject.toJson(csvToMapForSingle).length() - 1);

					} else if (currentHeader.equalsIgnoreCase("@PORT_INFORMATION")
							&& csvRecord.getRecordNumber() > newRowNum) {

						String header1 = "@PORT_INFORMATION";
						map1.put("NE ID", csvRecord.get(0));
						map1.put("Port ID", csvRecord.get(1));
						map1.put("VR ID", csvRecord.get(2));
						map1.put("Port AdministrativeState", csvRecord.get(3));
						map1.put("Connect Type", csvRecord.get(4));
						map1.put("UDE Type", csvRecord.get(5));
						map1.put("MTU", csvRecord.get(6));
						map1.put("Speed Duplex", csvRecord.get(7));
						map1.put("Fec Mode", csvRecord.get(8));
						csvToMapForSingle.clear();
						csvToMapForSingle.put(header1, map1);
						JSONData6 = JSONData6 + "," + gsonObject.toJson(csvToMapForSingle).substring(1,
								gsonObject.toJson(csvToMapForSingle).length() - 1);
					}else if (currentHeader.equalsIgnoreCase("@VIRTUAL_ROUTING_INFORMATION")
							&& csvRecord.getRecordNumber() > newRowNum) {
						String header1 = "@VIRTUAL_ROUTING_INFORMATION";
						map1.put("NE ID", csvRecord.get(0));
						map1.put("VR ID", csvRecord.get(1));
						csvToMapForSingle.clear();
						csvToMapForSingle.put(header1, map1);
						JSONData6 = JSONData6 + "," + gsonObject.toJson(csvToMapForSingle).substring(1,
								gsonObject.toJson(csvToMapForSingle).length() - 1);

					}

					if (currentHeader.equalsIgnoreCase("@IP_INFORMATION") && csvRecord.getRecordNumber() > newRowNum) {
						for (int i = 0; i < csvRecord.size(); i++) {
							singleRow.add(csvRecord.get(i));
							size1 = csvRecord.size();
						}

						multiRow.add(singleRow);
					} else if (prevHeader.equalsIgnoreCase("@IP_INFORMATION")
							&& csvRecord.get(0).toString().contains("@")) {
						Map<String, List<String>> map11 = new LinkedHashMap<>();
						for (int i = 0; i < multiRow.size(); i++) {

							for (int j = 0; j < size1; j++) {
								if (j == 0) {
									singleRow.add(multiRow.get(i).get(j));
								} else if (j == 1) {
									singleRow1.add(multiRow.get(i).get(j));
								} else if (j == 2) {
									singleRow2.add(multiRow.get(i).get(j));
								} else if (j == 3) {
									singleRow3.add(multiRow.get(i).get(j));
								} else if (j == 4) {
									singleRow4.add(multiRow.get(i).get(j));
								} else if (j == 5) {
									singleRow5.add(multiRow.get(i).get(j));
								} else if (j == 6) {
									singleRow6.add(multiRow.get(i).get(j));
								} else if (j == 7) {
									singleRow7.add(multiRow.get(i).get(j));
								} else if (j == 8) {
									singleRow8.add(multiRow.get(i).get(j));
								} else if (j == 9) {
									singleRow9.add(multiRow.get(i).get(j));
								}
							}
						}
						map12.put("NE ID", singleRow);
						map12.put("CPU ID", singleRow1);
						map12.put("External Interface Name", singleRow2);
						map12.put("IP Address", singleRow3);
						map12.put("IP Prefix Length", singleRow4);
						map12.put("IP Get Type", singleRow5);
						map12.put("Management", singleRow6);
						map12.put("Control", singleRow7);
						map12.put("Bearer", singleRow8);
						map12.put("IEEE1588", singleRow9);
						csvToMapForMultiple.clear();
						String header1 = "@IP_INFORMATION";
						csvToMapForMultiple.put(header1, map12);
						JSONData6 = JSONData6 + "," + gsonObject.toJson(csvToMapForMultiple).substring(1,
								gsonObject.toJson(csvToMapForMultiple).length() - 1);
						multiRow.clear();

					}

					if (currentHeader.equalsIgnoreCase("@VLAN_INFORMATION")
							&& csvRecord.getRecordNumber() > newRowNum) {
						for (int i = 0; i < csvRecord.size(); i++) {
							singleRow.add(csvRecord.get(i));
							size1 = csvRecord.size();
						}

						multiRow.add(singleRow);
					} else if (prevHeader.equalsIgnoreCase("@VLAN_INFORMATION")
							&& csvRecord.get(0).toString().contains("@")) {
						Map<String, List<String>> map11 = new LinkedHashMap<>();
						for (int i = 0; i < multiRow.size(); i++) {

							for (int j = 0; j < size1; j++) {
								if (j == 0) {
									singleRow.add(multiRow.get(i).get(j));
								} else if (j == 1) {
									singleRow1.add(multiRow.get(i).get(j));
								} else if (j == 2) {
									singleRow2.add(multiRow.get(i).get(j));
								} else if (j == 3) {
									singleRow3.add(multiRow.get(i).get(j));
								}else if (j == 4) {
									singleRow4.add(multiRow.get(i).get(j));
								}
							}
						}

						map12.put("NE ID", singleRow);
						map12.put("CPU ID", singleRow1);
						map12.put("VLAN Interface Name", singleRow2);
						map12.put("VLAN ID", singleRow3);
						map12.put("VR ID", singleRow4);
						csvToMapForMultiple.clear();
						String header1 = "@VLAN_INFORMATION";
						csvToMapForMultiple.put(header1, map12);
						JSONData6 = JSONData6 + "," + gsonObject.toJson(csvToMapForMultiple).substring(1,
								gsonObject.toJson(csvToMapForMultiple).length() - 1);
						multiRow.clear();

					}

					else if (currentHeader.equalsIgnoreCase("@LAG_INFORMATION")
							&& csvRecord.getRecordNumber() > newRowNum) {

						String header1 = "@LAG_INFORMATION";
						map1.put("NE ID", csvRecord.get(0));
						map1.put("CPU ID", csvRecord.get(1));
						map1.put("LAG ID", csvRecord.get(2));
						map1.put("VR ID", csvRecord.get(3));
						map1.put("LAG Interface Name", csvRecord.get(4));
						
						csvToMapForSingle.clear();
						csvToMapForSingle.put(header1, map1);
						JSONData6 = JSONData6 + "," + gsonObject.toJson(csvToMapForSingle).substring(1,
								gsonObject.toJson(csvToMapForSingle).length() - 1);

					}
					if (currentHeader.equalsIgnoreCase("@ROUTE_INFORMATION")
							&& csvRecord.getRecordNumber() > newRowNum) {
						for (int i = 0; i < csvRecord.size(); i++) {
							singleRow.add(csvRecord.get(i));
							size1 = csvRecord.size();
						}

						multiRow.add(singleRow);
					} else if (prevHeader.equalsIgnoreCase("@ROUTE_INFORMATION")
							&& csvRecord.get(0).toString().contains("@")) {
						Map<String, List<String>> map11 = new LinkedHashMap<>();
						for (int i = 0; i < multiRow.size(); i++) {

							for (int j = 0; j < size1; j++) {
								if (j == 0) {
									singleRow.add(multiRow.get(i).get(j));
								} else if (j == 1) {
									singleRow1.add(multiRow.get(i).get(j));
								} else if (j == 2) {
									singleRow2.add(multiRow.get(i).get(j));
								} else if (j == 3) {
									singleRow3.add(multiRow.get(i).get(j));
								} else if (j == 4) {
									singleRow4.add(multiRow.get(i).get(j));
								} else if (j == 5) {
									singleRow5.add(multiRow.get(i).get(j));
								}else if (j == 6) {
									singleRow6.add(multiRow.get(i).get(j));
								}
							}
						}
						map12.put("NE ID", singleRow);
						map12.put("CPU ID", singleRow1);
						map12.put("VR ID", singleRow2);
						map12.put("IP Prefix", singleRow3);
						map12.put("IP Prefix Length", singleRow4);
						map12.put("IP Gateway", singleRow5);
						map12.put("Route Interface Name", singleRow6);
						csvToMapForMultiple.clear();
						String header1 = "@ROUTE_INFORMATION";
						csvToMapForMultiple.put(header1, map12);
						JSONData6 = JSONData6 + "," + gsonObject.toJson(csvToMapForMultiple).substring(1,
								gsonObject.toJson(csvToMapForMultiple).length() - 1);
						multiRow.clear();

					}

					else if (currentHeader.equalsIgnoreCase("@SYSTEM_LOCATION_INFORMATION")
							&& csvRecord.getRecordNumber() > newRowNum) {

						String header1 = "@SYSTEM_LOCATION_INFORMATION";
						map1.put("NE ID", csvRecord.get(0));
						map1.put("User Defined Mode", csvRecord.get(1));
						map1.put("Latitude", csvRecord.get(2));
						map1.put("Longitude", csvRecord.get(3));
						map1.put("Height", csvRecord.get(4));
						csvToMapForSingle.clear();
						csvToMapForSingle.put(header1, map1);
						JSONData6 = JSONData6 + "," + gsonObject.toJson(csvToMapForSingle).substring(1,
								gsonObject.toJson(csvToMapForSingle).length() - 1);

					}
					if (!currentHeader.isEmpty() && currentHeader.equalsIgnoreCase("@RU_INFORMATION")
							&& csvRecord.getRecordNumber() > newRowNum) {
						String header1 = "@RU_INFORMATION";
						map1.put("NE ID", csvRecord.get(0));
						map1.put("Start Frequency", csvRecord.get(1));
						csvToMapForSingle.clear();
						csvToMapForSingle.put(header1, map1);
						JSONData6 = JSONData6 + "," + gsonObject.toJson(csvToMapForSingle).substring(1,
								gsonObject.toJson(csvToMapForSingle).length() - 1);
					}
					
					
					if (!currentHeader.isEmpty() && currentHeader.equalsIgnoreCase("@IPSEC_INFORMATION")
							&& csvRecord.getRecordNumber() > newRowNum) {
						String header1 = "@IPSEC_INFORMATION";
						map1.put("NE ID", csvRecord.get(0));
						map1.put("CPU ID", csvRecord.get(1));
						map1.put("VR ID", csvRecord.get(2));
						map1.put("Interface Name1", csvRecord.get(3));
						map1.put("Peer IP Version", csvRecord.get(4));
						map1.put("First Peer IP", csvRecord.get(5));
						map1.put("Second Peer IP", csvRecord.get(6));
						map1.put("Inner IP Version", csvRecord.get(7));
						map1.put("Tunnel Mode", csvRecord.get(8));
						map1.put("Interface Name2", csvRecord.get(9));
						map1.put("Interface Name3", csvRecord.get(10));
						map1.put("Crypto Algorithm", csvRecord.get(11));
						map1.put("Hash Algorithm", csvRecord.get(12));
						map1.put("Local ID Type", csvRecord.get(13));
						map1.put("Local ID", csvRecord.get(14));
						csvToMapForSingle.clear();
						csvToMapForSingle.put(header1, map1);
						JSONData6 = JSONData6 + "," + gsonObject.toJson(csvToMapForSingle).substring(1,
								gsonObject.toJson(csvToMapForSingle).length() - 1);
					}
					
					if (!currentHeader.isEmpty() && currentHeader.equalsIgnoreCase("@PKI_INFORMATION")
							&& csvRecord.getRecordNumber() > newRowNum) {
						String header1 = "@PKI_INFORMATION";
						map1.put("NE ID", csvRecord.get(0));
						map1.put("CPU ID", csvRecord.get(1));
						map1.put("IP Address", csvRecord.get(2));
						map1.put("FQDN", csvRecord.get(3));
						map1.put("Port", csvRecord.get(4));
						map1.put("Path", csvRecord.get(5));
						map1.put("DN", csvRecord.get(6));
						map1.put("DN Domain", csvRecord.get(7));
						map1.put("CA DN", csvRecord.get(8));
						map1.put("Hash Algorithm", csvRecord.get(9));
						csvToMapForSingle.clear();
						csvToMapForSingle.put(header1, map1);
						JSONData6 = JSONData6 + "," + gsonObject.toJson(csvToMapForSingle).substring(1,
								gsonObject.toJson(csvToMapForSingle).length() - 1);
					}

				}

				char c1 = '"';
				String v1 = Character.toString(c1);
				String v3 = "\\\\" + Character.toString(c1);
				JSONData6 = JSONData6.replaceAll(Character.toString(c1), v3).replaceAll("]", "\\\\]").replace("[",
						"\\[");
				JSONObject = "{" + JSONData6 + "}";
				System.out.println(JSONObject);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if (sourcePath.contains("AU_20B") || sourcePath.contains("AU_20A")) {

			try {
				GsonBuilder gsonMapBuilder = new GsonBuilder();
				Gson gsonObject = gsonMapBuilder.create();
				BufferedReader fileReader = null;
				CSVParser csvParser = null;
				fileReader = new BufferedReader(new FileReader(sourcePath));

				csvParser = new CSVParser(fileReader,
						CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());
				Iterable<CSVRecord> csvRecords = csvParser.getRecords();

				// Map<String, List<Map<String, String>>> csvToMap = new LinkedHashMap<>();
				Map<String, Map<String, List<String>>> csvToMapForMultiple = new LinkedHashMap<>();
				Map<String, Map<String, String>> csvToMapForSingle = new LinkedHashMap<>();
				String JSONData6 = null;
				String prevHeader = null;
				String currentHeader = null;
				String firstHeader = "@DU";
				long rowNum = 0;
				long newRowNum = 0;
				int size1 = 0;

				List<List<String>> multiRow = new LinkedList<>();
				for (CSVRecord csvRecord : csvRecords) {
					List<String> singleRow = new LinkedList<>();
					List<String> singleRow1 = new LinkedList<>();
					List<String> singleRow2 = new LinkedList<>();
					List<String> singleRow3 = new LinkedList<>();
					List<String> singleRow4 = new LinkedList<>();
					List<String> singleRow5 = new LinkedList<>();
					List<String> singleRow6 = new LinkedList<>();
					List<String> singleRow7 = new LinkedList<>();
					List<String> singleRow8 = new LinkedList<>();
					List<String> singleRow9 = new LinkedList<>();
					List<String> singleRow10 = new LinkedList<>();
					List<String> singleRow11 = new LinkedList<>();
					List<String> singleRow12 = new LinkedList<>();
					List<String> singleRow13 = new LinkedList<>();
					Map<String, List<String>> map12 = new LinkedHashMap<>();
					// List<Map<String, String>> list = new LinkedList<>();
					Map<String, String> map1 = new LinkedHashMap<>();

					long recordNumber = csvRecord.getRecordNumber();
					if (csvRecord.get(0).toString().contains("@") || csvRecord.getRecordNumber() == 1) {
						rowNum = csvRecord.getRecordNumber();

						newRowNum = rowNum + 1;

						if (csvRecord.getRecordNumber() == 1) {
							prevHeader = firstHeader;
							currentHeader = "@DU";

						} else {
							prevHeader = currentHeader;
							currentHeader = csvRecord.get(0).toString();
						}
					}
					if (currentHeader.equalsIgnoreCase("@DU")) {

						String header1 = "@DU";
						map1.put("NE ID", csvRecord.get(0));
						map1.put("NE Type", csvRecord.get(1));
						map1.put("NE Version", csvRecord.get(2));
						map1.put("Release Version", csvRecord.get(3));
						map1.put("Network", csvRecord.get(4));
						map1.put("NE Name", csvRecord.get(5));
						map1.put("AdministrativeState", csvRecord.get(6));
						map1.put("gNB ID", csvRecord.get(7));
						map1.put("gNB ID Length", csvRecord.get(8));
						map1.put("gNB DU ID", csvRecord.get(9));
						map1.put("gNB DU Name", csvRecord.get(10));
						map1.put("Endpoint CU IP address", csvRecord.get(11));
						map1.put("Time Offset", csvRecord.get(12));
						map1.put("NE Serial Number", csvRecord.get(13));

						csvToMapForSingle.put(header1, map1);
						JSONData6 = gsonObject.toJson(csvToMapForSingle);
						JSONData6 = JSONData6.substring(1, JSONData6.length() - 1);

					} else if (currentHeader.equalsIgnoreCase("@MAIN_BOARD_INFORMATION")
							&& csvRecord.getRecordNumber() > newRowNum) {
						String header1 = "@MAIN_BOARD_INFORMATION";
						map1.put("NE ID", csvRecord.get(0));
						map1.put("Unit Type", csvRecord.get(1));
						map1.put("Unit ID", csvRecord.get(2));
						map1.put("Board Type", csvRecord.get(3));
						csvToMapForSingle.clear();
						csvToMapForSingle.put(header1, map1);
						JSONData6 = JSONData6 + "," + gsonObject.toJson(csvToMapForSingle).substring(1,
								gsonObject.toJson(csvToMapForSingle).length() - 1);

					}

					else if (currentHeader.equalsIgnoreCase("@CLOCK_INFORMATION")
							&& csvRecord.getRecordNumber() > newRowNum) {

						String header1 = "@CLOCK_INFORMATION";
						map1.put("NE ID", csvRecord.get(0));
						map1.put("Clock Source ID", csvRecord.get(1));
						map1.put("Clock Source", csvRecord.get(2));
						map1.put("Priority Level", csvRecord.get(3));
						map1.put("Quality Level", csvRecord.get(4));
						csvToMapForSingle.clear();
						csvToMapForSingle.put(header1, map1);
						JSONData6 = JSONData6 + "," + gsonObject.toJson(csvToMapForSingle).substring(1,
								gsonObject.toJson(csvToMapForSingle).length() - 1);
					}

					else if (currentHeader.equalsIgnoreCase("@PTP_INFORMATION")
							&& csvRecord.getRecordNumber() > newRowNum) {
						String header1 = "@PTP_INFORMATION";
						map1.put("NE ID", csvRecord.get(0));
						map1.put("IP Version", csvRecord.get(1));
						map1.put("First Master IP", csvRecord.get(2));
						map1.put("Second Master IP", csvRecord.get(3));
						map1.put("Clock Profile", csvRecord.get(4));
						map1.put("PTP Domain", csvRecord.get(5));
						csvToMapForSingle.clear();
						csvToMapForSingle.put(header1, map1);
						JSONData6 = JSONData6 + "," + gsonObject.toJson(csvToMapForSingle).substring(1,
								gsonObject.toJson(csvToMapForSingle).length() - 1);

					} else if (currentHeader.equalsIgnoreCase("@PORT_INFORMATION")
							&& csvRecord.getRecordNumber() > newRowNum) {

						String header1 = "@PORT_INFORMATION";
						map1.put("NE ID", csvRecord.get(0));
						map1.put("Port ID", csvRecord.get(1));
						map1.put("Port AdministrativeState", csvRecord.get(2));
						map1.put("Connect Type", csvRecord.get(3));
						map1.put("UDE Type", csvRecord.get(4));
						map1.put("MTU", csvRecord.get(5));
						map1.put("Speed Duplex", csvRecord.get(6));
						map1.put("Fec Mode", csvRecord.get(7));
						csvToMapForSingle.clear();
						csvToMapForSingle.put(header1, map1);
						JSONData6 = JSONData6 + "," + gsonObject.toJson(csvToMapForSingle).substring(1,
								gsonObject.toJson(csvToMapForSingle).length() - 1);
					}

					if (currentHeader.equalsIgnoreCase("@IP_INFORMATION") && csvRecord.getRecordNumber() > newRowNum) {
						for (int i = 0; i < csvRecord.size(); i++) {
							singleRow.add(csvRecord.get(i));
							size1 = csvRecord.size();
						}

						multiRow.add(singleRow);
					} else if (prevHeader.equalsIgnoreCase("@IP_INFORMATION")
							&& csvRecord.get(0).toString().contains("@")) {
						Map<String, List<String>> map11 = new LinkedHashMap<>();
						for (int i = 0; i < multiRow.size(); i++) {

							for (int j = 0; j < size1; j++) {
								if (j == 0) {
									singleRow.add(multiRow.get(i).get(j));
								} else if (j == 1) {
									singleRow1.add(multiRow.get(i).get(j));
								} else if (j == 2) {
									singleRow2.add(multiRow.get(i).get(j));
								} else if (j == 3) {
									singleRow3.add(multiRow.get(i).get(j));
								} else if (j == 4) {
									singleRow4.add(multiRow.get(i).get(j));
								} else if (j == 5) {
									singleRow5.add(multiRow.get(i).get(j));
								} else if (j == 6) {
									singleRow6.add(multiRow.get(i).get(j));
								} else if (j == 7) {
									singleRow7.add(multiRow.get(i).get(j));
								} else if (j == 8) {
									singleRow8.add(multiRow.get(i).get(j));
								} else if (j == 9) {
									singleRow9.add(multiRow.get(i).get(j));
								}
							}
						}
						map12.put("NE ID", singleRow);
						map12.put("CPU ID", singleRow1);
						map12.put("External Interface Name", singleRow2);
						map12.put("IP Address", singleRow3);
						map12.put("IP Prefix Length", singleRow4);
						map12.put("IP Get Type", singleRow5);
						map12.put("Management", singleRow6);
						map12.put("Control", singleRow7);
						map12.put("Bearer", singleRow8);
						map12.put("IEEE1588", singleRow9);
						csvToMapForMultiple.clear();
						String header1 = "@IP_INFORMATION";
						csvToMapForMultiple.put(header1, map12);
						JSONData6 = JSONData6 + "," + gsonObject.toJson(csvToMapForMultiple).substring(1,
								gsonObject.toJson(csvToMapForMultiple).length() - 1);
						multiRow.clear();

					}

					if (currentHeader.equalsIgnoreCase("@VLAN_INFORMATION")
							&& csvRecord.getRecordNumber() > newRowNum) {
						for (int i = 0; i < csvRecord.size(); i++) {
							singleRow.add(csvRecord.get(i));
							size1 = csvRecord.size();
						}

						multiRow.add(singleRow);
					} else if (prevHeader.equalsIgnoreCase("@VLAN_INFORMATION")
							&& csvRecord.get(0).toString().contains("@")) {
						Map<String, List<String>> map11 = new LinkedHashMap<>();
						for (int i = 0; i < multiRow.size(); i++) {

							for (int j = 0; j < size1; j++) {
								if (j == 0) {
									singleRow.add(multiRow.get(i).get(j));
								} else if (j == 1) {
									singleRow1.add(multiRow.get(i).get(j));
								} else if (j == 2) {
									singleRow2.add(multiRow.get(i).get(j));
								} else if (j == 3) {
									singleRow3.add(multiRow.get(i).get(j));
								}
							}
						}

						map12.put("NE ID", singleRow);
						map12.put("CPU ID", singleRow1);
						map12.put("VLAN Interface Name", singleRow2);
						map12.put("VLAN ID", singleRow3);
						csvToMapForMultiple.clear();
						String header1 = "@VLAN_INFORMATION";
						csvToMapForMultiple.put(header1, map12);
						JSONData6 = JSONData6 + "," + gsonObject.toJson(csvToMapForMultiple).substring(1,
								gsonObject.toJson(csvToMapForMultiple).length() - 1);
						multiRow.clear();

					}

					else if (currentHeader.equalsIgnoreCase("@LAG_INFORMATION")
							&& csvRecord.getRecordNumber() > newRowNum) {

						String header1 = "@LAG_INFORMATION";
						map1.put("NE ID", csvRecord.get(0));
						map1.put("CPU ID", csvRecord.get(1));
						map1.put("LAG Interface Name", csvRecord.get(2));
						map1.put("LAG ID", csvRecord.get(3));
						csvToMapForSingle.clear();
						csvToMapForSingle.put(header1, map1);
						JSONData6 = JSONData6 + "," + gsonObject.toJson(csvToMapForSingle).substring(1,
								gsonObject.toJson(csvToMapForSingle).length() - 1);

					}
					if (currentHeader.equalsIgnoreCase("@ROUTE_INFORMATION")
							&& csvRecord.getRecordNumber() > newRowNum) {
						for (int i = 0; i < csvRecord.size(); i++) {
							singleRow.add(csvRecord.get(i));
							size1 = csvRecord.size();
						}

						multiRow.add(singleRow);
					} else if (prevHeader.equalsIgnoreCase("@ROUTE_INFORMATION")
							&& csvRecord.get(0).toString().contains("@")) {
						Map<String, List<String>> map11 = new LinkedHashMap<>();
						for (int i = 0; i < multiRow.size(); i++) {

							for (int j = 0; j < size1; j++) {
								if (j == 0) {
									singleRow.add(multiRow.get(i).get(j));
								} else if (j == 1) {
									singleRow1.add(multiRow.get(i).get(j));
								} else if (j == 2) {
									singleRow2.add(multiRow.get(i).get(j));
								} else if (j == 3) {
									singleRow3.add(multiRow.get(i).get(j));
								} else if (j == 4) {
									singleRow4.add(multiRow.get(i).get(j));
								} else if (j == 5) {
									singleRow5.add(multiRow.get(i).get(j));
								}
							}
						}
						map12.put("NE ID", singleRow);
						map12.put("CPU ID", singleRow1);
						map12.put("IP Prefix", singleRow2);
						map12.put("IP Prefix Length", singleRow3);
						map12.put("IP Gateway", singleRow4);
						map12.put("Route Interface Name", singleRow5);
						csvToMapForMultiple.clear();
						String header1 = "@ROUTE_INFORMATION";
						csvToMapForMultiple.put(header1, map12);
						JSONData6 = JSONData6 + "," + gsonObject.toJson(csvToMapForMultiple).substring(1,
								gsonObject.toJson(csvToMapForMultiple).length() - 1);
						multiRow.clear();

					}

					else if (currentHeader.equalsIgnoreCase("@SYSTEM_LOCATION_INFORMATION")
							&& csvRecord.getRecordNumber() > newRowNum) {

						String header1 = "@SYSTEM_LOCATION_INFORMATION";
						map1.put("NE ID", csvRecord.get(0));
						map1.put("User Defined Mode", csvRecord.get(1));
						map1.put("Latitude", csvRecord.get(2));
						map1.put("Longitude", csvRecord.get(3));
						map1.put("Height", csvRecord.get(4));
						csvToMapForSingle.clear();
						csvToMapForSingle.put(header1, map1);
						JSONData6 = JSONData6 + "," + gsonObject.toJson(csvToMapForSingle).substring(1,
								gsonObject.toJson(csvToMapForSingle).length() - 1);

					}
					if (!currentHeader.isEmpty() && currentHeader.equalsIgnoreCase("@RU_INFORMATION")
							&& csvRecord.getRecordNumber() > newRowNum) {
						String header1 = "@RU_INFORMATION";
						map1.put("NE ID", csvRecord.get(0));
						map1.put("Start Frequency", csvRecord.get(1));
						csvToMapForSingle.clear();
						csvToMapForSingle.put(header1, map1);
						JSONData6 = JSONData6 + "," + gsonObject.toJson(csvToMapForSingle).substring(1,
								gsonObject.toJson(csvToMapForSingle).length() - 1);
					}

				}

				char c1 = '"';
				String v1 = Character.toString(c1);
				String v3 = "\\\\" + Character.toString(c1);
				JSONData6 = JSONData6.replaceAll(Character.toString(c1), v3).replaceAll("]", "\\\\]").replace("[",
						"\\[");
				JSONObject = "{" + JSONData6 + "}";
				System.out.println(JSONObject);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (sourcePath.contains("GROW_CELL")) {

			try {
				int size1 = 0;
				GsonBuilder gsonMapBuilder = new GsonBuilder();
				Gson gsonObject = gsonMapBuilder.create();
				BufferedReader fileReader = null;
				CSVParser csvParser = null;
				fileReader = new BufferedReader(new FileReader(sourcePath));
				csvParser = new CSVParser(fileReader,
						CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());

				Iterable<CSVRecord> csvRecords = csvParser.getRecords();
				Map<String, List<Map<String, String>>> csvToMap = new LinkedHashMap<>();
				Map<String, Map<String, List<String>>> csvToMapForMultiple = new LinkedHashMap<>();
				Map<String, Map<String, String>> csvToMapForSingle = new LinkedHashMap<>();
				List<List<String>> multiRow = new LinkedList<>();
				String JSONObject1 = null;
				String JSONObject2 = null;
				String prevHeader = null;
				String currentHeader = null;
				String firstHeader = "@CELL_INFORMATION";
				long rowNum = 0;
				long newRowNum = 0;
				for (CSVRecord csvRecord : csvRecords) {
					List<String> singleRow = new LinkedList<>();
					List<String> singleRow1 = new LinkedList<>();
					List<String> singleRow2 = new LinkedList<>();
					List<String> singleRow3 = new LinkedList<>();
					List<String> singleRow4 = new LinkedList<>();
					List<String> singleRow5 = new LinkedList<>();
					List<String> singleRow6 = new LinkedList<>();
					List<String> singleRow7 = new LinkedList<>();
					List<String> singleRow8 = new LinkedList<>();
					List<String> singleRow9 = new LinkedList<>();
					List<String> singleRow10 = new LinkedList<>();
					List<String> singleRow11 = new LinkedList<>();
					List<String> singleRow12 = new LinkedList<>();
					List<String> singleRow13 = new LinkedList<>();
					List<String> singleRow14 = new LinkedList<>();
					List<String> singleRow15 = new LinkedList<>();
					List<String> singleRow16 = new LinkedList<>();
					List<String> singleRow17 = new LinkedList<>();
					List<String> singleRow18 = new LinkedList<>();
					List<String> singleRow19 = new LinkedList<>();
					List<String> singleRow20 = new LinkedList<>();
					List<String> singleRow21 = new LinkedList<>();
					List<String> singleRow22 = new LinkedList<>();
					List<String> singleRow23 = new LinkedList<>();
					List<String> singleRow24 = new LinkedList<>();
					List<String> singleRow25 = new LinkedList<>();
					List<String> singleRow26 = new LinkedList<>();
					List<String> singleRow27 = new LinkedList<>();
					List<String> singleRow28 = new LinkedList<>();
					List<String> singleRow29 = new LinkedList<>();
					List<String> singleRow30 = new LinkedList<>();
					List<String> singleRow31 = new LinkedList<>();
					List<String> singleRow32 = new LinkedList<>();
					List<String> singleRow33 = new LinkedList<>();
					List<String> singleRow34 = new LinkedList<>();
					List<String> singleRow35 = new LinkedList<>();
					List<String> singleRow36 = new LinkedList<>();
					List<String> singleRow37 = new LinkedList<>();
					List<String> singleRow38 = new LinkedList<>();

					Map<String, List<String>> map12 = new LinkedHashMap<>();
					List<Map<String, String>> list = new LinkedList<>();
					Map<String, String> map1 = new LinkedHashMap<>();

					long recordNumber = csvRecord.getRecordNumber();
					String s = csvRecord.get(0).toString();
					if (csvRecord.get(0).toString().contains("@") || csvRecord.getRecordNumber() == 1) {
						rowNum = csvRecord.getRecordNumber();
						if (csvRecord.getRecordNumber() == 1) {
							newRowNum = rowNum;
						} else {
							newRowNum = rowNum + 1;
						}
						if (csvRecord.getRecordNumber() == 1) {
							prevHeader = firstHeader;
							currentHeader = "@CELL_INFORMATION";

						} else {
							prevHeader = currentHeader;
							currentHeader = csvRecord.get(0).toString();
						}
					}
					if (currentHeader.equalsIgnoreCase("@CELL_INFORMATION")
							&& csvRecord.getRecordNumber() > newRowNum) {
						for (int i = 0; i < csvRecord.size(); i++) {
							singleRow.add(csvRecord.get(i));
							size1 = csvRecord.size();
						}
						multiRow.add(singleRow);

					} else if (prevHeader.equalsIgnoreCase("@CELL_INFORMATION")
							&& csvRecord.get(0).toString().contains("@")) {
						for (int i = 0; i < multiRow.size(); i++) {

							for (int j = 0; j < size1; j++) {
								if (j == 0) {
									singleRow.add(multiRow.get(i).get(j));
								} else if (j == 1) {
									singleRow1.add(multiRow.get(i).get(j));
								} else if (j == 2) {
									singleRow2.add(multiRow.get(i).get(j));
								} else if (j == 3) {
									singleRow3.add(multiRow.get(i).get(j));
								} else if (j == 4) {
									singleRow4.add(multiRow.get(i).get(j));
								} else if (j == 5) {
									singleRow5.add(multiRow.get(i).get(j));
								} else if (j == 6) {
									singleRow6.add(multiRow.get(i).get(j));
								} else if (j == 7) {
									singleRow7.add(multiRow.get(i).get(j));
								} else if (j == 8) {
									singleRow8.add(multiRow.get(i).get(j));
								} else if (j == 9) {
									singleRow9.add(multiRow.get(i).get(j));
								} else if (j == 10) {
									singleRow10.add(multiRow.get(i).get(j));
								} else if (j == 11) {
									singleRow11.add(multiRow.get(i).get(j));
								} else if (j == 12) {
									singleRow12.add(multiRow.get(i).get(j));
								} else if (j == 13) {
									singleRow13.add(multiRow.get(i).get(j));
								} else if (j == 14) {
									singleRow14.add(multiRow.get(i).get(j));
								} else if (j == 15) {
									singleRow15.add(multiRow.get(i).get(j));
								} else if (j == 16) {
									singleRow16.add(multiRow.get(i).get(j));
								} else if (j == 17) {
									singleRow17.add(multiRow.get(i).get(j));
								} else if (j == 18) {
									singleRow18.add(multiRow.get(i).get(j));
								} else if (j == 19) {
									singleRow19.add(multiRow.get(i).get(j));
								} else if (j == 20) {
									singleRow20.add(multiRow.get(i).get(j));
								} else if (j == 21) {
									singleRow21.add(multiRow.get(i).get(j));
								} else if (j == 22) {
									singleRow22.add(multiRow.get(i).get(j));
								} else if (j == 23) {
									singleRow23.add(multiRow.get(i).get(j));
								} else if (j == 24) {
									singleRow24.add(multiRow.get(i).get(j));
								} else if (j == 25) {
									singleRow25.add(multiRow.get(i).get(j));
								} else if (j == 26) {
									singleRow26.add(multiRow.get(i).get(j));
								} else if (j == 27) {
									singleRow27.add(multiRow.get(i).get(j));
								} else if (j == 28) {
									singleRow28.add(multiRow.get(i).get(j));
								} else if (j == 29) {
									singleRow29.add(multiRow.get(i).get(j));
								} else if (j == 30) {
									singleRow30.add(multiRow.get(i).get(j));
								} else if (j == 31) {
									singleRow31.add(multiRow.get(i).get(j));
								} else if (j == 32) {
									singleRow32.add(multiRow.get(i).get(j));
								} else if (j == 33) {
									singleRow33.add(multiRow.get(i).get(j));
								} else if (j == 34) {
									singleRow34.add(multiRow.get(i).get(j));
								} else if (j == 35) {
									singleRow35.add(multiRow.get(i).get(j));
								} else if (j == 36) {
									singleRow36.add(multiRow.get(i).get(j));
								} else if (j == 37) {
									singleRow37.add(multiRow.get(i).get(j));
								} else if (j == 38) {
									singleRow38.add(multiRow.get(i).get(j));
								}
							}

						}
						map12.put("State", singleRow);
						map12.put("Sector ID", singleRow1);
						map12.put("Carrier ID", singleRow2);
						map12.put("Cell Index in DSP", singleRow3);
						map12.put("DSP ID", singleRow4);
						map12.put("CC ID", singleRow5);
						map12.put("RU Port ID", singleRow6);
						map12.put("RU Conf", singleRow7);
						map12.put("Multi Carrier Type", singleRow8);
						map12.put("Virtual RF Port Mapping", singleRow9);
						map12.put("Dl Max Tx Power", singleRow10);
						map12.put("Pucch Center Mode", singleRow11);
						map12.put("PCI", singleRow12);
						map12.put("DL Antenna Count", singleRow13);
						map12.put("UL Antenna Count", singleRow14);
						map12.put("Earfcn DL", singleRow15);
						map12.put("Earfcn UL", singleRow16);
						map12.put("Cell Band Carrier", singleRow17);
						map12.put("Bandwidth", singleRow18);
						map12.put("CRS", singleRow19);
						map12.put("eMTC", singleRow20);
						map12.put("Frequency Profile", singleRow21);
						map12.put("TAC", singleRow22);
						map12.put("EAID", singleRow23);
						map12.put("HSF", singleRow24);
						map12.put("ZCZC", singleRow25);
						map12.put("RSI", singleRow26);
						map12.put("Rcc ID", singleRow27);
						map12.put("TH MaxEirp", singleRow28);
						map12.put("TH RSSI", singleRow29);
						map12.put("Preferred Earfcn", singleRow30);
						map12.put("Subframe Assignment", singleRow31);
						map12.put("Special Subframe Patterns", singleRow32);
						map12.put("Dynamic Spectrum Sharing Mode", singleRow33);
						map12.put("CDMA Blanking Case", singleRow34);
						map12.put("Auto GPS", singleRow35);
						map12.put("Latitude", singleRow36);
						map12.put("Longitude", singleRow37);
						map12.put("Height", singleRow38);
						String header1 = "@CELL_INFORMATION";
						csvToMapForMultiple.put(header1, map12);
						multiRow.clear();
					}
					if (currentHeader.equalsIgnoreCase("@NB_IOT_CELL_INFORMATION")
							&& csvRecord.getRecordNumber() > newRowNum) {
						for (int i = 0; i < csvRecord.size(); i++) {
							singleRow.add(csvRecord.get(i));
							size1 = csvRecord.size();
						}
						multiRow.add(singleRow);

					} else if (prevHeader.equalsIgnoreCase("@NB_IOT_CELL_INFORMATION")
							&& csvRecord.get(0).toString().contains("@")) {
						for (int i = 0; i < multiRow.size(); i++) {

							for (int j = 0; j < size1; j++) {
								if (j == 0) {
									singleRow.add(multiRow.get(i).get(j));
								} else if (j == 1) {
									singleRow1.add(multiRow.get(i).get(j));
								} else if (j == 2) {
									singleRow2.add(multiRow.get(i).get(j));
								} else if (j == 3) {
									singleRow3.add(multiRow.get(i).get(j));
								} else if (j == 4) {
									singleRow4.add(multiRow.get(i).get(j));
								} else if (j == 5) {
									singleRow5.add(multiRow.get(i).get(j));
								} else if (j == 6) {
									singleRow6.add(multiRow.get(i).get(j));
								} else if (j == 7) {
									singleRow7.add(multiRow.get(i).get(j));
								} else if (j == 8) {
									singleRow8.add(multiRow.get(i).get(j));
								} else if (j == 9) {
									singleRow9.add(multiRow.get(i).get(j));
								} else if (j == 10) {
									singleRow10.add(multiRow.get(i).get(j));
								} else if (j == 11) {
									singleRow11.add(multiRow.get(i).get(j));
								} else if (j == 12) {
									singleRow12.add(multiRow.get(i).get(j));
								} else if (j == 13) {
									singleRow13.add(multiRow.get(i).get(j));
								} else if (j == 14) {
									singleRow14.add(multiRow.get(i).get(j));
								} else if (j == 15) {
									singleRow15.add(multiRow.get(i).get(j));
								} else if (j == 16) {
									singleRow16.add(multiRow.get(i).get(j));
								} else if (j == 17) {
									singleRow17.add(multiRow.get(i).get(j));
								}
							}

						}
						map12.put("State", singleRow);
						map12.put("Cell Num", singleRow1);
						map12.put("Parent Cell Number", singleRow2);
						map12.put("NB IoT PCI", singleRow3);
						map12.put("Operation Mode Info", singleRow4);
						map12.put("NB IoT TAC", singleRow5);
						map12.put("Use Parent PCI for Guard-band", singleRow6);
						map12.put("Initial Nprach", singleRow7);
						map12.put("Nprach Start Time CL0", singleRow8);
						map12.put("Nprach Subcarrier Offset CL0", singleRow9);
						map12.put("Nprach Start Time CL1", singleRow10);
						map12.put("Nprach Subcarrier Offset CL1", singleRow11);
						map12.put("Nprach Start Time CL2", singleRow12);
						map12.put("Nprach Subcarrier Offset CL2", singleRow13);
						map12.put("Guard Band", singleRow14);
						map12.put("Avoid UL Interfering", singleRow15);
						map12.put("DL RB", singleRow16);
						map12.put("UL RB", singleRow17);
						String header1 = "@NB_IOT_CELL_INFORMATION";
						csvToMapForMultiple.put(header1, map12);
						multiRow.clear();
					}

					if (currentHeader.equalsIgnoreCase("@NON_ANCHOR_NB_IOT_CELL_INFORMATION")
							&& csvRecord.getRecordNumber() > newRowNum) {
						for (int i = 0; i < csvRecord.size(); i++) {
							singleRow.add(csvRecord.get(i));
							size1 = csvRecord.size();
						}
						multiRow.add(singleRow);

					} else if (prevHeader.equalsIgnoreCase("@NON_ANCHOR_NB_IOT_CELL_INFORMATION")
							&& csvRecord.get(0).toString().contains("@")) {
						for (int i = 0; i < multiRow.size(); i++) {

							for (int j = 0; j < size1; j++) {
								if (j == 0) {
									singleRow.add(multiRow.get(i).get(j));
								} else if (j == 1) {
									singleRow1.add(multiRow.get(i).get(j));
								} else if (j == 2) {
									singleRow2.add(multiRow.get(i).get(j));
								} else if (j == 3) {
									singleRow3.add(multiRow.get(i).get(j));
								} else if (j == 4) {
									singleRow4.add(multiRow.get(i).get(j));
								} else if (j == 5) {
									singleRow5.add(multiRow.get(i).get(j));
								}

							}
						}
						map12.put("State", singleRow);
						map12.put("Cell Num", singleRow1);
						map12.put("Operation Mode Info", singleRow2);
						map12.put("Guard Band", singleRow3);
						map12.put("DL RB", singleRow4);
						map12.put("UL RB", singleRow5);
						String header1 = "@NON_ANCHOR_NB_IOT_CELL_INFORMATION";
						csvToMapForMultiple.put(header1, map12);
						multiRow.clear();
					}
					if (currentHeader.equalsIgnoreCase("@CHANNEL_BOARD_INFORMATION")
							&& csvRecord.getRecordNumber() > newRowNum) {
						for (int i = 0; i < csvRecord.size(); i++) {
							singleRow.add(csvRecord.get(i));
							size1 = csvRecord.size();
						}
						multiRow.add(singleRow);

					} else if (prevHeader.equalsIgnoreCase("@CHANNEL_BOARD_INFORMATION")
							&& csvRecord.get(0).toString().contains("@")) {
						for (int i = 0; i < multiRow.size(); i++) {

							for (int j = 0; j < size1; j++) {
								if (j == 0) {
									singleRow.add(multiRow.get(i).get(j));
								} else if (j == 1) {
									singleRow1.add(multiRow.get(i).get(j));
								} else if (j == 2) {
									singleRow2.add(multiRow.get(i).get(j));
								} else if (j == 3) {
									singleRow3.add(multiRow.get(i).get(j));
								}

							}
						}
						map12.put("State", singleRow);
						map12.put("Unit Type", singleRow1);
						map12.put("Unit ID", singleRow2);
						map12.put("Board Type", singleRow3);
						String header1 = "@CHANNEL_BOARD_INFORMATION";
						csvToMapForMultiple.put(header1, map12);
						multiRow.clear();

					}
					if (currentHeader.equalsIgnoreCase("@CPRI_PORT_INFORMATION")
							&& csvRecord.getRecordNumber() > newRowNum) {
						for (int i = 0; i < csvRecord.size(); i++) {
							singleRow.add(csvRecord.get(i));
							size1 = csvRecord.size();
						}
						multiRow.add(singleRow);

					} else if (prevHeader.equalsIgnoreCase("@CPRI_PORT_INFORMATION")
							&& csvRecord.get(0).toString().contains("@")) {
						for (int i = 0; i < multiRow.size(); i++) {

							for (int j = 0; j < size1; j++) {
								if (j == 0) {
									singleRow.add(multiRow.get(i).get(j));
								} else if (j == 1) {
									singleRow1.add(multiRow.get(i).get(j));
								} else if (j == 2) {
									singleRow2.add(multiRow.get(i).get(j));
								} else if (j == 3) {
									singleRow3.add(multiRow.get(i).get(j));
								} else if (j == 4) {
									singleRow4.add(multiRow.get(i).get(j));
								} else if (j == 5) {
									singleRow5.add(multiRow.get(i).get(j));
								} else if (j == 6) {
									singleRow6.add(multiRow.get(i).get(j));
								}

							}
						}
						map12.put("State", singleRow);
						map12.put("Unit Type", singleRow1);
						map12.put("Unit ID", singleRow2);
						map12.put("Port ID", singleRow3);
						map12.put("Connection Type", singleRow4);
						map12.put("FSU Inter Node ID", singleRow5);
						map12.put("FSU DU CPRI Port ID", singleRow6);
						String header1 = "@CPRI_PORT_INFORMATION";
						csvToMapForMultiple.put(header1, map12);
						multiRow.clear();

					}
					if (currentHeader.equalsIgnoreCase("@RU_INFORMATION") && csvRecord.getRecordNumber() > newRowNum) {
						for (int i = 0; i < csvRecord.size(); i++) {
							singleRow.add(csvRecord.get(i));
							size1 = csvRecord.size();
						}
						multiRow.add(singleRow);

					} else if (prevHeader.equalsIgnoreCase("@RU_INFORMATION")
							&& csvRecord.get(0).toString().contains("@")) {
						for (int i = 0; i < multiRow.size(); i++) {

							for (int j = 0; j < size1; j++) {
								if (j == 0) {
									singleRow.add(multiRow.get(i).get(j));
								} else if (j == 1) {
									singleRow1.add(multiRow.get(i).get(j));
								} else if (j == 2) {
									singleRow2.add(multiRow.get(i).get(j));
								} else if (j == 3) {
									singleRow3.add(multiRow.get(i).get(j));
								} else if (j == 4) {
									singleRow4.add(multiRow.get(i).get(j));
								} else if (j == 5) {
									singleRow5.add(multiRow.get(i).get(j));
								} else if (j == 6) {
									singleRow6.add(multiRow.get(i).get(j));
								} else if (j == 7) {
									singleRow7.add(multiRow.get(i).get(j));
								} else if (j == 8) {
									singleRow8.add(multiRow.get(i).get(j));
								} else if (j == 9) {
									singleRow9.add(multiRow.get(i).get(j));
								} else if (j == 10) {
									singleRow10.add(multiRow.get(i).get(j));
								} else if (j == 11) {
									singleRow11.add(multiRow.get(i).get(j));
								} else if (j == 12) {
									singleRow12.add(multiRow.get(i).get(j));
								} else if (j == 13) {
									singleRow13.add(multiRow.get(i).get(j));
								} else if (j == 14) {
									singleRow14.add(multiRow.get(i).get(j));
								} else if (j == 15) {
									singleRow15.add(multiRow.get(i).get(j));
								} else if (j == 16) {
									singleRow16.add(multiRow.get(i).get(j));
								}
							}

						}
						map12.put("State", singleRow);
						map12.put("RU Conf", singleRow1);
						map12.put("RU Port", singleRow2);
						map12.put("Connected DU Board Type", singleRow3);
						map12.put("RU Type", singleRow4);
						map12.put("Start Earfcn1", singleRow5);
						map12.put("Start Earfcn2", singleRow6);
						map12.put("Serial Number", singleRow7);
						map12.put("Azimuth", singleRow8);
						map12.put("Beamwidth", singleRow9);
						map12.put("Fcc ID", singleRow10);
						map12.put("Call Sign", singleRow11);
						map12.put("CBSD Category", singleRow12);
						map12.put("X Pole Antenna", singleRow13);
						map12.put("Antenna Gain dBi", singleRow14);
						map12.put("Cable Loss", singleRow15);
						map12.put("Accuracy Margin dB", singleRow16);

						String header1 = "@RU_INFORMATION";
						csvToMapForMultiple.put(header1, map12);
						multiRow.clear();

					}
					if (currentHeader.equalsIgnoreCase("@ADDITIONAL_CPRI_INFORMATION")
							&& csvRecord.getRecordNumber() > newRowNum) {
						for (int i = 0; i < csvRecord.size(); i++) {
							singleRow.add(csvRecord.get(i));
							size1 = csvRecord.size();
						}
						multiRow.add(singleRow);

					} else if (prevHeader.equalsIgnoreCase("@ADDITIONAL_CPRI_INFORMATION")
							&& csvRecord.get(0).toString().contains("@")) {
						for (int i = 0; i < multiRow.size(); i++) {

							for (int j = 0; j < size1; j++) {
								if (j == 0) {
									singleRow.add(multiRow.get(i).get(j));
								} else if (j == 1) {
									singleRow1.add(multiRow.get(i).get(j));
								} else if (j == 2) {
									singleRow2.add(multiRow.get(i).get(j));
								} else if (j == 3) {
									singleRow3.add(multiRow.get(i).get(j));
								} else if (j == 4) {
									singleRow4.add(multiRow.get(i).get(j));
								} else if (j == 5) {
									singleRow5.add(multiRow.get(i).get(j));
								}

							}
						}
						map12.put("State", singleRow);
						map12.put("RU Conf", singleRow1);
						map12.put("Connected DU Board Type", singleRow2);
						map12.put("Additional Board ID", singleRow3);
						map12.put("Additional Port ID", singleRow4);
						map12.put("RU Additional Port ID", singleRow5);
						String header1 = "@ADDITIONAL_CPRI_INFORMATION";
						csvToMapForMultiple.put(header1, map12);
						multiRow.clear();

					}
					if (currentHeader.equalsIgnoreCase("@RU_ANTENNA_PORT_INFORMATION")
							&& csvRecord.getRecordNumber() > newRowNum) {
						for (int i = 0; i < csvRecord.size(); i++) {
							singleRow.add(csvRecord.get(i));
							size1 = csvRecord.size();
						}
						multiRow.add(singleRow);

					} else if (prevHeader.equalsIgnoreCase("@RU_ANTENNA_PORT_INFORMATION")
							&& csvRecord.get(0).toString().contains("@")) {

						for (int i = 0; i < multiRow.size(); i++) {

							for (int j = 0; j < size1; j++) {
								if (j == 0) {
									singleRow.add(multiRow.get(i).get(j));
								} else if (j == 1) {
									singleRow1.add(multiRow.get(i).get(j));
								} else if (j == 2) {
									singleRow2.add(multiRow.get(i).get(j));
								} else if (j == 3) {
									singleRow3.add(multiRow.get(i).get(j));
								} else if (j == 4) {
									singleRow4.add(multiRow.get(i).get(j));
								}

							}
						}
						map12.put("State", singleRow);
						map12.put("RU Conf", singleRow1);
						map12.put("Connected DU Board Type", singleRow2);
						map12.put("Antenna Port ID", singleRow3);
						map12.put("Antenna Cable Length", singleRow4);
						String header1 = "@RU_ANTENNA_PORT_INFORMATION";
						csvToMapForMultiple.put(header1, map12);
						multiRow.clear();

					}

					if (currentHeader.equalsIgnoreCase("@RU_GROUP_INFORMATION")
							&& csvRecord.getRecordNumber() > newRowNum) {
						for (int i = 0; i < csvRecord.size(); i++) {
							singleRow.add(csvRecord.get(i));
							size1 = csvRecord.size();
						}
						multiRow.add(singleRow);

					} else if (prevHeader.equalsIgnoreCase("@RU_GROUP_INFORMATION")
							&& csvRecord.get(0).toString().contains("@")) {
						for (int i = 0; i < multiRow.size(); i++) {

							for (int j = 0; j < size1; j++) {
								if (j == 0) {
									singleRow.add(multiRow.get(i).get(j));
								} else if (j == 1) {
									singleRow1.add(multiRow.get(i).get(j));
								} else if (j == 2) {
									singleRow2.add(multiRow.get(i).get(j));
								}

							}
						}
						map12.put("State", singleRow);
						map12.put("RU Conf", singleRow1);
						map12.put("Group ID", singleRow2);
						String header1 = "@RU_GROUP_INFORMATION";
						csvToMapForMultiple.put(header1, map12);
						multiRow.clear();

					}
					if (currentHeader.equalsIgnoreCase("@DSP_INFORMATION") && csvRecord.getRecordNumber() > newRowNum) {
						for (int i = 0; i < csvRecord.size(); i++) {
							singleRow.add(csvRecord.get(i));
							size1 = csvRecord.size();
						}
						multiRow.add(singleRow);

					} else if (prevHeader.equalsIgnoreCase("@DSP_INFORMATION")
							&& csvRecord.get(0).toString().contains("@")) {
						for (int i = 0; i < multiRow.size(); i++) {

							for (int j = 0; j < size1; j++) {
								if (j == 0) {
									singleRow.add(multiRow.get(i).get(j));
								} else if (j == 1) {
									singleRow1.add(multiRow.get(i).get(j));
								} else if (j == 2) {
									singleRow2.add(multiRow.get(i).get(j));
								} else if (j == 3) {
									singleRow3.add(multiRow.get(i).get(j));
								} else if (j == 4) {
									singleRow4.add(multiRow.get(i).get(j));
								}

							}
						}
						map12.put("State", singleRow);
						map12.put("Unit Type", singleRow1);
						map12.put("Unit ID", singleRow2);
						map12.put("DSP ID", singleRow3);
						map12.put("Optic Distance", singleRow4);
						String header1 = "@DSP_INFORMATION";
						csvToMapForMultiple.put(header1, map12);

						JSONObject2 = gsonObject.toJson(csvToMapForMultiple).substring(1,
								gsonObject.toJson(csvToMapForMultiple).length() - 1);
						System.out.println(JSONObject2);

					}

					if (currentHeader.equalsIgnoreCase("@CBRS_CHANNEL_INFORMATION")
							&& csvRecord.getRecordNumber() > newRowNum) {
						String header1 = "@CBRS_CHANNEL_INFORMATION";
						map1.put("State", csvRecord.get(0));
						map1.put("Sector ID", "");
						map1.put("Carrier ID", "");
						map1.put("Black Listed Channel", "");
						csvToMapForSingle.put(header1, map1);
						JSONObject1 = gsonObject.toJson(csvToMapForSingle).substring(1,
								gsonObject.toJson(csvToMapForSingle).length() - 1);

					}

				}
				JSONObject = "{" + JSONObject2 + "," + JSONObject1 + "}";
				char c1 = '"';
				String v1 = Character.toString(c1);
				String v3 = "\\\\" + Character.toString(c1);
				JSONObject = JSONObject.replaceAll(Character.toString(c1), v3).replaceAll("]", "\\\\]").replace("[",
						"\\[");
				System.out.println(JSONObject);

			}

			catch (Exception e) {
				e.printStackTrace();
			}

		}
		return JSONObject;
	}

	@SuppressWarnings("unchecked")
	public static JSONObject createCmdSysFromBatch(String batchFilePath, String cmdSysFilePath,
			ProgramTemplateEntity programTemplateEntity, String ciqFileName, String enbId, String endName) {
		StringBuilder sb = new StringBuilder();
		JSONObject resultMap = new JSONObject();
		BufferedReader bufferedReader = null;
		StringBuilder cmdLine = new StringBuilder();
		FileReader fileReader = null;
		String cmdTimeIntervel = "";
		String line = "";
		String cmdPart1 = "";
		String cmdPart2 = "";
		String cmd = "";
		String prompt = "$";
		/*
		 * String cmdTimeIntervel=""; String terminalName = ""; String prompt = "";
		 */
		try {
			logger.info("createCmdSysFromBatch() called batchFilePath: " + batchFilePath + ", cliFilePath: "
					+ cmdSysFilePath);
			/*
			 * JSONObject objData =
			 * CommonUtil.parseDataToJSON(programTemplateEntity.getValue()); objData =
			 * (JSONObject) objData.get("connLocation"); objData = (JSONObject)
			 * objData.get("NE"); JSONArray terminals = (JSONArray)
			 * objData.get("terminals");
			 * 
			 * for(int i=0;i<terminals.size(); i++){ objData = (JSONObject)
			 * terminals.get(i); terminalName = (String) objData.get("terminalName");
			 * if(terminalName.equalsIgnoreCase("cli")){ prompt=(String)
			 * objData.get("prompt"); break; } }
			 */

			String cmdSysEscapeCmds = LoadPropertyFiles.getInstance().getProperty("cmdSysEscapeCmds");
			String[] escapeCmds = null;
			if (CommonUtil.isValidObject(cmdSysEscapeCmds) && StringUtils.isNotEmpty(cmdSysEscapeCmds)) {
				escapeCmds = cmdSysEscapeCmds.split(",");
			}
			sb.append("send \". /home/lsm/.profile\\r\"\n");
			sb.append("\n");

			fileReader = new FileReader(batchFilePath);
			bufferedReader = new BufferedReader(fileReader);
			while ((line = bufferedReader.readLine()) != null) {
				if (!line.trim().isEmpty() && !line.contains("#")) {
					if (!CommonUtil.isValidObject(escapeCmds)
							|| !Arrays.stream(escapeCmds).anyMatch(line.trim()::equals)) {
						cmdTimeIntervel = "";
						cmdLine.setLength(0);
						cmdLine.append("cmd_sys eNB_" + enbId + " ");
						if (StringUtils.isNotEmpty(line) && line.contains("::::")) {
							String[] cmds = line.split("::::");
							if (CommonUtil.isValidObject(cmds) && cmds.length > 1) {
								cmdPart1 = cmds[0].replace(endName, "");
								if (StringUtils.isNotEmpty(cmdPart1) && cmdPart1.contains(":")) {
									cmdPart1 = cmdPart1.substring(0, cmdPart1.indexOf(":") + 1);
								}
								cmdPart2 = cmds[1];
								if (CommonUtil.isValidObject(cmdPart2) && cmdPart2.contains(";")) {
									String timeIntervel = cmdPart2.substring(cmdPart2.lastIndexOf(";") + 1,
											cmdPart2.length());
									if (CommonUtil.isValidObject(timeIntervel)
											&& StringUtils.isNotEmpty(timeIntervel)) {
										cmdTimeIntervel = timeIntervel.trim();
										cmdPart2 = cmdPart2.substring(0, cmdPart2.indexOf(";") + 1);
									}
								}
							}
							cmdLine.append(cmdPart1);
							cmdLine.append(cmdPart2);
						} else {
							if (CommonUtil.isValidObject(line) && line.contains(";")) {
								String timeIntervel = line.substring(line.lastIndexOf(";") + 1, line.length());
								if (CommonUtil.isValidObject(timeIntervel) && StringUtils.isNotEmpty(timeIntervel)) {
									cmdTimeIntervel = timeIntervel.trim();
									line = line.substring(0, line.indexOf(";") + 1);
								}
							}
							cmdLine.append(line);
						}
						cmd = cmdLine.toString();
						cmd = cmd.replaceAll("\"", "\\\\\"");
						if (CommonUtil.isValidObject(cmd) && StringUtils.isNotEmpty(cmd) && cmd.length() > 0) {
							sb.append("expect \"" + prompt + "\" \n");
							sb.append("send \"" + cmd + "\\r\"\n");
							if (CommonUtil.isValidObject(cmdTimeIntervel) && StringUtils.isNotEmpty(cmdTimeIntervel)
									&& cmdTimeIntervel.length() > 0) {
								sb.append("expect \"" + prompt + "\" \n");
								sb.append("send \"sleep " + cmdTimeIntervel + "\\r\"\n");
							}
						}
					}
				} else if (line.trim().isEmpty()) {
					sb.append("\n");
				}

			}
			resultMap = saveConfiguredFileContent(cmdSysFilePath, sb.toString());
		} catch (Exception e) {
			logger.error("Failed in createCmdSysFromBatch() in CommonUtil", e);
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason",
					GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GENERATE_CMD_SYS_FILE));
		}
		return resultMap;
	}

	public static StringBuilder expectForCli(StringBuilder sb) {
		try {
			sb.append("expect {\n");
			sb.append("\"LOGIN PASSWORD :\"\n");
			sb.append("        { send \"ROOT\\r\"\n");
			sb.append("          expect {\n");
			sb.append("                        \"REALLY?*(Y/N)*\"\n");
			sb.append("                        { send \"Y\\r\"\n");
			sb.append("                          expect {\n");
			sb.append("                                        \"RESULT = NOK\" { }\n");
			sb.append("                                        \"RESULT = OK\" { }\n");
			sb.append("                                        \" : COMPLD*;\" { }\n");
			sb.append("                                 }\n");
			sb.append("                        }\n");
			sb.append("                        \"RESULT = NOK\" { }\n");
			sb.append("                        \"RESULT = OK\" { }\n");
			sb.append("                        \" : COMPLD*;\" { }\n");
			sb.append("                 }\n");
			sb.append("        }\n");
			sb.append("\"REALLY?*(Y/N)*\"\n");
			sb.append("        { send \"Y\\r\"\n");
			sb.append("          expect {\n");
			sb.append("                        \"RESULT = NOK\" { }\n");
			sb.append("                        \"RESULT = OK\" { }\n");
			sb.append("                        \" : COMPLD*;\" { }\n");
			sb.append("                }\n");
			sb.append("        }\n");
			sb.append("\"INH-COUNT =\" { }\n");
			sb.append("\"ALW-COUNT =\" { }\n");
			sb.append("\"RESULT = NOK\" { }\n");
			sb.append("\"RESULT = OK\" { }\n");
			sb.append("\" : COMPLD*;\" { }\n");
			sb.append("\"CSR*#\" { }\n");
			sb.append("}\n");
		} catch (Exception e) {
			logger.error("Failed in expectForCli() in CommonUtil", e);
		}
		return sb;
	}

	@SuppressWarnings("unchecked")
	public static JSONObject createCliFromBatch(String batchFilePath, String cliFilePath, String neVersion,
			String relVersion, ProgramTemplateEntity programTemplateEntity, String ciqFileName, String enbId,
			String endName) {
		StringBuilder sb = new StringBuilder();
		JSONObject resultMap = new JSONObject();
		BufferedReader bufferedReader = null;
		StringBuilder cmdLine = new StringBuilder();
		FileReader fileReader = null;
		String line = "";
		String cmdPart1 = "";
		String cmdPart2 = "";
		String cmd = "";
		String cmdTimeIntervel = "";
		String terminalName = "";
		String prompt = "*";
		String termUsername = "root";
		String termPassword = "root";
		try {
			logger.info("createCliFromBatch() went inside if : " + batchFilePath + ", cliFilePath: " + cliFilePath
					+ ", neVersion: " + neVersion + ", relVersion: " + relVersion);
			JSONObject objData = CommonUtil.parseDataToJSON(programTemplateEntity.getValue());
			objData = (JSONObject) objData.get("connLocation");
			objData = (JSONObject) objData.get("NE");
			JSONArray terminals = (JSONArray) objData.get("terminals");

			for (int i = 0; i < terminals.size(); i++) {
				objData = (JSONObject) terminals.get(i);
				terminalName = (String) objData.get("terminalName");
				if (terminalName.equalsIgnoreCase("cli")) {
					prompt = (String) objData.get("prompt");
					termUsername = (String) objData.get("termUsername");
					termPassword = (String) objData.get("termPassword");
					break;
				}
			}

			logger.info("createCliFromBatch() prompt: " + prompt);

			sb.append("send \"/pkg/" + neVersion + "/ENB/" + relVersion + "/bin/cli.ohm\\r\"\n");
			sb.append("\n");

			sb.append("expect \"USERNAME :\"\n");
			sb.append("send \"" + termUsername + "\\r\"\n");
			sb.append("expect \"PASSWORD :\"\n");
			sb.append("send \"" + termPassword + "\\r\"\n");
			sb.append("expect \"" + prompt.replace("enbname", endName).replace("enbId", enbId) + "\"" + " \n\n");
			fileReader = new FileReader(batchFilePath);
			bufferedReader = new BufferedReader(fileReader);

			if (!(batchFilePath.contains(Constants.PRECHECK_CSR_FILE_NAME)
					|| batchFilePath.contains(Constants.PRECHECK_BSM_FILE_NAME)
					|| batchFilePath.contains(Constants.POSTCHECK_CSR_FILE_NAME)
					|| batchFilePath.contains(Constants.POSTCHECK_BSM_FILE_NAME))) {
				sb.append("send \"" + Constants.SPT_MIMO_INH_CMD + "\\r\"\n");
				sb = expectForCli(sb);
				sb.append("sleep " + Constants.SPT_MIMO_INH_CMD_SLEEP + "\n\n");
			}
			while ((line = bufferedReader.readLine()) != null) {
				if (!line.trim().isEmpty() && !line.contains("#")) {
					cmdTimeIntervel = "";
					cmdLine.setLength(0);
					if (StringUtils.isNotEmpty(line) && line.contains("::::")) {
						String[] cmds = line.split("::::");
						if (CommonUtil.isValidObject(cmds) && cmds.length > 1) {
							cmdPart1 = cmds[0].replace(endName, "");
							if (StringUtils.isNotEmpty(cmdPart1) && cmdPart1.contains(":")) {
								cmdPart1 = cmdPart1.substring(0, cmdPart1.indexOf(":") + 1);
							}
							cmdPart2 = cmds[1];
							if (CommonUtil.isValidObject(cmdPart2) && cmdPart2.contains(";")) {
								String timeIntervel = cmdPart2.substring(cmdPart2.lastIndexOf(";") + 1,
										cmdPart2.length());
								if (CommonUtil.isValidObject(timeIntervel) && StringUtils.isNotEmpty(timeIntervel)) {
									cmdTimeIntervel = timeIntervel.trim();
									cmdPart2 = cmdPart2.substring(0, cmdPart2.indexOf(";") + 1);
								}
							}
						}
						cmdLine.append(cmdPart1);
						cmdLine.append(cmdPart2);
					} else {
						if (CommonUtil.isValidObject(line) && line.contains(";")) {
							String timeIntervel = line.substring(line.lastIndexOf(";") + 1, line.length());
							if (CommonUtil.isValidObject(timeIntervel) && StringUtils.isNotEmpty(timeIntervel)) {
								cmdTimeIntervel = timeIntervel.trim();
								line = line.substring(0, line.indexOf(";") + 1);
							}
						}
						cmdLine.append(line);
					}
					cmd = cmdLine.toString();
					cmd = cmd.replaceAll("\"", "\\\\\"");
					if (CommonUtil.isValidObject(cmd) && StringUtils.isNotEmpty(cmd) && cmd.length() > 0) {
						sb.append("send \"" + cmd + "\\r\"\n");
						sb = expectForCli(sb);
						if (CommonUtil.isValidObject(cmdTimeIntervel) && StringUtils.isNotEmpty(cmdTimeIntervel)
								&& cmdTimeIntervel.length() > 0) {
							sb.append("sleep " + cmdTimeIntervel + "\n\n");
						}
					}
				} else if (line.trim().isEmpty()) {
					// sb.append("\n");
				}
			}
			if (!(batchFilePath.contains(Constants.PRECHECK_CSR_FILE_NAME)
					|| batchFilePath.contains(Constants.PRECHECK_BSM_FILE_NAME)
					|| batchFilePath.contains(Constants.POSTCHECK_CSR_FILE_NAME)
					|| batchFilePath.contains(Constants.POSTCHECK_BSM_FILE_NAME))) {
				sb.append("send \"" + Constants.SPT_ALW_MSG_CMD + "\\r\"\n");
				sb = expectForCli(sb);
				sb.append("sleep " + Constants.SPT_ALW_MSG_CMD_SLEEP + "\n\n");
			}
			sb.append("send \"exit\\r\"\n");
			resultMap = saveConfiguredFileContent(cliFilePath, sb.toString());
		} catch (Exception e) {
			logger.error("Failed in createCliFromBatch() in CommonUtil", e);
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GENERATE_CLI_FILE));
		}
		return resultMap;
	}

	@SuppressWarnings("unchecked")
	public static JSONObject createBashFromVbs(String vbsFilePath, String bashFilePath,
			ProgramTemplateEntity programTemplateEntity, String ciqFileName, String enbId, String endName) {
		StringBuilder sb = new StringBuilder();
		JSONObject resultMap = new JSONObject();
		BufferedReader bufferedReader = null;
		FileReader fileReader = null;
		String line = "";
		String cmd = "";
		String cmdTimeIntervel = "";
		String terminalName = "";
		String prompt = "*";
		try {
			logger.info("createBashFromVbs() called vbsFilePath: " + vbsFilePath + ", bashFilePath: " + bashFilePath);
			JSONObject objData = CommonUtil.parseDataToJSON(programTemplateEntity.getValue());
			objData = (JSONObject) objData.get("connLocation");
			objData = (JSONObject) objData.get("NE");
			JSONArray terminals = (JSONArray) objData.get("terminals");

			for (int i = 0; i < terminals.size(); i++) {
				objData = (JSONObject) terminals.get(i);
				terminalName = (String) objData.get("terminalName");
				if (terminalName.equalsIgnoreCase("bash")) {
					prompt = (String) objData.get("prompt");
					break;
				}
			}

			logger.info("createBashFromVbs() prompt: " + prompt);

			fileReader = new FileReader(vbsFilePath);
			bufferedReader = new BufferedReader(fileReader);
			while ((line = bufferedReader.readLine()) != null) {
				if (!line.trim().isEmpty()) {
					if (line.contains(Constants.BSM_SEND_KEY)) {
						cmd = StringUtils.substringAfterLast(line, Constants.BSM_SEND_KEY);
						cmd = cmd.replaceAll("\"\"", "\"");
						cmd = StringUtils.substringAfter(cmd, "\"");
						cmd = StringUtils.substringBeforeLast(cmd, "\"");
						cmd = cmd.replaceAll("\"", "\\\\\"");
						// sb.append("expect \"" + prompt.replace("enbname", endName).replace("enbId",
						// enbId) + "\"" + " \n");
						sb.append("send \"" + cmd + "\\r\"\n");
					} else if (line.contains(Constants.BSM_SLEEP_KEY)) {
						cmdTimeIntervel = StringUtils.substringAfterLast(line, Constants.BSM_SLEEP_KEY);
						if (CommonUtil.isValidObject(cmdTimeIntervel) && StringUtils.isNotEmpty(cmdTimeIntervel)
								&& cmdTimeIntervel.length() > 0) {
							// sb.append("expect \"" + prompt.replace("enbname", endName).replace("enbId",
							// enbId) + "\"" + " \n");
							sb.append("send \"sleep " + cmdTimeIntervel.trim() + "\\r\"\n");
						}
					}
				} else if (line.trim().isEmpty()) {
					sb.append("\n");
				}
			}
			resultMap = saveConfiguredFileContent(bashFilePath, sb.toString());
		} catch (Exception e) {
			logger.error("Failed in createBashFromVbs() in CommonUtil", e);
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason",
					GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GENERATE_BASH_FILE));
		}
		return resultMap;
	}

	@SuppressWarnings("unchecked")
	public static JSONObject createCliFromVbs(String vbsFilePath, String cliFilePath, String neVersion,
			String relVersion, ProgramTemplateEntity programTemplateEntity, String ciqFileName, String enbId,
			String endName) {
		StringBuilder sb = new StringBuilder();
		JSONObject resultMap = new JSONObject();
		BufferedReader bufferedReader = null;
		FileReader fileReader = null;
		String line = "";
		String cmd = "";
		String cmdTimeIntervel = "";
		String terminalName = "";
		String prompt = "*";
		String termUsername = "root";
		String termPassword = "root";
		try {
			logger.info("createCliFromVbs() called vbsFilePath: " + vbsFilePath + ", cliFilePath: " + cliFilePath);
			JSONObject objData = CommonUtil.parseDataToJSON(programTemplateEntity.getValue());
			objData = (JSONObject) objData.get("connLocation");
			objData = (JSONObject) objData.get("NE");
			JSONArray terminals = (JSONArray) objData.get("terminals");

			for (int i = 0; i < terminals.size(); i++) {
				objData = (JSONObject) terminals.get(i);
				terminalName = (String) objData.get("terminalName");
				if (terminalName.equalsIgnoreCase("cli")) {
					prompt = (String) objData.get("prompt");
					termUsername = (String) objData.get("termUsername");
					termPassword = (String) objData.get("termPassword");
					break;
				}
			}

			logger.info("createCliFromVbs() prompt: " + prompt);
			if (StringUtils.isNotEmpty(neVersion) && StringUtils.isNotEmpty(relVersion)) {
				sb.append("send \"/pkg/" + neVersion + "/ENB/" + relVersion + "/bin/cli.ohm\\r\"\n");
				sb.append("\n");

				sb.append("expect \"USERNAME :\"\n");
				sb.append("send \"" + termUsername + "\\r\"\n");
				sb.append("expect \"PASSWORD :\"\n");
				sb.append("send \"" + termPassword + "\\r\"\n");
				sb.append("expect \"" + prompt.replace("enbname", endName).replace("enbId", enbId) + "\"" + " \n\n");
			} else {
				sb.append("\n");
			}

			fileReader = new FileReader(vbsFilePath);
			bufferedReader = new BufferedReader(fileReader);
			if (!(vbsFilePath.contains(Constants.PRECHECK_CSR_FILE_NAME)
					|| vbsFilePath.contains(Constants.PRECHECK_BSM_FILE_NAME)
					|| vbsFilePath.contains(Constants.POSTCHECK_CSR_FILE_NAME)
					|| vbsFilePath.contains(Constants.POSTCHECK_BSM_FILE_NAME))) {
				sb.append("send \"" + Constants.SPT_MIMO_INH_CMD + "\\r\"\n");
				sb = expectForCli(sb);
				sb.append("sleep " + Constants.SPT_MIMO_INH_CMD_SLEEP + "\n\n");
			}
			while ((line = bufferedReader.readLine()) != null) {
				if (!line.trim().isEmpty()) {
					if (line.contains(Constants.BSM_SEND_KEY)) {
						cmd = StringUtils.substringAfterLast(line, Constants.BSM_SEND_KEY);
						cmd = cmd.replaceAll("\"\"", "\"");
						cmd = StringUtils.substringAfter(cmd, "\"");
						cmd = StringUtils.substringBeforeLast(cmd, "\"");
						cmd = cmd.replaceAll("\"", "\\\\\"");

						sb.append("send \"" + cmd + "\\r\"\n");
						sb = expectForCli(sb);
					} else if (line.contains(Constants.BSM_SLEEP_KEY)) {
						cmdTimeIntervel = StringUtils.substringAfterLast(line, Constants.BSM_SLEEP_KEY);
						if (CommonUtil.isValidObject(cmdTimeIntervel) && StringUtils.isNotEmpty(cmdTimeIntervel)
								&& cmdTimeIntervel.length() > 0) {
							int sleepTime = Integer.parseInt(cmdTimeIntervel);
							if (sleepTime > 1000) {
								sleepTime = sleepTime / 1000;
							}
							sb.append("sleep " + sleepTime + "\n\n");
						}
					}
				} else if (line.trim().isEmpty()) {
					// sb.append("\n");
				}
			}
			if (!(vbsFilePath.contains(Constants.PRECHECK_CSR_FILE_NAME)
					|| vbsFilePath.contains(Constants.PRECHECK_BSM_FILE_NAME)
					|| vbsFilePath.contains(Constants.POSTCHECK_CSR_FILE_NAME)
					|| vbsFilePath.contains(Constants.POSTCHECK_BSM_FILE_NAME))) {
				sb.append("send \"" + Constants.SPT_ALW_MSG_CMD + "\\r\"\n");
				sb = expectForCli(sb);
				sb.append("sleep " + Constants.SPT_ALW_MSG_CMD_SLEEP + "\n\n");
			}

			sb.append("send \"exit\\r\"\n");
			resultMap = saveConfiguredFileContent(cliFilePath, sb.toString());
		} catch (Exception e) {
			logger.error("Failed in createCliFromVbs() in CommonUtil", e);
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GENERATE_CLI_FILE));
		}
		return resultMap;
	}

	@SuppressWarnings("unchecked")
	public static JSONObject createVbsFromCmds(String vbsFilePath, List<GrowConstantsEntity> resultMapForConstants,
			String vbsSleep, String btsId) {
		JSONObject resultMap = new JSONObject();
		StringBuilder sb = new StringBuilder();
		try {
			if (StringUtils.isEmpty(btsId)) {
				btsId = "1327";
			}
			/*
			 * TreeMap<String, String> treeMap = new TreeMap<String, String>();
			 * treeMap.putAll(resultMapForConstants);
			 */

			String date = DateUtil.dateToString(new Date(), Constants.VBS_DD_MM_YYYY);
			logger.info("createVbsFromCmds() called vbsFilePath: " + vbsFilePath + ", vbsSleep: " + vbsSleep
					+ ", btsId: " + btsId + ", date: " + date);
			sb.append(Constants.VBS_SUB_MAIN + "\n");
			for (GrowConstantsEntity resultMapForConstant : resultMapForConstants) {
				String cmd = resultMapForConstant.getValue().toString();
				logger.info("createVbsFromCmds() called cmd: " + cmd);
				cmd = cmd.replaceAll(Constants.VBS_BTS_ID, btsId).replaceAll(Constants.VBS_CSR_DATE, date);
				logger.info("createVbsFromCmds() called cmd after replace: " + cmd);
				sb.append(Constants.VBS_SEND_KEY + "\"" + cmd + "\"\n");
				sb.append(Constants.VBS_CARRIER_KEY + "\n");
				sb.append(Constants.VBS_SLEEP_KEY + vbsSleep + "\n");
			}
			sb.append(Constants.VBS_END_SUB + "\n");
			resultMap = saveConfiguredFileContent(vbsFilePath, sb.toString());
		} catch (Exception e) {
			logger.error("Failed in createCliFromVbs() in CommonUtil", e);
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GENERATE_VBS_FILE));
		}
		return resultMap;

	}

	@SuppressWarnings("unchecked")
	public static JSONObject createConfdCliFromBatch(String batchFilePath, String confdCliFilePath,
			ProgramTemplateEntity programTemplateEntity, String ciqFileName, String enbId, String endName) {
		StringBuilder sb = new StringBuilder();
		JSONObject resultMap = new JSONObject();
		BufferedReader bufferedReader = null;
		StringBuilder cmdLine = new StringBuilder();
		FileReader fileReader = null;
		String line = "";
		String cmdPart1 = "";
		String cmdPart2 = "";
		String cmd = "";
		String cmdTimeIntervel = "";
		String terminalName = "";
		String prompt = "*";
		try {
			logger.info("createConfdCliFromBatch() called batchFilePath: " + batchFilePath + ", ConfdcliFilePath: "
					+ confdCliFilePath);
			JSONObject objData = CommonUtil.parseDataToJSON(programTemplateEntity.getValue());
			objData = (JSONObject) objData.get("connLocation");
			objData = (JSONObject) objData.get("NE");
			JSONArray terminals = (JSONArray) objData.get("terminals");

			for (int i = 0; i < terminals.size(); i++) {
				objData = (JSONObject) terminals.get(i);
				terminalName = (String) objData.get("terminalName");
				if (terminalName.equalsIgnoreCase("Confd-cli")) {
					prompt = (String) objData.get("prompt");
					break;
				}
			}

			logger.info("createConfdCliFromBatch() prompt: " + prompt);

			fileReader = new FileReader(batchFilePath);
			bufferedReader = new BufferedReader(fileReader);

			while ((line = bufferedReader.readLine()) != null) {
				if (!line.trim().isEmpty() && !line.contains("#")) {
					if (line.contains(":")) {
						line = StringUtils.substringBeforeLast(line, ":");
					}
					cmdLine.setLength(0);
					if (StringUtils.isNotEmpty(line) && line.contains("::::")) {
						String[] cmds = line.split("::::");
						if (CommonUtil.isValidObject(cmds) && cmds.length > 1) {
							cmdPart1 = cmds[0].replace(endName, "");
							if (StringUtils.isNotEmpty(cmdPart1) && cmdPart1.contains(":")) {
								cmdPart1 = cmdPart1.substring(0, cmdPart1.indexOf(":") + 1);
							}
							cmdPart2 = cmds[1];
							if (CommonUtil.isValidObject(cmdPart2) && cmdPart2.contains(";")) {
								String timeIntervel = cmdPart2.substring(cmdPart2.lastIndexOf(";") + 1,
										cmdPart2.length());
								if (CommonUtil.isValidObject(timeIntervel) && StringUtils.isNotEmpty(timeIntervel)) {
									cmdTimeIntervel = timeIntervel.trim();
									cmdPart2 = cmdPart2.substring(0, cmdPart2.indexOf(";") + 1);
								}
							}
						}
						cmdLine.append(cmdPart1);
						cmdLine.append(cmdPart2);
					} else {
						if (CommonUtil.isValidObject(line) && line.contains(";")) {
							String timeIntervel = line.substring(line.lastIndexOf(";") + 1, line.length());
							if (CommonUtil.isValidObject(timeIntervel) && StringUtils.isNotEmpty(timeIntervel)) {
								cmdTimeIntervel = timeIntervel.trim();
								line = line.substring(0, line.indexOf(";") + 1);
							}
						}
						cmdLine.append(line);
					}
					cmd = cmdLine.toString();
					cmd = cmd.replaceAll("\"", "\\\\\"");
					if (CommonUtil.isValidObject(cmd) && StringUtils.isNotEmpty(cmd) && cmd.length() > 0) {
						sb.append("expect \"vsmuser\"\n");
						sb.append("send \"set paginate false\\r\"\n");
						sb.append("expect \"vsmuser\"\n");
						sb.append("send \"" + cmd.replace("|", "\\|") + "\\r\"\n");
						// sb.append("send \"sleep 1000\\r\"\n");
						if (CommonUtil.isValidObject(cmdTimeIntervel) && StringUtils.isNotEmpty(cmdTimeIntervel)
								&& cmdTimeIntervel.length() > 0) {
							sb.append("send \"sleep " + cmdTimeIntervel.trim() + "\\r\"\n");
						}
					}
				} else if (line.trim().isEmpty()) {
					// sb.append("\n");
				}

			}
			sb.append("expect \"vsmuser\"\n\n");
			sb.append("send \"exit\\r\"\n");
			resultMap = saveConfiguredFileContent(confdCliFilePath, sb.toString());
		} catch (Exception e) {
			logger.error("Failed in createConfdCliFromBatch() in CommonUtil", e);
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason",
					GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GENERATE_CONFD_CLI_FILE));
		}
		return resultMap;
	}

	public static JSONObject createExpect(String filepath, String convertFileName, UploadFileEntity scriptEntity,
			String neId, String neName) {
		JSONObject resultMap = new JSONObject();
		StringBuilder stringBuffer = new StringBuilder();
		logger.info("createExpect() called filepath: " + filepath + ", convertFileName: " + convertFileName);
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filepath))) {
			String line = null;
			String prompt = scriptEntity.getPrompt();
			int lastlineNum = 0;

			while ((line = bufferedReader.readLine()) != null) {

				if (StringUtils.isNotEmpty(line) && line.length() > 0) {
					stringBuffer.append("send \"" + line + "\\r\"\n");
					lastlineNum = stringBuffer.toString().length();

					if (CommonUtil.isValidObject(prompt) && StringUtils.isNotEmpty(prompt) && prompt.length() > 0) {
						stringBuffer.append(
								"expect \"" + prompt.replace("enbname", neName).replace("enbId", neId) + "\"" + " \n");
					}

				}
			}

			String filetxt = stringBuffer.substring(0, lastlineNum);
			resultMap = saveConfiguredFileContent(convertFileName, filetxt);

		} catch (IOException e) {
			logger.error("Failed in createExpect() in CommonUtil", e);
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason",
					GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GENERATE_EXPECT_FILE));
		}
		return resultMap;
	}

	public static void removeCtrlChars(String filepath) {
		try {
			// filepath =
			// "/home/user/Swetha/RCT/rctsoftware/Samsung/SMART/Customer/26/Migration/55/PreCheck/Output/73144_PreAuditofeNB_CDU308T8RPreVerificationv2_10012019_01_04_03.txt";
			String tempFilePath = LoadPropertyFiles.getInstance().getProperty("BASE_PATH") + "Output.txt";
			Runtime.getRuntime().exec("chmod -R 777 " + filepath);
			logger.info("removeCtrlChars() filepath: " + filepath + ", tempFilePath: " + tempFilePath);
			String cmd = "sed '/.\\x08/d' " + filepath + " > " + tempFilePath + "";
			String[] cmdExecution = { "/bin/sh", "-c", cmd };
			Process process = Runtime.getRuntime().exec(cmdExecution);

			String mvcmd = "mv " + tempFilePath + " " + filepath;
			Runtime.getRuntime().exec("chmod -R 777 " + tempFilePath);
			Runtime.getRuntime().exec("chmod -R 777 " + filepath);
			Runtime.getRuntime().exec("chmod -R 777 " + mvcmd);
			String[] mvcmdExecution = { "/bin/sh", "-c", mvcmd };
			process = Runtime.getRuntime().exec(mvcmdExecution);

		} catch (IOException e) {
			logger.error("Failed in createExpect() in CommonUtil", e);
		}
	}

	/**
	 * purpose : To save Configured File Content
	 * 
	 * @param filePath
	 * @param fileContent
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static JSONObject saveConfiguredFileContent(String filePath, String fileContent) {
		JSONObject resultMap = new JSONObject();
		String sourceFilePath = filePath.replace("\"", "").trim();
		try {
			File newFile = new File(sourceFilePath);
			FileUtils.writeStringToFile(newFile, fileContent);
			resultMap.put("status", Constants.SUCCESS);
			resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.SUCCESSFULLY_CREATED_FILE));
		} catch (Exception e) {
			logger.error(" CommonUtil::saveConfiguredFileContent():: ::" + ExceptionUtils.getFullStackTrace(e));
		}

		return resultMap;

	}

	@SuppressWarnings("unchecked")
	public static JSONObject saveConfiguredCsvFileContent(String filePath, String fileContent) {
		JSONObject resultMap = new JSONObject();
		String sourceFilePath = filePath.replace("\"", "").trim();
		try {
			// BASH_CSV_NB-IoTAdd_GROW_ENB_070243_HARLEM_WILLIAM_09182020.sh
			/*
			 * String Grow_Cell_File=sourceFilePath+"/Grow_Cell_File"; String
			 * Grow_Enb_File=sourceFilePath+"/Grow_Enb_File"; String
			 * pnp_File=sourceFilePath+"/pnp_File";
			 */

			/*
			 * if(sourceFilePath.contains("GROW_ENB")) { String
			 * Grow_Enb_File=sourceFilePath+"/Grow_Enb_File"; File dest = new
			 * File(Grow_Enb_File);
			 * 
			 * if (!dest.exists()) { FileUtil.createDirectory(Grow_Enb_File); }
			 * 
			 * //File newFile = new File(sourceFilePath); FileUtils.writeStringToFile(dest,
			 * fileContent); } else if(sourceFilePath.contains("GROW_CELL")) { String
			 * Grow_Cell_File=sourceFilePath+"/Grow_Cell_File"; File dest = new
			 * File(Grow_Cell_File);
			 * 
			 * if (!dest.exists()) { FileUtil.createDirectory(Grow_Cell_File); }
			 * 
			 * //File newFile = new File(sourceFilePath); FileUtils.writeStringToFile(dest,
			 * fileContent); } else if(sourceFilePath.contains("pnp_macro")) { String
			 * pnp_File=sourceFilePath+"/pnp_File"; File dest = new File(pnp_File);
			 * 
			 * if (!dest.exists()) { FileUtil.createDirectory(pnp_File); }
			 */
			File newFile = new File(sourceFilePath);
			FileUtils.writeStringToFile(newFile, fileContent);

			resultMap.put("status", Constants.SUCCESS);
			resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.SUCCESSFULLY_CREATED_FILE));
		} catch (Exception e) {
			logger.error(" CommonUtil::saveConfiguredFileContent():: ::" + ExceptionUtils.getFullStackTrace(e));
		}

		return resultMap;

	}

	/**
	 * purpose : To getArgumentValue
	 * 
	 * @param ciqName
	 * @param connectionSudoPassword
	 * @param connectionLocationPwd
	 * @param connectionLocationUserName
	 * @param neId,neName,networkConfigEntity,argName
	 * @return argValue
	 * @throws Exception
	 */

	@SuppressWarnings("unchecked")
	public String getArgumentValue(String programId, String ciqName, String neId, String neName,
			NetworkConfigEntity networkConfigEntity, String migrationType, String migrationSubType, String argName,
			String userName, String sessionId, String connectionLocationUserName, String connectionLocationPwd,
			String connectionSudoPassword, String saneUserName, String sanePassword) {
		String argValue = "";
		boolean decodeFound = false;
		try {
			logger.info("getArgumentValue() argName: " + argName);
			String timeStamp = new SimpleDateFormat("MMddyyyy_HH_mm_ss")
					.format(new Timestamp(System.currentTimeMillis()));
			String dbcollectionFileName = CommonUtil.createMongoDbFileName(
					String.valueOf(networkConfigEntity.getProgramDetailsEntity().getId()), ciqName);
			if (Constants.NE_ID.equalsIgnoreCase(argName)) {
				argValue = neId;
				decodeFound = true;
			} else if (Constants.NE_NAME.equalsIgnoreCase(argName)) {
				argValue = neName;
				decodeFound = true;
			} else if (Constants.CDU_IP.equalsIgnoreCase(argName)) {
				String vznEnbIP = fileUploadService.getEnBDataByPath(dbcollectionFileName, Constants.VZ_GROW_IPPLAN,
						neId, Constants.VZ_GROW_eNB_OAM_IP_eNB_SB_IP);
				String eNB_OAM_IP = fileUploadService.getEnBDataByPath(dbcollectionFileName,
						Constants.SPT_GROW_SHEET_FDD_TDD, neId, Constants.ORAN_SPRINT_COMM_SCRIPT_8_CHG_IP_ADDR);
				String enbIP = "127.0.0.1";

				if (StringUtils.isNotEmpty(vznEnbIP)) {
					enbIP = vznEnbIP;
				} else if (StringUtils.isNotEmpty(eNB_OAM_IP)) {
					String sprintEnbIP = CommonUtil.getLeftSubStringWithLen(eNB_OAM_IP, 3);
					if (StringUtils.isNotEmpty(sprintEnbIP)) {
						enbIP = sprintEnbIP;
					} else {
						enbIP = eNB_OAM_IP;
					}
				}
				argValue = enbIP;
				decodeFound = true;
			} else if (Constants.DIR.equalsIgnoreCase(argName)) {
				StringBuilder dirPath = new StringBuilder();
				dirPath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH")).append(Constants.CUSTOMER)
						.append(Constants.SEPARATOR).append(programId)
						.append(Constants.RUN_MIGRATION_OUTPUT.replace("migration", migrationType)
								.replace("enbId", StringUtils.substringBeforeLast(neId, "."))
								.replace("subtype", migrationSubType).replaceAll(" ", "_"));
				argValue = dirPath.toString();
				decodeFound = true;
			} else if (Constants.CASCADE_ID.equalsIgnoreCase(argName)) {
				String cascasdeId = fileUploadService.getEnBDataByPath(dbcollectionFileName, Constants.AUDIT_FDD_TDD,
						neId, Constants.AUDIT_CASCADE);
				if (!StringUtils.isNotEmpty(cascasdeId)) {
					cascasdeId = neId;
				}
				argValue = cascasdeId;
				decodeFound = true;
			} else if (Constants.AD_ID.equalsIgnoreCase(argName)) {
				argValue = userName;
				decodeFound = true;
			} else if (Constants.LSM_USERNAME.equalsIgnoreCase(argName)) {
				argValue = networkConfigEntity.getNeUserName();
				decodeFound = true;
			} else if (Constants.LSM_PWD.equalsIgnoreCase(argName)) {
				argValue = networkConfigEntity.getNePassword();
				decodeFound = true;
			} else if (Constants.MARKET.equalsIgnoreCase(argName)) {
				argValue = networkConfigEntity.getNeMarket();
				decodeFound = true;
			} else if (Constants.RS_IP.equalsIgnoreCase(argName)) {
				argValue = networkConfigEntity.getNeRsIp();
				decodeFound = true;
			} else if (Constants.MSMA_IP.equalsIgnoreCase(argName)) {
				argValue = networkConfigEntity.getNeIp();
				decodeFound = true;
			} else if (Constants.MCMA_IP.equalsIgnoreCase(argName)) {
				decodeFound = true;
				if (CommonUtil.isValidObject(networkConfigEntity)
						&& CommonUtil.isValidObject(networkConfigEntity.getNeDetails())) {
					for (NetworkConfigDetailsEntity detailsEntity : networkConfigEntity.getNeDetails()) {
						if (CommonUtil.isValidObject(detailsEntity.getServerTypeEntity())) {
							if (detailsEntity.getServerTypeEntity().getId() == Constants.NW_CONFIG_VLSM_MCMA_ID
									&& CommonUtil.isValidObject(detailsEntity.getServerIp())
									&& detailsEntity.getServerIp().length() > 0) {
								argValue = detailsEntity.getServerIp();
								break;
							}
						}
					}
				}
				argValue = "-mcmaIp" + " '" + argValue + "'";
			} else if (Constants.JUMP_BOX_IP.equalsIgnoreCase(argName)) {
				decodeFound = true;
				if (CommonUtil.isValidObject(networkConfigEntity)
						&& CommonUtil.isValidObject(networkConfigEntity.getNeDetails())) {
					for (NetworkConfigDetailsEntity detailsEntity : networkConfigEntity.getNeDetails()) {
						if (CommonUtil.isValidObject(detailsEntity.getServerTypeEntity())) {
							if (detailsEntity.getServerTypeEntity().getId() == Constants.NW_CONFIG_JUMP_ID
									&& CommonUtil.isValidObject(detailsEntity.getServerIp())
									&& detailsEntity.getServerIp().length() > 0) {
								argValue = detailsEntity.getServerIp();
								break;
							}
						}
					}
				}
			} else if (Constants.JUMP_SANE_IP.equalsIgnoreCase(argName)) {
				decodeFound = true;
				if (CommonUtil.isValidObject(networkConfigEntity)
						&& CommonUtil.isValidObject(networkConfigEntity.getNeDetails())) {
					for (NetworkConfigDetailsEntity detailsEntity : networkConfigEntity.getNeDetails()) {
						if (CommonUtil.isValidObject(detailsEntity.getServerTypeEntity())) {
							if (detailsEntity.getServerTypeEntity().getId() == Constants.NW_CONFIG_SANE_ID
									&& CommonUtil.isValidObject(detailsEntity.getServerIp())
									&& detailsEntity.getServerIp().length() > 0) {
								argValue = detailsEntity.getServerIp();
								break;
							}
						}
					}
				}
			} else if (Constants.SANE_OPTIONS.equalsIgnoreCase(argName)) {
				decodeFound = true;
				if (CommonUtil.isValidObject(networkConfigEntity)
						&& CommonUtil.isValidObject(networkConfigEntity.getNeDetails())) {
					for (NetworkConfigDetailsEntity detailsEntity : networkConfigEntity.getNeDetails()) {
						if (CommonUtil.isValidObject(detailsEntity.getServerTypeEntity())) {
							if (detailsEntity.getServerTypeEntity().getId() == Constants.NW_CONFIG_SANE_ID
									&& CommonUtil.isValidObject(detailsEntity.getPath())
									&& detailsEntity.getPath().length() > 0) {
								argValue = detailsEntity.getPath();
								break;
							}
						}
					}
				}
			} else if (Constants.UNQ_ID.equalsIgnoreCase(argName)) {
				decodeFound = true;
				argValue = sessionId + "_" + timeStamp;
			} else if (Constants.VLSM_RS_IP.equalsIgnoreCase(argName)) {
				decodeFound = true;
				argValue = networkConfigEntity.getNeRsIp();
			} else if (Constants.LSM_IP.equalsIgnoreCase(argName)) {
				decodeFound = true;
				argValue = networkConfigEntity.getNeIp();
			} else if (Constants.PUT_SERVER_IP.equalsIgnoreCase(argName)) {
				decodeFound = true;
				argValue = networkConfigEntity.getNeIp();
			} else if (Constants.VLSM_IP.equalsIgnoreCase(argName)) {
				decodeFound = true;
				argValue = networkConfigEntity.getNeIp();
			} else if (Constants.OPS_ATP_INPUT_FILE.equalsIgnoreCase(argName)) {
				decodeFound = true;
				StringBuilder dirPath = new StringBuilder();
				dirPath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH")).append(Constants.CUSTOMER)
						.append(Constants.RAN_ATP_INPUT.replace("programId", programId)
								.replace("migrationType", migrationType).replace("neId", neId)
								.replace("subType", migrationSubType).replaceAll(" ", "_"));
				argValue = dirPath.toString();
			} else if (Constants.IS_LAB.equalsIgnoreCase(argName)) {
				decodeFound = true;
				String deploymentType = LoadPropertyFiles.getInstance().getProperty("deploymentType");
				String isLab = "";
				if ("Live".equalsIgnoreCase(deploymentType)) {
					isLab = "false";
				} else if ("Lab".equalsIgnoreCase(deploymentType)) {
					isLab = "true";
				}
				argValue = isLab;
			} else if (Constants.CREDENTIALS.equalsIgnoreCase(argName)) {
				decodeFound = true;
				JSONObject parentCred = new JSONObject();
				JSONObject childCred = new JSONObject();
				String deploymentType = LoadPropertyFiles.getInstance().getProperty("deploymentType");
				String isLab = "";
				if ("Live".equalsIgnoreCase(deploymentType)) {
					isLab = "false";
				} else if ("Lab".equalsIgnoreCase(deploymentType)) {
					isLab = "true";
				}
				String vznEnbIP = fileUploadService.getEnBDataByPath(dbcollectionFileName, Constants.VZ_GROW_IPPLAN,
						neId, Constants.VZ_GROW_eNB_OAM_IP_eNB_SB_IP);
				String eNB_OAM_IP = fileUploadService.getEnBDataByPath(dbcollectionFileName,
						Constants.SPT_GROW_SHEET_FDD_TDD, neId, Constants.ORAN_SPRINT_COMM_SCRIPT_8_CHG_IP_ADDR);
				String enbIP = "127.0.0.1";

				if (StringUtils.isNotEmpty(vznEnbIP)) {
					enbIP = vznEnbIP;
				} else if (StringUtils.isNotEmpty(eNB_OAM_IP)) {
					String sprintEnbIP = CommonUtil.getLeftSubStringWithLen(eNB_OAM_IP, 3);
					if (StringUtils.isNotEmpty(sprintEnbIP)) {
						enbIP = sprintEnbIP;
					} else {
						enbIP = eNB_OAM_IP;
					}
				}
				if (CommonUtil.isValidObject(networkConfigEntity)) {
					if (CommonUtil.isValidObject(networkConfigEntity.getNeDetails())
							&& networkConfigEntity.getNeDetails().size() > 0) {
						for (NetworkConfigDetailsEntity networkConfigDetailsEntity : networkConfigEntity
								.getNeDetails()) {
							/*
							 * if(isLab == "true" &&
							 * networkConfigDetailsEntity.getServerTypeEntity().getId() !=
							 * Constants.NW_CONFIG_VLSM_MCMA_ID ){ continue; }
							 */
							if (networkConfigDetailsEntity.getServerTypeEntity()
									.getId() == Constants.NW_CONFIG_SANE_ID) {
								childCred = new JSONObject();
								childCred.put("ip", networkConfigDetailsEntity.getServerIp());
								childCred.put("user", saneUserName);
								childCred.put("pass", sanePassword);
								childCred.put("type", networkConfigDetailsEntity.getServerTypeEntity().getId());
								parentCred.put(networkConfigDetailsEntity.getServerName(), childCred);
							} else {
								childCred = new JSONObject();
								childCred.put("ip", networkConfigDetailsEntity.getServerIp());
								childCred.put("user", networkConfigDetailsEntity.getServerUserName());
								childCred.put("pass", networkConfigDetailsEntity.getServerPassword());
								childCred.put("type", networkConfigDetailsEntity.getServerTypeEntity().getId());
								parentCred.put(networkConfigDetailsEntity.getServerName(), childCred);
							}
						}
					}
					childCred = new JSONObject();
					childCred.put("ip", networkConfigEntity.getNeIp());
					childCred.put("user", networkConfigEntity.getNeUserName());
					childCred.put("pass", networkConfigEntity.getNePassword());
					childCred.put("type", "4");
					parentCred.put(networkConfigEntity.getNeName(), childCred);

					childCred = new JSONObject();
					childCred.put("ip", enbIP);
					childCred.put("user", connectionLocationUserName);
					childCred.put("pass", connectionLocationPwd);
					childCred.put("super-pass", connectionSudoPassword);
					childCred.put("type", "5");
					parentCred.put(neName, childCred);
				}
				argValue = parentCred.toJSONString();
			} else if (Constants.HOP_STRING.equalsIgnoreCase(argName)) {
				decodeFound = true;
				String deploymentType = LoadPropertyFiles.getInstance().getProperty("deploymentType");
				String isLab = "";
				if ("Live".equalsIgnoreCase(deploymentType)) {
					isLab = "false";
				} else if ("Lab".equalsIgnoreCase(deploymentType)) {
					isLab = "true";
				}
				String hopString = "";
				if (CommonUtil.isValidObject(networkConfigEntity)) {
					if (CommonUtil.isValidObject(networkConfigEntity.getNeDetails())
							&& networkConfigEntity.getNeDetails().size() > 0) {
						for (NetworkConfigDetailsEntity networkConfigDetailsEntity : networkConfigEntity
								.getNeDetails()) {
							/*
							 * if(isLab == "true" &&
							 * networkConfigDetailsEntity.getServerTypeEntity().getId() !=
							 * Constants.NW_CONFIG_VLSM_MCMA_ID ){ continue; }
							 */
							hopString = hopString + networkConfigDetailsEntity.getServerName() + ",";
						}
					}
					hopString = hopString + networkConfigEntity.getNeName() + ",";
					hopString = hopString + neName;
				}
				argValue = hopString;
			}
			if (!decodeFound) {
				logger.info("getArgumentValue() decodeFound: " + decodeFound + ", Arg Name is assigning to Arg Vame");
				argValue = argName;
			}
			logger.info("getArgumentValue() argName: " + argName + ", argValue: " + argValue);
		} catch (Exception e) {
			if (!decodeFound) {
				argValue = argName;
			}
			logger.error("Exception CommonUtil.getArgumentValue() " + ExceptionUtils.getFullStackTrace(e));
		}
		return argValue;
	}

	public static String executeCommand(String command, String outputFileName, String scriptArguments)
			throws RctException {
		StringBuilder output = new StringBuilder();
		Process p;
		try {

			String[] fileName = command.split("\\.");
			String cmdExe = command;
			String fileExtension = FilenameUtils.getExtension(command);

			if (scriptArguments != null) {
				if ("py".equalsIgnoreCase(fileExtension)) {
					cmdExe = "python ".concat(command).concat(" ").concat(scriptArguments);
				} else if ("perl".equalsIgnoreCase(fileExtension)) {
					cmdExe = "perl ".concat(command).concat(" ").concat(scriptArguments);
				} else if ("pl".equalsIgnoreCase(fileExtension)) {
					cmdExe = "perl ".concat(command).concat(" ").concat(scriptArguments);
				} else if ("sh".equalsIgnoreCase(fileExtension)) {
					cmdExe = command.concat(" ").concat(scriptArguments);
				}
			}
			String[] cmdToExec = { "/bin/sh", "-c", cmdExe };
			logger.info("executeCommand() command: " + command + ", outputFileName: " + outputFileName
					+ ", scriptArguments: " + scriptArguments);

			logger.info("executeCommand() cmdExe:" + cmdExe);
			
			logger.error("executeCommand() command: " + command + ", outputFileName: " + outputFileName
					+ ", scriptArguments: " + scriptArguments);

			logger.error("executeCommand() cmdExe:" + cmdExe);
                        StringBuilder objStringBuilder=new StringBuilder();
			objStringBuilder.append("[/bin/sh, -c, ");
			objStringBuilder.append(cmdExe);
			objStringBuilder.append("]");
			logger.error("executeCommand() terminal command: " + objStringBuilder.toString());
			logger.info("executeCommand() terminal command: " + objStringBuilder.toString());

			Runtime.getRuntime().exec("chmod -R 777 " + command);
			Runtime.getRuntime().exec("chmod -R 777 " + cmdExe);

			Thread.sleep(2000);
			p = Runtime.getRuntime().exec(cmdToExec);
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			BufferedReader error = new BufferedReader(new InputStreamReader(p.getErrorStream()));

			OutputStream os = new FileOutputStream(outputFileName, true);

			String[] paths = command.split("/");

			String exeFileString = "Executing " + paths[paths.length - 1] + "\n";

			os.write(exeFileString.getBytes());
			output.append(exeFileString);

			if (reader != null) {
				String line;
				while ((line = reader.readLine()) != null) {

					if (line.contains("Permission denied, please try again")) {
						os.write("Operation Failed wrong password : Permission denied, please try again.".getBytes(), 0,
								70);
					}
					if (line.contains("No route to host")) {
						os.write("Operation Failed No route to host :ssh connect to host IP port 22: No route to host."
								.getBytes(), 0, 85);
					}

					System.out.println(line + "\n");
					String lineData = line + "\n";

					String resultString = lineData.replaceAll("\\[\\dm", "").replaceAll("\\[[\\d;]*m", "")
							.replaceAll("\\u0007", "").replaceAll("\\u001B", "").replaceAll("\\]\\d;", "")
							.replaceAll("[*\\[]K", "");

					output.append(resultString);
					os.write(resultString.getBytes());
				}
			}
			if (error != null) {
				String line;
				while ((line = error.readLine()) != null) {
					System.out.println(line + "\n");
					String lineData = line + "\n";

					String resultString = lineData.replaceAll("\\[\\dm", "").replaceAll("\\[[\\d;]*m", "")
							.replaceAll("\\u0007", "").replaceAll("\\u001B", "").replaceAll("\\]\\d;", "")
							.replaceAll("[*\\[]K", "");

					output.append(resultString);
					os.write(resultString.getBytes());
				}
			}
			os.close();
			// Removing ctrl H char from output log
			// outputFileName
			// ="/home/user/Downloads/73144_PreAuditofeNB_CDU308T8RPreVerificationv2_10012019_01_04_03.txt";
			String tempOutputFilePath = LoadPropertyFiles.getInstance().getProperty("BASE_PATH") + Constants.SEPARATOR
					+ "Output.txt";
			String cmd = "sed '/.\\x08/d' " + outputFileName + " > " + tempOutputFilePath + "";
			String[] cmdExecution = { "/bin/sh", "-c", cmd };
			Process process = Runtime.getRuntime().exec(cmdExecution);

			String mvcmd = "mv " + tempOutputFilePath + " " + outputFileName;
			String[] mvcmdExecution = { "/bin/sh", "-c", mvcmd };
			process = Runtime.getRuntime().exec(mvcmdExecution);
		} catch (Exception e) {
			logger.error("Exception RunTestServiceImpl in executeCommand() " + ExceptionUtils.getFullStackTrace(e));
			// throw new RctException(e.getMessage());
		}
		return output.toString();
	}

	public static boolean isIpv4Address(String ipAddress) {
		if (StringUtils.isNotEmpty(ipAddress)) {
			String ipv4Pattern = "(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])";

			Matcher m1 = Pattern.compile(ipv4Pattern, Pattern.CASE_INSENSITIVE).matcher(ipAddress.trim());
			if (m1.matches()) {
				return true;
			}
			return false;
		} else {
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	public static JSONObject createBashFromXml5GAudit(String xmlFilePath, String bashFilePath, String mcmip,
			String eNodeID, String endName) {
		StringBuilder objCommand = new StringBuilder();
		JSONObject resultMap = new JSONObject();
		try {
			String gNodeID = endName + "_" + eNodeID;
			boolean statusOFIpv4 = isIpv4Address(mcmip);
			String xmlfileContent = getXmlFileContent(xmlFilePath);
			xmlfileContent = xmlfileContent.replaceAll("\"", "\\\\\"");

			objCommand.append("send \"");
			// objCommand.append("curl -X POST -k -g -6 -u
			// \\\"ais176\\@samsung.com:S\\@msung0ssuser\\\" -H \\'Content-type:
			// application\\/xml\\\' -d \\'");
			objCommand.append(
					"curl -X POST -k -g -6 -u \\\'ossuser:osspasswd\\\' -H \\'Content-Type: application\\/xml\\\' -d \\");
			objCommand.append("'");
			objCommand.append(xmlfileContent);
			objCommand.append("'");
			objCommand.append("\\");
			if (statusOFIpv4) {
				if (endName.equalsIgnoreCase("eNB")) {
					objCommand.append(" https:\\/\\/" + mcmip + ":7443\\/oss\\/netconfByNeId\\/" + gNodeID + "");
				} else {
					objCommand.append(" https:\\/\\/" + mcmip + ":7443\\/oss\\/netconf\\/" + gNodeID + "");
				}
			} else {
				if (endName.equalsIgnoreCase("eNB")) {
					objCommand.append(" https:\\/\\/\\[" + mcmip + "\\]:7443\\/oss\\/netconfByNeId\\/" + gNodeID + "");
				} else {
					objCommand.append(" https:\\/\\/\\[" + mcmip + "\\]:7443\\/oss\\/netconf\\/" + gNodeID + "");
				}
			}
			objCommand.append("\\r\"\n\n");

			resultMap = saveConfiguredFileContent(bashFilePath, objCommand.toString());

		} catch (Exception e) {
			logger.error("Failed in createBashFromXml5GAudit() in CommonUtil", e);
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason",
					GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GENERATE_BASH_FILE));
		}

		return resultMap;

	}
	
	public static JSONObject createBashFromXml5GAuditTwamp(String xmlFilePath, String bashFilePath, String mcmip,
			String eNodeID, String endName, String fileName, String replaceData) {
		StringBuilder objCommand = new StringBuilder();
		JSONObject resultMap = new JSONObject();
		try {
			String gNodeID = endName + "_" + eNodeID;
			boolean statusOFIpv4 = isIpv4Address(mcmip);
			String xmlfileContent = getXmlFileContent(xmlFilePath);
			xmlfileContent = xmlfileContent.replaceAll("\"", "\\\\\"");
			if(fileName.contains(XmlCommandsConstants.AU_TWAMP_F1C_LINK)) {
				xmlfileContent = xmlfileContent.replaceAll("Remote_Ip_Addr", replaceData);
			} else if(fileName.contains(XmlCommandsConstants.AU_DIAGNOSTIC)) {
				xmlfileContent = xmlfileContent.replaceAll("Test_ID", replaceData);
			}
			

			objCommand.append("send \"");
			// objCommand.append("curl -X POST -k -g -6 -u
			// \\\"ais176\\@samsung.com:S\\@msung0ssuser\\\" -H \\'Content-type:
			// application\\/xml\\\' -d \\'");
			objCommand.append(
					"curl -X POST -k -g -6 -u \\\'ossuser:osspasswd\\\' -H \\'Content-Type: application\\/xml\\\' -d \\");
			objCommand.append("'");
			objCommand.append(xmlfileContent);
			objCommand.append("'");
			objCommand.append("\\");
			if (statusOFIpv4) {
				if (endName.equalsIgnoreCase("eNB")) {
					objCommand.append(" https:\\/\\/" + mcmip + ":7443\\/oss\\/netconfByNeId\\/" + gNodeID + "");
				} else {
					objCommand.append(" https:\\/\\/" + mcmip + ":7443\\/oss\\/netconf\\/" + gNodeID + "");
				}
			} else {
				if (endName.equalsIgnoreCase("eNB")) {
					objCommand.append(" https:\\/\\/\\[" + mcmip + "\\]:7443\\/oss\\/netconfByNeId\\/" + gNodeID + "");
				} else {
					objCommand.append(" https:\\/\\/\\[" + mcmip + "\\]:7443\\/oss\\/netconf\\/" + gNodeID + "");
				}
			}
			objCommand.append("\\r\"\n\n");

			resultMap = saveConfiguredFileContent(bashFilePath, objCommand.toString());

		} catch (Exception e) {
			logger.error("Failed in createBashFromXml5GAudit() in CommonUtil", e);
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason",
					GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GENERATE_BASH_FILE));
		}

		return resultMap;

	}
	
	
	public static String getCurlCommandAudit(String xmlFilePath, String mcmip,
			String eNodeID, String endName) {
		StringBuilder objCommand = new StringBuilder();
		try {
			String gNodeID = endName + "_" + eNodeID;
			boolean statusOFIpv4 = isIpv4Address(mcmip);
			String xmlfileContent = getXmlFileContent(xmlFilePath);
			objCommand.append(
					"curl -X POST -k -g -6 -u 'ossuser:osspasswd' -H 'Content-Type: application/xml' -d ");
			objCommand.append("'");
			objCommand.append(xmlfileContent);
			objCommand.append("'");
			if (statusOFIpv4) {
				if (endName.equalsIgnoreCase("eNB")) {
					objCommand.append(" https://" + mcmip + ":7443/oss/netconfByNeId/" + gNodeID + "");
				} else {
					objCommand.append(" https://" + mcmip + ":7443/oss/netconf/" + gNodeID + "");
				}
			} else {
				if (endName.equalsIgnoreCase("eNB")) {
					objCommand.append(" https://[" + mcmip + "]:7443/oss/netconfByNeId/" + gNodeID + "");
				} else {
					objCommand.append(" https://[" + mcmip + "]:7443/oss/netconf/" + gNodeID + "");
				}
			}

		} catch (Exception e) {
			logger.error("Failed in createBashFromXml5GAudit() in CommonUtil", e);
		}

		return objCommand.toString();

	}
	
	public static String getCurlCommandforXML(String xmlFilePath, String mcmip,
			String data) {
		StringBuilder objCommand = new StringBuilder();
		try {
			boolean statusOFIpv4 = isIpv4Address(mcmip);
			String xmlfileContent = getXmlFileContent(xmlFilePath);
			if (xmlfileContent.contains("<?xml")) {
				xmlfileContent = StringUtils.substringAfter(xmlfileContent, "?>");
			}
			objCommand.append(
					"curl -X POST -k -g -6 -u 'ossuser:osspasswd' -H 'Content-Type: application/xml' -d ");
			objCommand.append("'");
			objCommand.append(xmlfileContent);
			objCommand.append("'");
			if (statusOFIpv4) {
				if (data.contains("eNB")) {
					objCommand.append(" https://" + mcmip + ":7443/oss/netconfByNeId/" + data + "");
				} else {
					objCommand.append(" https://" + mcmip + ":7443/oss/netconf/" + data + "");
				}
			} else {
				if (data.contains("eNB")) {
					objCommand.append(" https://[" + mcmip + "]:7443/oss/netconfByNeId/" + data + "");
				} else {
					objCommand.append(" https://[" + mcmip + "]:7443/oss/netconf/" + data + "");
				}
			}

		} catch (Exception e) {
			logger.error("Failed in getCurlCommandforXML() in CommonUtil", e);
		}

		return objCommand.toString();

	}
	public static String getCurlCommandforXMLFSU(String xmlFilePath, String mcmip,
			String data) {
		StringBuilder objCommand = new StringBuilder();
		try {
			boolean statusOFIpv4 = isIpv4Address(mcmip);
			String xmlfileContent = getXmlFileContent(xmlFilePath);
			if (xmlfileContent.contains("<?xml")) {
				xmlfileContent = StringUtils.substringAfter(xmlfileContent, "?>");
			}
			objCommand.append(
					"curl -X POST -k -g -6 -u 'ossuser:osspasswd' -H 'Content-Type: application/xml' -d ");
			objCommand.append("'");
			objCommand.append(xmlfileContent);
			objCommand.append("'");
			if (statusOFIpv4) {
				if (data.contains("FSU")) {
					objCommand.append(" https://" + mcmip + ":7443/oss/netconf/" + data + "");
				} else {
					objCommand.append(" https://" + mcmip + ":7443/oss/netconf/" + data + "");
				}
			} else {
				if (data.contains("FSU")) {
					objCommand.append(" https://[" + mcmip + "]:7443/oss/netconf/" + data + "");
				} else {
					objCommand.append(" https://[" + mcmip + "]:7443/oss/netconf/" + data + "");
				}
			}

		} catch (Exception e) {
			logger.error("Failed in getCurlCommandforXML() in CommonUtil", e);
		}

		return objCommand.toString();

	}
	
	public static String getCurlCommandforCreation(String xmlFilePath, String mcmip,String enbId,String Type ,String Date) {
		StringBuilder objCommand = new StringBuilder();
		try {
			String date2=DateUtil.dateToString(new Date(), "yyyy-MM-dd");
        	String endTime =DateUtil.dateToString(new Date(), "yyyy-MM-dd");
			//boolean statusOFIpv4 = isIpv4Address(mcmip);
			//String xmlfileContent = getXmlFileContent(xmlFilePath);
			
			objCommand.append(
					"curl -X GET -k -g -6 -u 'ossuser:osspasswd'");
			
			//objCommand.append("https://[" + mcmip + "]:7443/oss/neAlarmsByNeName/"+"eNB_"+enbId + "");
			  objCommand.append(" 'https://[" + mcmip +"]:7443/oss/neAlarmsByNeName/"+Type +enbId +"?startTime="+Date+"%2000:00:00"+"&endTime="+Date+"%2022:00:00'");

			

		} catch (Exception e) {
			logger.error("Failed in getCurlCommandforXML() in CommonUtil", e);
		}

		return objCommand.toString();

	}
	public static String getCurlCommandforpackageinventory(String xmlFilePath, String mcmip,String enbId,String Type ,String Date) {
		StringBuilder objCommand = new StringBuilder();
		try {
			boolean statusOFIpv4 = isIpv4Address(mcmip);
			String xmlfileContent="";
			if("enb_".equals(Type)) {
	        	 xmlfileContent="<nc:rpc xmlns:nc=\"urn:ietf:params:xml:ns:netconf:base:1.0\"><mid:retrieve-package-inventory xmlns:mid=\"http://www.samsung.com/global/business/4GvRAN/ns/macro_indoor_dist\"/></nc:rpc>";
				}else if("DU_".equals(Type)) {
					xmlfileContent="<nc:rpc xmlns:nc=\"urn:ietf:params:xml:ns:netconf:base:1.0\"><gnbau:retrieve-package-inventory xmlns:gnbau=\"http://www.samsung.com/global/business/5GvRAN/ns/gnb-au\"/></nc:rpc>";
				}else if("FSU_".equals(Type)) {
					xmlfileContent="<nc:rpc xmlns:nc=\"urn:ietf:params:xml:ns:netconf:base:1.0\"><cfsu:retrieve-package-inventory xmlns:cfsu=\"http://www.samsung.com/global/business/5GvRAN/ns/cfsu\"/></nc:rpc>";
				}			objCommand.append(
					"curl -X POST -k -g -6 -u 'ossuser:osspasswd' -H 'Content-Type: application/xml' -d ");
			objCommand.append("'");
			objCommand.append(xmlfileContent);
			objCommand.append("'");
			
			if (statusOFIpv4) {
				objCommand.append(" https://" + mcmip + ":7443/oss/netconfByNeId/" + Type +enbId);
			}else {
				objCommand.append(" https://[" + mcmip +"]:7443/oss/netconfByNeId/"+Type +enbId);
			}
			//objCommand.append("https://[" + mcmip + "]:7443/oss/neAlarmsByNeName/"+"eNB_"+enbId + "");
			 // objCommand.append(" 'https://[" + mcmip +"]:7443/oss/netconfByNeId/"+Type +enbId +"?startTime="+Date+"%2000:00:00"+"&endTime="+Date+"%2022:00:00'");

			

		} catch (Exception e) {
			logger.error("Failed in getCurlCommandforXML() in CommonUtil", e);
		}

		return objCommand.toString();

	}
	
	public static String getCurlCommandforXML5G(String xmlFilePath, String mcmip,
			String data, String file, LinkedHashMap<String, CiqMapValuesModel> objMapDetails) {
		StringBuilder objCommand = new StringBuilder();
		try {
			boolean statusOFIpv4 = isIpv4Address(mcmip);
			String xmlfileContent = getXmlFileContent(xmlFilePath);
			if (xmlfileContent.contains("<?xml")) {
				xmlfileContent = StringUtils.substringAfter(xmlfileContent, "?>");
			}
			if (file.contains("DCM") || file.contains("param-config-Houston") || file.contains("Nola")
					|| file.contains("Sacramento") || file.contains("Pensacola")) {
				Pattern pat = Pattern.compile("\\$\\{cell-num=\\d\\}");

				Matcher mat = pat.matcher(xmlfileContent);
				while (mat.find()) {
					String cellnum = mat.group().replaceAll("[^0-9]", "");
					String temp = objMapDetails.get("CC" + cellnum + " Cell Identity").getHeaderValue();
					if (!temp.equalsIgnoreCase("tbd")) {
						xmlfileContent = xmlfileContent.replaceAll("${cell-num=" + cellnum + "}",
								new Integer(Integer.parseInt(temp)).toString());
					}
				}
			}
			objCommand.append(
					"curl -X POST -k -g -6 -u 'ossuser:osspasswd' -H 'Content-Type: application/xml' -d ");
			objCommand.append("'");
			objCommand.append(xmlfileContent);
			objCommand.append("'");
			if (statusOFIpv4) {
				if (data.contains("eNB")) {
					objCommand.append(" https://" + mcmip + ":7443/oss/netconfByNeId/" + data + "");
				} else {
					objCommand.append(" https://" + mcmip + ":7443/oss/netconf/" + data + "");
				}
			} else {
				if (data.contains("eNB")) {
					objCommand.append(" https://[" + mcmip + "]:7443/oss/netconfByNeId/" + data + "");
				} else {
					objCommand.append(" https://[" + mcmip + "]:7443/oss/netconf/" + data + "");
				}
			}

		} catch (Exception e) {
			logger.error("Failed in getCurlCommandforXML() in CommonUtil", e);
		}

		return objCommand.toString();

	}
	
	public static String getCurlCommandforXML5GDSS(String xmlFilePath, String mcmip,
			String data) {
		StringBuilder objCommand = new StringBuilder();
		try {
			boolean statusOFIpv4 = isIpv4Address(mcmip);
			String xmlfileContent = getXmlFileContent(xmlFilePath);
			if (xmlfileContent.contains("<?xml")) {
				xmlfileContent = StringUtils.substringAfter(xmlfileContent, "?>");
			}
			objCommand.append(
					"curl -X POST -k -g -6 -u 'ossuser:osspasswd' -H 'Content-Type: application/xml' -d ");
			objCommand.append("'");
			objCommand.append(xmlfileContent);
			objCommand.append("'");
			if (statusOFIpv4) {
				if (data.contains("eNB")) {
					objCommand.append(" https://" + mcmip + ":7443/oss/netconfByNeId/" + data + "");
				} else {
					objCommand.append(" https://" + mcmip + ":7443/oss/netconf/" + data + "");
				}
			} else {
				if (data.contains("eNB")) {
					objCommand.append(" https://[" + mcmip + "]:7443/oss/netconfByNeId/" + data + "");
				} else {
					objCommand.append(" https://[" + mcmip + "]:7443/oss/netconf/" + data + "");
				}
			}

		} catch (Exception e) {
			logger.error("Failed in getCurlCommandforXML() in CommonUtil", e);
		}

		return objCommand.toString();

	}
	
	public static String getCurlCommandforNeGrow(String neGrowJson, String mcmip,
			String endbName, String csvFilePath) {
		StringBuilder objCommand = new StringBuilder();
		try {
			boolean statusOFIpv4 = isIpv4Address(mcmip);
			if(neGrowJson!=null) {
				String v3 = "\\\\" + Character.toString('"');
				neGrowJson = neGrowJson.replaceAll(v3, Character.toString('"')).replaceAll("\\\\]", "]").replace("\\[","[");
			}			
			objCommand.append(
					"curl -X POST -k -g -6 -u 'ossuser:osspasswd' -H 'Content-Type: application/json' -d ");
			objCommand.append("'");
			objCommand.append(neGrowJson);
			objCommand.append("'");
			if ((csvFilePath.contains("GROW_CELL") || csvFilePath.contains("AU_CaCell_"))
					&& !csvFilePath.contains("AU_20") && !csvFilePath.contains("AU_21") && !csvFilePath.contains("AU_22")) {
				if (!csvFilePath.contains("AU_CaCell_")) {
					if (statusOFIpv4) {
						objCommand.append(" https://" + mcmip + ":7443/oss/ne/" + endbName + "/cells" + "");
					} else {
						objCommand.append(" https://[" + mcmip + "]:7443/oss/ne/" + endbName + "/cells" + "");
					}
				} else {
					if (statusOFIpv4) {
						objCommand.append(" https://" + mcmip + ":7443/oss/ne/" + "GROW_" + endbName + "/cells" + "");
					} else {
						objCommand.append(" https://[" + mcmip + "]:7443/oss/ne/" + "GROW_" + endbName + "/cells" + "");
					}

				}
			}
			else if (csvFilePath.contains("vDUCellGrow")) {
				if (statusOFIpv4) {
					objCommand.append(" https://" + mcmip + ":7443/oss/ne/" + "GROW_" + endbName + "/cells" + "");
				} else {
					objCommand.append(" https://[" + mcmip + "]:7443/oss/ne/" + "GROW_" + endbName + "/cells" + "");
				}	
			}
			else if (csvFilePath.contains("vDU_Cell")) {
				if (statusOFIpv4) {
					objCommand.append(" https://" + mcmip + ":7443/oss/ne/" + "GROW_" + endbName + "/cells" + "");
				} else {
					objCommand.append(" https://[" + mcmip + "]:7443/oss/ne/" + "GROW_" + endbName + "/cells" + "");
				}	
			}
			
			else {
				if (statusOFIpv4) {
					objCommand.append(" https://" + mcmip + ":7443/oss/ne" + "");
				} else {
					objCommand.append(" https://[" + mcmip + "]:7443/oss/ne" + "");
				}

			}
		} catch (Exception e) {
			logger.error("Failed in getCurlCommandforNeGrow() in CommonUtil", e);
		}

		return objCommand.toString();

	}

	
	@SuppressWarnings("unchecked")
	public static JSONObject createBashFromXml5GAuditEnbSites(String xmlFilePath, String bashFilePath, String mcmip,
			String eNodeID, String endName, LinkedHashSet<String> endIdsList, String expectPrompt) {
		StringBuilder objCommand = new StringBuilder();
		JSONObject resultMap = new JSONObject();
		try {
			boolean statusOFIpv4 = isIpv4Address(mcmip);
			String xmlfileContent = getXmlFileContent(xmlFilePath);
			xmlfileContent = xmlfileContent.replaceAll("\"", "\\\\\"");

			AtomicInteger incData = new AtomicInteger();

			for (String enbId : endIdsList) {
				if (incData.get() > 0) {
					objCommand.append(expectPrompt);

					objCommand.append("\n\n");
				}
				String enbID = "eNB" + "_" + enbId;
				objCommand.append("send \"");
				// objCommand.append("curl -X POST -k -g -6 -u
				// \\\"ais176\\@samsung.com:S\\@msung0ssuser\\\" -H \\'Content-type:
				// application\\/xml\\\' -d \\'");
				objCommand.append(
						"curl -X POST -k -g -6 -u \\\'ossuser:osspasswd\\\' -H \\'Content-Type: application\\/xml\\\' -d \\");
				objCommand.append("'");
				objCommand.append(xmlfileContent);
				objCommand.append("'");
				objCommand.append("\\");
				if (statusOFIpv4) {
					objCommand.append(" https:\\/\\/" + mcmip + ":7443\\/oss\\/netconfByNeId\\/" + enbID + "");
				} else {
					objCommand.append(" https:\\/\\/\\[" + mcmip + "\\]:7443\\/oss\\/netconfByNeId\\/" + enbID + "");
				}
				objCommand.append("\\r\"\n\n");

				incData.getAndIncrement();

			}

			resultMap = saveConfiguredFileContent(bashFilePath, objCommand.toString());

		} catch (Exception e) {
			logger.error("Failed in createBashFromXml5GAudit() in CommonUtil", e);
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason",
					GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GENERATE_BASH_FILE));
		}

		return resultMap;

	}

	
	public static void savecurlCommand(String bashFile, String curlCommand) {
		try {
			String filename = FilenameUtils.removeExtension(bashFile);
			saveConfiguredFileContent(filename + ".txt", curlCommand);
		} catch (Exception e) {
			logger.error(" CommonUtil::savecurlCommand():: ::" + ExceptionUtils.getFullStackTrace(e));
		}

	}
	
	public static Object createJson(File jsonpath) {
		return null;

	}

	public static String getchecklistNewName(String chekListfileName) {
		// TODO Auto-generated method stub

		StringBuilder newCheckListForMongo = new StringBuilder();

		try {

			String[] checklistArray = chekListfileName.split("_");

			for (String checkStrings : checklistArray) {
				String checkNewString = "";
				if (!checkStrings.contains("xlsx")) {
					if (checkStrings.length() > 3) {
						checkNewString = checkStrings.substring(0, 3);
					} else {
						checkNewString = checkStrings;
					}
					newCheckListForMongo.append(checkNewString);
					newCheckListForMongo.append("_");
				} else {
					newCheckListForMongo.append(checkStrings);
				}

			}

		} catch (Exception e) {
			logger.error("Exception RunTestServiceImpl in executeCommand() " + ExceptionUtils.getFullStackTrace(e));
		}
		return newCheckListForMongo.toString();
	}

	public void appendMessage(String filePath, String fileName, String msg, String enbId, String place)
			throws IOException {
		BufferedWriter out = null;
		try {
			StringBuffer premigString = new StringBuffer();
			if (place.equalsIgnoreCase("Right"))
				premigString.append(msg + " " + enbId + "\n");
			else if (place.equalsIgnoreCase("left"))
				premigString.append(enbId + " " + msg + "\n");
			else
				premigString.append(msg + "\n");
			FileWriter fstream = new FileWriter(filePath, true); // true tells to append data.
			out = new BufferedWriter(fstream);
			out.write(premigString.toString());
		} catch (IOException e) {
			System.err.println("Error: " + e.getMessage());
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	public static String getCurlCommandAuditTwamp(String xmlFilePath, String mcmip,
			String eNodeID, String endName, String fileName, String replaceData) {
		StringBuilder objCommand = new StringBuilder();
		try {
			String gNodeID = endName + "_" + eNodeID;
			boolean statusOFIpv4 = isIpv4Address(mcmip);
			String xmlfileContent = getXmlFileContent(xmlFilePath);
			if(fileName.contains(XmlCommandsConstants.AU_TWAMP_F1C_LINK)) {
				xmlfileContent = xmlfileContent.replaceAll("Remote_Ip_Addr", replaceData);
			} else if(fileName.contains(XmlCommandsConstants.AU_DIAGNOSTIC)) {
				xmlfileContent = xmlfileContent.replaceAll("Test_ID", replaceData);
			}
			objCommand.append(
					"curl -X POST -k -g -6 -u 'ossuser:osspasswd' -H 'Content-Type: application/xml' -d ");
			objCommand.append("'");
			objCommand.append(xmlfileContent);
			objCommand.append("'");
			if (statusOFIpv4) {
				if (endName.equalsIgnoreCase("eNB")) {
					objCommand.append(" https://" + mcmip + ":7443/oss/netconfByNeId/" + gNodeID + "");
				} else {
					objCommand.append(" https://" + mcmip + ":7443/oss/netconf/" + gNodeID + "");
				}
			} else {
				if (endName.equalsIgnoreCase("eNB")) {
					objCommand.append(" https://[" + mcmip + "]:7443/oss/netconfByNeId/" + gNodeID + "");
				} else {
					objCommand.append(" https://[" + mcmip + "]:7443/oss/netconf/" + gNodeID + "");
				}
			}

		} catch (Exception e) {
			logger.error("Failed in createBashFromXml5GAudit() in CommonUtil", e);
		}

		return objCommand.toString();

	}
	
	
	public static String getCurlCommandAudit4GTest(String xmlFilePath, String mcmip,
			String eNodeID, String endName, String fileName, List<String> replaceData) {
		StringBuilder objCommand = new StringBuilder();
		try {
			String gNodeID = endName + "_" + eNodeID;
			boolean statusOFIpv4 = isIpv4Address(mcmip);
			String xmlfileContent = getXmlFileContent(xmlFilePath);
			if(fileName.contains(XmlCommandsConstants.AUDIT_4G_OCNS_TEST_CELL_NUM)) {
				xmlfileContent = xmlfileContent.replaceAll("CELL_NUM", replaceData.get(0));
			} else if(fileName.contains(XmlCommandsConstants.AUDIT_4G_OCNS_TEST_TERMINATE)) {
				xmlfileContent = xmlfileContent.replaceAll("INVOCATION_ID", replaceData.get(0));
			} else if(fileName.contains(XmlCommandsConstants.AUDIT_4G_CHECK_RSSI) || fileName.contains(XmlCommandsConstants.AUDIT_4G_CHECK_TXPOWER) ||fileName.contains(XmlCommandsConstants.AUDIT_4G_CARRIER_RSSI)) {
				xmlfileContent = xmlfileContent.replaceAll("LCC_CARD", replaceData.get(0));
				xmlfileContent = xmlfileContent.replaceAll("CRPI_PORT_ID", replaceData.get(1));
			}else if(fileName.contains(XmlCommandsConstants.AUDIT_4G_CONFIGURE_FCC_ID)) {
				xmlfileContent = xmlfileContent.replaceAll("FCC_INDEX", replaceData.get(0));
				xmlfileContent = xmlfileContent.replaceAll("A3LRT4401-48A1", replaceData.get(1));
			}
			objCommand.append(
					"curl -X POST -k -g -6 -u 'ossuser:osspasswd' -H 'Content-Type: application/xml' -d ");
			objCommand.append("'");
			objCommand.append(xmlfileContent);
			objCommand.append("'");
			if (statusOFIpv4) {
				if (endName.equalsIgnoreCase("eNB")) {
					objCommand.append(" https://" + mcmip + ":7443/oss/netconfByNeId/" + gNodeID + "");
				} else {
					objCommand.append(" https://" + mcmip + ":7443/oss/netconf/" + gNodeID + "");
				}
			} else {
				if (endName.equalsIgnoreCase("eNB")) {
					objCommand.append(" https://[" + mcmip + "]:7443/oss/netconfByNeId/" + gNodeID + "");
				} else {
					objCommand.append(" https://[" + mcmip + "]:7443/oss/netconf/" + gNodeID + "");
				}
			}

		} catch (Exception e) {
			logger.error("Failed in createBashFromXml5GAudit() in CommonUtil", e);
		}

		return objCommand.toString();

	}
	
	public static String getCurlCommandAuditCbandTw(String xmlFilePath, String mcmip,
			String eNodeID, String endName, String fileName, List<String> replaceData) {
		StringBuilder objCommand = new StringBuilder();
		try {
			String gNodeID = endName + "_" + eNodeID;
			boolean statusOFIpv4 = isIpv4Address(mcmip);
			String xmlfileContent = getXmlFileContent(xmlFilePath);
			if(fileName.contains(XmlCommandsConstants.CBAND_F1U_TESTTWPING) || fileName.contains(XmlCommandsConstants.DSS_F1U_TESTTWPING)) {
				xmlfileContent = xmlfileContent.replaceAll("SOURCE_IP", replaceData.get(0));
				xmlfileContent = xmlfileContent.replaceAll("DESTINATION_IP", replaceData.get(1));
			} else if(fileName.contains(XmlCommandsConstants.CBAND_F1U_TESTTWPING_DIAGNOSIS) || fileName.contains(XmlCommandsConstants.DSS_F1U_TESTTWPING_DIAGNOSIS)) {
				xmlfileContent = xmlfileContent.replaceAll("TEST_ID", replaceData.get(0));
			} 
			objCommand.append(
					"curl -X POST -k -g -6 -u 'ossuser:osspasswd' -H 'Content-Type: application/xml' -d ");
			objCommand.append("'");
			objCommand.append(xmlfileContent);
			objCommand.append("'");
			if (statusOFIpv4) {
				if (endName.equalsIgnoreCase("eNB")) {
					objCommand.append(" https://" + mcmip + ":7443/oss/netconfByNeId/" + gNodeID + "");
				} else {
					objCommand.append(" https://" + mcmip + ":7443/oss/netconf/" + gNodeID + "");
				}
			} else {
				if (endName.equalsIgnoreCase("eNB")) {
					objCommand.append(" https://[" + mcmip + "]:7443/oss/netconfByNeId/" + gNodeID + "");
				} else {
					objCommand.append(" https://[" + mcmip + "]:7443/oss/netconf/" + gNodeID + "");
				}
			}

		} catch (Exception e) {
			logger.error("Failed in createBashFromXml5GAudit() in CommonUtil", e);
		}

		return objCommand.toString();

	}
	public static JSONObject createBashFromXml5GAuditTwampIAU(String xmlFilePath, String bashFilePath, String mcmip,
			String eNodeID, String endName, String fileName, String replaceData) {
		StringBuilder objCommand = new StringBuilder();
		JSONObject resultMap = new JSONObject();
		try {
			String gNodeID = endName + "_" + eNodeID;
			boolean statusOFIpv4 = isIpv4Address(mcmip);
			String xmlfileContent = getXmlFileContent(xmlFilePath);
			xmlfileContent = xmlfileContent.replaceAll("\"", "\\\\\"");
			if(fileName.contains(XmlCommandsConstants.IAU_TWAMP_F1C_LINK)) {
				xmlfileContent = xmlfileContent.replaceAll("Remote_Ip_Addr", replaceData);
			} else if(fileName.contains(XmlCommandsConstants.IAU_DIAGNOSTIC)) {
				xmlfileContent = xmlfileContent.replaceAll("Test_ID", replaceData);
			}
			

			objCommand.append("send \"");
			// objCommand.append("curl -X POST -k -g -6 -u
			// \\\"ais176\\@samsung.com:S\\@msung0ssuser\\\" -H \\'Content-type:
			// application\\/xml\\\' -d \\'");
			objCommand.append(
					"curl -X POST -k -g -6 -u \\\'ossuser:osspasswd\\\' -H \\'Content-Type: application\\/xml\\\' -d \\");
			objCommand.append("'");
			objCommand.append(xmlfileContent);
			objCommand.append("'");
			objCommand.append("\\");
			if (statusOFIpv4) {
				if (endName.equalsIgnoreCase("eNB")) {
					objCommand.append(" https:\\/\\/" + mcmip + ":7443\\/oss\\/netconfByNeId\\/" + gNodeID + "");
				} else {
					objCommand.append(" https:\\/\\/" + mcmip + ":7443\\/oss\\/netconf\\/" + gNodeID + "");
				}
			} else {
				if (endName.equalsIgnoreCase("eNB")) {
					objCommand.append(" https:\\/\\/\\[" + mcmip + "\\]:7443\\/oss\\/netconfByNeId\\/" + gNodeID + "");
				} else {
					objCommand.append(" https:\\/\\/\\[" + mcmip + "\\]:7443\\/oss\\/netconf\\/" + gNodeID + "");
				}
			}
			objCommand.append("\\r\"\n\n");

			resultMap = saveConfiguredFileContent(bashFilePath, objCommand.toString());

		} catch (Exception e) {
			logger.error("Failed in createBashFromXml5GAudit() in CommonUtil", e);
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason",
					GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GENERATE_BASH_FILE));
		}

		return resultMap;

	}
	public static String getCurlCommandAuditTwampIAU(String xmlFilePath, String mcmip,
			String eNodeID, String endName, String fileName, String replaceData) {
		StringBuilder objCommand = new StringBuilder();
		try {
			String gNodeID = endName + "_" + eNodeID;
			boolean statusOFIpv4 = isIpv4Address(mcmip);
			String xmlfileContent = getXmlFileContent(xmlFilePath);
			if(fileName.contains(XmlCommandsConstants.IAU_TWAMP_F1C_LINK)) {
				xmlfileContent = xmlfileContent.replaceAll("Remote_Ip_Addr", replaceData);
			} else if(fileName.contains(XmlCommandsConstants.IAU_DIAGNOSTIC)) {
				xmlfileContent = xmlfileContent.replaceAll("Test_ID", replaceData);
			}
			objCommand.append(
					"curl -X POST -k -g -6 -u 'ossuser:osspasswd' -H 'Content-Type: application/xml' -d ");
			objCommand.append("'");
			objCommand.append(xmlfileContent);
			objCommand.append("'");
			if (statusOFIpv4) {
				if (endName.equalsIgnoreCase("eNB")) {
					objCommand.append(" https://" + mcmip + ":7443/oss/netconfByNeId/" + gNodeID + "");
				} else {
					objCommand.append(" https://" + mcmip + ":7443/oss/netconf/" + gNodeID + "");
				}
			} else {
				if (endName.equalsIgnoreCase("eNB")) {
					objCommand.append(" https://[" + mcmip + "]:7443/oss/netconfByNeId/" + gNodeID + "");
				} else {
					objCommand.append(" https://[" + mcmip + "]:7443/oss/netconf/" + gNodeID + "");
				}
			}

		} catch (Exception e) {
			logger.error("Failed in createBashFromXml5GAudit() in CommonUtil", e);
		}

		return objCommand.toString();

	}
	@SuppressWarnings({ "unchecked", "rawtypes", "resource", "static-access" })
	public static String getJsonfromCSV(String sourcePath){
		String JSONObjectString = null;

		try {
			BufferedReader fileReader = new BufferedReader(new FileReader(sourcePath));
			CSVParser csvParser = new CSVParser(fileReader,
					CSVFormat.DEFAULT.withIgnoreHeaderCase().withTrim());

			Iterable<CSVRecord> csvRecords = csvParser.getRecords();
			String headerKey = "";
			String prevHeader = null;
			boolean headerFound = false;
			ArrayList<String> keys = null;
			LinkedHashMap neGrowJson = new LinkedHashMap<>();
			LinkedHashMap<String, ArrayList<String>> neGrowData = null;
			for (CSVRecord csvRecord : csvRecords) {
				
				if(csvRecord.get(0).toString().startsWith("@")) {
					prevHeader = headerKey;
					headerKey = csvRecord.get(0).toString();
					headerFound = true;
					if(keys != null) {
						LinkedHashMap jsonData = new LinkedHashMap<>();
						for(String keyHeader : keys) {
							ArrayList<String> rowData = neGrowData.get(keyHeader);
							if(rowData.size()==1) {
								jsonData.put(keyHeader, rowData.get(0));
							} else {
								jsonData.put(keyHeader, rowData);
							}								
						}
						neGrowJson.put(prevHeader, jsonData);
					}
					keys = new ArrayList();
					continue;
				}
				
				if(headerFound) {
					neGrowData = new LinkedHashMap<>();
					for(int i=0; i<csvRecord.size();i++) {
						keys.add(csvRecord.get(i).toString());
						ArrayList<String> rowData = new ArrayList<>();
						neGrowData.put(csvRecord.get(i).toString(), rowData);
						
					}
					headerFound = false;
					continue;
				}
				
				for(int i=0; i<csvRecord.size();i++) {
					neGrowData.get(keys.get(i)).add(csvRecord.get(i).toString());
				}
				
				
			}
			if(keys != null) {
				LinkedHashMap jsonData = new LinkedHashMap<>();
				for(String keyHeader : keys) {
					ArrayList<String> rowData = neGrowData.get(keyHeader);
					if(rowData.size()==1) {
						jsonData.put(keyHeader, rowData.get(0));
					} else {
						jsonData.put(keyHeader, rowData);
					}								
				}
				neGrowJson.put(headerKey, jsonData);
			}
			
			JSONObjectString = new JSONObject().toJSONString(neGrowJson);
			char c1 = '"';
			String v1 = Character.toString(c1);
			String v3 = "\\\\" + Character.toString(c1);
			JSONObjectString = JSONObjectString.replaceAll(Character.toString(c1), v3).replaceAll("]", "\\\\]").replace("[","\\[");
			System.out.println(JSONObjectString);
				
		} catch (Exception e) {
			e.printStackTrace();
		}

		return JSONObjectString;
	}
	
	@SuppressWarnings("unused")
	public static JSONObject createBashFromCSVCBand(String csvFilePath, String bashFilePath, String mcmip, String eNodeID,
			List<CIQDetailsModel> listCIQDetailsModel, String endbName) {
		StringBuilder objCommand = new StringBuilder();
		JSONObject resultMap = new JSONObject();
		try {
			// String id = ciqDetailsModel.getCiqMap().get("gNBID").getHeaderValue();

			boolean statusOFIpv4 = isIpv4Address(mcmip);
			String csvfileContentAsJSON = null;
			csvfileContentAsJSON = getJsonfromCSV(csvFilePath);
			
			objCommand.append("send \"");
			// objCommand.append("curl -X POST -k -g -6 -u
			// \\\"ais176\\@samsung.com:S\\@msung0ssuser\\\" -H \\'Content-type:
			// application\\/xml\\\' -d \\'");
			objCommand.append(
					"curl -X POST -k -g -6 -u \\\'ossuser:osspasswd\\\' -H \\'Content-Type: application\\/json\\\' -d \\");
			objCommand.append("'");
			objCommand.append(csvfileContentAsJSON);
			objCommand.append("'");
			objCommand.append("\\");
			 if (csvFilePath.contains("vDUCellGrow")) {
				    String id="";
				 	String[] arrOfStr = endbName.split("_", 2);
					for (String a : arrOfStr) {
						if(a.length()==10) {
			            	id= "0"+a;
			            	 endbName= id+"_"+arrOfStr[1];
			            	}
			            break;}
					
				 System.out.println(endbName);
				if (statusOFIpv4) {
					objCommand.append(" https://" + mcmip + ":7443/oss/ne/" + "GROW_" + endbName + "/cells" + "");
				}
				else {
					objCommand.append(" https://[" + mcmip + "]:7443/oss/ne/" + "GROW_" + endbName + "/cells" + "");
				}	
			}
			 else if (csvFilePath.contains("vDU_Cell")) {
				 String id="";
				 	String[] arrOfStr = endbName.split("_", 2);
					for (String a : arrOfStr) {
						if(a.length()==10) {
			            	id= "0"+a;
			            	 endbName= id+"_"+arrOfStr[1];
			            	}
			            break;}
					
				 System.out.println(endbName);
					if (statusOFIpv4) {
						objCommand.append(" https://" + mcmip + ":7443/oss/ne/" + "GROW_" + endbName + "/cells" + "");
					}
					else {
						objCommand.append(" https://[" + mcmip + "]:7443/oss/ne/" + "GROW_" + endbName + "/cells" + "");
					}	
				}
			else if (statusOFIpv4) {
				objCommand.append(" https:\\/\\/" + mcmip + ":7443\\/oss\\/ne" + "");
			} else {
				objCommand.append(" https:\\/\\/\\[" + mcmip + "\\]:7443\\/oss\\/ne" + "");
			}

			objCommand.append("\\r\"\n\n");
			resultMap = saveConfiguredCsvFileContent(bashFilePath, objCommand.toString());
			String curlCommand = getCurlCommandforNeGrow(csvfileContentAsJSON, mcmip, endbName, csvFilePath);
			savecurlCommand(bashFilePath, curlCommand);
			

		} catch (Exception e) {
			logger.error("Failed in curlComandForm() in CommonUtil", e);
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason",
					GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GENERATE_BASH_FILE));
		}

		return resultMap;

	}
	public static String getCurlCommandAudit4GTestjson(String mcmip, String eNodeID,
			String endName, List<String> replaceData) {
		StringBuilder objCommand = new StringBuilder();
		try {
			String gNodeID = endName + "_" + eNodeID;
			boolean statusOFIpv4 = isIpv4Address(mcmip);
			
			objCommand.append("curl -X POST -k -g -6 -u 'ossuser:osspasswd' -H 'Content-Type: application/json' -d ");
			objCommand.append("'");
			objCommand.append("{\"NE Name\":\"" + replaceData.get(0) + "\"}");
			objCommand.append("'");

			if (statusOFIpv4) {
				objCommand.append(" https://" + mcmip + ":7443/oss/neinfo/" + gNodeID + "");
			} else {
				objCommand.append(" https://[" + mcmip + "]:7443/oss/neinfo/" + gNodeID + "");
			}
		} catch (Exception e) {
			logger.error("Failed in getCurlCommandAudit4GTestjson() in CommonUtil", e);
		}

		return objCommand.toString();

	}
}
