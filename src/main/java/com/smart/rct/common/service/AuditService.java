package com.smart.rct.common.service;

import java.util.List;
import java.util.Map;

import com.smart.rct.common.entity.AuditTrailEntity;
import com.smart.rct.common.models.AuditTrailModel;

public interface AuditService {

	boolean savedetail(AuditTrailEntity auditTrailEntity);

	Map<String, Object> getAuditDetails(int page, int count);

	List<String> getAuditFilters(String searchStatus, AuditTrailEntity auditTrailEntity);

	Map<String, Object> getAuditDetailsOnSearch(AuditTrailModel auditTrailModel, int page, int count);

}
