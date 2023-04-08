package com.smart.rct.premigration.repository;

import java.util.List;
import java.util.Map;

import com.smart.rct.migration.entity.UseCaseBuilderEntity;
import com.smart.rct.migration.model.UseCaseBuilderModel;
import com.smart.rct.premigration.entity.CiqUploadAuditTrailDetEntity;
import com.smart.rct.premigration.models.EnbPreGrowAuditModel;
import com.smart.rct.usermanagement.models.User;

public interface EnbPreGrowRepository {

	public Map<String, Object> getNeGrowDetails(EnbPreGrowAuditModel enbModel, int page, int count);
	
	public List<UseCaseBuilderEntity> getUseCaseList(UseCaseBuilderModel useCaseBuilderModel);

	public List<CiqUploadAuditTrailDetEntity> getCiqList(User user, String programId, String fromDate, String toDate);

	public Map<String, List<String>> getSmDetails(Integer programId);

	public Map<String, List<String>> getSmSearchDetails(Integer programId);

	public Map<String, List<String>> getCiqNeSearchDetails(Integer programId);


}
