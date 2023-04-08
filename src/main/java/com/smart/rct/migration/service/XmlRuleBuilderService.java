package com.smart.rct.migration.service;

import java.util.List;
import java.util.Map;

import com.smart.rct.migration.entity.XmlRuleBuilderEntity;
import com.smart.rct.migration.model.XmlRuleBuilderModel;
import com.smart.rct.migration.model.XmlSerachModel;
import com.smart.rct.usermanagement.models.User;
import com.smart.rct.util.PaginationModel;

public interface XmlRuleBuilderService {

	public boolean createXmlRuleBuilder(XmlRuleBuilderModel xmlRuleBuilderModel, String migrationType, int programId,
			String subType, String sessionId, int customerId);

	public boolean deleteXmlRuleBuilder(int xmlRuleBuilderId);

	public Map<String,Object> loadXmlRuleBuilderSearchDetails(PaginationModel paginationModel,
			XmlSerachModel searchModel, int programId, String migrationType, String subType, User user, int customerId);

	public  Map<String, Object> loadXmlRuleBuilderDetails(PaginationModel paginationModel, int programId,
			String migrationType, String subType, User user, int customerId);

	public boolean updateXmlRuleBuilder(XmlRuleBuilderModel xmlRuleBuilderModel, String migrationType, int programId,
			String subType, String sessionId);

	public boolean duplicateFileName(String ruleName, int customerId, String migrationType, int programId, String role,String subType);
	
	public List<XmlRuleBuilderEntity> getXmlRuleBuilderEntityList(int programId);
	
	public XmlRuleBuilderEntity findByRuleName(int programId, String ruleName);
}
