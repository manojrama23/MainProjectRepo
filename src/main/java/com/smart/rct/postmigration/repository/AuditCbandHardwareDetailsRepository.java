package com.smart.rct.postmigration.repository;

import java.util.List;

import com.smart.rct.postmigration.entity.AuditCbandHardwareDetailsEntity;

public interface AuditCbandHardwareDetailsRepository {

	List<AuditCbandHardwareDetailsEntity> getAuditHardwareDetailsEntityList(String hardwareName);

}
