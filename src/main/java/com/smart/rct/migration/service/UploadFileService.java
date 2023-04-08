package com.smart.rct.migration.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.smart.rct.common.entity.LsmEntity;
import com.smart.rct.common.entity.NeVersionEntity;
import com.smart.rct.common.entity.NetworkConfigEntity;
import com.smart.rct.common.entity.NetworkTypeDetailsEntity;
import com.smart.rct.common.models.NetworkTypeDetailsModel;
import com.smart.rct.exception.RctException;
import com.smart.rct.migration.entity.UploadFileEntity;
import com.smart.rct.usermanagement.models.User;

public interface UploadFileService {

	boolean uploadFile(MultipartFile file, String uploadPath);

	boolean createUploadScript(UploadFileEntity uploadFileEntity);

	Map<String, Object> getUploadScriptDetails(int customerId,int page, int count,String migrationType,int programId,String subType,User user);

	Map<String, List<String>> getLsmDetails(List<NetworkTypeDetailsModel> neList);
	
	Map<String, List<String>> getNeDetails(List<NetworkTypeDetailsModel> neList,int programId);

	Map<String, List<String>> getNetworkType(String custName);

	boolean updateUploadScript(UploadFileEntity uploadFileEntity);

	boolean deleteUploadScript(int id) throws RctException;

	boolean saveViewScript(String filePath, String fileName, String scriptFileContent);

	String readContentFromFile(String filePath, String fileName);

	NetworkTypeDetailsEntity getNwEntity(String networkType);

	LsmEntity getLsm(String lsmVersion, String lsmName);
	
	NetworkConfigEntity getNeEntity(String lsmVersion, String lsmName,int programId);
	
	NeVersionEntity getNeVersionEntity(String neVersion,String programId);

	UploadFileEntity getUploadScriptByPath(StringBuilder uploadPath,String fileName);
	
	UploadFileEntity getUploadScriptDuplicate(String fileName,String migrationType,String programName,String subType);

	void deleteDirectory(String path);
	
	String getFilePath(int id);
	
	List<UploadFileEntity> getUploadFileEntity(String filePath);

	Map<String, Object> searchUploadScript(String fileName, String uploadedBy,String startDate,String endDate,int customerId, int page, int count,String migrationType,String programName,String subType,User user,String state);
	
	public boolean isFileEmpty(MultipartFile multipartFile);
	
	public boolean executeCommand(String command);
	
	public boolean removeFile(String filePath);

}
