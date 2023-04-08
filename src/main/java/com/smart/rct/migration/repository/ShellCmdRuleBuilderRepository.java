package com.smart.rct.migration.repository;

import java.util.Map;

import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.migration.entity.ShellCmdRuleBuilderEntity;
import com.smart.rct.migration.model.ShellCmdRuleBuilderModel;
import com.smart.rct.usermanagement.models.User;

public interface ShellCmdRuleBuilderRepository {

	boolean createCmdRule(ShellCmdRuleBuilderEntity cmdRuleEntity);

//	Map<String, Object> searchCmdRule(String searchBy, String searchParameter,int customerId, int page, int count);

	Map<String, Object> loadCmdRuleBuilderSearchDetails(int customerId,
			int page, int count, String migrationType, int programId, String subType, User user);

	boolean updateCmdRuleBuilder(ShellCmdRuleBuilderEntity cmdRuleEntity);

	boolean deleteCmdRule(int id);

	boolean findByRuleName(String ruleName, String migrationType, int programId, String userRole,String subType);

	String findCommand(String cmdRules,String migrationType,String subType,int programId);

	public CustomerDetailsEntity getCustomerDetailsEntity(int customerDetailsId);

	public Map<String, Object> loadCmdRuleBuilderSearchDetails(ShellCmdRuleBuilderModel cmdRuleBuilderModel, int customerId,
			int page, int count, String migrationType, int programId, String subType, User user);

}
