package com.smart.rct.postmigration.repository;

import java.util.List;

import com.smart.rct.postmigration.entity.AuditFirmwareDetailsEntity;

public interface AuditFirmwareDetailsRepository {

	List<AuditFirmwareDetailsEntity> getAuditFirmwareDetailsEntityList(String firmwareName, String neType);

	List<AuditFirmwareDetailsEntity> getAuditFirmwareDetailsEntityList(String firmwareName, String neType,
			String neVersion);

	List<AuditFirmwareDetailsEntity> getAuditFirmwareDetailsEntityALL(String firmwareName, String neType, String softwareVersion, String packageVersion);

}
