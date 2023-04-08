package com.smart.rct.postmigration.repository;

import java.util.List;

import com.smart.rct.postmigration.entity.Audit5GMMHardwareDetailsEntity;


public interface Audit5GMMHardwareDetailsRepository {

	List<Audit5GMMHardwareDetailsEntity> getAuditHardwareDetailsEntityList(String hardwareName, String type);

}
