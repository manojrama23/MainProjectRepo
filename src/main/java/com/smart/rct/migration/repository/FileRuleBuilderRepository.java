package com.smart.rct.migration.repository;

import java.util.Map;

import com.smart.rct.migration.entity.FileRuleBuilderEntity;
import com.smart.rct.migration.model.FileRuleBuilderModel;
import com.smart.rct.usermanagement.models.User;

public interface FileRuleBuilderRepository {

	boolean createFileRuleBuilder(FileRuleBuilderEntity fileRuleBuilderEntity);

	// Map<String, Object> searchByRuleName(String searchBy, String
	// searchParameter,int customerId,int page, int count);

	Map<String, Object> loadFileRuleBuilderSearchDetails(int customerId,
			int page, int count, String migrationType, int programId, String subType, User user);

	boolean updateFileRuleBuilder(FileRuleBuilderEntity fileRuleBuilderEntity);

	boolean deleteFileRule(int id);

	// List<FileRuleBuilderEntity> getFileRuleBuilderList();
	//
	// boolean duplicateFileName(FileRuleBuilderEntity fileRuleEntity);

	boolean findByRuleName(String fileName, int customerId, String migrationType, int programId, String userRole,String subType);

	public Map<String, Object> loadFileRuleBuilderSearchDetails(FileRuleBuilderModel fileRuleBuilderModel, int customerId,
			int page, int count, String migrationType, int programId, String subType, User user);	

}
