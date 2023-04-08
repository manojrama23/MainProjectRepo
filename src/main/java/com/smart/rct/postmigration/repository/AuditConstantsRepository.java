package com.smart.rct.postmigration.repository;

import java.util.List;

import com.smart.rct.postmigration.entity.AuditConstantsEntity;

public interface AuditConstantsRepository {

	List<AuditConstantsEntity> getAuditConstantsEntityList(String programName, String parameterName, String type);

	List<AuditConstantsEntity> getAuditConstantsEntityList(String programName, String parameterName);

}
