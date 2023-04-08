package com.smart.rct.postmigration.repository;

import java.util.List;

import com.smart.rct.postmigration.entity.AuditHardwareDetailsEntity;

public interface AuditHardwareDetailsRepository {

	List<AuditHardwareDetailsEntity> getAuditHardwareDetailsEntityList(String hardwareName);

	List<AuditHardwareDetailsEntity> getAuditHardwareDetailsEntityListRx(String hardwareName, String vendorName,
			String type,String waveLength);

	List<AuditHardwareDetailsEntity> getAuditHardwareDetailsEntityList(String hardwareName, String type);

}
