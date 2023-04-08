package com.smart.rct.migration.repository;

import java.util.List;

import com.smart.rct.migration.entity.PrePostMapping;
import com.smart.rct.migration.entity.RetTestEntity;


public interface PrePostRepository {

	boolean savePrePostDeatil(PrePostMapping objInfo);

	boolean deletePrePostDeatil(String enbId, int programId);

	List<PrePostMapping> getAuditPrePostEntity(String enbId, int programId);

}
