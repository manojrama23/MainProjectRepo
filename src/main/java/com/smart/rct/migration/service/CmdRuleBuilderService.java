package com.smart.rct.migration.service;

import java.util.Map;

import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.exception.RctException;
import com.smart.rct.migration.entity.CmdRuleBuilderEntity;
import com.smart.rct.migration.model.CmdRuleBuilderModel;
import com.smart.rct.usermanagement.models.User;

public interface CmdRuleBuilderService {

	boolean createCmdRule(CmdRuleBuilderEntity cmdRuleEntity);

	Map<String, Object> searchCmdRule(int customerId, int page, int count,
			String migrationType, int programId, String subType, User user);

	boolean updateCmdRuleBuilder(CmdRuleBuilderEntity cmdRuleEntity);

	boolean deleteCmdRule(int id) throws RctException;

	boolean duplicateFileName(String ruleName, String migrationType, int programId, String userRole,String subType);

	public CustomerDetailsEntity getCustomerDetailsEntity(int customerDetailsId);

	public Map<String, Object> searchCmdRule(CmdRuleBuilderModel cmdRuleBuilderModel, int customerId, int page, int count,
			String migrationType, int programId, String subType, User user);

}
