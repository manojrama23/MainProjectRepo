package com.smart.rct.postmigration.repository;

import java.util.List;

import com.smart.rct.postmigration.entity.AuditDetailConstantsEntity;

public interface AuditDetailConstantsRepository {

	List<AuditDetailConstantsEntity> getAuditConstantsEntityList(String bandWidth, String productCode, String band, String diversity);

	

}
