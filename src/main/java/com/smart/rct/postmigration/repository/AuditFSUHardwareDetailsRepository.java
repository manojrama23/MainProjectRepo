package com.smart.rct.postmigration.repository;

import java.util.List;

import com.smart.rct.postmigration.entity.Audit4GfsuHardwareDetailsEntity;


public interface AuditFSUHardwareDetailsRepository {

	List<Audit4GfsuHardwareDetailsEntity> getAuditHardwareDetailsEntityList(String hardwareName, String type);

}
