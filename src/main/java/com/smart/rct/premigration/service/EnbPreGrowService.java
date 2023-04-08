package com.smart.rct.premigration.service;

import java.util.List;
import java.util.Map;

import com.smart.rct.premigration.entity.CiqUploadAuditTrailDetEntity;
import com.smart.rct.premigration.models.EnbPreGrowAuditModel;
import com.smart.rct.usermanagement.models.User;

public interface EnbPreGrowService {

	public Map<String, Object> getNeGrowDetails(EnbPreGrowAuditModel enbModel, int page, int count);

	public List<CiqUploadAuditTrailDetEntity> getCiqList(User user, String valueOf, String startDate, String curdate);

	public Map<String, List<String>> getSmDetails(Integer programId);

	public Map<String, List<String>> getSmSearchDetails(Integer programId);

	public Map<String, List<String>> getCiqNeSearchDetails(Integer programId);


}
