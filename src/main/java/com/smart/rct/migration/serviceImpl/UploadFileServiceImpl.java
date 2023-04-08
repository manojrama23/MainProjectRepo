package com.smart.rct.migration.serviceImpl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.smart.rct.common.entity.LsmEntity;
import com.smart.rct.common.entity.NeVersionEntity;
import com.smart.rct.common.entity.NetworkConfigEntity;
import com.smart.rct.common.entity.NetworkTypeDetailsEntity;
import com.smart.rct.common.models.NetworkTypeDetailsModel;
import com.smart.rct.exception.RctException;
import com.smart.rct.migration.entity.UploadFileEntity;
import com.smart.rct.migration.model.UploadFileModel;
import com.smart.rct.migration.model.UploadFileModelConnection;
import com.smart.rct.migration.repository.UploadFileRepository;
import com.smart.rct.migration.service.UploadFileService;
import com.smart.rct.usermanagement.models.User;
import com.smart.rct.util.FileUtil;
import com.smart.rct.util.PasswordCrypt;

@Service
public class UploadFileServiceImpl implements UploadFileService {
	static final  Logger logger = LoggerFactory.getLogger(UploadFileServiceImpl.class);

	@Autowired
	UploadFileRepository uploadFileRepository;

	/**
	 * This api uploads script
	 * 
	 * @param file, uploadPath
	 * @return boolean
	 */
	@Override
	public boolean uploadFile(MultipartFile file, String uploadPath) {

		boolean uploadStatus = false;
		try {
			FileUtil.createDirectory(uploadPath);

			try {

				FileUtil.transferMultipartFile(file, uploadPath);

				uploadStatus = true;
			} catch (Exception e) {
				uploadStatus = false;
				logger.error("uploadFile() UploadFileServiceImpl" + ExceptionUtils.getFullStackTrace(e));
				FileUtil.deleteFileOrFolder(uploadPath);
			}

		} catch (Exception e) {
			uploadStatus = false;
			logger.error("Exception  uploadFile() in  UploadFileServiceImpl:" + ExceptionUtils.getFullStackTrace(e));
		}
		return uploadStatus;
	}
	
	@Override
	public boolean isFileEmpty(MultipartFile multipartFile){
		
		BufferedReader br;
		int c ;
		int count = 0;
		try {
	
		     InputStream is = multipartFile.getInputStream();
		     br = new BufferedReader(new InputStreamReader(is));
		     while ((c = br.read()) != -1) {
		    	 if((char)c != '\n' & (char)c != '\t' & (char)c != ' ')
		    	 {
		    		 count++;
		    	 }
		     }
		     br.close();
		     if(count>0)
		 		return false;
		 	else
		 		return true;
		     
		}catch (IOException e) {
			  logger.error("Exception  uploadFile() in  UploadFileServiceImpl:" + ExceptionUtils.getFullStackTrace(e));      
		}
		
		return false;
	}
	
	@Override
	public boolean executeCommand(String command) {
		Process p;
		String sErrorLine = null;
		boolean bErrorType = false;
		try {
			p = Runtime.getRuntime().exec(command);
			BufferedReader readerError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			while((sErrorLine = readerError.readLine()) != null) {
				bErrorType = true;
				
			}
		} catch (Exception e) {
			logger.error("Exception UploadFileServiceImpl in executeCommand() " + ExceptionUtils.getFullStackTrace(e));
		}
		return bErrorType;

	}
	
	@Override
	public boolean removeFile(String filePath){
		
		File file = new File(filePath);
		if(file.exists()){
			return file.delete();
		}
		return false;
	}

