package com.smart.rct.migration.service;

import java.util.Map;

import com.smart.rct.exception.RctException;
import com.smart.rct.migration.entity.FileRuleBuilderEntity;
import com.smart.rct.migration.model.FileRuleBuilderModel;
import com.smart.rct.usermanagement.models.User;

public interface FileRuleBuilderService {
	boolean createFileRuleBuilder(FileRuleBuilderEntity fileRuleBuilderEntity);

	Map<String, Object> searchFileRuleBuilder(int customerId, int page, int count, String migrationType,
			int programId, String subType, User user);

	boolean deleteFileRule(int id) throws RctException;

	boolean updateFileRuleBuilder(FileRuleBuilderEntity fileRuleEntity);

	// List<FileRuleBuilderEntity> getFileRuleBuilderList();

	boolean duplicateFileName(String ruleName, int customerId, String migrationType, int programId, String string,String subType);

	public Map<String, Object> searchFileRuleBuilder(FileRuleBuilderModel fileRuleBuilderModel, int customerId,
			int page, int count, String migrationType, int programId, String subType, User user);
}
