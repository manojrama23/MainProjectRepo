package com.smart.rct.migration.repository;

import java.util.List;

import com.smart.rct.migration.entity.RetTestEntity;


public interface RetRepository {
	List<RetTestEntity> getAuditRetDetailsEntityList(String enbid, String uniqueId);
	
	boolean saveRetDeatil(RetTestEntity objInfo);

	List<RetTestEntity> getAuditRetEntity(String enbId);
}
