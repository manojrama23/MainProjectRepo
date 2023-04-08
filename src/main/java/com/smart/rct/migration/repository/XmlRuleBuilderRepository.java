package com.smart.rct.migration.repository;

import java.util.List;
import java.util.Map;

import com.smart.rct.migration.entity.XmlElementEntity;
import com.smart.rct.migration.entity.XmlRootEntity;
import com.smart.rct.migration.entity.XmlRuleBuilderEntity;
import com.smart.rct.migration.model.XmlElementModel;
import com.smart.rct.migration.model.XmlRootModel;
import com.smart.rct.migration.model.XmlSerachModel;
import com.smart.rct.usermanagement.models.User;

public interface XmlRuleBuilderRepository {

	public boolean createXmlRuleBuilder(XmlRuleBuilderEntity xmlRuleBuilderEntity, List<XmlRootModel> xmlRootModelList,
			List<XmlElementModel> xmlElementModelList, int programId);

	public boolean deleteXmlRuleBuilder(int xmlRuleBuilderId);

	public Map<String,Object> loadXmlRuleBuilderSearchDetails(int parseInt, int parseInt2,
			XmlSerachModel searchModel, String migrationType, int programId, String subType, User user, int customerId);

	public  Map<String, Object> loadXmlRuleBuilderDetails(int parseInt, int parseInt2, String migrationType,
			int programId, String subType, User user, int customerId);

	public XmlRuleBuilderEntity getXmlRuleBuilderEntity(int parseInt);

	public boolean updateXmlRuleBuilder(XmlRuleBuilderEntity xmlRuleBuilderEntity, List<XmlRootModel> xmlRootModelList,
			List<XmlElementModel> xmlElementModelList, int programId);

	public boolean findByRuleName(String ruleName, int customerId, String migrationType, int programId,
			String userRole,String subType);
	
	public List<XmlElementEntity> getXmlElementEntity(Integer xmlRuleId);
	
	public List<XmlRootEntity> getXmlRootEntity(Integer xmlRuleId);
	
	public boolean deleteXmlElementById(int id);
	
	public boolean deleteXmlRootById(int id);

	public List<XmlRuleBuilderEntity> getXmlRuleBuilderEntityList(int programId);

	public XmlRuleBuilderEntity findByRuleName(int programId, String ruleName);
	
}
