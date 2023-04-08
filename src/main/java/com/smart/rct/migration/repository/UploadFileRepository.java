package com.smart.rct.migration.repository;

import java.util.List;
import java.util.Map;

import com.smart.rct.common.entity.LsmEntity;
import com.smart.rct.common.entity.NeVersionEntity;
import com.smart.rct.common.entity.NetworkConfigEntity;
import com.smart.rct.common.entity.NetworkTypeDetailsEntity;
import com.smart.rct.migration.entity.UploadFileEntity;
import com.smart.rct.usermanagement.models.User;

public interface UploadFileRepository {

	boolean createUploadScript(UploadFileEntity uploadFileEntity);

	Map<String, Object> getUploadScriptDetails(int customerId, int page, int count, String migrationType, int programId,
			String subType, User user);

	// List<LsmModel> getLsmDetails();
	List<LsmEntity> getLsmDetails();

	List<NetworkConfigEntity> getNeDetails(int programId);

	// Map<String, Object> getNetworkType();
	List<NetworkTypeDetailsEntity> getNetworkType(String custName);

	boolean updateUploadScript(UploadFileEntity uploadFileEntity);

	boolean deleteUploadScript(int id);

	NetworkTypeDetailsEntity getNwEntity(String networkType);

	LsmEntity getLsm(String lsmVersion, String lsmName);

	NetworkConfigEntity getNeEntity(String lsmVersion, String lsmName, int programId);

	NeVersionEntity getNeVersionEntity(String neVersion, String programId);

	UploadFileEntity getUploadScriptByPath(StringBuilder uploadPath, String fileName);

	UploadFileEntity getUploadScriptDuplicate(String fileName, String migrationType, String programName,
			String subType);

	UploadFileEntity getFilePath(int id);

	UploadFileEntity getUploadFileEntity(int scriptId);

	List<UploadFileEntity> getUploadFileEntity(String filePath);

	Map<String, Object> searchUploadScript(String fileName, String uploadedBy, String startDate, String endDate,
			int customerId, int page, int count, String migrationType, String programName, String subType, User user,
			String state);
}
