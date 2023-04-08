package com.smart.rct.postmigration.repository;

import java.util.List;

import com.smart.rct.postmigration.entity.Audit5GCbandFirmwareDetailsEntity;

public interface Audit5GCbandFirmwareDetailsRepository {

	List<Audit5GCbandFirmwareDetailsEntity> getAuditFirmwareDetailsEntityList(String firmwareName, String relVersion,
			String neVersion, String prodCode);

}