	/**
	 * this method will create Upload script
	 * 
	 * @param uploadFileEntity
	 * @return boolean
	 */
	@Override
	public boolean createUploadScript(UploadFileEntity uploadFileEntity) {
		boolean status = false;
		try {

			status = uploadFileRepository.createUploadScript(uploadFileEntity);
		} catch (Exception e) {
			logger.error(
					"Exception createUploadScript() in UploadFileServiceImpl " + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	/**
	 * This api returns upload script details
	 * 
	 * @param page, count
	 * @return Map
	 */
	@Override
	public Map<String, Object> getUploadScriptDetails(int customerId,int page, int count,String migrationType,int programName,String subType,User user) {
		Map<String, Object> objMapDetails = new HashMap<>();
		Map<String, Object> uploadScriptEntity = null;
		List<UploadFileModel> objListUploadFileModel = new ArrayList<>();
		
		try {
			uploadScriptEntity = uploadFileRepository.getUploadScriptDetails(customerId,page, count,migrationType,programName,subType,user);

			if (uploadScriptEntity != null && uploadScriptEntity.size() > 0) {

				@SuppressWarnings("unchecked")
				List<UploadFileEntity> uploadList = (List<UploadFileEntity>) uploadScriptEntity
						.get("uploadScriptTableDetails");

				if (uploadList != null && !uploadList.isEmpty()) {

					for (UploadFileEntity objUploadFileEntity : uploadList) {
						UploadFileModel objUploadFileModel = new UploadFileModel();
						UploadFileModelConnection objUploadFileModelConnection = new UploadFileModelConnection();
						Map<String, Object> connectionTerminal = new HashMap<>();
						objUploadFileModel.setUseCount(objUploadFileEntity.getUseCount());
						objUploadFileModel.setFileName(objUploadFileEntity.getFileName());
						objUploadFileModel.setFilePath(objUploadFileEntity.getFilePath());
						objUploadFileModel.setId(objUploadFileEntity.getId());
						objUploadFileModel.setArguments(objUploadFileEntity.getArguments());

//						objUploadFileModel.setLsmDetailsId(objUploadFileEntity.getLsmEntity().getId());
						if(objUploadFileEntity.getNeListEntity()!=null ) {
							objUploadFileModel.setLsmName(objUploadFileEntity.getNeListEntity().getNeName());
							objUploadFileModel.setLsmVersion(objUploadFileEntity.getNeListEntity().getNeVersionEntity().getNeVersion());
						}else if(objUploadFileEntity.getNeVersion()!=null) {
							objUploadFileModel.setLsmVersion(objUploadFileEntity.getNeVersion().getNeVersion());
						}
							
						//objUploadFileModel
						//		.setNwType(objUploadFileEntity.getNetworkTypeDetailsEntity().getNetworkType());
//						objUploadFileModel.setNwTypeDetailsId(objUploadFileEntity.getNetworkTypeDetailsEntity().getId());
						objUploadFileModel.setRemarks(objUploadFileEntity.getRemarks());
						objUploadFileModel.setUploadedBy(objUploadFileEntity.getUploadedBy());
						objUploadFileModel.setProgram(objUploadFileEntity.getProgram());
						objUploadFileModel.setMigrationType(objUploadFileEntity.getMigrationType());
						objUploadFileModel.setState(objUploadFileEntity.getState());
						//objUploadFileEntity.getNeListEntity().getNeVersionEntity();
						objUploadFileModel.setSubType(objUploadFileEntity.getSubType());
						objUploadFileModel.setNeVersion(objUploadFileEntity.getNeVersion());
						
					    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
					    String sCreationDate = dateFormat.format(objUploadFileEntity.getCreationDate());  
						
					    objUploadFileModel.setCreationDate(sCreationDate);
					    
					    objUploadFileModel.setScriptType(objUploadFileEntity.getScriptType());
					    objUploadFileModel.setConnectionLocation(objUploadFileEntity.getConnectionLocation());
					    objUploadFileModel.setConnectionLocationUserName(objUploadFileEntity.getConnectionLocationUserName());
					    objUploadFileModel.setConnectionLocationPwd(objUploadFileEntity.getConnectionLocationPwd());
					    
					    
					    objUploadFileModelConnection.setTerminalName(objUploadFileEntity.getConnectionTerminal());
					    objUploadFileModelConnection.setTermUsername(objUploadFileEntity.getConnectionTerminalUserName());
					    objUploadFileModelConnection.setTermPassword(PasswordCrypt.decrypt(objUploadFileEntity.getConnectionTerminalPwd()));
					    objUploadFileModelConnection.setPrompt(objUploadFileEntity.getPrompt());
					    connectionTerminal.put("connectionTerminal", objUploadFileModelConnection);
					    objUploadFileModel.setConnectionTerminalDetails(connectionTerminal);
					    
						objListUploadFileModel.add(objUploadFileModel);

					}

				}

			}

			objMapDetails.put("uploadScriptTableDetails", objListUploadFileModel);
			objMapDetails.put("count", uploadScriptEntity.get("count"));
		} catch (Exception e) {
			logger.error("Exception  getUploadScriptDetails() in  UploadFileServiceImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return objMapDetails;

	}

	/**
	 * this method will return lsm details
	 * 
	 * @param
	 * @return Map
	 */
	@SuppressWarnings("unChecked")
	@Override
	public Map<String, List<String>> getLsmDetails(List<NetworkTypeDetailsModel> neList) {
		List<LsmEntity> objList = null;
		Map<String, Set<String>> objMap = new LinkedHashMap<>();
		Map<Integer, List<String>> neTypeNVersionMap = new LinkedHashMap<>();
		JSONObject neJson = new JSONObject();
		try {
			objList = uploadFileRepository.getLsmDetails();

			if (objList != null && !objList.isEmpty()) {
				Set<Integer> nwTypeIdSet = objList.stream().map(x -> x.getNetworkTypeDetailsEntity().getId()).collect(Collectors.toSet());

				if (nwTypeIdSet != null && !nwTypeIdSet.isEmpty()) {
					for (Integer neTypeId : nwTypeIdSet) {
						List<String> objListLsmVer = new ArrayList<>();
						for (LsmEntity objModel : objList) {

							if (neTypeId.equals(objModel.getNetworkTypeDetailsEntity().getId())) {
								if (!objListLsmVer.contains(objModel.getLsmVersion()))
									objListLsmVer.add(objModel.getLsmVersion());
							}
						}
						neTypeNVersionMap.put(neTypeId, objListLsmVer);
					}

				}
			}

			if (objList != null && !objList.isEmpty()) {
				Set<String> objVersionsSet = objList.stream().map(x -> x.getLsmVersion()).collect(Collectors.toSet());

				if (objVersionsSet != null && !objVersionsSet.isEmpty()) {
					for (String objVersion : objVersionsSet) {
						Set<String> objListLsms = new HashSet();

						for (LsmEntity objModel : objList) {

							if (objVersion.equals(objModel.getLsmVersion())) {
								objListLsms.add(objModel.getLsmName());
							}
						}
						objMap.put(objVersion, objListLsms);
					}
				}
			}


			if (neList != null && !neList.isEmpty()) {
				for (NetworkTypeDetailsModel NetModel : neList) {
					Map map = new HashMap();
					List<String>  list = neTypeNVersionMap.get(NetModel.getId());
					if (list != null && !list.isEmpty()) {
						for (String version : list) {
							map.put(version, objMap.get(version));
						}
					}
					//map1.put(NetModel.getNetworkType(), map);
				}
			}

			neJson.putAll(objMap);

		} catch (Exception e) {
			logger.error("Exception  getLsmDetails() in  UploadFileServiceImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return neJson;
	}
	
	/**
	 * this method will return lsm details
	 * 
	 * @param
	 * @return Map
	 */
	@SuppressWarnings("unChecked")
	@Override
	public Map<String, List<String>> getNeDetails(List<NetworkTypeDetailsModel> neList,int programId) {
		List<NetworkConfigEntity> objList = null;
		Map<String, Set<String>> objMap = new LinkedHashMap<>();
		Map<Integer, List<String>> neTypeNVersionMap = new LinkedHashMap<>();
		JSONObject neJson = new JSONObject();
		try {
			objList = uploadFileRepository.getNeDetails(programId);

			if (objList != null && !objList.isEmpty()) {
				Set<Integer> nwTypeIdSet = objList.stream().map(x -> x.getId()).collect(Collectors.toSet());

				if (nwTypeIdSet != null && !nwTypeIdSet.isEmpty()) {
					for (Integer neTypeId : nwTypeIdSet) {
						List<String> objListLsmVer = new ArrayList<>();
						for (NetworkConfigEntity objModel : objList) {

							if (neTypeId.equals(objModel.getNeVersionEntity().getId())) {
								if (!objListLsmVer.contains(objModel.getNeVersionEntity().getNeVersion()))
									objListLsmVer.add(objModel.getNeVersionEntity().getNeVersion());
							}
						}
						neTypeNVersionMap.put(neTypeId, objListLsmVer);
					}

				}
			}

			if (objList != null && !objList.isEmpty()) {
				Set<String> objVersionsSet = objList.stream().map(x -> x.getNeVersionEntity().getNeVersion()).collect(Collectors.toSet());

				if (objVersionsSet != null && !objVersionsSet.isEmpty()) {
					for (String objVersion : objVersionsSet) {
						Set<String> objListLsms = new HashSet();

						for (NetworkConfigEntity objModel : objList) {

							if (objVersion.equals(objModel.getNeVersionEntity().getNeVersion())) {
								objListLsms.add(objModel.getNeName());
							}
						}
						objMap.put(objVersion, objListLsms);
					}
				}
			}


			if (neList != null && !neList.isEmpty()) {
				for (NetworkTypeDetailsModel NetModel : neList) {
					Map map = new HashMap();
					 List<String> list = neTypeNVersionMap.get(NetModel.getId());
					if (list != null && !list.isEmpty()) {
						for (String version : list) {
							map.put(version, objMap.get(version));
						}
					}
					//map1.put(NetModel.getNetworkType(), map);
				}
			}

			neJson.putAll(objMap);

		} catch (Exception e) {
			logger.error("Exception  getNeDetails() in  UploadFileServiceImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return neJson;
	}

	/**
	 * this method will return network type details
	 * 
	 * @param
	 * @return Map
	 */
	@Override
	public Map<String, List<String>> getNetworkType(String custName) {
		Map<String, List<String>> objMap = new LinkedHashMap<>();
		List<NetworkTypeDetailsEntity> nwTypeList = null;
		try {
			nwTypeList = uploadFileRepository.getNetworkType(custName);
			if (nwTypeList != null && !nwTypeList.isEmpty()) {
				Set<String> objNwTypeSet = nwTypeList.stream().map(x -> x.getNetworkType()).collect(Collectors.toSet());
				List<String> objNwTypeList = new ArrayList<>();
				if (objNwTypeSet != null && !objNwTypeSet.isEmpty()) {
					for (String objNwType : objNwTypeSet) {

						objNwTypeList.add(objNwType);

					}
					objMap.put("nwType", objNwTypeList);
				}
			}

		} catch (Exception e) {
			logger.error("Exception  getUploadScriptDetails() in  UploadFileServiceImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return objMap;
	}

	/**
	 * this method will update upload script details
	 * 
	 * @param uploadFileEntity
	 * @return boolean
	 */
	@Override
	public boolean updateUploadScript(UploadFileEntity uploadFileEntity) {
		boolean status = false;
		try {
			status = uploadFileRepository.updateUploadScript(uploadFileEntity);
		} catch (Exception e) {
			logger.error(
					"Exception  updateUploadScript() in  UploadFileServiceImpl:" + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	/**
	 * this method will delete upload script details
	 * 
	 * @param id
	 * @return boolean
	 * @throws RctException 
	 */
	@Override
	public boolean deleteUploadScript(int id) throws RctException {
		boolean status = false;
		try {
			status = uploadFileRepository.deleteUploadScript(id);
		} catch (Exception e) {
			logger.error("Exception deleteFileRule: " + ExceptionUtils.getFullStackTrace(e));
			if (e instanceof DataIntegrityViolationException)
				throw new RctException("Operation Failed : Upload Script is already associated in other places.");
			else {
				throw new RctException("Operation Failed : Failed to Delete Upload Script");
			}
		}
		return status;
	}

	/**
	 * this method will save script content into script
	 * 
	 * @param fileName,scriptFileContent
	 * @return boolean
	 */
	@Override
	public boolean saveViewScript(String filePath, String fileName, String scriptFileContent) {
		boolean status = false;
		StringBuilder contentBuilder = new StringBuilder();
		contentBuilder.append(filePath).append(fileName);
		String str = contentBuilder.toString();

		BufferedWriter bw = null;
		FileWriter fw = null;

		try {
			File file = new File(str);
			// if file doesn't exists
			if (!file.exists()) {
				logger.error("File does not exist");
			}
			fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);

			bw.write(scriptFileContent);
			status = true;

		} catch (IOException e) {
			logger.error(
					"Exception  saveViewScript() in  UploadFileServiceImpl:" + ExceptionUtils.getFullStackTrace(e));
		} finally {

			try {

				if (bw != null)
					bw.close();

				if (fw != null)
					fw.close();

			} catch (IOException ex) {

				logger.error("Exception  saveViewScript() in  UploadFileServiceImpl:"
						+ ExceptionUtils.getFullStackTrace(ex));

			}
		}
		return status;
	}

	/**
	 * this method will return the script content from script file
	 * 
	 * @param id,fileName,scriptFileContent
	 * @return boolean
	 */
	@SuppressWarnings("resource")
	@Override
	public String readContentFromFile(String filePath, String fileName) {
		StringBuilder contentBuilder = new StringBuilder();
		contentBuilder.append(filePath).append(fileName);
		String str = contentBuilder.toString();
		StringBuilder contentLine = new StringBuilder();
		String line = null;

		try {
			BufferedReader reader = new BufferedReader(new FileReader(str));

			while ((line = reader.readLine()) != null) {
				contentLine.append(line).append("\n");
			}
		} catch (IOException e) {
			logger.error("Exception  readContentFromFile() in  UploadFileServiceImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return contentLine.toString();
	}

	@Override
	public NetworkTypeDetailsEntity getNwEntity(String networkType) {
		NetworkTypeDetailsEntity nwEntity = null;
		try {
			nwEntity = uploadFileRepository.getNwEntity(networkType);
		} catch (Exception e) {
			logger.error("Exception  getNwEntity() in  UploadFileServiceImpl:" + ExceptionUtils.getFullStackTrace(e));
		}
		return nwEntity;
	}

	@Override
	public LsmEntity getLsm(String lsmVersion, String lsmName) {
		LsmEntity lsmEntity = null;
		try {
			lsmEntity = uploadFileRepository.getLsm(lsmVersion, lsmName);
		} catch (Exception e) {
			logger.error("Exception  getLsm() in  UploadFileServiceImpl:" + ExceptionUtils.getFullStackTrace(e));
		}
		return lsmEntity;
	}
	
	@Override
	public NetworkConfigEntity getNeEntity(String lsmVersion, String lsmName,int programId) {
		NetworkConfigEntity neEntity = null;
		try {
			neEntity = uploadFileRepository.getNeEntity(lsmVersion, lsmName,programId);
		} catch (Exception e) {
			logger.error("Exception  getNeEntity() in  UploadFileServiceImpl:" + ExceptionUtils.getFullStackTrace(e));
		}
		return neEntity;
	}
	
	@Override
	public NeVersionEntity getNeVersionEntity(String neVersion, String programId) {
		NeVersionEntity neEntity = null;
		try {
			neEntity = uploadFileRepository.getNeVersionEntity(neVersion, programId);
		} catch (Exception e) {
			logger.error("Exception  getNeVersionEntity() in  UploadFileServiceImpl:" + ExceptionUtils.getFullStackTrace(e));
		}
		return neEntity;
	}

	@Override
	public UploadFileEntity getUploadScriptByPath(StringBuilder uploadPath,String fileName) {
		UploadFileEntity uploadScriptEntity = null;
		try {
			uploadScriptEntity = uploadFileRepository.getUploadScriptByPath(uploadPath,fileName);
		} catch (Exception e) {
			logger.error("Exception  getUploadScriptByPath() in  UploadFileServiceImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return uploadScriptEntity;

	}
	
	@Override
	public UploadFileEntity getUploadScriptDuplicate(String fileName,String migrationType,String programName,String subType) {
		UploadFileEntity uploadScriptEntity = null;
		try {
			uploadScriptEntity = uploadFileRepository.getUploadScriptDuplicate(fileName,migrationType,programName,subType);
		} catch (Exception e) {
			logger.error("Exception  getUploadScriptDuplicate() in  UploadFileServiceImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return uploadScriptEntity;

	}

	@Override
	public void deleteDirectory(String path) {
//		UploadFileEntity uploadFileEntity = uploadFileRepository.getFilePath(id);
//		String filePath = uploadFileEntity.getFilePath();
//		String fileName = uploadFileEntity.getFileName();
//		StringBuilder sb = new StringBuilder();
//		sb.append(filePath).append(fileName);
		if (path != null) {
			File file = new File(path);
			try {
				if (file.exists()) {
					file.delete();
				}
			} catch (Exception e) {
				logger.error("Exception  deleteDirectory() in  UploadFileServiceImpl:"
						+ ExceptionUtils.getFullStackTrace(e));
			}
		}
	}

	@Override
	public String getFilePath(int id) {
		UploadFileEntity uploadFileEntity = null;
		String filePath = null;
		StringBuilder sb = new StringBuilder();
		try {
			uploadFileEntity = uploadFileRepository.getFilePath(id);
			filePath = uploadFileEntity.getFilePath();
			String fileName = uploadFileEntity.getFileName();
			sb.append(filePath).append(fileName);
		} catch (Exception e) {
			logger.error(
					"Exception  getFilePath() in  UploadFileServiceImpl:" + ExceptionUtils.getFullStackTrace(e));
		}
		return sb.toString();
	}
	
	@Override
	public List<UploadFileEntity> getUploadFileEntity(String filePath) {
		List<UploadFileEntity> uploadFileEntity = null;
		
		try {
			uploadFileEntity = uploadFileRepository.getUploadFileEntity(filePath);
			
		} catch (Exception e) {
			logger.error(
					"Exception  getFilePath() in  UploadFileServiceImpl:" + ExceptionUtils.getFullStackTrace(e));
		}
		return uploadFileEntity;
	}
	
	/**
	 * 
	 * this method will search the upload script
	 * 
	 * @param searchBy,searchParameter,page,count
	 * @return map
	 */

	@Override
	public Map<String, Object> searchUploadScript(String fileName, String uploadedBy,String startDate,String endDate, int customerId, int page,
			int count,String migrationType,String programName,String subType,User user,String state) {
		Map<String, Object> searchUploadScript = null;
		try {
			searchUploadScript = uploadFileRepository.searchUploadScript(fileName, uploadedBy,startDate,endDate,
					customerId, page, count,migrationType,programName,subType,user,state);
		} catch (Exception e) {
			logger.error("Exception searchUploadScript: " + ExceptionUtils.getFullStackTrace(e));
		}
		return searchUploadScript;
	}

}
